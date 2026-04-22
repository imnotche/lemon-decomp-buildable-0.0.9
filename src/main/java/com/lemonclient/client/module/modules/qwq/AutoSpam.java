// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.function.Predicate;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.regex.Pattern;
import net.minecraft.network.play.server.SPacketChat;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoSpam", category = Category.qwq)
public class AutoSpam extends Module
{
    StringSetting string;
    IntegerSetting delay;
    BooleanSetting hide;
    BooleanSetting randomThing;
    BooleanSetting letter;
    BooleanSetting number;
    IntegerSetting character;
    BooleanSetting antiSpam;
    String sent;
    int waited;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public AutoSpam() {
        this.string = this.registerString("Message", "/msg _yonkie_ gay");
        this.delay = this.registerInteger("Delay", 1, 0, 200);
        this.hide = this.registerBoolean("Hide", true);
        this.randomThing = this.registerBoolean("Random Thing", false);
        this.letter = this.registerBoolean("Letter", true, () -> this.randomThing.getValue());
        this.number = this.registerBoolean("Number", true, () -> this.randomThing.getValue());
        this.character = this.registerInteger("Character", 20, 0, 256, () -> this.randomThing.getValue());
        this.antiSpam = this.registerBoolean("AntiSpam", true);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AutoSpam.mc.world != null && AutoSpam.mc.player != null && this.hide.getValue()) {
                if (event.getPacket() instanceof SPacketChat) {
                    final String message = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    final Matcher matcher = Pattern.compile("<.*?> ").matcher(message);
                    String username = "";
                    if (matcher.find()) {
                        final String username2 = matcher.group();
                        username = username2.substring(1, username2.length() - 2);
                    }
                    else if (message.contains(":")) {
                        final int spaceIndex = message.indexOf(" ");
                        if (spaceIndex != -1) {
                            username = message.substring(0, spaceIndex);
                        }
                    }
                    final String username3 = ColorMain.cleanColor(username);
                    if ((message.toLowerCase().contains("to") && message.contains(":")) || username3.equals(AutoSpam.mc.player.getName())) {
                        event.cancel();
                        MessageBus.sendDeleteMessage("Spamming", "AutoSpam", 14);
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onTick() {
        if (AutoSpam.mc.world == null || AutoSpam.mc.player == null) {
            return;
        }
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        this.waited = 0;
        final StringBuilder phrase = new StringBuilder(this.string.getText());
        if (this.randomThing.getValue()) {
            final String characters = (this.letter.getValue() ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" : "") + (this.number.getValue() ? "0123456789" : "");
            final Random random = new Random();
            for (int i = 0; i < this.character.getValue(); ++i) {
                final int index = random.nextInt(characters.length());
                phrase.append(characters.charAt(index));
            }
        }
        if (this.antiSpam.getValue()) {
            final Random random2 = new Random();
            final int nextInt = random2.nextInt(16777216);
            final String hex = String.format("#%06x", nextInt);
            phrase.append(" [").append(hex).append("]");
        }
        MessageBus.sendServerMessage(this.sent = phrase.toString());
    }
}
