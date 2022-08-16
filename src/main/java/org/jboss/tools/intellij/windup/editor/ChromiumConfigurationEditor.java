/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.editor;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;
import org.jboss.tools.intellij.windup.editor.server.ConfigurationEditorVerticle;
import org.jboss.tools.intellij.windup.editor.server.VertxService;
import org.jboss.tools.intellij.windup.model.WindupConfiguration;

import javax.swing.*;

public class ChromiumConfigurationEditor implements Disposable {

    private static final String URL = "http://localhost:";
    private static final String PATH = "/static/configuration-editor/views/unified.html?id=";

    private ConfigurationFile configurationFile;
    private WindupConfiguration configuration;
    private VertxService vertxService;
    private ConfigurationEditorVerticle verticle;
    private JBCefBrowser browser;

    public ChromiumConfigurationEditor(ConfigurationFile file) {
        this.configurationFile = file;
        this.configuration = file.getConfiguration();
        this.vertxService = file.getVertxService();
        this.browser = this.createControls();
    }

    private JBCefBrowser createControls() {
        String url = this.init();
        try {
            if (!JBCefApp.isSupported()) {
                System.out.println("Configuration Editor NOT SUPPORTED. Missing Java Chromium Embedded Library.");
                return null;
            }
            JBCefBrowser myBrowser = new JBCefBrowser(url);
//            CefBrowser myDevTools = myBrowser.getCefBrowser().getDevTools();
//            JBCefBrowser myDevToolsBrowser = new JBCefBrowser(myDevTools,
//                    myBrowser.getJBCefClient());
            return myBrowser;
        }
        catch (Exception e) {
            System.out.println("ERROR!!!");
            e.printStackTrace();
        }
        return null;
    }

    private String init() {
        this.verticle = new ConfigurationEditorVerticle(
                this.configurationFile.getModelService(),
                this.configuration,
                this.vertxService,
                this.configurationFile);
        return URL + this.vertxService.getServerPort() + PATH + configuration.getId();
    }

    public JComponent getComponent() {
        return this.browser.getComponent();
    }

    @Override
    public void dispose() {
        System.out.println("ChromiumConfigurationEditorDisposing...");
        this.verticle.dispose();
    }
}
