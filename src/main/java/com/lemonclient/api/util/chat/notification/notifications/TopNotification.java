// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat.notification.notifications;

import java.util.Iterator;
import com.lemonclient.api.util.font.CFontRenderer;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import com.lemonclient.client.LemonClient;
import java.util.HashMap;
import com.lemonclient.api.util.chat.notification.NotificationType;
import java.util.Map;
import com.lemonclient.api.util.chat.notification.Notification;

public class TopNotification extends Notification
{
    public Map<Integer, Integer> offsetDirLeft;
    public Map<Integer, Integer> offsetDirRight;
    public int arrAmount;
    public boolean shouldAdd;
    public int delaydir;
    
    public TopNotification(final NotificationType type, final String title, final String message, final int length) {
        super(type, title, message, length);
        this.offsetDirLeft = new HashMap<Integer, Integer>();
        this.offsetDirRight = new HashMap<Integer, Integer>();
        this.arrAmount = 0;
        this.shouldAdd = true;
        this.delaydir = 0;
    }
    
    @Override
    public void render(final int RealDisplayWidth, final int RealDisplayHeight) {
        final CFontRenderer font = LemonClient.INSTANCE.cFontRenderer;
        ++this.delaydir;
        final int height = font.getHeight() * 4;
        final int width = RealDisplayWidth / 4;
        final int offset = this.getOffset(width);
        final Color color = (this.type == NotificationType.INFO) ? Color.BLACK : this.getDefaultTypeColor();
        final boolean shouldEffect = offset >= width - 5;
        final int cx = RealDisplayWidth / 2;
        final int cy = RealDisplayHeight / 8;
        final int x = cx - offset;
        final int dWidth = offset * 2;
        if (shouldEffect) {
            if (this.shouldAdd) {
                this.offsetDirLeft.put(this.arrAmount, -16 - 10 * this.arrAmount);
                this.offsetDirRight.put(this.arrAmount, -16 - 10 * this.arrAmount);
                ++this.arrAmount;
                if (this.arrAmount >= 3) {
                    this.arrAmount = 0;
                    this.shouldAdd = false;
                }
            }
            GL11.glLineWidth(2.0f);
            for (final Map.Entry<Integer, Integer> offsetdir : this.offsetDirLeft.entrySet()) {
                final int value = offsetdir.getValue();
                final int alpha = calculateAlphaChangeColor(255, 10, 50, value);
                RenderUtil.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                this.drawC(x - value, cy, height, height);
            }
            for (final Map.Entry<Integer, Integer> offsetdir : this.offsetDirRight.entrySet()) {
                final int value = offsetdir.getValue();
                final int alpha = calculateAlphaChangeColor(255, 10, 50, value);
                RenderUtil.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                this.drawBC(x + dWidth + value, cy, height, height);
            }
        }
        final int delay = 3;
        if (this.delaydir >= 3) {
            this.delaydir = 0;
            for (final Map.Entry<Integer, Integer> offsetdir2 : this.offsetDirLeft.entrySet()) {
                if (offsetdir2.getValue() >= 50) {
                    offsetdir2.setValue(-16);
                }
                else {
                    offsetdir2.setValue(offsetdir2.getValue() + 1);
                }
            }
            for (final Map.Entry<Integer, Integer> offsetdir2 : this.offsetDirRight.entrySet()) {
                if (offsetdir2.getValue() >= 50) {
                    offsetdir2.setValue(-16);
                }
                else {
                    offsetdir2.setValue(offsetdir2.getValue() + 1);
                }
            }
        }
        RenderUtil.drawRect(x, cy, dWidth, height, color);
        RenderUtil.drawTriangle(x, cy, x - 15 - 1, cy + height / 2.0, x, cy + height, color);
        RenderUtil.drawTriangle(x + dWidth, cy + height, x + 15 + dWidth, cy + height / 2.0, x + dWidth, cy, color);
        final int fx = x + dWidth / 2;
        final int alpha2 = calculateAlphaChangeColor(10, 255, width, offset);
        font.drawString(this.title, fx - font.getStringWidth(this.title) / 2.0f, (float)(cy + 3), new GSColor(255, 255, 255, alpha2));
        font.drawString(this.message, fx - font.getStringWidth(this.message) / 2.0f, (float)(cy + font.getHeight() + 8), new GSColor(255, 255, 255, alpha2));
        if (!this.shouldAdd && !shouldEffect) {
            this.offsetDirLeft.remove(this.arrAmount);
            this.offsetDirRight.remove(this.arrAmount);
            ++this.arrAmount;
            if (this.arrAmount >= 3) {
                this.arrAmount = 0;
                this.shouldAdd = true;
            }
        }
    }
    
    public static Integer calculateAlphaChangeColor(final int oldAlpha, final int newAlpha, final int step, final int currentStep) {
        return Math.max(0, Math.min(255, oldAlpha + (newAlpha - oldAlpha) * Math.max(0, Math.min(step, currentStep)) / step));
    }
    
    public void drawBC(final int cx, final int cy, final int height, final int margin) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(3);
        GL11.glVertex2d(cx, cy);
        GL11.glVertex2d(cx + margin, cy + height / 2.0);
        GL11.glVertex2d(cx, cy + height);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    public void drawC(final int cx, final int cy, final int height, final int margin) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(3);
        GL11.glVertex2d(cx, cy);
        GL11.glVertex2d(cx - margin, cy + height / 2.0);
        GL11.glVertex2d(cx, cy + height);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
}
