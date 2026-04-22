// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.util.EnumFacing;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockWeb;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.init.Blocks;
import java.util.Iterator;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.client.LemonClient;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.player.PredictUtil;
import java.util.Arrays;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoWeb", category = Category.Combat)
public class AutoWeb extends Module
{
    ModeSetting page;
    BooleanSetting packetSwitch;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    IntegerSetting delay;
    IntegerSetting multiPlace;
    BooleanSetting strict;
    BooleanSetting raytrace;
    BooleanSetting noInWeb;
    BooleanSetting checkSelf;
    BooleanSetting onlyGround;
    BooleanSetting down;
    BooleanSetting face;
    BooleanSetting feet;
    BooleanSetting onlyAir;
    BooleanSetting air;
    DoubleSetting minTargetSpeed;
    DoubleSetting range;
    IntegerSetting tickPredict;
    BooleanSetting calculateYPredict;
    IntegerSetting startDecrease;
    IntegerSetting exponentStartDecrease;
    IntegerSetting decreaseY;
    IntegerSetting exponentDecreaseY;
    BooleanSetting splitXZ;
    BooleanSetting manualOutHole;
    BooleanSetting aboveHoleManual;
    BooleanSetting stairPredict;
    IntegerSetting nStair;
    DoubleSetting speedActivationStair;
    private final Timing timer;
    private int progress;
    
