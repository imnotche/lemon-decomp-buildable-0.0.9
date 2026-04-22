// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.function.Predicate;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.regex.Pattern;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.player.social.SocialManager;
import net.minecraft.network.play.server.SPacketChat;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.HashMap;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoIgnore", category = Category.qwq)
public class AutoIgnore extends Module
{
    BooleanSetting filterFriend;
    BooleanSetting ignoreAll;
    BooleanSetting playerCheck;
    IntegerSetting times;
    IntegerSetting life;
    HashMap<String, Integer> messageTimes;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public AutoIgnore() {
        this.filterFriend = this.registerBoolean("Filter Friend", false);
        this.ignoreAll = this.registerBoolean("AllWhisper", false);
        this.playerCheck = this.registerBoolean("PlayerCheck", true);
        this.times = this.registerInteger("Times", 10, 0, 30);
        this.life = this.registerInteger("LifeTime", 600, 0, 3000);
        this.messageTimes = new HashMap<String, Integer>();
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AutoIgnore.mc.player != null) {
                if (event.getPacket() instanceof SPacketChat) {
                    final String message = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    if (this.ignoreAll.getValue() && message.contains(":")) {
                        String username = "";
                        final int spaceIndex = message.indexOf(" ");
                        if (spaceIndex != -1) {
                            username = message.substring(0, spaceIndex);
                        }
                        if ((!username.isEmpty() && !SocialManager.isOnIgnoreList(username) && !SocialManager.isOnFriendList(username)) || !this.filterFriend.getValue()) {
                            SocialManager.addIgnore(username);
                            MessageBus.sendClientDeleteMessage(username + " has been added to ignore list", Notification.Type.INFO, "AutoIgnore", 13);
                        }
                    }
                    final String s = message.replaceAll("\\[.*?]|<.*?>|\\d+", "");
                    this.addToList(s);
                    if (this.messageTimes.get(s) > this.times.getValue()) {
                        final Matcher matcher = Pattern.compile("<.*?> ").matcher(message);
                        String username2 = "";
                        if (matcher.find()) {
                            final String username3 = matcher.group();
                            username2 = username3.substring(1, username3.length() - 2);
                        }
                        else if (message.contains(":")) {
                            final int spaceIndex2 = message.indexOf(" ");
                            if (spaceIndex2 != -1) {
                                username2 = message.substring(0, spaceIndex2);
                            }
                        }
                        final String username4 = ColorMain.cleanColor(username2);
                        if (!username4.equals(AutoIgnore.mc.player.getName()) && (!this.playerCheck.getValue() || AutoIgnore.mc.player.connection.getPlayerInfo(username4) != null)) {
                            if ((!username4.isEmpty() && !SocialManager.isOnIgnoreList(username4) && !SocialManager.isOnFriendList(username4)) || !this.filterFriend.getValue()) {
                                SocialManager.addIgnore(username4);
                                MessageBus.sendClientDeleteMessage(username4 + " has been added to ignore list", Notification.Type.INFO, "AutoIgnore", 13);
                            }
                            event.cancel();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void addToList(final String string) {
        int time = 1;
        if (this.messageTimes.containsKey(string)) {
            time += this.messageTimes.get(string);
        }
        this.messageTimes.put(string, time);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AutoIgnore.this.messageTimes.put(string, AutoIgnore.this.messageTimes.get(string) - 1);
            }
        }, this.life.getValue() * 1000);
    }
}
