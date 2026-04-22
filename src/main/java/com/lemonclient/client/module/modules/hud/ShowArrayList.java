// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.util.chat.AnimationUtil;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Comparator;
import java.util.function.Predicate;
import com.lemonclient.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ShowArrayList", category = Category.HUD, drawn = false)
public final class ShowArrayList extends Module
{
    private int count;
    IntegerSetting width;
    IntegerSetting height;
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    DoubleSetting animationSpeed;
    ColorSetting color;
    public static ShowArrayList INSTANCE;
    
    public ShowArrayList() {
        this.width = this.registerInteger("X", 0, 0, 1920);
        this.height = this.registerInteger("Y", 0, 0, 1080);
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.animationSpeed = this.registerDouble("Animation Speed", 3.5, 0.0, 5.0);
        this.color = this.registerColor("Color", new GSColor(210, 100, 165));
        ShowArrayList.INSTANCE = this;
    }
    
    private static String getArrayList(final Module module) {
        return module.getName() + ChatFormatting.GRAY + module.getHudInfo();
    }
    
    @Override
    public void onRender() {
        if (!this.isEnabled() || ShowArrayList.mc.world == null || ShowArrayList.mc.player == null) {
            return;
        }
        this.count = 0;
        ModuleManager.getModules().stream().filter(ShowArrayList::render).sorted(Comparator.comparingInt(ShowArrayList::getWidth)).forEach(module -> ShowArrayList.drawRect(module));
    }
    
    private static boolean render(final Module it) {
        return it.isDrawn();
    }
    
    private static Integer getWidth(final Module it) {
        return FontUtil.getStringWidth(ModuleManager.getModule(ColorMain.class).customFont.getValue(), getArrayList(it)) * -1;
    }
    
    private static void drawRect(final Module module) {
        final boolean customFont = ModuleManager.getModule(ColorMain.class).customFont.getValue();
        if (module.isDrawn()) {
            final String modText = getArrayList(module);
            final float modWidth = (float)FontUtil.getStringWidth(customFont, modText);
            final float remainingAnimation = module.remainingAnimation;
            final float smoothSpeed = (float)(0.009999999776482582 + ShowArrayList.INSTANCE.animationSpeed.getValue() / 30.0);
            final float minSpeed = 0.1f;
            if (module.isEnabled()) {
                if (module.remainingAnimation < modWidth) {
                    final float end = modWidth + 1.0f;
                    module.remainingAnimation = AnimationUtil.moveTowards(remainingAnimation, end, smoothSpeed, minSpeed, false);
                }
                else if (module.remainingAnimation > modWidth) {
                    final float end2 = modWidth - 1.0f;
                    module.remainingAnimation = AnimationUtil.moveTowards(remainingAnimation, end2, smoothSpeed, minSpeed, false);
                }
            }
            else {
                if (module.remainingAnimation <= 0.0f) {
                    return;
                }
                final float end3 = -modWidth;
                module.remainingAnimation = AnimationUtil.moveTowards(remainingAnimation, end3, smoothSpeed, minSpeed, false);
            }
            if (ShowArrayList.INSTANCE.sortRight.getValue()) {
                FontUtil.drawStringWithShadow(customFont, modText, (float)(int)(ShowArrayList.INSTANCE.width.getValue() - module.remainingAnimation), (float)(ShowArrayList.INSTANCE.height.getValue() + 10 * ShowArrayList.INSTANCE.count * (ShowArrayList.INSTANCE.sortUp.getValue() ? 1 : -1)), ShowArrayList.INSTANCE.color.getValue());
            }
            else {
                FontUtil.drawStringWithShadow(customFont, modText, (float)(int)(ShowArrayList.INSTANCE.width.getValue() - 2 - modWidth + module.remainingAnimation), (float)(ShowArrayList.INSTANCE.height.getValue() + 10 * ShowArrayList.INSTANCE.count * (ShowArrayList.INSTANCE.sortUp.getValue() ? 1 : -1)), ShowArrayList.INSTANCE.color.getValue());
            }
            final ShowArrayList instance = ShowArrayList.INSTANCE;
            ++instance.count;
        }
    }
}
