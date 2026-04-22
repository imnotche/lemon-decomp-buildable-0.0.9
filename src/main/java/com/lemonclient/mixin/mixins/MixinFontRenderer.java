package com.lemonclient.mixin.mixins;

import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.modules.misc.NameProtect;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FontRenderer.class})
public class MixinFontRenderer {
    @Redirect(method={"drawStringWithShadow"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (((Boolean)colorMain.highlightSelf.getValue()).booleanValue()) {
            text = colorMain.highlight(text);
        }
        if (NameProtect.INSTANCE.isEnabled()) {
            text = NameProtect.INSTANCE.replaceName(text);
        }
        return (Boolean)colorMain.textFont.getValue() != false ? (int)FontUtil.drawStringWithShadow(true, text, (int)x, (int)y, new GSColor(color)) : fontRenderer.drawString(text, x, y, color, true);
    }
}
