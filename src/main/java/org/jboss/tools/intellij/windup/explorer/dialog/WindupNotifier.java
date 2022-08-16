/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat. All rights reserved.
 *--------------------------------------------------------------------------------------------*/
package org.jboss.tools.intellij.windup.explorer.dialog;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;

public class WindupNotifier {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Windup Notification", NotificationDisplayType.BALLOON, true);

    public static void notifyError(String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR)
                .notify(null);
    }
}
