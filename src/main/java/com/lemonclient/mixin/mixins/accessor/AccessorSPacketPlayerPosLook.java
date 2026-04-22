package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={SPacketPlayerPosLook.class})
public interface AccessorSPacketPlayerPosLook {
}
