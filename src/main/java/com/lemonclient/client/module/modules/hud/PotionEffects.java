// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import net.minecraft.potion.Potion;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.Minecraft;
import java.awt.Color;
import net.minecraft.potion.PotionEffect;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "PotionEffects", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 300)
public class PotionEffects extends HUDModule
{
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    private final PotionList list;
    
    public PotionEffects() {
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.list = new PotionList();
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }
    
    Color getColour(final PotionEffect potion) {
        final int colour = potion.getPotion().getLiquidColor();
        final float r = (colour >> 16 & 0xFF) / 255.0f;
        final float g = (colour >> 8 & 0xFF) / 255.0f;
        final float b = (colour & 0xFF) / 255.0f;
        return new Color(r, g, b);
    }
    
    private class PotionList implements HUDList
    {
        @Override
        public int getSize() {
            return PotionEffects.mc.player.getActivePotionEffects().size();
        }
        
        @Override
        public String getItem(final int index) {
            final PotionEffect effect = (PotionEffect)PotionEffects.mc.player.getActivePotionEffects().toArray()[index];
            final String name = I18n.format(effect.getPotion().getName());
            final int amplifier = effect.getAmplifier() + 1;
            return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(effect, 1.0f);
        }
        
        @Override
        public Color getItemColor(final int i) {
            if (PotionEffects.mc.player.getActivePotionEffects().toArray().length != 0) {
                return PotionEffects.this.getColour((PotionEffect)PotionEffects.mc.player.getActivePotionEffects().toArray()[i]);
            }
            return null;
        }
        
        @Override
        public boolean sortUp() {
            return PotionEffects.this.sortUp.getValue();
        }
        
        @Override
        public boolean sortRight() {
            return PotionEffects.this.sortRight.getValue();
        }
    }
}
