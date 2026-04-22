// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiIngame.class })
public class MixinPlayerOverlay
{
    @Inject(method = { "renderPumpkinOverlay" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderPumpkinOverlayHook(final ScaledResolution scaledRes, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noOverlay.getValue()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderPotionEffects" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderPotionEffectsHook(final ScaledResolution scaledRes, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noOverlay.getValue()) {
            callbackInfo.cancel();
        }
    }
}
