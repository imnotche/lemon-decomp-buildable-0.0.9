// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Render.class })
public class MixinRender
{
    @Inject(method = { "renderLivingLabel" }, at = { @At("HEAD") }, cancellable = true)
    private void renderLivingLabel(final Entity entityIn, final String str, final double x, final double y, final double z, final int maxDistance, final CallbackInfo ci) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.nameTag.getValue()) {
            ci.cancel();
        }
    }
}
