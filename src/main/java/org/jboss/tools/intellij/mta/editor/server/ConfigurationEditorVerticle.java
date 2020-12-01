package org.jboss.tools.intellij.mta.editor.server;

import com.google.common.collect.Sets;
import com.intellij.openapi.Disposable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

import java.util.Set;

public class ConfigurationEditorVerticle extends AbstractVerticle implements Handler, Disposable {

    private static final String JSON_TYPE = "application/json";

    private VertxService vertxService;
    private MtaConfiguration configuration;
    private String editorPath;
    private String serverPath;
    private Router router;
    private Set<Route> routes = Sets.newHashSet();

    public ConfigurationEditorVerticle(MtaConfiguration configuration, VertxService vertxService) {
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
        this.createPOSTRoute("updateOption", this::handleUpdateOption);
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
        this.routes.add(this.router.post("/mta/" + this.configuration.getId() + "/" + path).handler(requestHandler));
    }

    private void handleGetOptions(RoutingContext ctx) {
        jsonHeader(ctx);
        end(ctx, JsonUtil.getOptions(this.configuration));
    }

    private void handleGetHelp(RoutingContext ctx) {
        jsonHeader(ctx);
        end(ctx, JsonUtil.getHelpData());
    }

    private void handleUpdateOption(RoutingContext ctx) {
        System.out.println("handleUpdateOption... ");
        JsonObject option = ctx.getBodyAsJson();
        ctx.response().end();
    }

    @Override
    public void dispose() {
        if (context != null) {
            this.vertx.undeploy(deploymentID());
        }
        this.routes.forEach(Route::remove);
    }
}
