package org.jboss.tools.intellij.mta.editor.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.DocumentRunnable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
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
import org.jboss.tools.intellij.mta.editor.ConfigurationFile;
import org.jboss.tools.intellij.mta.explorer.nodes.ConfigurationNode;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;
import org.jboss.tools.intellij.mta.services.ModelService;

import java.io.IOException;
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
    private ConfigurationFile configurationFile;
    private Set<Route> routes = Sets.newHashSet();

    public ConfigurationEditorVerticle(
            ModelService modelService,
            MtaConfiguration configuration,
            VertxService vertxService,
            ConfigurationFile configurationFile) {
        this.modelService = modelService;
        this.configuration = configuration;
        this.vertxService = vertxService;
        this.router = vertxService.getRouter();
        this.configurationFile = configurationFile;
        this.setupRouterHeaders();
        this.createRoutes();
        this.vertxService.getVertx().deployVerticle(this);
        System.out.print("Configuration Editor verticle started");
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
        this.createPOSTRoute("promptExternal", this::promptExternal);
        this.createPOSTRoute("addOptionValue", this::addOptionValue);
        this.router.errorHandler(500, (RoutingContext ctx) -> {
            System.err.println("Handling configuration editor failure");
            Throwable failure = ctx.failure();
            if (failure != null) {
                failure.printStackTrace();
            }
        });
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
        System.out.println("handleGetOptions...");
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    private void handleGetHelp(RoutingContext ctx) {
        jsonHeader(ctx);
        end(ctx, JsonUtil.getHelpData());
    }

    private void updateOption(RoutingContext ctx) {
        System.out.println("updateOption... ");
        try {
            JsonObject option = ctx.getBodyAsJson();
            String name = option.getString("name");
            Object value = option.getValue("value");
            if (name.equals("name")) {
                if (value == null || value.equals("")) {
                    value = "unknown";
                }
                this.configuration.setName((String) value);
                this.configurationFile.rename(null, this.configuration.getName());
                ApplicationManager.getApplication().invokeLater(() -> {
                    ConfigurationNode.openConfigurationEditor(this.configurationFile, this.modelService.getProject());
                });
            } else if (value == null || value == "" || ((value instanceof Boolean && !((Boolean) value)))) {
                this.configuration.getOptions().remove(name);
            } else {
                if (value instanceof JsonArray) {
                    value = ((JsonArray) value).getList();
                } else if (value instanceof Boolean) {
                    value = value.toString();
                }
                this.configuration.getOptions().put(name, value);
            }
            jsonHeader(ctx);
            end(ctx, JsonUtil.getOptions(this.configuration));
            if (name.equals("name")) {
                this.modelService.getTreeModel().invalidate(this.configuration.getNode(), false);
            }
        }
        catch (Exception e) {
            System.out.println("Error during updateOption: " + e.getMessage());
            e.printStackTrace();
            jsonHeader(ctx);
            end(ctx, JsonUtil.getOptions(this.configuration));
        }
    }

    private void promptExternal(RoutingContext ctx) {
        System.out.println("promptExternal begin");
        JsonObject option = ctx.getBodyAsJson();
        String optionName = option.getString("name");
        boolean chooseFiles = !optionName.equals("userRulesDirectory");
        boolean chooseMultiple = ((JsonArray)option.getValue("ui-type")).contains("many");
        FileChooserDescriptor descriptor = new FileChooserDescriptor(
                chooseFiles,
                true,
                false,
                false,
                false,
                chooseMultiple);
        try {
            Runnable r = ()->
            {
                FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(descriptor, this.modelService.getProject(), null);
                VirtualFile[] files = dialog.choose(this.modelService.getProject());
                System.out.println("Selected files: ");
                System.out.println(Arrays.toString(files));
                if (files.length > 0) {
                    for (VirtualFile file : files) {
                        this.addOptionValue(optionName, file.getPath());
                    }
                }
                jsonHeader(ctx);
                end(ctx, JsonUtil.getOptions(this.configuration));
                System.out.println("invokeLater end");
            };
            ApplicationManager.getApplication().invokeLater(r);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("promptExternal end");
    }

    private void addOptionValue(RoutingContext ctx) {
        JsonObject jsonOption = ctx.getBodyAsJson();
        JsonObject data = (JsonObject)jsonOption.getValue("option");
        String newValue = jsonOption.getString("value");
        String optionName = data.getString("name");
        this.addOptionValue(optionName, newValue);
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    private void addOptionValue(String optionName, String newValue) {
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
    }

    @Override
    public void dispose() {
        if (context != null) {
            this.vertx.undeploy(deploymentID());
        }
        this.routes.forEach(Route::remove);
    }
}
