// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.modules.render.SwingSpeed;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityLivingBase.class })
public class MixinEntityLivingBase
{
    @Inject(method = { "getArmSwingAnimationEnd" }, at = { @At("HEAD") }, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (SwingSpeed.INSTANCE.isEnabled()) {
            info.setReturnValue(SwingSpeed.INSTANCE.speed.getValue());
        }
    }
}
