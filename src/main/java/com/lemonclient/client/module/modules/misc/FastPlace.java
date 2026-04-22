// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.init.Items;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "FastPlace", category = Category.Misc)
public class FastPlace extends Module
{
    BooleanSetting exp;
    BooleanSetting crystals;
    BooleanSetting offhandCrystal;
    BooleanSetting everything;
    
    public FastPlace() {
        this.exp = this.registerBoolean("Exp", false);
        this.crystals = this.registerBoolean("Crystals", false);
        this.offhandCrystal = this.registerBoolean("Offhand Crystal", false);
        this.everything = this.registerBoolean("Everything", false);
    }
    
    @Override
    public void onUpdate() {
        if ((this.exp.getValue() && FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) || FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.crystals.getValue() && FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.offhandCrystal.getValue() && FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.everything.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        FastPlace.mc.playerController.blockHitDelay = 0;
    }
}
