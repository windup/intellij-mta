/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.mta.editor;

import com.intellij.internal.statistic.collectors.fus.fileTypes.FileTypeUsageSchemaDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ConfigurationFileTypeFactory implements FileTypeUsageSchemaDescriptor {

    @NonNls public static final String CONFIGURATION_EXTENSION = "mta-configuration";
    @NonNls static final String DOT_CONFIGURATION_EXTENSION = "." + CONFIGURATION_EXTENSION;

    @Override
    public boolean describes(@NotNull VirtualFile file) {
        return false;
    }

    public static boolean isFxml(@NotNull PsiFile file) {
        final VirtualFile virtualFile = file.getViewProvider().getVirtualFile();
        return isFxml(virtualFile);
    }

    public static boolean isFxml(@NotNull VirtualFile virtualFile) {
        if (CONFIGURATION_EXTENSION.equals(virtualFile.getExtension())) {
            final FileType fileType = virtualFile.getFileType();
            if (fileType == getFileType() && !fileType.isBinary()) {
                return virtualFile.getName().endsWith(DOT_CONFIGURATION_EXTENSION);
            }
        }
        return false;
    }

    @NotNull
    public static FileType getFileType() {
        return FileTypeManager.getInstance().getFileTypeByExtension(CONFIGURATION_EXTENSION);
    }
}
