/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class ConfigurationEditorWrapper extends UserDataHolderBase implements FileEditor {

    private static final Logger LOGGER = Logger.getInstance(ConfigurationEditorWrapper.class);

    public static final String EDITOR_NAME = "Kantra Configuration";

    protected final VirtualFile file;

    private final ChromiumConfigurationEditor editor;

    public ConfigurationEditorWrapper(@NotNull Project project, @NotNull VirtualFile file) {
        this.file = file;
        this.editor = new ChromiumConfigurationEditor((ConfigurationFile) file);
    }

    @NotNull
    public JComponent getComponent() {
        return this.editor.getComponent();
    }

    public JComponent getPreferredFocusedComponent() {
        return this.editor.getComponent();
    }

    @NotNull
    @NonNls
    public String getName() {
        return EDITOR_NAME;
    }

    @NotNull
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return FileEditorState.INSTANCE;
    }

    public void setState(@NotNull FileEditorState state) {
    }

    public boolean isModified() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void selectNotify() {
    }

    public void deselectNotify() {
    }

    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    public void dispose() {
        this.editor.dispose();
        Disposer.dispose(this);
    }

    @Override
    public VirtualFile getFile() {
        return this.file;
    }
}
