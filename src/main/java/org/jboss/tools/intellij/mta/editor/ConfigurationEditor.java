package org.jboss.tools.intellij.mta.editor;

import com.intellij.openapi.Disposable;
import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ConfigurationEditor extends JFXPanel implements Disposable {

    public ConfigurationEditor() {
        PlatformImpl.setImplicitExit(false);
        PlatformImpl.runLater(() -> this.init());
    }

    private void init() {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SLASH) {
                engine.executeScript("smoothScrollToBottom()");
            }
        });
        Scene scene = new Scene(webView, Color.ALICEBLUE);
        super.setScene(scene);
    }

    @Override
    public void dispose() {

    }
}
