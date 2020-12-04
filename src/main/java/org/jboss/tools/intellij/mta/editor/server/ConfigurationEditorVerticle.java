package org.jboss.tools.intellij.mta.editor.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.vfs.VirtualFile;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.services.ModelService;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationEditorVerticle extends AbstractVerticle implements Handler, Disposable {

    private static final String JSON_TYPE = "application/json";

    private ModelService modelService;
    private VertxService vertxService;
    private MtaConfiguration configuration;
    private String editorPath;
    private String serverPath;
    private Router router;
    private Set<Route> routes = Sets.newHashSet();

    public ConfigurationEditorVerticle(
            ModelService modelService,
            MtaConfiguration configuration,
            VertxService vertxService) {
        this.modelService = modelService;
        this.configuration = configuration;
        this.vertxService = vertxService;
        this.router = vertxService.getRouter();
        this.setupRouterHeaders();
        this.createRoutes();
        this.vertxService.getVertx().deployVerticle(this);
    }

    private void setupRouterHeaders() {
        this.router.route().handler(CorsHandler.create("*").allowedMethods(Sets.newHashSet(
                HttpMethod.GET,
                HttpMethod.PUT,
                HttpMethod.POST,
                HttpMethod.DELETE,
                HttpMethod.OPTIONS,
                HttpMethod.CONNECT,
                HttpMethod.TRACE)).allowedHeaders(Sets.newHashSet(
                "Content-Type",
                "Access-Control-Request-Method",
                "Access-Control-Allow-Credentials",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Headers")));
    }

    private void createRoutes() {
        this.createGETRoute("options", this::handleGetOptions);
        this.createGETRoute("help", this::handleGetHelp);
        this.createPOSTRoute("updateOption", this::updateOption);
        this.createPOSTRoute("promptWorkspaceFileOrFolder", this::promptWorkspaceFileOrFolder);
        this.createPOSTRoute("promptExternal", this::promptExternal);
        this.createPOSTRoute("addOptionValue", this::addOptionValue);
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void handle(Object event) {
        System.out.println("ConfigurationEditorVerticle#handle");
    }

    private void send(Object data) {
        this.vertx.eventBus().send(this.editorPath, data);
    }

    private void jsonHeader(RoutingContext ctx) {
        ctx.response().putHeader("content-type", JSON_TYPE);
    }

    private void end(RoutingContext ctx) {
        ctx.response().end();
    }

    private void end(RoutingContext ctx, JsonObject json) {
        ctx.response().end(json.encodePrettily());
    }

    private void createGETRoute(String path, Handler<RoutingContext> requestHandler) {
        this.routes.add(this.router.get("/mta/" + this.configuration.getId() + "/" + path)
                .produces(JSON_TYPE).handler(requestHandler));
    }

    private void createPOSTRoute(String path, Handler<RoutingContext> requestHandler) {
        this.routes.add(this.router.post("/mta/" + this.configuration.getId() + "/" + path)
                .produces(JSON_TYPE).handler(requestHandler));
    }

    private void handleGetOptions(RoutingContext ctx) {
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    private void handleGetHelp(RoutingContext ctx) {
        jsonHeader(ctx);
        end(ctx, JsonUtil.getHelpData());
    }

    private void updateOption(RoutingContext ctx) {
        System.out.println("updateOption... ");
        JsonObject option = ctx.getBodyAsJson();
        String name = option.getString("name");
        Object value = option.getValue("value");
        if (name.equals("name")) {
            this.configuration.setName((String)value);
        }
        else if (value == null || value == "" || ((value instanceof Boolean && (Boolean)value == false))) {
            this.configuration.getOptions().remove(name);
        }
        else {
            if (value instanceof JsonArray) {
                value = ((JsonArray)value).getList();
            }
            else if (value instanceof Boolean) {
                value = value.toString();
            }
            this.configuration.getOptions().put(name, value);
        }
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    private void promptWorkspaceFileOrFolder(RoutingContext ctx) {
        System.out.println("promptWorkspaceFileOrFolder... ");
        JsonObject option = ctx.getBodyAsJson();
        String name = option.getString("name");
        Object value = option.getValue("value");

        ctx.response().end();
    }

    private void promptExternal(RoutingContext ctx) {
        System.out.println("promptExternal... ");
        JsonObject option = ctx.getBodyAsJson();
        String name = option.getString("name");
        Object value = option.getValue("value");

        FileChooserDescriptor descriptor = new FileChooserDescriptor(
                true,
                true,
                false,
                false,
                false,
                false);
        try {
            Runnable r = ()->
            {
                FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(descriptor, this.modelService.getProject(), null);
                VirtualFile[] files = dialog.choose(this.modelService.getProject());
                System.out.println(Arrays.toString(files));
            };
            WriteCommandAction.runWriteCommandAction(this.modelService.getProject(), r);
//            ApplicationManager.getApplication().runWriteAction(() -> {
//            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ctx.response().end();
    }

    private void addOptionValue(RoutingContext ctx) {
        JsonObject jsonOption = ctx.getBodyAsJson();
        JsonObject data = (JsonObject)jsonOption.getValue("option");
        String newValue = jsonOption.getString("value");
        String optionName = data.getString("name");
        Map<String, Object> options = this.configuration.getOptions();
        if (options.containsKey(optionName)) {
            List<String> optionList = (List<String>)options.get(optionName);
            optionList.add(newValue);
        }
        else {
            List<String> values = Lists.newArrayList();
            values.add(newValue);
            options.put(optionName, values);
        }
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    @Override
    public void dispose() {
        if (context != null) {
            this.vertx.undeploy(deploymentID());
        }
        this.routes.forEach(Route::remove);
    }
}
