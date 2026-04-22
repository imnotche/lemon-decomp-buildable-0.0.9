// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.font;

import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.render.GSColor;
import net.minecraft.client.Minecraft;

public class FontUtil
{
    private static final Minecraft mc;
    
    public static float drawStringWithShadow(final boolean customFont, final String text, final float x, final float y, final GSColor color) {
        if (customFont) {
            return LemonClient.INSTANCE.cFontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return (float)FontUtil.mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
    }
    
    public static float drawStringWithShadow(final boolean customFont, final String text, final String mark, final float x, final float y, final GSColor color) {
        FontUtil.mc.fontRenderer.drawStringWithShadow(mark, x, y, color.getRGB());
        if (customFont) {
            return LemonClient.INSTANCE.cFontRenderer.drawStringWithShadow(text, x + FontUtil.mc.fontRenderer.getStringWidth(mark), y, color);
        }
        return (float)FontUtil.mc.fontRenderer.drawStringWithShadow(text, x + FontUtil.mc.fontRenderer.getStringWidth(mark), y, color.getRGB());
    }
    
    public static int getStringWidth(final boolean customFont, final String string) {
        if (customFont) {
            return LemonClient.INSTANCE.cFontRenderer.getStringWidth(string);
        }
        return FontUtil.mc.fontRenderer.getStringWidth(string);
    }
    
    public static int getFontHeight(final boolean customFont) {
        if (customFont) {
            return LemonClient.INSTANCE.cFontRenderer.getHeight();
        }
        return FontUtil.mc.fontRenderer.FONT_HEIGHT;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
