// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoCreeper", category = Category.Combat)
public class AutoCreeper extends Module
{
    DoubleSetting minDamage;
    IntegerSetting delay;
    DoubleSetting enemyRange;
    DoubleSetting range;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting predict;
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
    Timing timer;
    EntityPlayer target;
    
    public AutoCreeper() {
        this.minDamage = this.registerDouble("Min Damage", 6.0, 0.0, 36.0);
        this.delay = this.registerInteger("Delay", 50, 0, 1000);
        this.enemyRange = this.registerDouble("Enemy Range", 10.0, 0.0, 16.0);
        this.range = this.registerDouble("Range", 5.0, 0.0, 6.0);
        this.rotate = this.registerBoolean("Rotate", false);
        this.packet = this.registerBoolean("Packet", false);
        this.swing = this.registerBoolean("Swing", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.predict = this.registerBoolean("Predict", true);
        this.tickPredict = this.registerInteger("TickPredict", 8, 0, 30, () -> this.predict.getValue());
        this.calculateYPredict = this.registerBoolean("CalculateYPredict", true, () -> this.predict.getValue());
        this.startDecrease = this.registerInteger("StartDecrease", 39, 0, 200, () -> this.predict.getValue() && this.calculateYPredict.getValue());
        this.exponentStartDecrease = this.registerInteger("ExponentStart", 2, 1, 5, () -> this.predict.getValue() && this.calculateYPredict.getValue());
        this.decreaseY = this.registerInteger("DecreaseY", 2, 1, 5, () -> this.predict.getValue() && this.calculateYPredict.getValue());
        this.exponentDecreaseY = this.registerInteger("ExponentDecreaseY", 1, 1, 3, () -> this.predict.getValue() && this.calculateYPredict.getValue());
        this.splitXZ = this.registerBoolean("SplitXZ", true, () -> this.predict.getValue());
        this.manualOutHole = this.registerBoolean("ManualOutHole", false, () -> this.predict.getValue());
        this.aboveHoleManual = this.registerBoolean("AboveHoleManual", false, () -> this.predict.getValue() && this.manualOutHole.getValue());
        this.stairPredict = this.registerBoolean("StairPredict", false, () -> this.predict.getValue());
        this.nStair = this.registerInteger("NStair", 2, 1, 4, () -> this.predict.getValue() && this.stairPredict.getValue());
        this.speedActivationStair = this.registerDouble("SpeedActivationStair", 0.11, 0.0, 1.0, () -> this.predict.getValue() && this.stairPredict.getValue());
        this.timer = new Timing();
    }
    
    @Override
    public void onTick() {
        final int slot = this.getSlot();
        if (slot == -1) {
            return;
        }
        final EntityPlayer nearestPlayer = PlayerUtil.getNearestPlayer(this.enemyRange.getValue());
        this.target = nearestPlayer;
        final EntityPlayer origin = nearestPlayer;
        if (this.target == null) {
            return;
        }
        if (AutoEz.INSTANCE.isEnabled()) {
            AutoEz.INSTANCE.addTargetedPlayer(this.target.getName());
        }
        final PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings(this.tickPredict.getValue(), this.calculateYPredict.getValue(), this.startDecrease.getValue(), this.exponentStartDecrease.getValue(), this.decreaseY.getValue(), this.exponentDecreaseY.getValue(), this.splitXZ.getValue(), this.manualOutHole.getValue(), this.aboveHoleManual.getValue(), this.stairPredict.getValue(), this.nStair.getValue(), this.speedActivationStair.getValue());
        if (this.predict.getValue()) {
            this.target = PredictUtil.predictPlayer(this.target, settings);
        }
        BlockPos blockPos = null;
        double dmg = 0.0;
        for (final BlockPos pos : EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue(), this.range.getValue(), false, false, 0)) {
            if (BurrowUtil.getFirstFacing(pos) == null) {
                continue;
            }
            final double damage = DamageUtil.calculateDamage(origin, this.target.getPositionVector(), this.target.boundingBox, pos.x + 0.5, pos.y, pos.z + 0.5, 3.0f, "Default");
            if (damage < this.minDamage.getValue()) {
                continue;
            }
            if (dmg >= damage) {
                continue;
            }
            blockPos = pos;
            dmg = damage;
        }
        if (blockPos == null) {
            return;
        }
        if (this.timer.passedMs(this.delay.getValue())) {
            this.timer.reset();
            final BlockPos finalBlockPos = blockPos;
            InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(finalBlockPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
        }
    }
    
    public int getSlot() {
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoCreeper.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() == Items.SPAWN_EGG) {
                newSlot = i;
                break;
            }
        }
        return newSlot;
    }
}
