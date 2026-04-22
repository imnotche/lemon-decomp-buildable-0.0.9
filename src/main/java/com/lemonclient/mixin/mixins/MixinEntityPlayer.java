// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.WaterPushEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.api.event.events.PlayerJumpEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.Portal;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityPlayer.class })
public abstract class MixinEntityPlayer
{
    @Shadow
    public abstract String getName();
    
    @ModifyConstant(method = { "getPortalCooldown" }, constant = { @Constant(intValue = 10) })
    private int getPortalCooldownHook(final int n) {
        int intValue = n;
        final Portal portal = ModuleManager.getModule(Portal.class);
        if (portal.isEnabled() && portal.fastPortal.getValue()) {
            intValue = portal.cooldown.getValue();
        }
        return intValue;
    }
    
    @Inject(method = { "jump" }, at = { @At("HEAD") }, cancellable = true)
    public void onJump(final CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().player.getName() == this.getName()) {
            LemonClient.EVENT_BUS.post(new PlayerJumpEvent());
        }
    }
    
    @Inject(method = { "isPushedByWater" }, at = { @At("HEAD") }, cancellable = true)
    private void onPushedByWater(final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final WaterPushEvent event = new WaterPushEvent();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
