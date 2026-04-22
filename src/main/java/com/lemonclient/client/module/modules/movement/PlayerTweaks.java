// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.MovementInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import java.util.function.Predicate;
import com.lemonclient.api.event.events.WaterPushEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.EntityCollisionEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "PlayerTweaks", category = Category.Movement)
public class PlayerTweaks extends Module
{
    public BooleanSetting guiMove;
    BooleanSetting noPush;
    BooleanSetting noFall;
    public BooleanSetting noSlow;
    BooleanSetting antiKnockBack;
    @EventHandler
    private final Listener<InputUpdateEvent> eventListener;
    @EventHandler
    private final Listener<EntityCollisionEvent> entityCollisionEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<WaterPushEvent> waterPushEventListener;
    
    public PlayerTweaks() {
        this.guiMove = this.registerBoolean("Gui Move", false);
        this.noPush = this.registerBoolean("No Push", false);
        this.noFall = this.registerBoolean("No Fall", false);
        this.noSlow = this.registerBoolean("No Slow", false);
        this.antiKnockBack = this.registerBoolean("Velocity", false);
        this.eventListener = new Listener<InputUpdateEvent>(event -> {
            if (this.noSlow.getValue() && PlayerTweaks.mc.player.isHandActive() && !PlayerTweaks.mc.player.isRiding()) {
                event.getMovementInput();
                final MovementInput movementInput = new MovementInput();
                movementInput.moveStrafe *= 5.0f;
                event.getMovementInput();
                final MovementInput movementInput2 = new MovementInput();
                movementInput2.moveForward *= 5.0f;
            }
        }, new Predicate[0]);
        this.entityCollisionEventListener = new Listener<EntityCollisionEvent>(event -> {
            if (this.noPush.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.antiKnockBack.getValue()) {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == PlayerTweaks.mc.player.getEntityId()) {
                    event.cancel();
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    event.cancel();
                }
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.noFall.getValue() && event.getPacket() instanceof CPacketPlayer && PlayerTweaks.mc.player.fallDistance >= 3.0) {
                final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.onGround = true;
            }
        }, new Predicate[0]);
        this.waterPushEventListener = new Listener<WaterPushEvent>(event -> {
            if (this.noPush.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.guiMove.getValue() && PlayerTweaks.mc.currentScreen != null && !(PlayerTweaks.mc.currentScreen instanceof GuiChat)) {
            if (Keyboard.isKeyDown(200)) {
                final EntityPlayerSP player = PlayerTweaks.mc.player;
                player.rotationPitch -= 5.0f;
            }
            if (Keyboard.isKeyDown(208)) {
                final EntityPlayerSP player2 = PlayerTweaks.mc.player;
                player2.rotationPitch += 5.0f;
            }
            if (Keyboard.isKeyDown(205)) {
                final EntityPlayerSP player3 = PlayerTweaks.mc.player;
                player3.rotationYaw += 5.0f;
            }
            if (Keyboard.isKeyDown(203)) {
                final EntityPlayerSP player4 = PlayerTweaks.mc.player;
                player4.rotationYaw -= 5.0f;
            }
            if (PlayerTweaks.mc.player.rotationPitch > 90.0f) {
                PlayerTweaks.mc.player.rotationPitch = 90.0f;
            }
            if (PlayerTweaks.mc.player.rotationPitch < -90.0f) {
                PlayerTweaks.mc.player.rotationPitch = -90.0f;
            }
        }
    }
}
