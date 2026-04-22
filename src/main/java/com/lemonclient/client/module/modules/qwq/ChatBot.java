// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.function.Predicate;
import com.lemonclient.api.util.misc.ServerUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChat;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.regex.Pattern;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ChatBot", category = Category.qwq)
public class ChatBot extends Module
{
    ModeSetting mode;
    IntegerSetting delay;
    String botmessage;
    boolean msg;
    int waited;
    private final Pattern CHAT_PATTERN;
    private final Pattern CHAT_PATTERN2;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public ChatBot() {
        this.mode = this.registerMode("Mode", Arrays.asList("Client", "Everyone"), "Everyone");
        this.delay = this.registerInteger("Delay", 0, 0, 20, () -> this.mode.getValue().equals("Everyone"));
        this.CHAT_PATTERN = Pattern.compile("<.*?> ");
        this.CHAT_PATTERN2 = Pattern.compile("(.*?)");
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (!this.msg) {
                if (event.getPacket() instanceof SPacketChat) {
                    String s = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    final Matcher matcher = this.CHAT_PATTERN.matcher(s);
                    String username = "unnamed";
                    final Matcher matcher2 = this.CHAT_PATTERN2.matcher(s);
                    if (matcher2.find()) {
                        matcher2.group();
                        s = matcher2.replaceFirst("");
                    }
                    if (matcher.find()) {
                        final String username2 = matcher.group();
                        username = username2.substring(1, username2.length() - 2);
                        s = matcher.replaceFirst("");
                    }
                    if (s.startsWith("!")) {
                        final String s2 = s.substring(Math.min(s.length(), 1));
                        if (!s2.startsWith("online")) {
                            if (s2.startsWith("ping")) {
                                String s3 = s2.substring(Math.min(s2.length(), 5));
                                final ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<NetworkPlayerInfo>(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());
                                for (final Entity qwq : ChatBot.mc.world.loadedEntityList) {
                                    if (qwq instanceof EntityPlayer && s3.contains(qwq.getName())) {
                                        s3 = qwq.getName();
                                    }
                                }
                                final String finalS = s3;
                                final NetworkPlayerInfo profile = infoMap.stream().filter(networkPlayerInfo -> finalS.toLowerCase().contains(networkPlayerInfo.getGameProfile().getName().toLowerCase())).findFirst().orElse(null);
                                if (profile != null) {
                                    final String message = profile.getGameProfile().getName() + "'s ping is " + profile.getResponseTime();
                                    String messageSanitized = message.replaceAll("\u79ae", "");
                                    if (messageSanitized.length() > 255) {
                                        messageSanitized = messageSanitized.substring(0, 255);
                                    }
                                    this.botmessage = messageSanitized;
                                    this.msg = true;
                                }
                            }
                            else if (s2.startsWith("myping")) {
                                final ArrayList<NetworkPlayerInfo> infoMap2 = new ArrayList<NetworkPlayerInfo>(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());
                                final String finalUsername = username;
                                final NetworkPlayerInfo profile2 = infoMap2.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(finalUsername)).findFirst().orElse(null);
                                if (profile2 != null) {
                                    final String message2 = "Your ping is " + profile2.getResponseTime();
                                    String messageSanitized2 = message2.replaceAll("\u79ae", "");
                                    if (messageSanitized2.length() > 255) {
                                        messageSanitized2 = messageSanitized2.substring(0, 255);
                                    }
                                    this.botmessage = messageSanitized2;
                                    this.msg = true;
                                }
                            }
                            else if (s2.startsWith("tps")) {
                                final String message3 = "The tps is now " + ServerUtil.getTPS();
                                String messageSanitized3 = message3.replaceAll("\u79ae", "");
                                if (messageSanitized3.length() > 255) {
                                    messageSanitized3 = messageSanitized3.substring(0, 255);
                                }
                                this.botmessage = messageSanitized3;
                                this.msg = true;
                            }
                            else if (s2.startsWith("help")) {
                                final String uwu = "The commands are : tps, myping, ping playername";
                                String messageSanitized4 = uwu.replaceAll("\u79ae", "");
                                if (messageSanitized4.length() > 255) {
                                    messageSanitized4 = messageSanitized4.substring(0, 255);
                                }
                                this.botmessage = messageSanitized4;
                                this.msg = true;
                            }
                            else if (s2.startsWith("gay")) {
                                String s4 = s2.substring(Math.min(s2.length(), 4));
                                final ArrayList<NetworkPlayerInfo> infoMap3 = new ArrayList<NetworkPlayerInfo>(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());
                                for (final Entity qwq2 : ChatBot.mc.world.loadedEntityList) {
                                    if (qwq2 instanceof EntityPlayer && s4.contains(qwq2.getName())) {
                                        s4 = qwq2.getName();
                                    }
                                }
                                final String finalS2 = s4;
                                final String finalS = "";
                                final NetworkPlayerInfo profile3 = infoMap3.stream().filter(networkPlayerInfo -> finalS.toLowerCase().contains(networkPlayerInfo.getGameProfile().getName().toLowerCase())).findFirst().orElse(null);
                                if (profile3 != null) {
                                    final String name = profile3.getGameProfile().getName();
                                    this.botmessage = name + " is " + String.format("%.1f", Math.random() * 100.0) + "% gay";
                                    this.msg = true;
                                }
                            }
                            else if (s2.startsWith("byebyebot")) {
                                this.botmessage = "!online owob";
                                this.msg = true;
                            }
                            else {
                                final String uwu2 = "Sorry, I cant understand this command";
                                String messageSanitized5 = uwu2.replaceAll("\u79ae", "");
                                if (messageSanitized5.length() > 255) {
                                    messageSanitized5 = messageSanitized5.substring(0, 255);
                                }
                                this.botmessage = messageSanitized5;
                                this.msg = true;
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.msg) {
            if (this.mode.getValue().equals("Client")) {
                MessageBus.sendClientDeleteMessage(this.botmessage, Notification.Type.INFO, "ChatBot", 4);
                this.msg = false;
            }
            else if (this.waited++ >= this.delay.getValue()) {
                MessageBus.sendServerMessage(this.botmessage);
                this.waited = 0;
                this.msg = false;
            }
        }
    }
}
