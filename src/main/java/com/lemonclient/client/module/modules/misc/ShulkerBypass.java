// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import net.minecraft.client.Minecraft;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Peek", category = Category.Misc)
public class ShulkerBypass extends Module
{
    BooleanSetting notifications;
    BooleanSetting shulker;
    IntegerSetting cmdDelay;
    BooleanSetting map;
    BooleanSetting book;
    public static boolean notification;
    public static boolean shulkers;
    public static boolean maps;
    public static boolean books;
    public static int delay;
    
    public ShulkerBypass() {
        this.notifications = this.registerBoolean("Notification", false);
        this.shulker = this.registerBoolean("Shulker", true);
        this.cmdDelay = this.registerInteger("Shulker Delay", 0, 0, 20);
        this.map = this.registerBoolean("Map", true);
        this.book = this.registerBoolean("Book", true);
    }
    
    public void onEnable() {
        if (Minecraft.getMinecraft().player == null) {
            return;
        }
        MessageBus.sendMessage("[ShulkerBypass] To use this throw a shulker on the ground", Notification.Type.INFO, "Peek", 3, this.notifications.getValue());
    }
    
    @Override
    public void onUpdate() {
        ShulkerBypass.notification = this.notifications.getValue();
        ShulkerBypass.delay = this.cmdDelay.getValue();
        ShulkerBypass.shulkers = this.shulker.getValue();
        ShulkerBypass.maps = this.map.getValue();
        ShulkerBypass.books = this.book.getValue();
    }
}
