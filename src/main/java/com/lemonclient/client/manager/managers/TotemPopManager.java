package com.lemonclient.client.manager.managers;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.TotemPopEvent;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.Manager;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;

public enum TotemPopManager implements Manager
{
    INSTANCE;

    public Minecraft mc = Minecraft.getMinecraft();
    public boolean sendMsgs = false;
    public ChatFormatting chatFormatting = ChatFormatting.WHITE;
    public ChatFormatting nameFormatting = ChatFormatting.WHITE;
    public ChatFormatting friFormatting = ChatFormatting.WHITE;
    public ChatFormatting numberFormatting = ChatFormatting.WHITE;
    public boolean friend;
    public String self;
    public String type4;
    private final HashMap<String, Integer> playerPopCount = new HashMap();
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener = new Listener<PacketEvent.Receive>(event -> {
        if (this.getPlayer() == null || this.getWorld() == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            Entity entity = packet.getEntity((World)this.getWorld());
            if (packet.getOpCode() == 35) {
                LemonClient.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<TotemPopEvent>(event -> {
        if (this.getPlayer() == null || this.getWorld() == null) {
            return;
        }
        if (event.getEntity() == null) {
            return;
        }
        String name = event.getEntity().getName();
        if (this.mc.player.connection.getPlayerInfo(name) == null) {
            return;
        }
        if (this.playerPopCount.get(name) == null) {
            this.playerPopCount.put(name, 1);
        } else {
            this.playerPopCount.put(name, this.playerPopCount.get(name) + 1);
        }
        if (this.sendMsgs) {
            if (this.mc.player.getName().equals(name)) {
                switch (this.self) {
                    case "Disable": {
                        return;
                    }
                    case "I": {
                        name = "I";
                    }
                }
            }
            int pop = this.playerPopCount.get(name);
            if (name.equals("I") || SocialManager.isFriend(name) && !this.type4.equals("Enemy")) {
                if (this.friend) {
                    name = "My Friend " + name;
                }
                MessageBus.sendClientDeleteMessage(this.friFormatting + name + this.chatFormatting + " popped " + this.numberFormatting + pop + this.chatFormatting + " totem" + (pop > 1 ? "s" : "") + ".", Notification.Type.INFO, "TotemPopCounter" + name, 1000);
            }
            if (!(name.equals("I") || SocialManager.isFriend(name) || this.type4.equals("Friend"))) {
                MessageBus.sendClientDeleteMessage(this.nameFormatting + name + this.chatFormatting + " popped " + this.numberFormatting + pop + this.chatFormatting + " totem" + (pop > 1 ? "s" : "") + ".", Notification.Type.INFO, "TotemPopCounter" + name, 1000);
            }
        }
    }, new Predicate[0]);

    public void death(EntityPlayer entityPlayer) {
        String name = entityPlayer.getName();
        if (this.mc.player.connection.getPlayerInfo(name) == null) {
            return;
        }
        if (!this.playerPopCount.containsKey(name)) {
            return;
        }
        int pop = this.getPlayerPopCount(name);
        this.playerPopCount.remove(entityPlayer.getName());
        if (this.sendMsgs) {
            if (this.mc.player.getName().equals(name)) {
                switch (this.self) {
                    case "Disable": {
                        return;
                    }
                    case "I": {
                        name = "I";
                    }
                }
            }
            if (name.equals("I") || SocialManager.isFriend(name) && !this.type4.equals("Enemy")) {
                if (this.friend) {
                    name = "My Friend " + name;
                }
                MessageBus.sendClientPrefixMessage(this.friFormatting + name + this.chatFormatting + " died after popping " + this.numberFormatting + pop + this.chatFormatting + " totem" + (pop > 1 ? "s." : "."), Notification.Type.INFO);
            }
            if (!(name.equals("I") || SocialManager.isFriend(name) || this.type4.equals("Friend"))) {
                MessageBus.sendClientPrefixMessage(this.nameFormatting + name + this.chatFormatting + " died after popping " + this.numberFormatting + pop + this.chatFormatting + " totem" + (pop > 1 ? "s." : "."), Notification.Type.INFO);
            }
        }
    }

    public int getPlayerPopCount(String name) {
        if (this.playerPopCount.containsKey(name)) {
            return this.playerPopCount.get(name);
        }
        return 0;
    }
}
