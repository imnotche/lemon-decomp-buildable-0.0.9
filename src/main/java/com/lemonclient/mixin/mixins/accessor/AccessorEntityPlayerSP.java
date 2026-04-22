package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityPlayerSP.class})
public interface AccessorEntityPlayerSP {
    @Accessor(value="handActive")
    public void gsSetHandActive(boolean var1);
}
