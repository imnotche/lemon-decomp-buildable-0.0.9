// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ World.class })
public class MixinWorld
{
    @Inject(method = { "checkLightFor" }, at = { @At("HEAD") }, cancellable = true)
    private void updateLightmapHook(final EnumSkyBlock lightType, final BlockPos pos, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noSkylight.getValue() && lightType == EnumSkyBlock.SKY) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }
}
