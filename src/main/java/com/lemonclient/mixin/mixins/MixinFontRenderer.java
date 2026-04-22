// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.modules.misc.NameProtect;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ FontRenderer.class })
public class MixinFontRenderer
{
    @Redirect(method = { "drawStringWithShadow" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(final FontRenderer fontRenderer, String text, final float x, final float y, final int color, final boolean dropShadow) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (colorMain.highlightSelf.getValue()) {
            text = colorMain.highlight(text);
        }
        if (NameProtect.INSTANCE.isEnabled()) {
            text = NameProtect.INSTANCE.replaceName(text);
        }
        return colorMain.textFont.getValue() ? ((int)FontUtil.drawStringWithShadow(true, text, (float)(int)x, (float)(int)y, new GSColor(color))) : fontRenderer.drawString(text, x, y, color, true);
    }
}
