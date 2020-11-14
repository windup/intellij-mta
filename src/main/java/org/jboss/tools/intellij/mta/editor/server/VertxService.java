package org.jboss.tools.intellij.mta.editor.server;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class VertxService {

    private Vertx vertx;
    private EventBus eventBus;
    private HttpServer server;
    private Router router;

    public VertxService() {
        this.init();
    }

    private void init() {
        this.vertx = Vertx.vertx();
        this.eventBus = vertx.eventBus();
        this.router = Router.router(this.vertx);
        this.startServer();
    }

    private void startServer() {
        SockJSBridgeOptions opts = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex("to.server.*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("to.client.*"));
        Router ebHandler = SockJSHandler.create(this.vertx).bridge(opts);
//        this.router.mountSubRouter("/bus/*", ebHandler);
        this.router.mountSubRouter("/bus/", ebHandler);
        this.server = this.vertx.createHttpServer().requestHandler(this.router).listen(8077);
    }

    public Router getRouter() {
        return this.router;
    }

    public Vertx getVertx() {
        return this.vertx;
    }
}