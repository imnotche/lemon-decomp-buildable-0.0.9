// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.misc.CrystalUtil;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "CrystalHit", category = Category.Combat)
public class CrystalHit extends Module
{
    IntegerSetting range;
    IntegerSetting delay;
    BooleanSetting swing;
    BooleanSetting packetBreak;
    BooleanSetting antiWeakness;
    BooleanSetting weakBypass;
    BooleanSetting packetSwitch;
    BooleanSetting silent;
    int delayTime;
    
    public CrystalHit() {
        this.range = this.registerInteger("Range", 4, 0, 10);
        this.delay = this.registerInteger("Delay", 0, 0, 40);
        this.swing = this.registerBoolean("Swing", false);
        this.packetBreak = this.registerBoolean("Packet Break", false);
        this.antiWeakness = this.registerBoolean("Anti Weakness", false);
        this.weakBypass = this.registerBoolean("Bypass Switch", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> !this.weakBypass.getValue());
        this.silent = this.registerBoolean("Silent Switch", false, () -> !this.weakBypass.getValue());
        this.delayTime = 0;
    }
    
    @Override
    public void onUpdate() {
        final EntityEnderCrystal crystal = CrystalHit.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> (EntityEnderCrystal)entity).min(Comparator.comparing(c -> CrystalHit.mc.player.getDistance(c))).orElse(null);
        if (crystal != null && CrystalHit.mc.player.getDistance(crystal) <= this.range.getValue() && this.delayTime++ >= this.delay.getValue()) {
            CrystalUtil.breakCrystal(crystal, this.packetBreak.getValue(), this.swing.getValue(), this.packetSwitch.getValue(), this.silent.getValue(), this.antiWeakness.getValue(), this.weakBypass.getValue());
        }
    }
}
