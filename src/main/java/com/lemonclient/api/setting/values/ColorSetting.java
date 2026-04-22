// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import java.awt.Color;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.function.Supplier;
import com.lemonclient.client.module.Module;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.Setting;

public class ColorSetting extends Setting<GSColor>
{
    private boolean rainbow;
    private final boolean rainbowEnabled;
    private final boolean alphaEnabled;
    
    public ColorSetting(final String name, final Module module, final boolean rainbow, final GSColor value) {
        super(value, name, module);
        this.rainbow = false;
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = false;
    }
    
    public ColorSetting(final String name, final Module module, final boolean rainbow, final GSColor value, final boolean alphaEnabled) {
        super(value, name, module);
        this.rainbow = false;
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = alphaEnabled;
    }
    
    public ColorSetting(final String name, final String configName, final Module module, final Supplier<Boolean> isVisible, final boolean rainbow, final boolean rainbowEnabled, final boolean alphaEnabled, final GSColor value) {
        super(value, name, configName, module, isVisible);
        this.rainbow = false;
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }
    
    @Override
    public GSColor getValue() {
        if (!this.rainbow) {
            return super.getValue();
        }
        final String s = ColorMain.INSTANCE.rainbowMode.getValue();
        switch (s) {
            case "Sin": {
                return getRainbowSin(0, 0, 1.0, 1, 1.0, 0, false);
            }
            case "Tan": {
                return getRainbowTan(0, 0, 1.0, 1, 1.0, 0, false);
            }
            case "Sec": {
                return getRainbowSec(0, 0, 1.0, 1, 1.0, 0, false);
            }
            case "CoTan": {
                return getRainbowCoTan(0, 0, 1.0, 1, 1.0, 0, false);
            }
            case "CoSec": {
                return getRainbowCoSec(0, 0, 1.0, 1, 1.0, 0, false);
            }
            default: {
                return getRainbowColor(0, 0, 0, false);
            }
        }
    }
    
    public static GSColor getRainbowColor(final int incr, final int multiply, final int start, final boolean stop) {
        return GSColor.fromHSB(((stop ? start : System.currentTimeMillis()) + incr * multiply) % 11520L / 11520.0f * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowColor(final double incr) {
        return GSColor.fromHSB((float)(incr % 11520.0 / 11520.0) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowSin(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin, final int start, final boolean stop) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.sin(((stop ? start : System.currentTimeMillis()) + incr / millSin * multiply) % 11520.0 / 11520.0)) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowTan(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin, final int start, final boolean stop) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.tan(((stop ? start : System.currentTimeMillis()) + incr / millSin * multiply % 11520.0) / 11520.0)) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowSec(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin, final int start, final boolean stop) {
        return GSColor.fromHSB((float)(height * multiplyHeight * (1.0 / Math.sin(((stop ? start : System.currentTimeMillis()) + (float)incr / millSin * multiply) % 11520.0 / 11520.0))) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowCoSec(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin, final int start, final boolean stop) {
        return GSColor.fromHSB((float)(height * multiplyHeight * (1.0 / Math.cos(((stop ? start : System.currentTimeMillis()) + incr / millSin * multiply) % 11520.0 / 11520.0))) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowCoTan(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin, final int start, final boolean stop) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.tan(((stop ? start : System.currentTimeMillis()) + incr / millSin * multiply) % 11520.0 / 11520.0)) * ModuleManager.getModule(ColorMain.class).rainbowSpeed.getValue().floatValue(), 1.0f, 1.0f);
    }
    
    public void setValue(final GSColor value) {
        super.setValue(new GSColor(value));
    }
    
    public GSColor getColor() {
        return super.getValue();
    }
    
    public boolean getRainbow() {
        return this.rainbow;
    }
    
    public void setRainbow(final boolean rainbow) {
        this.rainbow = rainbow;
    }
    
    public boolean rainbowEnabled() {
        return this.rainbowEnabled;
    }
    
    public boolean alphaEnabled() {
        return this.alphaEnabled;
    }
    
    public long toLong() {
        long temp = this.getColor().getRGB() & 0xFFFFFF;
        if (this.rainbowEnabled) {
            temp += (this.rainbow ? 1 : 0) << 24;
        }
        if (this.alphaEnabled) {
            temp += (long)this.getColor().getAlpha() << 32;
        }
        return temp;
    }
    
    public void fromLong(final long number) {
        if (this.rainbowEnabled) {
            this.rainbow = ((number & 0x1000000L) != 0x0L);
        }
        else {
            this.rainbow = false;
        }
        this.setValue(new GSColor((int)(number & 0xFFFFFFL)));
        if (this.alphaEnabled) {
            this.setValue(new GSColor(this.getColor(), (int)((number & 0xFF00000000L) >> 32)));
        }
    }
}
