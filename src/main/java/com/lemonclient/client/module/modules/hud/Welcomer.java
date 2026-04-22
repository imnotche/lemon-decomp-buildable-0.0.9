// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "Welcomer", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 450, posZ = 0)
public class Welcomer extends HUDModule
{
    StringSetting prefix;
    StringSetting suffix;
    ColorSetting color;
    
    public Welcomer() {
        this.prefix = this.registerString("Prefix", "Hi ");
        this.suffix = this.registerString("Suffix", " :^)");
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new WelcomerList(), 9, 1);
    }
    
    private class WelcomerList implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return Welcomer.this.prefix.getText() + Welcomer.mc.player.getName() + Welcomer.this.suffix.getText();
        }
        
        @Override
        public Color getItemColor(final int index) {
            return Welcomer.this.color.getValue();
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
