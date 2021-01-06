package org.jboss.tools.intellij.mta.editor.server;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.io.File;

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
        IdeaPluginDescriptor descriptor = PluginManagerCore.getPlugin(PluginId.getId("org.jboss.tools.intellij.mta"));
        if (descriptor != null) {
            this.router.route().handler(BodyHandler.create().setUploadsDirectory(System.getProperty("java.io.tmpdir")));
            File webroot = new File(descriptor.getPluginPath().toFile(), "lib/webroot");
            String root = webroot.getAbsolutePath();
            this.router.route("/static/*").handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(root));
            this.server = this.vertx.createHttpServer().requestHandler(this.router).listen(8077);
        }
    }

    public Router getRouter() {
        return this.router;
    }

    public Vertx getVertx() {
        return this.vertx;
    }
}