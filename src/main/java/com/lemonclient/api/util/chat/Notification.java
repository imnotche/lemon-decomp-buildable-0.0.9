// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import java.awt.Color;
import java.util.Iterator;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import com.lemonclient.api.util.misc.Wrapper;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.util.text.TextFormatting;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.hud.Notifications;

public class Notification
{
    public String text;
    public double width;
    public double height;
    public float x;
    public String mark;
    Type type;
    public float y;
    public float position;
    public boolean in;
    AnimationUtil animationUtils;
    AnimationUtil yAnimationUtils;
    public static String ICON_NOTIFY_INFO;
    public static String ICON_NOTIFY_SUCCESS;
    public static String ICON_NOTIFY_WARN;
    public static String ICON_NOTIFY_ERROR;
    public static String ICON_NOTIFY_DISABLED;
    
    public Notification(final String text, final Type type) {
        this.height = 30.0;
        this.in = true;
        this.animationUtils = new AnimationUtil();
        this.yAnimationUtils = new AnimationUtil();
        String mark = "";
        this.type = type;
        if (ModuleManager.getModule(Notifications.class).mark.getValue()) {
            switch (this.type) {
                case ERROR: {
                    mark = TextFormatting.DARK_RED + Notification.ICON_NOTIFY_ERROR + " ";
                    break;
                }
                case INFO: {
                    mark = TextFormatting.YELLOW + Notification.ICON_NOTIFY_INFO + " ";
                    break;
                }
                case SUCCESS: {
                    mark = TextFormatting.GREEN + Notification.ICON_NOTIFY_SUCCESS + " ";
                    break;
                }
                case WARNING: {
                    mark = TextFormatting.RED + Notification.ICON_NOTIFY_WARN + " ";
                    break;
                }
                case DISABLE: {
                    mark = TextFormatting.RED + Notification.ICON_NOTIFY_DISABLED + " ";
                    break;
                }
            }
        }
        this.text = text;
        this.mark = mark;
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        this.width = FontUtil.getStringWidth(colorMain.customFont.getValue(), this.text) + 25;
        this.x = (float)this.width;
    }
    
    public void onRender() {
        int i = 0;
        for (final Notification notification : NotificationManager.notifications) {
            if (notification == this) {
                break;
            }
            ++i;
        }
        final Notifications notification2 = ModuleManager.getModule(Notifications.class);
        this.y = this.yAnimationUtils.animate((float)((float)i * (this.height + 5.0)), this.y, notification2.ySpeed.getValue().floatValue());
        final ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        int color = this.getColor(notification2.backGround.getValue());
        Color outlineColor = this.getOutColor(notification2.backGround.getValue());
        switch (this.type) {
            case ERROR: {
                color = this.getColor(notification2.errorBackGround.getValue());
                outlineColor = this.getOutColor(notification2.errorBackGround.getValue());
                break;
            }
            case SUCCESS: {
                color = this.getColor(notification2.successBackGround.getValue());
                outlineColor = this.getOutColor(notification2.successBackGround.getValue());
                break;
            }
            case WARNING: {
                color = this.getColor(notification2.warningBackGround.getValue());
                outlineColor = this.getOutColor(notification2.warningBackGround.getValue());
                break;
            }
            case DISABLE: {
                color = this.getColor(notification2.disableBackGround.getValue());
                outlineColor = this.getOutColor(notification2.disableBackGround.getValue());
                break;
            }
        }
        RenderUtil.drawRectS(sr.getScaledWidth() + this.x - this.width, sr.getScaledHeight() - 50 - this.y - this.height, sr.getScaledWidth() + this.x, sr.getScaledHeight() - 50 - this.y, color);
        if (notification2.outline.getValue()) {
            RenderUtil.drawRectSOutline(sr.getScaledWidth() + this.x - this.width, sr.getScaledHeight() - 50 - this.y - this.height, sr.getScaledWidth() + this.x, sr.getScaledHeight() - 50 - this.y, outlineColor);
        }
        FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), this.text, this.mark, (float)(int)(sr.getScaledWidth() + this.x - this.width + 10.0), (float)(int)(sr.getScaledHeight() - 50.0f - this.y - 18.0f), new GSColor(204, 204, 204, 232));
    }
    
    private int getColor(final GSColor color) {
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), notifications.alpha.getValue()).getRGB();
    }
    
    private Color getOutColor(final GSColor color) {
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), notifications.outlineAlpha.getValue());
    }
    
    static {
        Notification.ICON_NOTIFY_INFO = "\u2139";
        Notification.ICON_NOTIFY_SUCCESS = "\u2713";
        Notification.ICON_NOTIFY_WARN = "\u26a0";
        Notification.ICON_NOTIFY_ERROR = "\u26a0";
        Notification.ICON_NOTIFY_DISABLED = "\u2717";
    }
    
    public enum Type
    {
        SUCCESS, 
        INFO, 
        WARNING, 
        ERROR, 
        DISABLE
    }
}
