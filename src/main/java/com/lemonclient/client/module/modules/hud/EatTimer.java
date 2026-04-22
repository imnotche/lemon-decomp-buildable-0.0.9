// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.event.events.Render2DEvent;
import net.minecraft.item.ItemFood;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "EatTimer", category = Category.HUD, drawn = false)
public class EatTimer extends Module
{
    IntegerSetting timer;
    int tick;
    boolean holding;
    
    public EatTimer() {
        this.timer = this.registerInteger("Timer", 32, 0, 100);
        this.tick = 100;
        this.holding = false;
    }
    
    public void onEnable() {
        this.holding = false;
        this.tick = 100;
    }
    
    @Override
    public void onTick() {
        if (EatTimer.mc.world == null || EatTimer.mc.player == null) {
            this.tick = 100;
            return;
        }
        ++this.tick;
        this.holding = (EatTimer.mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || EatTimer.mc.player.getHeldItemOffhand().getItem() instanceof ItemFood);
        if (EatTimer.mc.player.isHandActive() && this.holding && this.tick > this.timer.getValue()) {
            this.tick = 0;
        }
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (EatTimer.mc.world == null || EatTimer.mc.player == null) {
            this.tick = 100;
            return;
        }
        if (this.holding) {
            if (this.tick <= this.timer.getValue()) {
                final double percent = this.tick / (double)this.timer.getValue();
                final String text = String.format("%.1f", percent * 100.0) + "%";
                int divider = EatTimer.mc.gameSettings.guiScale;
                if (divider == 0) {
                    divider = 3;
                }
                final boolean font = ModuleManager.getModule(ColorMain.class).customFont.getValue();
                FontUtil.drawStringWithShadow(font, text, (float)(EatTimer.mc.displayWidth / divider / 2 - FontUtil.getStringWidth(font, text) / 2), (float)(EatTimer.mc.displayHeight / divider / 2 + 16), new GSColor(255, 255, 255));
            }
        }
        else {
            this.tick = 100;
        }
    }
}
