// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat.notification;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class NotificationsManager
{
    public static final LinkedBlockingQueue<Notification> pendingNotifications;
    private static Notification currentNotification;
    public static ArrayList<Notifications> notifications;
    
    public static void show(final Notification notification) {
        NotificationsManager.pendingNotifications.add(notification);
    }
    
    public static void show(final Notifications notification) {
        NotificationsManager.notifications.add(notification);
    }
    
    public static void update() {
        if (NotificationsManager.currentNotification != null && !NotificationsManager.currentNotification.isShown()) {
            NotificationsManager.currentNotification = null;
        }
        if (NotificationsManager.currentNotification == null && !NotificationsManager.pendingNotifications.isEmpty()) {
            (NotificationsManager.currentNotification = NotificationsManager.pendingNotifications.poll()).show();
        }
    }
    
    public static void render() {
        try {
            final int divider = Minecraft.getMinecraft().gameSettings.guiScale;
            final int width = Minecraft.getMinecraft().displayWidth / divider;
            final int height = Minecraft.getMinecraft().displayHeight / divider;
            update();
            if (NotificationsManager.currentNotification != null) {
                NotificationsManager.currentNotification.render(width, height);
            }
        }
        catch (final Exception ex) {}
    }
    
    public static void drawNotifications() {
        try {
            final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            double startY;
            final double lastY = startY = res.getScaledHeight() - 25;
            for (int i = 0; i < NotificationsManager.notifications.size(); ++i) {
                final Notifications not = NotificationsManager.notifications.get(i);
                if (not.shouldDelete()) {
                    NotificationsManager.notifications.remove(not);
                    for (int cao = 0; cao > not.width; --cao) {
                        not.animationX = cao - not.width;
                    }
                    startY += not.getHeight() + 3.0;
                }
                not.draw(startY, lastY);
                for (int number = 0; number < not.width; ++number) {
                    not.animationX = number + not.width;
                }
                startY -= not.getHeight() + 2.0;
            }
        }
        catch (final Throwable t) {}
    }
    
    static {
        NotificationsManager.notifications = new ArrayList<Notifications>();
        pendingNotifications = new LinkedBlockingQueue<Notification>();
        NotificationsManager.currentNotification = null;
    }
}
