// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.modules.render.NoRender;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.Nametags;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderPlayer.class })
public abstract class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName*" }, at = { @At("HEAD") }, cancellable = true)
    private void renderLivingLabel(final AbstractClientPlayer entity, final double x, final double y, final double z, final String name, final double distanceSq, final CallbackInfo callbackInfo) {
        if (entity.getName().length() == 0) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(Nametags.class)) {
            callbackInfo.cancel();
        }
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.nameTag.getValue()) {
            callbackInfo.cancel();
        }
    }
}
