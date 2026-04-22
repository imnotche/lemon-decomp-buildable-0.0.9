// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.util.Arrays;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Notifications", category = Category.HUD, drawn = false)
public class Notifications extends Module
{
    public ColorSetting backGround;
    public ColorSetting successBackGround;
    public ColorSetting warningBackGround;
    public ColorSetting errorBackGround;
    public ColorSetting disableBackGround;
    public IntegerSetting alpha;
    public BooleanSetting outline;
    public IntegerSetting outlineAlpha;
    public BooleanSetting mark;
    public DoubleSetting xSpeed;
    public DoubleSetting ySpeed;
    public IntegerSetting max;
    public ModeSetting mode;
    public BooleanSetting disableChat;
    
    public Notifications() {
        this.backGround = this.registerColor("Info BackGround", new GSColor(255, 255, 255));
        this.successBackGround = this.registerColor("Success BackGround", new GSColor(0, 255, 0));
        this.warningBackGround = this.registerColor("Warning BackGround", new GSColor(255, 0, 0));
        this.errorBackGround = this.registerColor("Error BackGround", new GSColor(0, 0, 0));
        this.disableBackGround = this.registerColor("Disable BackGround", new GSColor(255, 0, 0));
        this.alpha = this.registerInteger("Alpha", 168, 0, 255);
        this.outline = this.registerBoolean("Outline", true);
        this.outlineAlpha = this.registerInteger("Outline Alpha", 200, 0, 255);
        this.mark = this.registerBoolean("Icon", true);
        this.xSpeed = this.registerDouble("Animation XSpeed", 0.1, 0.01, 0.5);
        this.ySpeed = this.registerDouble("Animation YSpeed", 0.1, 0.01, 5.0);
        this.max = this.registerInteger("Max Count", 10, 0, 100);
        this.mode = this.registerMode("Mode", Arrays.asList("Remove", "Cancel"), "Remove");
        this.disableChat = this.registerBoolean("No Chat Msg", true);
    }
}
