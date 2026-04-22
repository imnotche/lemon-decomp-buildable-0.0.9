// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import java.util.Iterator;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.hud.Notifications;
import java.util.ArrayList;

public class NotificationManager
{
    public static ArrayList<Notification> notifications;
    
    public static void add(final Notification notify) {
        final Notifications notification = ModuleManager.getModule(Notifications.class);
        final int max = notification.max.getValue();
        if (max != 0 && NotificationManager.notifications.size() >= max) {
            final String s = notification.mode.getValue();
            switch (s) {
                case "Remove": {
                    NotificationManager.notifications.remove(NotificationManager.notifications.get(0));
                    break;
                }
                case "Cancel": {
                    return;
                }
            }
        }
        notify.y = (float)(NotificationManager.notifications.size() * 25);
        NotificationManager.notifications.add(notify);
    }
    
    public static void draw() {
        if (NotificationManager.notifications.isEmpty()) {
            return;
        }
        Notification remove = null;
        for (final Notification notify : NotificationManager.notifications) {
            if (notify.x == 0.0f) {
                notify.in = !notify.in;
            }
            if (Math.abs(notify.x - notify.width) < 0.1 && !notify.in) {
                remove = notify;
            }
            final Notifications notifications = ModuleManager.getModule(Notifications.class);
            if (notify.in) {
                notify.x = notify.animationUtils.animate(0.0f, notify.x, notifications.xSpeed.getValue().floatValue());
            }
            else {
                notify.x = (float)notify.animationUtils.animate(notify.width, notify.x, notifications.xSpeed.getValue().floatValue());
            }
            notify.onRender();
        }
        if (remove != null) {
            NotificationManager.notifications.remove(remove);
        }
    }
    
    static {
        NotificationManager.notifications = new ArrayList<Notification>();
    }
}
