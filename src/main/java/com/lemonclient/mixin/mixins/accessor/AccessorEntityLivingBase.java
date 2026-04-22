package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityLivingBase.class})
public interface AccessorEntityLivingBase {
    @Invoker(value="onItemUseFinish")
    public void invokeOnItemUseFinish();
}
