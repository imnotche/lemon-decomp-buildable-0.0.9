// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.awt.Color;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "Watermark", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 0)
public class Watermark extends HUDModule
{
    BooleanSetting custom;
    StringSetting text;
    ColorSetting color;
    
    public Watermark() {
        this.custom = this.registerBoolean("Custom", false);
        this.text = this.registerString("Text", "", () -> this.custom.getValue());
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new WatermarkList(), 9, 1);
    }
    
    private class WatermarkList implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            if (Watermark.this.custom.getValue()) {
                return Watermark.this.text.getText();
            }
            return "LemonClient v0.0.9";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return Watermark.this.color.getValue();
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
