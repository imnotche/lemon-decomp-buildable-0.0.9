// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.manager.managers;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.client.Minecraft;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.RotateFixer;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import com.lemonclient.api.util.misc.CollectionUtil;
import com.lemonclient.api.event.Phase;
import java.util.ArrayList;
import com.lemonclient.api.event.events.RenderEntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.player.PlayerPacket;
import java.util.List;
import com.lemonclient.client.manager.Manager;

public enum PlayerPacketManager implements Manager
{
    INSTANCE;
    
    private final List<PlayerPacket> packets;
    private Vec3d prevServerSidePosition;
    private Vec3d serverSidePosition;
    private Vec2f prevServerSideRotation;
    private Vec2f serverSideRotation;
    private Vec2f clientSidePitch;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> tickEventListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityEventHeadListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityEventReturnListener;
    
    PlayerPacketManager() {
        this.packets = new ArrayList<PlayerPacket>();
        this.prevServerSidePosition = Vec3d.ZERO;
        this.serverSidePosition = Vec3d.ZERO;
        this.prevServerSideRotation = Vec2f.ZERO;
        this.serverSideRotation = Vec2f.ZERO;
        this.clientSidePitch = Vec2f.ZERO;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.BY || this.packets.isEmpty()) {
            }
            else {
                final PlayerPacket packet = CollectionUtil.maxOrNull(this.packets, PlayerPacket::getPriority);
                if (packet != null) {
                    event.cancel();
                    event.apply(packet);
                }
                this.packets.clear();
            }
        }, new Predicate[0]);
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (event.isCancelled()) {
            }
            else {
                final Packet rawPacket = event.getPacket();
                final EntityPlayerSP player = this.getPlayer();
                if (player != null && rawPacket instanceof CPacketPlayer) {
                    final CPacketPlayer packet2 = (CPacketPlayer)rawPacket;
                    if (packet2.moving) {
                        this.serverSidePosition = new Vec3d(packet2.x, packet2.y, packet2.z);
                    }
                    if (packet2.rotating) {
                        this.serverSideRotation = new Vec2f(packet2.yaw, packet2.pitch);
                        player.rotationYawHead = packet2.yaw;
                    }
                }
            }
        }, -200, new Predicate[0]);
        this.tickEventListener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase != TickEvent.Phase.START) {
            }
            else {
                this.prevServerSidePosition = this.serverSidePosition;
                this.prevServerSideRotation = this.serverSideRotation;
            }
        }, new Predicate[0]);
        this.renderEntityEventHeadListener = new Listener<RenderEntityEvent.Head>(event -> {
            if (!ModuleManager.getModule(RotateFixer.class).isEnabled()) {
            }
            else {
                final EntityPlayerSP player2 = this.getPlayer();
                if (player2 == null || player2.isRiding() || event.getType() != RenderEntityEvent.Type.TEXTURE || event.getEntity() != player2 || Minecraft.getMinecraft().currentScreen != null) {
                }
                else {
                    this.clientSidePitch = new Vec2f(player2.prevRotationPitch, player2.rotationPitch);
                    player2.prevRotationPitch = this.prevServerSideRotation.y;
                    player2.rotationPitch = this.serverSideRotation.y;
                }
            }
        }, new Predicate[0]);
        this.renderEntityEventReturnListener = new Listener<RenderEntityEvent.Return>(event -> {
            if (ModuleManager.getModule(RotateFixer.class).isEnabled()) {
                final EntityPlayerSP player3 = this.getPlayer();
                if (player3 != null && !player3.isRiding() && event.getType() == RenderEntityEvent.Type.TEXTURE && event.getEntity() == player3 && Minecraft.getMinecraft().currentScreen == null) {
                    player3.prevRotationPitch = this.clientSidePitch.x;
                    player3.rotationPitch = this.clientSidePitch.y;
                }
            }
        }, new Predicate[0]);
    }
    
    public void addPacket(final PlayerPacket packet) {
        this.packets.add(packet);
    }
    
    public Vec3d getPrevServerSidePosition() {
        return this.prevServerSidePosition;
    }
    
    public Vec3d getServerSidePosition() {
        return this.serverSidePosition;
    }
    
    public Vec2f getPrevServerSideRotation() {
        return this.prevServerSideRotation;
    }
    
    public Vec2f getServerSideRotation() {
        return this.serverSideRotation;
    }
}
