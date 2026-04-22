// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.Gui;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.ChatModifier;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiNewChat.class })
public abstract class MixinGuiNewChat
{
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectBackgroundClean(final int left, final int top, final int right, final int bottom, final int color) {
        final ChatModifier chatModifier = ModuleManager.getModule(ChatModifier.class);
        if (!chatModifier.isEnabled() || !chatModifier.clearBkg.getValue()) {
            Gui.drawRect(left, top, right, bottom, color);
        }
    }
}
