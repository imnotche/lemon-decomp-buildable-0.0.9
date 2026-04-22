// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat.notification;

import java.awt.Color;

public abstract class Notification
{
    protected final NotificationType type;
    protected final String title;
    protected final String message;
    protected long start;
    protected final long fadedIn;
    protected final long fadeOut;
    protected final long end;
    
    public Notification(final NotificationType type, final String title, final String message, final int length) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.fadedIn = 100L * length;
        this.fadeOut = this.fadedIn + 150L * length;
        this.end = this.fadeOut + this.fadedIn;
    }
    
    public void show() {
        this.start = System.currentTimeMillis();
    }
    
    public boolean isShown() {
        return this.getTime() <= this.end;
    }
    
    protected long getTime() {
        return System.currentTimeMillis() - this.start;
    }
    
    protected int getOffset(final double maxWidth) {
        if (this.getTime() < this.fadedIn) {
            return (int)(Math.tanh(this.getTime() / (double)this.fadedIn * 3.0) * maxWidth);
        }
        if (this.getTime() > this.fadeOut) {
            return (int)(Math.tanh(3.0 - (this.getTime() - this.fadeOut) / (double)(this.end - this.fadeOut) * 3.0) * maxWidth);
        }
        return (int)maxWidth;
    }
    
    protected Color getDefaultTypeColor() {
        if (this.type == NotificationType.INFO) {
            return Color.BLUE;
        }
        if (this.type == NotificationType.WARNING) {
            return new Color(218, 165, 32);
        }
        if (this.type == NotificationType.LOAD) {
            return new Color(255, 255, 150);
        }
        if (this.type == NotificationType.WELCOME) {
            return new Color(255, 255, 75);
        }
        return Color.RED;
    }
    
    public abstract void render(final int p0, final int p1);
}
