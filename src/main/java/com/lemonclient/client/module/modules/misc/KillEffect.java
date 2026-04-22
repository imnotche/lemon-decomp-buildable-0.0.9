// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "KillEffect", category = Category.Misc)
public class KillEffect extends Module
{
    BooleanSetting thunder;
    IntegerSetting numbersThunder;
    BooleanSetting sound;
    IntegerSetting numberSound;
    ArrayList<EntityPlayer> playersDead;
    
    public KillEffect() {
        this.thunder = this.registerBoolean("Thunder", true);
        this.numbersThunder = this.registerInteger("Number Thunder", 1, 1, 10);
        this.sound = this.registerBoolean("Sound", true);
        this.numberSound = this.registerInteger("Number Sound", 1, 1, 10);
        this.playersDead = new ArrayList<EntityPlayer>();
    }
    
    @Override
    protected void onEnable() {
        this.playersDead.clear();
    }
    
    @Override
    public void onUpdate() {
        if (KillEffect.mc.world == null) {
            this.playersDead.clear();
            return;
        }
        KillEffect.mc.world.playerEntities.forEach(entity -> {
            if (this.playersDead.contains(entity)) {
                if (entity.getHealth() > 0.0f) {
                    this.playersDead.remove(entity);
                }
            }
            else if (entity.getHealth() == 0.0f) {
                if (this.thunder.getValue()) {
                    for (int i = 0; i < this.numbersThunder.getValue(); ++i) {
                        KillEffect.mc.world.spawnEntity(new EntityLightningBolt(KillEffect.mc.world, entity.posX, entity.posY, entity.posZ, true));
                    }
                }
                if (this.sound.getValue()) {
                    for (int j = 0; j < this.numberSound.getValue(); ++j) {
                        KillEffect.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                    }
                }
                this.playersDead.add(entity);
            }
        });
    }
}
