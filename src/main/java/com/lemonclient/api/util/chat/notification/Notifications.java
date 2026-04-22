// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat.notification;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import com.lemonclient.api.util.misc.Timing;

public class Notifications
{
    public static String ICON_NOTIFY_INFO;
    public static String ICON_NOTIFY_SUCCESS;
    public static String ICON_NOTIFY_WARN;
    public static String ICON_NOTIFY_ERROR;
    public static String ICON_NOTIFY_DISABLED;
    public Timing timer;
    public Type t;
    public long stayTime;
    public String message;
    public double lastY;
    public double posY;
    public double width;
    public double height;
    public double animationX;
    public int color;
    
    public Notifications(final String message, final Type type) {
        this.timer = new Timing();
        this.message = message;
        this.timer.reset();
        this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(message) + 35;
        this.height = 20.0;
        this.animationX = this.width;
        this.stayTime = 1000L;
        this.posY = -1.0;
        this.t = type;
        if (type.equals(Type.INFO)) {
            this.color = -14342875;
        }
        else if (type.equals(Type.ERROR)) {
            this.color = new Color(36, 36, 36).getRGB();
        }
        else if (type.equals(Type.SUCCESS)) {
            this.color = new Color(36, 36, 36).getRGB();
        }
        else if (type.equals(Type.DISABLE)) {
            this.color = new Color(36, 36, 36).getRGB();
        }
        else if (type.equals(Type.WARNING)) {
            this.color = -14342875;
        }
    }
    
    public static int reAlpha(final int color, final float alpha) {
        final Color c = new Color(color);
        final float r = 0.003921569f * c.getRed();
        final float g = 0.003921569f * c.getGreen();
        final float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }
    
    public void draw(final double getY, final double lastY) {
        this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.message) + 25;
        this.height = 22.0;
        this.lastY = lastY;
        this.animationX = this.getAnimationState(this.animationX, this.isFinished() ? this.width : 0.0, 450.0);
        if (this.posY == -1.0) {
            this.posY = getY;
        }
        else {
            this.posY = this.getAnimationState(this.posY, getY, 350.0);
        }
        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        final int x1 = (int)(res.getScaledWidth() - this.width + this.animationX / 2.0);
        final int x2 = (int)(res.getScaledWidth() + this.animationX / 2.0);
        int y1 = (int)this.posY - 22;
        final int y2 = (int)(y1 + this.height);
        RenderUtil.drawRect((float)x1, (float)y1, (float)x2, (float)y2, reAlpha(this.color, 0.85f));
        RenderUtil.drawRect((float)x1, (float)(y2 - 1), (float)(x1 + Math.min((x2 - x1) * (System.currentTimeMillis() - this.timer.getPassedTimeMs()) / this.stayTime, x2 - x1)), (float)y2, reAlpha(-1, 0.85f));
        switch (this.t) {
            case ERROR: {
                Minecraft.getMinecraft().fontRenderer.drawString(Notifications.ICON_NOTIFY_ERROR, x1 + 5, y1 + 7, -65794);
                break;
            }
            case INFO: {
                Minecraft.getMinecraft().fontRenderer.drawString(Notifications.ICON_NOTIFY_INFO, x1 + 5, y1 + 7, -65794);
                break;
            }
            case SUCCESS: {
                Minecraft.getMinecraft().fontRenderer.drawString(Notifications.ICON_NOTIFY_SUCCESS, x1 + 5, y1 + 7, -65794);
                break;
            }
            case WARNING: {
                Minecraft.getMinecraft().fontRenderer.drawString(Notifications.ICON_NOTIFY_WARN, x1 + 5, y1 + 7, -65794);
                break;
            }
            case DISABLE: {
                Minecraft.getMinecraft().fontRenderer.drawString(Notifications.ICON_NOTIFY_DISABLED, x1 + 5, y1 + 7, -65794);
                break;
            }
        }
        ++y1;
        if (this.message.contains(" Enabled")) {
            Minecraft.getMinecraft().fontRenderer.drawString(this.message, x1 + 19, (int)(y1 + this.height / 4.0), -1);
            Minecraft.getMinecraft().fontRenderer.drawString(" Enabled", x1 + 20 + Minecraft.getMinecraft().fontRenderer.getStringWidth(this.message), (int)(y1 + this.height / 4.0), -9868951);
        }
        else if (this.message.contains(" Disabled")) {
            Minecraft.getMinecraft().fontRenderer.drawString(this.message, x1 + 19, (int)(y1 + this.height / 4.0), -1);
            Minecraft.getMinecraft().fontRenderer.drawString(" Disabled", x1 + 20 + Minecraft.getMinecraft().fontRenderer.getStringWidth(this.message), (int)(y1 + this.height / 4.0), -9868951);
        }
        else {
            Minecraft.getMinecraft().fontRenderer.drawString(this.message, x1 + 20, (int)(y1 + this.height / 4.0), -1);
        }
    }
    
    public boolean shouldDelete() {
        return this.isFinished() && this.animationX >= this.width;
    }
    
    public boolean isFinished() {
        return this.timer.passedMs(this.stayTime) && this.posY == this.lastY;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public double getAnimationState(double animation, final double finalState, final double speed) {
        final float add = (float)(Minecraft.getMinecraft().timer.tickLength * speed * speed);
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add;
            }
            else {
                animation = finalState;
            }
        }
        else if (animation - add > finalState) {
            animation -= add;
        }
        else {
            animation = finalState;
        }
        return animation;
    }
    
    static {
        Notifications.ICON_NOTIFY_INFO = "\u2139";
        Notifications.ICON_NOTIFY_SUCCESS = "\u2713";
        Notifications.ICON_NOTIFY_WARN = "\u26a0";
        Notifications.ICON_NOTIFY_ERROR = "\u26a0";
        Notifications.ICON_NOTIFY_DISABLED = "\u2717";
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
