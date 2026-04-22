// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import com.lemonclient.client.command.CommandManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.function.Predicate;
import net.minecraft.util.text.TextComponentString;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.misc.ColorUtil;
import java.util.Arrays;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Pair;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ChatModifier", category = Category.Misc)
public class ChatModifier extends Module
{
    public BooleanSetting clearBkg;
    public Pair<Object, Object> watermarkSpecial;
    BooleanSetting greenText;
    BooleanSetting chatTimeStamps;
    ModeSetting format;
    ModeSetting decoration;
    ModeSetting color;
    BooleanSetting space;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> chatReceivedEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public ChatModifier() {
        this.clearBkg = this.registerBoolean("Clear Chat", false);
        this.greenText = this.registerBoolean("Green Text", false);
        this.chatTimeStamps = this.registerBoolean("Chat Time Stamps", false);
        this.format = this.registerMode("Format", Arrays.asList("H24:mm", "H12:mm", "H12:mm a", "H24:mm:ss", "H12:mm:ss", "H12:mm:ss a"), "H24:mm");
        this.decoration = this.registerMode("Deco", Arrays.asList("< >", "[ ]", "{ }", " "), "[ ]");
        this.color = this.registerMode("Color", ColorUtil.colors, ChatFormatting.GRAY.getName());
        this.space = this.registerBoolean("Space", false);
        this.chatReceivedEventListener = new Listener<ClientChatReceivedEvent>(event -> {
            if (this.chatTimeStamps.getValue()) {
                final String decoLeft = this.decoration.getValue().equalsIgnoreCase(" ") ? "" : this.decoration.getValue().split(" ")[0];
                String decoRight = this.decoration.getValue().equalsIgnoreCase(" ") ? "" : this.decoration.getValue().split(" ")[1];
                if (this.space.getValue()) {
                    decoRight += " ";
                }
                final String dateFormat = this.format.getValue().replace("H24", "k").replace("H12", "h");
                final String date = new SimpleDateFormat(dateFormat).format(new Date());
                new TextComponentString(ChatFormatting.getByName(this.color.getValue()) + decoLeft + date + decoRight + ChatFormatting.RESET);
                final TextComponentString textComponentString = null;
                final TextComponentString time = textComponentString;
                event.setMessage(time.appendSibling(event.getMessage()));
            }
        }, new Predicate[0]);
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (this.greenText.getValue() && event.getPacket() instanceof CPacketChatMessage) {
                if (!((CPacketChatMessage)event.getPacket()).getMessage().startsWith("/") && !((CPacketChatMessage)event.getPacket()).getMessage().startsWith(CommandManager.getCommandPrefix())) {
                    final String message = ((CPacketChatMessage)event.getPacket()).getMessage();
                    final String prefix = "";
                    final String prefix2 = ">";
                    final String s = prefix2 + message;
                    if (s.length() <= 255) {
                        ((CPacketChatMessage)event.getPacket()).message = s;
                    }
                }
            }
        }, new Predicate[0]);
    }
}
