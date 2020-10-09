package org.jboss.tools.intellij.mta.explorer.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SetOutputLocationDialog extends DialogWrapper {

    private JTextField text;
    private String current;

    public SetOutputLocationDialog(String current) {
        super(true);
        this.current = current;
        super.init();
        setTitle("Output Location");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        text = new JTextField();
        text.setPreferredSize(new Dimension(500, 30));
        text.setText(this.current);
        dialogPanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1;
        cons.gridx = 0;
        dialogPanel.add(text, cons);
        return dialogPanel;
    }

    public String getOutputLocation() {
        return this.text.getText();
    }
}
