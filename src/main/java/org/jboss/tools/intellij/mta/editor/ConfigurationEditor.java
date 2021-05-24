package org.jboss.tools.intellij.mta.editor;

import com.intellij.openapi.Disposable;
import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jboss.tools.intellij.mta.editor.server.ConfigurationEditorVerticle;
import org.jboss.tools.intellij.mta.editor.server.VertxService;
import org.jboss.tools.intellij.mta.model.MtaConfiguration;

public class ConfigurationEditor extends JFXPanel implements Disposable {

    private static final String URL = "http://localhost:";
    private static final String PATH = "/static/configuration-editor/views/unified.html?id=";

    private ConfigurationFile configurationFile;
    private MtaConfiguration configuration;
    private VertxService vertxService;
    private ConfigurationEditorVerticle verticle;

    public ConfigurationEditor(ConfigurationFile file) {
        this.configurationFile = file;
        this.configuration = file.getConfiguration();
        this.vertxService = file.getVertxService();
        PlatformImpl.setImplicitExit(false);
        PlatformImpl.runLater(() -> this.init());
    }

    private void init() {
        this.verticle = new ConfigurationEditorVerticle(
                this.configurationFile.getModelService(),
                this.configuration,
                this.vertxService,
                this.configurationFile);
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(URL + this.vertxService.getServerPort() + PATH + configuration.getId());
        Scene scene = new Scene(webView, Color.ALICEBLUE);
        super.setScene(scene);
    }

    @Override
    public void dispose() {
        System.out.println("ConfigurationEditorDisposing...");
        this.verticle.dispose();
    }
}
