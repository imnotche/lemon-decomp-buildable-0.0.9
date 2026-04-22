// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.modules.exploits.Portal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import net.minecraft.entity.MoverType;
import com.lemonclient.api.event.events.EventPlayerIsHandActive;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.dev.AntiPush;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.lemonclient.api.event.events.MotionUpdateEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.LemonClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.lemonclient.api.event.events.MotionUpdateEvents;
import net.minecraft.util.MovementInput;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.entity.AbstractClientPlayer;

@Mixin({ EntityPlayerSP.class })
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    protected Minecraft mc;
    @Shadow
    private boolean prevOnGround;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private boolean autoJumpEnabled;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    public MovementInput movementInput;
    private MotionUpdateEvents motionEvent;
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") }, cancellable = true)
    private void onUpdateWalkingPlayer_Head(final CallbackInfo callbackInfo) {
        this.motionEvent = new MotionUpdateEvents(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        LemonClient.EVENT_BUS.post(this.motionEvent);
        if (this.motionEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    public MixinEntityPlayerSP() {
        super(Minecraft.getMinecraft().world, Minecraft.getMinecraft().session.getProfile());
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") }, cancellable = true)
    public void OnPreUpdateWalkingPlayer(final CallbackInfo p_Info) {
        final MotionUpdateEvent l_Event = new MotionUpdateEvent(0);
        LemonClient.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) {
            p_Info.cancel();
        }
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("RETURN") }, cancellable = true)
    public void OnPostUpdateWalkingPlayer(final CallbackInfo p_Info) {
        final MotionUpdateEvent l_Event = new MotionUpdateEvent(1);
        LemonClient.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) {
            p_Info.cancel();
        }
    }
    
    @Shadow
    protected abstract boolean isCurrentViewEntity();
    
    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    public void pushOutOfBlocks(final double x, final double y, final double z, final CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isModuleEnabled(AntiPush.class)) {
            cir.cancel();
        }
    }
    
    @Inject(method = { "isHandActive" }, at = { @At("HEAD") }, cancellable = true)
    public void isHandActive(final CallbackInfoReturnable<Boolean> info) {
        final EventPlayerIsHandActive event = new EventPlayerIsHandActive();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
            info.setReturnValue(false);
        }
    }
    
    @Inject(method = { "Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V" }, at = { @At("HEAD") })
    public void getBrand(final String serverBrand, final CallbackInfo callbackInfo) {
        if (LemonClient.serverUtil != null) {
            LemonClient.serverUtil.setServerBrand(serverBrand);
        }
    }
    
    @Redirect(method = { "move" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(final AbstractClientPlayer player, final MoverType type, final double x, final double y, final double z) {
        final PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        if (type != MoverType.PLAYER && type != MoverType.SELF && ModuleManager.isModuleEnabled(AntiPush.class)) {
            moveEvent.cancel();
        }
        else {
            LemonClient.EVENT_BUS.post(moveEvent);
            super.move(type, moveEvent.getX(), moveEvent.getY(), moveEvent.getZ());
        }
    }
    
    @Inject(method = { "onUpdate" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V", shift = At.Shift.AFTER) })
    private void onUpdateInvokeOnUpdateWalkingPlayer(final CallbackInfo ci) {
        final Vec3d serverSidePos = PlayerPacketManager.INSTANCE.getServerSidePosition();
        final float serverSideRotationX = PlayerPacketManager.INSTANCE.getServerSideRotation().x;
        final float serverSideRotationY = PlayerPacketManager.INSTANCE.getServerSideRotation().y;
        this.lastReportedPosX = serverSidePos.x;
        this.lastReportedPosY = serverSidePos.y;
        this.lastReportedPosZ = serverSidePos.z;
        this.lastReportedYaw = serverSideRotationX;
        this.lastReportedPitch = serverSideRotationY;
        this.rotationYawHead = serverSideRotationX;
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") }, cancellable = true)
    public void onUpdateWalkingPlayerPre(final CallbackInfo callbackInfo) {
        Vec3d position = new Vec3d(this.posX, this.getEntityBoundingBox().minY, this.posZ);
        Vec2f rotation = new Vec2f(this.rotationYaw, this.rotationPitch);
        OnUpdateWalkingPlayerEvent event = new OnUpdateWalkingPlayerEvent(position, rotation);
        LemonClient.EVENT_BUS.post(event);
        event = event.nextPhase();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
            final boolean moving = event.isMoving() || this.isMoving(position);
            final boolean rotating = event.isRotating() || this.isRotating(rotation);
            position = event.getPosition();
            rotation = event.getRotation();
            ++this.positionUpdateTicks;
            this.sendSprintPacket();
            this.sendSneakPacket();
            this.sendPlayerPacket(moving, rotating, position, rotation);
        }
        event = event.nextPhase();
        LemonClient.EVENT_BUS.post(event);
    }
    
    private void sendSprintPacket() {
        final boolean sprinting = this.isSprinting();
        if (sprinting != this.serverSprintState) {
            if (sprinting) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            }
            else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = sprinting;
        }
    }
    
    private void sendSneakPacket() {
        final boolean sneaking = this.isSneaking();
        if (sneaking != this.serverSneakState) {
            if (sneaking) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            }
            else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = sneaking;
        }
    }
    
    public void sendPlayerPacket(boolean moving, final boolean rotating, final Vec3d position, final Vec2f rotation) {
        if (!this.isCurrentViewEntity()) {
            return;
        }
        if (this.isRiding()) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0, this.motionZ, rotation.x, rotation.y, this.onGround));
            moving = false;
        }
        else if (moving && rotating) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(position.x, position.y, position.z, rotation.x, rotation.y, this.onGround));
        }
        else if (moving) {
            this.connection.sendPacket(new CPacketPlayer.Position(position.x, position.y, position.z, this.onGround));
        }
        else if (rotating) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(rotation.x, rotation.y, this.onGround));
        }
        else if (this.prevOnGround != this.onGround) {
            this.connection.sendPacket(new CPacketPlayer(this.onGround));
        }
        if (moving) {
            this.lastReportedPosX = position.x;
            this.lastReportedPosY = position.y;
            this.lastReportedPosZ = position.z;
            this.positionUpdateTicks = 0;
        }
        if (rotating) {
            this.lastReportedYaw = rotation.x;
            this.lastReportedPitch = rotation.y;
        }
        this.prevOnGround = this.onGround;
        this.autoJumpEnabled = this.mc.gameSettings.autoJump;
    }
    
    private boolean isMoving(final Vec3d position) {
        final double xDiff = position.x - this.lastReportedPosX;
        final double yDiff = position.y - this.lastReportedPosY;
        final double zDiff = position.z - this.lastReportedPosZ;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4 || this.positionUpdateTicks >= 20;
    }
    
    private boolean isRotating(final Vec2f rotation) {
        final double yawDiff = rotation.x - this.lastReportedYaw;
        final double pitchDiff = rotation.y - this.lastReportedPitch;
        return yawDiff != 0.0 || pitchDiff != 0.0;
    }
    
    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreenHook(final EntityPlayerSP entityPlayerSP) {
        final Portal portal = ModuleManager.getModule(Portal.class);
        if (!portal.isEnabled() || !portal.chat.getValue()) {
            entityPlayerSP.closeScreen();
        }
    }
}
