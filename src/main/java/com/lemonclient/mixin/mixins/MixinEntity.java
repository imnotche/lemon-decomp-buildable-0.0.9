// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import java.util.List;
import com.lemonclient.api.event.events.StepEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.EntityCollisionEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Entity.class })
public abstract class MixinEntity
{
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public World world;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean isDead;
    @Shadow
    public float width;
    @Shadow
    public float height;
    private Float prevHeight;
    
    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    
    @Shadow
    public abstract boolean isSneaking();
    
    @Shadow
    @Override
    public abstract boolean equals(final Object p0);
    
    @Inject(method = { "applyEntityCollision" }, at = { @At("HEAD") }, cancellable = true)
    public void velocity(final Entity entityIn, final CallbackInfo ci) {
        final EntityCollisionEvent event = new EntityCollisionEvent();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "move" }, at = { @At(value = "INVOKE", target = "net/minecraft/entity/Entity.resetPositionToBB()V", ordinal = 1) })
    private void resetPositionToBBHook(final MoverType type, final double x, final double y, final double z, final CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this) && this.prevHeight != null) {
            this.stepHeight = this.prevHeight;
            this.prevHeight = null;
        }
    }
    
    @Inject(method = { "move" }, at = { @At("HEAD") })
    public void move(final MoverType type, final double tx, final double ty, final double tz, final CallbackInfo ci) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.getCurrentServerData() == null) {
            return;
        }
        double x = tx;
        double y = ty;
        double z = tz;
        if (ci.isCancelled()) {
            return;
        }
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        if (!mc.player.noClip) {
            if (type.equals(MoverType.PISTON)) {
                return;
            }
            mc.world.profiler.startSection("move");
            if (mc.player.isInWeb) {
                return;
            }
            double d2 = x;
            final double d3 = y;
            double d4 = z;
            if ((type == MoverType.SELF || type == MoverType.PLAYER) && mc.player.onGround && mc.player.isSneaking()) {
                final double d5 = 0.05;
                while (x != 0.0 && mc.world.getCollisionBoxes(mc.player, bb.offset(x, -mc.player.stepHeight, 0.0)).isEmpty()) {
                    if (x < 0.05 && x >= -0.05) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= 0.05;
                    }
                    else {
                        x += 0.05;
                    }
                    d2 = x;
                }
                while (z != 0.0 && mc.world.getCollisionBoxes(mc.player, bb.offset(0.0, -mc.player.stepHeight, z)).isEmpty()) {
                    if (z < 0.05 && z >= -0.05) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= 0.05;
                    }
                    else {
                        z += 0.05;
                    }
                    d4 = z;
                }
                while (x != 0.0 && z != 0.0 && mc.world.getCollisionBoxes(mc.player, bb.offset(x, -mc.player.stepHeight, z)).isEmpty()) {
                    if (x < 0.05 && x >= -0.05) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= 0.05;
                    }
                    else {
                        x += 0.05;
                    }
                    d2 = x;
                    if (z < 0.05 && z >= -0.05) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= 0.05;
                    }
                    else {
                        z += 0.05;
                    }
                    d4 = z;
                }
            }
            final List<AxisAlignedBB> list1 = mc.world.getCollisionBoxes(mc.player, bb.expand(x, y, z));
            if (y != 0.0) {
                for (int k = 0, l = list1.size(); k < l; ++k) {
                    y = list1.get(k).calculateYOffset(bb, y);
                }
                bb = bb.offset(0.0, y, 0.0);
            }
            if (x != 0.0) {
                for (int j5 = 0, l2 = list1.size(); j5 < l2; ++j5) {
                    x = list1.get(j5).calculateXOffset(bb, x);
                }
                if (x != 0.0) {
                    bb = bb.offset(x, 0.0, 0.0);
                }
            }
            if (z != 0.0) {
                for (int k2 = 0, i6 = list1.size(); k2 < i6; ++k2) {
                    z = list1.get(k2).calculateZOffset(bb, z);
                }
                if (z != 0.0) {
                    bb = bb.offset(0.0, 0.0, z);
                }
            }
            final boolean flag = mc.player.onGround || (d3 != y && d3 < 0.0);
            if (mc.player.stepHeight > 0.0f && flag && (d2 != x || d4 != z)) {
                final double d6 = x;
                final double d7 = y;
                final double d8 = z;
                y = mc.player.stepHeight;
                final List<AxisAlignedBB> list2 = mc.world.getCollisionBoxes(mc.player, bb.expand(d2, y, d4));
                AxisAlignedBB axisalignedbb2 = bb;
                final AxisAlignedBB axisalignedbb3 = axisalignedbb2.expand(d2, 0.0, d4);
                double d9 = y;
                for (int j6 = 0, k3 = list2.size(); j6 < k3; ++j6) {
                    d9 = list2.get(j6).calculateYOffset(axisalignedbb3, d9);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, d9, 0.0);
                double d10 = d2;
                for (int l3 = 0, i7 = list2.size(); l3 < i7; ++l3) {
                    d10 = list2.get(l3).calculateXOffset(axisalignedbb2, d10);
                }
                axisalignedbb2 = axisalignedbb2.offset(d10, 0.0, 0.0);
                double d11 = d4;
                for (int j7 = 0, k4 = list2.size(); j7 < k4; ++j7) {
                    d11 = list2.get(j7).calculateZOffset(axisalignedbb2, d11);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, 0.0, d11);
                AxisAlignedBB axisalignedbb4 = bb;
                double d12 = y;
                for (int l4 = 0, i8 = list2.size(); l4 < i8; ++l4) {
                    d12 = list2.get(l4).calculateYOffset(axisalignedbb4, d12);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, d12, 0.0);
                double d13 = d2;
                for (int j8 = 0, k5 = list2.size(); j8 < k5; ++j8) {
                    d13 = list2.get(j8).calculateXOffset(axisalignedbb4, d13);
                }
                axisalignedbb4 = axisalignedbb4.offset(d13, 0.0, 0.0);
                double d14 = d4;
                for (int l5 = 0, i9 = list2.size(); l5 < i9; ++l5) {
                    d14 = list2.get(l5).calculateZOffset(axisalignedbb4, d14);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, 0.0, d14);
                final double d15 = d10 * d10 + d11 * d11;
                final double d16 = d13 * d13 + d14 * d14;
                if (d15 > d16) {
                    x = d10;
                    z = d11;
                    y = -d9;
                    bb = axisalignedbb2;
                }
                else {
                    x = d13;
                    z = d14;
                    y = -d12;
                    bb = axisalignedbb4;
                }
                for (int j9 = 0, k6 = list2.size(); j9 < k6; ++j9) {
                    y = list2.get(j9).calculateYOffset(bb, y);
                }
                bb = bb.offset(0.0, y, 0.0);
                if (d6 * d6 + d8 * d8 < x * x + z * z) {
                    final StepEvent event = new StepEvent(bb);
                    LemonClient.EVENT_BUS.post(event);
                    if (event.isCancelled()) {
                        mc.player.stepHeight = 0.5f;
                    }
                }
            }
        }
    }
}
