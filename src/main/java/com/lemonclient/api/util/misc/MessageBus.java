// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import com.lemonclient.api.util.chat.ChatUtil;
import net.minecraft.util.text.ITextComponent;
import com.lemonclient.api.util.chat.NotificationManager;
import net.minecraft.util.text.TextFormatting;
import com.lemonclient.client.module.modules.hud.Notifications;
import net.minecraft.util.text.TextComponentString;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.client.Minecraft;
import com.mojang.realmsclient.gui.ChatFormatting;

public class MessageBus
{
    public static String watermark;
    public static ChatFormatting messageFormatting;
    protected static final Minecraft mc;
    
    public static void printDebug(final String text, final Boolean error) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        sendClientPrefixMessage((error ? colorMain.getDisabledColor() : colorMain.getEnabledColor()) + text, error ? Notification.Type.ERROR : Notification.Type.INFO);
    }
    
    public static void sendClientPrefixMessage(final String message, final Notification.Type type) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        setWatermark();
        final TextComponentString string1 = new TextComponentString(MessageBus.watermark + MessageBus.messageFormatting + message);
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        if (notifications.isEnabled()) {
            NotificationManager.add(new Notification(TextFormatting.GRAY + message, type));
            if (notifications.disableChat.getValue()) {
                return;
            }
        }
        MessageBus.mc.player.sendMessage(string1);
    }
    
    public static void sendMessage(final String message, final Notification.Type type, final String uniqueWord, final int senderID, final boolean notification) {
        if (notification) {
            sendClientDeleteMessage(message, type, uniqueWord, senderID);
        }
        else {
            sendDeleteMessage(message, uniqueWord, senderID);
        }
    }
    
    public static void sendClientDeleteMessage(final String message, final Notification.Type type, final String uniqueWord, final int senderID) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        setWatermark();
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        if (notifications.isEnabled()) {
            NotificationManager.add(new Notification(TextFormatting.GRAY + message, type));
            if (notifications.disableChat.getValue()) {
                return;
            }
        }
        ChatUtil.sendDeleteMessage(MessageBus.watermark + MessageBus.messageFormatting + message, uniqueWord, senderID);
    }
    
    public static void sendDeleteMessage(final String message, final String uniqueWord, final int senderID) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        setWatermark();
        ChatUtil.sendDeleteMessage(MessageBus.watermark + MessageBus.messageFormatting + message, uniqueWord, senderID);
    }
    
    public static void sendCommandMessage(final String message, final boolean prefix) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        setWatermark();
        final String watermark1 = prefix ? MessageBus.watermark : "";
        ChatUtil.sendDeleteMessage(watermark1 + MessageBus.messageFormatting + message, "Command", 6);
    }
    
    public static void sendMessage(final String message, final boolean prefix) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        setWatermark();
        final String watermark1 = prefix ? MessageBus.watermark : "";
        final TextComponentString string = new TextComponentString(watermark1 + MessageBus.messageFormatting + message);
        MessageBus.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(string, getIdFromString(message));
    }
    
    public static int getIdFromString(String name) {
        StringBuilder s = new StringBuilder();
        name = name.replace("\u79ae", "e");
        final String blacklist = "[^a-z]";
        for (int i = 0; i < name.length(); ++i) {
            s.append(Integer.parseInt(String.valueOf(name.charAt(i)).replaceAll(blacklist, "e"), 36));
        }
        try {
            s = new StringBuilder(s.toString());
        }
        catch (final StringIndexOutOfBoundsException ignored) {
            s = new StringBuilder(String.valueOf(Integer.MAX_VALUE));
        }
        return Integer.MAX_VALUE - Integer.parseInt(s.toString().toLowerCase());
    }
    
    public static void sendClientRawMessage(final String message) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        final TextComponentString string = new TextComponentString(MessageBus.messageFormatting + message);
        MessageBus.mc.player.sendMessage(string);
    }
    
    public static void sendServerMessage(final String message) {
        if (MessageBus.mc.world == null || MessageBus.mc.player == null) {
            return;
        }
        MessageBus.mc.player.connection.sendPacket(new CPacketChatMessage(message));
    }
    
    public static void setWatermark() {
        MessageBus.watermark = ChatFormatting.GREEN + "[" + ChatFormatting.YELLOW + "Lemon" + ChatFormatting.GREEN + "] " + ChatFormatting.RESET;
    }
    
    static {
        MessageBus.watermark = ChatFormatting.GREEN + "[" + ChatFormatting.YELLOW + "Lemon" + ChatFormatting.GREEN + "] " + ChatFormatting.RESET;
        MessageBus.messageFormatting = ChatFormatting.GRAY;
        mc = Minecraft.getMinecraft();
    }
}
