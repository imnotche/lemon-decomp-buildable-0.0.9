// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import java.util.Objects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "LevitationControl", category = Category.Movement)
public class LevitationControl extends Module
{
    DoubleSetting upAmplifier;
    DoubleSetting downAmplifier;
    
    public LevitationControl() {
        this.upAmplifier = this.registerDouble("Amplifier Up", 1.0, 1.0, 3.0);
        this.downAmplifier = this.registerDouble("Amplifier Down", 1.0, 1.0, 3.0);
    }
    
    @Override
    public void onUpdate() {
        if (LevitationControl.mc.player.isPotionActive(MobEffects.LEVITATION)) {
            final int amplifier = Objects.requireNonNull(LevitationControl.mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(25)))).getAmplifier();
            if (LevitationControl.mc.gameSettings.keyBindJump.isKeyDown()) {
                LevitationControl.mc.player.motionY = (0.05 * (amplifier + 1) - LevitationControl.mc.player.motionY) * 0.2 * this.upAmplifier.getValue();
            }
            else if (LevitationControl.mc.gameSettings.keyBindSneak.isKeyDown()) {
                LevitationControl.mc.player.motionY = -((0.05 * (amplifier + 1) - LevitationControl.mc.player.motionY) * 0.2 * this.downAmplifier.getValue());
            }
            else {
                LevitationControl.mc.player.motionY = 0.0;
            }
        }
    }
}
