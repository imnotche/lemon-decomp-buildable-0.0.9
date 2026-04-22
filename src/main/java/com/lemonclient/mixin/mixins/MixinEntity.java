package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.EntityCollisionEvent;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.client.LemonClient;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class MixinEntity {
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
    public abstract boolean equals(Object var1);

    @Inject(method={"applyEntityCollision"}, at={@At(value="HEAD")}, cancellable=true)
    public void velocity(Entity entityIn, CallbackInfo ci) {
        EntityCollisionEvent event = new EntityCollisionEvent();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="net/minecraft/entity/Entity.resetPositionToBB()V", ordinal=1)})
    private void resetPositionToBBHook(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this) && this.prevHeight != null) {
            this.stepHeight = this.prevHeight.floatValue();
            this.prevHeight = null;
        }
    }

    @Inject(method={"move"}, at={@At(value="HEAD")})
    public void move(MoverType type, double tx, double ty, double tz, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
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
            boolean flag;
            if (type.equals((Object)MoverType.PISTON)) {
                return;
            }
            mc.world.profiler.startSection("move");
            if (mc.player.isInWeb) {
                return;
            }
            double d2 = x;
            double d3 = y;
            double d4 = z;
            if ((type == MoverType.SELF || type == MoverType.PLAYER) && mc.player.onGround && mc.player.isSneaking()) {
                double d5 = 0.05;
                while (x != 0.0 && mc.world.getCollisionBoxes((Entity)mc.player, bb.offset(x, (double)(-mc.player.stepHeight), 0.0)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                }
                while (z != 0.0 && mc.world.getCollisionBoxes((Entity)mc.player, bb.offset(0.0, (double)(-mc.player.stepHeight), z)).isEmpty()) {
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
                while (x != 0.0 && z != 0.0 && mc.world.getCollisionBoxes((Entity)mc.player, bb.offset(x, (double)(-mc.player.stepHeight), z)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
            }
            List list1 = mc.world.getCollisionBoxes((Entity)mc.player, bb.expand(x, y, z));
            if (y != 0.0) {
                int l = list1.size();
                for (int k = 0; k < l; ++k) {
                    y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(bb, y);
                }
                bb = bb.offset(0.0, y, 0.0);
            }
            if (x != 0.0) {
                int l5 = list1.size();
                for (int j5 = 0; j5 < l5; ++j5) {
                    x = ((AxisAlignedBB)list1.get(j5)).calculateXOffset(bb, x);
                }
                if (x != 0.0) {
                    bb = bb.offset(x, 0.0, 0.0);
                }
            }
            if (z != 0.0) {
                int i6 = list1.size();
                for (int k5 = 0; k5 < i6; ++k5) {
                    z = ((AxisAlignedBB)list1.get(k5)).calculateZOffset(bb, z);
                }
                if (z != 0.0) {
                    bb = bb.offset(0.0, 0.0, z);
                }
            }
            boolean bl = flag = mc.player.onGround || d3 != y && d3 < 0.0;
            if (mc.player.stepHeight > 0.0f && flag && (d2 != x || d4 != z)) {
                double d14 = x;
                double d6 = y;
                double d7 = z;
                y = mc.player.stepHeight;
                List list = mc.world.getCollisionBoxes((Entity)mc.player, bb.expand(d2, y, d4));
                AxisAlignedBB axisalignedbb2 = bb;
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.expand(d2, 0.0, d4);
                double d8 = y;
                int k1 = list.size();
                for (int j1 = 0; j1 < k1; ++j1) {
                    d8 = ((AxisAlignedBB)list.get(j1)).calculateYOffset(axisalignedbb3, d8);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, d8, 0.0);
                double d18 = d2;
                int i2 = list.size();
                for (int l1 = 0; l1 < i2; ++l1) {
                    d18 = ((AxisAlignedBB)list.get(l1)).calculateXOffset(axisalignedbb2, d18);
                }
                axisalignedbb2 = axisalignedbb2.offset(d18, 0.0, 0.0);
                double d19 = d4;
                int k2 = list.size();
                for (int j2 = 0; j2 < k2; ++j2) {
                    d19 = ((AxisAlignedBB)list.get(j2)).calculateZOffset(axisalignedbb2, d19);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, 0.0, d19);
                AxisAlignedBB axisalignedbb4 = bb;
                double d20 = y;
                int i3 = list.size();
                for (int l2 = 0; l2 < i3; ++l2) {
                    d20 = ((AxisAlignedBB)list.get(l2)).calculateYOffset(axisalignedbb4, d20);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, d20, 0.0);
                double d21 = d2;
                int k3 = list.size();
                for (int j3 = 0; j3 < k3; ++j3) {
                    d21 = ((AxisAlignedBB)list.get(j3)).calculateXOffset(axisalignedbb4, d21);
                }
                axisalignedbb4 = axisalignedbb4.offset(d21, 0.0, 0.0);
                double d22 = d4;
                int i4 = list.size();
                for (int l3 = 0; l3 < i4; ++l3) {
                    d22 = ((AxisAlignedBB)list.get(l3)).calculateZOffset(axisalignedbb4, d22);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, 0.0, d22);
                double d23 = d18 * d18 + d19 * d19;
                double d9 = d21 * d21 + d22 * d22;
                if (d23 > d9) {
                    x = d18;
                    z = d19;
                    y = -d8;
                    bb = axisalignedbb2;
                } else {
                    x = d21;
                    z = d22;
                    y = -d20;
                    bb = axisalignedbb4;
                }
                int k4 = list.size();
                for (int j4 = 0; j4 < k4; ++j4) {
                    y = ((AxisAlignedBB)list.get(j4)).calculateYOffset(bb, y);
                }
                bb = bb.offset(0.0, y, 0.0);
                if (!(d14 * d14 + d7 * d7 >= x * x + z * z)) {
                    StepEvent event = new StepEvent(bb);
                    LemonClient.EVENT_BUS.post(event);
                    if (event.isCancelled()) {
                        mc.player.stepHeight = 0.5f;
                    }
                }
            }
        }
    }
}