    public AutoWeb() {
        this.page = this.registerMode("Page", Arrays.asList("Settings", "Predict"), "Settings");
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("Settings"));
        this.rotate = this.registerBoolean("Rotate", true, () -> this.page.getValue().equals("Settings"));
        this.packet = this.registerBoolean("Packet", true, () -> this.page.getValue().equals("Settings"));
        this.swing = this.registerBoolean("Swing", true, () -> this.page.getValue().equals("Settings"));
        this.delay = this.registerInteger("Delay", 50, 0, 2000, () -> this.page.getValue().equals("Settings"));
        this.multiPlace = this.registerInteger("MultiPlace", 1, 1, 8, () -> this.page.getValue().equals("Settings"));
        this.strict = this.registerBoolean("Strict", true, () -> this.page.getValue().equals("Settings"));
        this.raytrace = this.registerBoolean("Raytrace", false, () -> this.page.getValue().equals("Settings"));
        this.noInWeb = this.registerBoolean("NoInWeb", true, () -> this.page.getValue().equals("Settings"));
        this.checkSelf = this.registerBoolean("CheckSelf", true, () -> this.page.getValue().equals("Settings"));
        this.onlyGround = this.registerBoolean("SelfGround", true, () -> this.page.getValue().equals("Settings"));
        this.down = this.registerBoolean("Down", false, () -> this.page.getValue().equals("Settings"));
        this.face = this.registerBoolean("Face", false, () -> this.page.getValue().equals("Settings"));
        this.feet = this.registerBoolean("Feet", true, () -> this.page.getValue().equals("Settings"));
        this.onlyAir = this.registerBoolean("OnlyAir", true, () -> this.page.getValue().equals("Settings"));
        this.air = this.registerBoolean("Air", true, () -> this.page.getValue().equals("Settings"));
        this.minTargetSpeed = this.registerDouble("MinTargetSpeed", 10.0, 0.0, 50.0, () -> this.page.getValue().equals("Settings"));
        this.range = this.registerDouble("Range", 5.0, 1.0, 6.0, () -> this.page.getValue().equals("Settings"));
        this.tickPredict = this.registerInteger("Tick Predict", 8, 0, 30, () -> this.page.getValue().equals("Predict"));
        this.calculateYPredict = this.registerBoolean("Calculate Y Predict", true, () -> this.page.getValue().equals("Predict"));
        this.startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Predict"));
        this.exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Predict"));
        this.decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Predict"));
        this.exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Predict"));
        this.splitXZ = this.registerBoolean("Split XZ", true, () -> this.page.getValue().equals("Predict"));
        this.manualOutHole = this.registerBoolean("Manual Out Hole", false, () -> this.page.getValue().equals("Predict"));
        this.aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> this.manualOutHole.getValue() && this.page.getValue().equals("Predict"));
        this.stairPredict = this.registerBoolean("Stair Predict", false, () -> this.page.getValue().equals("Predict"));
        this.nStair = this.registerInteger("N Stair", 2, 1, 4, () -> this.stairPredict.getValue() && this.page.getValue().equals("Predict"));
        this.speedActivationStair = this.registerDouble("Speed Activation Stair", 0.3, 0.0, 1.0, () -> this.stairPredict.getValue() && this.page.getValue().equals("Predict"));
        this.timer = new Timing();
        this.progress = 0;
    }
    
    @Override
    public void onTick() {
        if (!this.timer.passedMs(this.delay.getValue())) {
            return;
        }
        if (this.onlyGround.getValue() && !AutoWeb.mc.player.onGround) {
            return;
        }
        this.progress = 0;
        final PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings(this.tickPredict.getValue(), this.calculateYPredict.getValue(), this.startDecrease.getValue(), this.exponentStartDecrease.getValue(), this.decreaseY.getValue(), this.exponentDecreaseY.getValue(), this.splitXZ.getValue(), this.manualOutHole.getValue(), this.aboveHoleManual.getValue(), this.stairPredict.getValue(), this.nStair.getValue(), this.speedActivationStair.getValue());
        for (final EntityPlayer player : AutoWeb.mc.world.playerEntities) {
            final EntityPlayer target = PredictUtil.predictPlayer(player, settings);
            if (!EntityUtil.invalid(target, this.range.getValue() + 3.0)) {
                if (isInWeb(player) && this.noInWeb.getValue()) {
                    continue;
                }
                if (LemonClient.speedUtil.getPlayerSpeed(player) < this.minTargetSpeed.getValue()) {
                    continue;
                }
                if (this.onlyAir.getValue() && player.onGround) {
                    continue;
                }
                if (this.down.getValue()) {
                    this.placeWeb(new BlockPos(target.posX, target.posY - 0.3, target.posZ));
                    this.placeWeb(new BlockPos(target.posX + 0.1, target.posY - 0.3, target.posZ + 0.1));
                    this.placeWeb(new BlockPos(target.posX - 0.1, target.posY - 0.3, target.posZ + 0.1));
                    this.placeWeb(new BlockPos(target.posX - 0.1, target.posY - 0.3, target.posZ - 0.1));
                    this.placeWeb(new BlockPos(target.posX + 0.1, target.posY - 0.3, target.posZ - 0.1));
                }
                if (this.face.getValue()) {
                    this.placeWeb(new BlockPos(target.posX + 0.2, target.posY + 1.5, target.posZ + 0.2));
                    this.placeWeb(new BlockPos(target.posX - 0.2, target.posY + 1.5, target.posZ + 0.2));
                    this.placeWeb(new BlockPos(target.posX - 0.2, target.posY + 1.5, target.posZ - 0.2));
                    this.placeWeb(new BlockPos(target.posX + 0.2, target.posY + 1.5, target.posZ - 0.2));
                }
                if (!this.air.getValue() || player.onGround || !this.feet.getValue()) {
                    continue;
                }
                if (HoleUtil.isHoleBlock(EntityUtil.getEntityPos(target), true, false, false)) {
                    continue;
                }
                this.placeWeb(new BlockPos(target.posX + 0.2, target.posY + 0.5, target.posZ + 0.2));
                this.placeWeb(new BlockPos(target.posX - 0.2, target.posY + 0.5, target.posZ + 0.2));
                this.placeWeb(new BlockPos(target.posX - 0.2, target.posY + 0.5, target.posZ - 0.2));
                this.placeWeb(new BlockPos(target.posX + 0.2, target.posY + 0.5, target.posZ - 0.2));
            }
        }
    }
    
    public static boolean isInWeb(final EntityPlayer player) {
        return isWeb(new BlockPos(player.posX + 0.3, player.posY + 1.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY + 1.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY + 1.5, player.posZ - 0.3)) || isWeb(new BlockPos(player.posX + 0.3, player.posY + 1.5, player.posZ - 0.3)) || isWeb(new BlockPos(player.posX + 0.3, player.posY - 0.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY - 0.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY - 0.5, player.posZ - 0.3)) || isWeb(new BlockPos(player.posX + 0.3, player.posY - 0.5, player.posZ - 0.3)) || isWeb(new BlockPos(player.posX + 0.3, player.posY + 0.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY + 0.5, player.posZ + 0.3)) || isWeb(new BlockPos(player.posX - 0.3, player.posY + 0.5, player.posZ - 0.3)) || isWeb(new BlockPos(player.posX + 0.3, player.posY + 0.5, player.posZ - 0.3));
    }
    
    private static boolean isWeb(final BlockPos pos) {
        return AutoWeb.mc.world.getBlockState(pos).getBlock() == Blocks.WEB && checkEntity(pos);
    }
    
    private boolean isSelf(final BlockPos pos) {
        if (!this.checkSelf.getValue()) {
            return false;
        }
        for (final Entity entity : AutoWeb.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity != AutoWeb.mc.player) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    private static boolean checkEntity(final BlockPos pos) {
        for (final Entity entity : AutoWeb.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityPlayer) {
                if (entity == AutoWeb.mc.player) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    private void placeWeb(final BlockPos pos) {
        if (this.progress >= this.multiPlace.getValue() || PlayerUtil.getDistance(pos) > this.range.getValue()) {
            return;
        }
        if (!AutoWeb.mc.world.isAirBlock(pos.up())) {
            return;
        }
        if (!this.canPlace(pos)) {
            return;
        }
        if (this.isSelf(pos)) {
            return;
        }
        if (BurrowUtil.findHotbarBlock(BlockWeb.class) == -1) {
            return;
        }
        InventoryUtil.run(BurrowUtil.findHotbarBlock(BlockWeb.class), this.packetSwitch.getValue(), () -> BlockUtil.placeBlock(pos, this.rotate.getValue(), this.packet.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue()));
        ++this.progress;
        this.timer.reset();
    }
    
    private boolean canPlace(final BlockPos pos) {
        return BlockUtil.canBlockFacing(pos) && BlockUtil.canReplace(pos) && this.strictPlaceCheck(pos);
    }
    
    private boolean strictPlaceCheck(final BlockPos pos) {
        if (!this.strict.getValue() && this.raytrace.getValue()) {
            return true;
        }
        for (final EnumFacing side : BlockUtil.getPlacableFacings(pos, true, this.raytrace.getValue())) {
            if (!BlockUtil.canClick(pos.offset(side))) {
                continue;
            }
            return true;
        }
        return false;
    }
}
