// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import java.util.function.Predicate;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Predict", category = Category.Misc)
public class Predict extends Module
{
    IntegerSetting range;
    IntegerSetting tickPredict;
    BooleanSetting calculateYPredict;
    IntegerSetting startDecrease;
    IntegerSetting exponentStartDecrease;
    IntegerSetting decreaseY;
    IntegerSetting exponentDecreaseY;
    BooleanSetting splitXZ;
    BooleanSetting hideSelf;
    IntegerSetting width;
    BooleanSetting justOnce;
    BooleanSetting manualOutHole;
    BooleanSetting aboveHoleManual;
    BooleanSetting stairPredict;
    IntegerSetting nStair;
    DoubleSetting speedActivationStair;
    ColorSetting mainColor;
    
    public Predict() {
        this.range = this.registerInteger("Range", 10, 0, 100);
        this.tickPredict = this.registerInteger("Tick Predict", 8, 0, 30);
        this.calculateYPredict = this.registerBoolean("Calculate Y Predict", true);
        this.startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> this.calculateYPredict.getValue());
        this.exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> this.calculateYPredict.getValue());
        this.decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> this.calculateYPredict.getValue());
        this.exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> this.calculateYPredict.getValue());
        this.splitXZ = this.registerBoolean("Split XZ", true);
        this.hideSelf = this.registerBoolean("Hide Self", false);
        this.width = this.registerInteger("Line Width", 2, 1, 5);
        this.justOnce = this.registerBoolean("Just Once", false);
        this.manualOutHole = this.registerBoolean("Manual Out Hole", false);
        this.aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> this.manualOutHole.getValue());
        this.stairPredict = this.registerBoolean("Stair Predict", false);
        this.nStair = this.registerInteger("N Stair", 2, 1, 4, () -> this.stairPredict.getValue());
        this.speedActivationStair = this.registerDouble("Speed Activation Stair", 0.3, 0.0, 1.0, () -> this.stairPredict.getValue());
        this.mainColor = this.registerColor("Color");
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        final PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings(this.tickPredict.getValue(), this.calculateYPredict.getValue(), this.startDecrease.getValue(), this.exponentStartDecrease.getValue(), this.decreaseY.getValue(), this.exponentDecreaseY.getValue(), this.splitXZ.getValue(), this.manualOutHole.getValue(), this.aboveHoleManual.getValue(), this.stairPredict.getValue(), this.nStair.getValue(), this.speedActivationStair.getValue());
        Predict.mc.world.playerEntities.stream().filter(entity -> !this.hideSelf.getValue() || entity != Predict.mc.player).filter(this::rangeEntityCheck).forEach(entity -> {
            final EntityPlayer clonedPlayer = PredictUtil.predictPlayer(entity, settings);
            RenderUtil.drawBoundingBox(clonedPlayer.getEntityBoundingBox(), this.width.getValue(), this.mainColor.getColor());
        });
        if (this.justOnce.getValue()) {
            this.disable();
        }
    }
    
    private boolean rangeEntityCheck(final Entity entity) {
        return entity.getDistance(Predict.mc.player) <= this.range.getValue();
    }
}
