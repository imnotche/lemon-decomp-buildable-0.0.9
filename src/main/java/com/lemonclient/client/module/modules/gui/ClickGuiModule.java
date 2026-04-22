// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.gui;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.util.render.GSColor;
import java.util.function.Supplier;
import com.lemonclient.client.LemonClient;
import java.util.Collections;
import java.util.Arrays;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ClickGUI", category = Category.GUI, bind = 25, drawn = false)
public class ClickGuiModule extends Module
{
    public IntegerSetting scrollSpeed;
    public IntegerSetting animationSpeed;
    public ModeSetting scrolling;
    public BooleanSetting showHUD;
    public BooleanSetting csgoLayout;
    public ModeSetting theme;
    public BooleanSetting gradient;
    
    public ClickGuiModule() {
        this.scrollSpeed = this.registerInteger("Scroll Speed", 10, 1, 20);
        this.animationSpeed = this.registerInteger("Animation Speed", 300, 0, 1000);
        this.scrolling = this.registerMode("Scrolling", Arrays.asList("Screen", "Container"), "Container");
        this.showHUD = this.registerBoolean("Show HUD Panels", false);
        this.csgoLayout = this.registerBoolean("CSGO Layout", false);
        this.theme = this.registerMode("Skin", Collections.singletonList("Clear"), "Clear", () -> false);
        this.gradient = this.registerBoolean("Gradient", true);
    }
    
    public void onEnable() {
        LemonClient.INSTANCE.gameSenseGUI.enterGUI();
        this.disable();
    }
    
    public ColorSetting registerColor(final String name, final String configName, final Supplier<Boolean> isVisible, final boolean rainbow, final boolean rainbowEnabled, final boolean alphaEnabled, final GSColor value) {
        final ColorSetting setting = new ColorSetting(name, configName, this, isVisible, rainbow, rainbowEnabled, alphaEnabled, value);
        SettingsManager.addSetting(setting);
        return setting;
    }
}
