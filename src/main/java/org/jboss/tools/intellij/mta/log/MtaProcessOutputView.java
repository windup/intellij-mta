package org.jboss.tools.intellij.mta.log;
;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.*;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public final class MtaProcessOutputView extends JPanel {

    private final JPanel myMessagePanel;
    private final JPanel myContentPanel;
    private final CardLayout myCardLayout;

    private volatile OutputView myCurrentView;

    private Project project;  

    public MtaProcessOutputView(Project project) {
        super(new BorderLayout(2, 0));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.project = project;
        myCurrentView = new OutputView(project);
        myCardLayout = new CardLayout();
        myContentPanel = new JPanel(myCardLayout);
        myContentPanel.add(myCurrentView.getComponent(), myCurrentView.getId());
        myMessagePanel = JBUI.Panels.simplePanel(myContentPanel);
        add(myMessagePanel, BorderLayout.CENTER);

        showView();
    }

    private void showView() {
        myCardLayout.show(myContentPanel, myCurrentView.getId());
        JComponent component = IdeFocusTraversalPolicy.getPreferredFocusedComponent(myMessagePanel);
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() ->
                IdeFocusManager.getGlobalInstance().requestFocus(component, true));
        repaint();
    }

    public JComponent getComponent() {
        return this;
    }
}
