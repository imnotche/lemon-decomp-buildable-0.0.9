// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import net.minecraft.client.renderer.ItemRenderer;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "RenderTweaks", category = Category.Render)
public class RenderTweaks extends Module
{
    public BooleanSetting viewClip;
    public BooleanSetting noAnimation;
    public BooleanSetting noEat;
    BooleanSetting lowOffhand;
    DoubleSetting lowOffhandSlider;
    BooleanSetting fovChanger;
    IntegerSetting fovChangerSlider;
    ItemRenderer itemRenderer;
    private float oldFOV;
    
    public RenderTweaks() {
        this.viewClip = this.registerBoolean("View Clip", false);
        this.noAnimation = this.registerBoolean("No Animation", false);
        this.noEat = this.registerBoolean("No Eat", false);
        this.lowOffhand = this.registerBoolean("Low Offhand", false);
        this.lowOffhandSlider = this.registerDouble("Offhand Height", 1.0, 0.1, 1.0, () -> this.lowOffhand.getValue());
        this.fovChanger = this.registerBoolean("FOV", false);
        this.fovChangerSlider = this.registerInteger("FOV Slider", 90, 70, 200, () -> this.fovChanger.getValue());
        this.itemRenderer = RenderTweaks.mc.entityRenderer.itemRenderer;
    }
    
    @Override
    public void onUpdate() {
        if (this.lowOffhand.getValue()) {
            this.itemRenderer.equippedProgressOffHand = this.lowOffhandSlider.getValue().floatValue();
        }
        if (this.fovChanger.getValue()) {
            RenderTweaks.mc.gameSettings.fovSetting = this.fovChangerSlider.getValue();
        }
        if (!this.fovChanger.getValue()) {
            RenderTweaks.mc.gameSettings.fovSetting = this.oldFOV;
        }
    }
    
    public void onEnable() {
        this.oldFOV = RenderTweaks.mc.gameSettings.fovSetting;
    }
    
    public void onDisable() {
        RenderTweaks.mc.gameSettings.fovSetting = this.oldFOV;
    }
}
