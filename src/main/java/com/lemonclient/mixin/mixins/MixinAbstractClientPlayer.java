// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import java.util.UUID;
import com.lemonclient.api.util.render.CapeUtil;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.Cape;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ AbstractClientPlayer.class })
public abstract class MixinAbstractClientPlayer
{
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();
    
    @Inject(method = { "getLocationCape" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationCape(final CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final UUID uuid = Objects.requireNonNull(this.getPlayerInfo()).getGameProfile().getId();
        if (ModuleManager.isModuleEnabled(Cape.class) && CapeUtil.hasCape(uuid)) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("lemonclient:cape.png"));
        }
    }
}
