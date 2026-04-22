// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import java.util.function.BiConsumer;
import com.lemonclient.api.event.events.RenderEvent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.function.Predicate;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import java.util.concurrent.ConcurrentHashMap;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraftforge.event.world.WorldEvent;
import com.lemonclient.api.event.events.PlayerLeaveEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PlayerJoinEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timer;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;
import net.minecraft.entity.Entity;
import java.util.Map;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "LogoutSpots", category = Category.Render)
public class LogoutSpots extends Module
{
    IntegerSetting range;
    BooleanSetting disconnectMsg;
    BooleanSetting reconnectMsg;
    BooleanSetting nameTag;
    IntegerSetting lineWidth;
    ModeSetting renderMode;
    ColorSetting color;
    Map<Entity, String> loggedPlayers;
    Set<EntityPlayer> worldPlayers;
    Timer timer;
    Timer timer2;
    @EventHandler
    private final Listener<PlayerJoinEvent> playerJoinEventListener;
    @EventHandler
    private final Listener<PlayerLeaveEvent> playerLeaveEventListener;
    @EventHandler
    private final Listener<WorldEvent.Unload> unloadListener;
    @EventHandler
    private final Listener<WorldEvent.Load> loadListener;
    
    public LogoutSpots() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.disconnectMsg = this.registerBoolean("Disconnect Msgs", true);
        this.reconnectMsg = this.registerBoolean("Reconnect Msgs", true);
        this.nameTag = this.registerBoolean("NameTag", true);
        this.lineWidth = this.registerInteger("Width", 1, 1, 10);
        this.renderMode = this.registerMode("Render", Arrays.asList("Both", "Outline", "Fill", "None"), "Both");
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
        this.loggedPlayers = new ConcurrentHashMap<Entity, String>();
        this.worldPlayers = ConcurrentHashMap.newKeySet();
        this.timer = new Timer();
        this.timer2 = new Timer();
        this.playerJoinEventListener = new Listener<PlayerJoinEvent>(event -> {
            if (LogoutSpots.mc.world != null) {
                this.loggedPlayers.keySet().removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(event.getName())) {
                        if (this.reconnectMsg.getValue() && this.timer2.getTimePassed() / 50L >= 5L) {
                            MessageBus.sendClientPrefixMessage(event.getName() + " reconnected.", Notification.Type.INFO);
                            this.timer2.reset();
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
        }, new Predicate[0]);
        this.playerLeaveEventListener = new Listener<PlayerLeaveEvent>(event -> {
            if (LogoutSpots.mc.world != null) {
                this.worldPlayers.removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(event.getName())) {
                        final String date = new SimpleDateFormat("k:mm").format(new Date());
                        this.loggedPlayers.put(entity, date);
                        if (this.disconnectMsg.getValue() && this.timer.getTimePassed() / 50L >= 5L) {
                            final String location = "(" + (int)entity.posX + "," + (int)entity.posY + "," + (int)entity.posZ + ")";
                            MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + ".", Notification.Type.INFO);
                            this.timer.reset();
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
        }, new Predicate[0]);
        this.unloadListener = new Listener<WorldEvent.Unload>(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.player == null || LogoutSpots.mc.world == null) {
                this.loggedPlayers.clear();
            }
        }, new Predicate[0]);
        this.loadListener = new Listener<WorldEvent.Load>(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.player == null || LogoutSpots.mc.world == null) {
                this.loggedPlayers.clear();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        LogoutSpots.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != LogoutSpots.mc.player).filter(entityPlayer -> entityPlayer.getDistance(LogoutSpots.mc.player) <= this.range.getValue()).forEach(entityPlayer -> this.worldPlayers.add(entityPlayer));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (LogoutSpots.mc.player != null && LogoutSpots.mc.world != null) {
            this.loggedPlayers.forEach(this::startFunction);
        }
    }
    
    public void onEnable() {
        this.loggedPlayers.clear();
        this.worldPlayers = ConcurrentHashMap.newKeySet();
    }
    
    public void onDisable() {
        this.worldPlayers.clear();
    }
    
    private void startFunction(final Entity entity, final String string) {
        if (entity.getDistance(LogoutSpots.mc.player) > this.range.getValue()) {
            return;
        }
        final int posX = (int)entity.posX;
        final int posY = (int)entity.posY;
        final int posZ = (int)entity.posZ;
        final String[] nameTagMessage = { entity.getName() + " (" + string + ")", "(" + posX + "," + posY + "," + posZ + ")" };
        GlStateManager.pushMatrix();
        if (this.nameTag.getValue()) {
            RenderUtil.drawNametag(entity, nameTagMessage, this.color.getValue(), 0);
        }
        final String s = this.renderMode.getValue();
        switch (s) {
            case "Both": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), this.lineWidth.getValue(), this.color.getValue());
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), this.lineWidth.getValue(), this.color.getValue());
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
        }
        GlStateManager.popMatrix();
    }
}
