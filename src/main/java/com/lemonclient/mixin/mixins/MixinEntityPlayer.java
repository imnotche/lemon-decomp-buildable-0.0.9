package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.PlayerJumpEvent;
import com.lemonclient.api.event.events.WaterPushEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.Portal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer {
    @Shadow
    public abstract String getName();

    @ModifyConstant(method={"getPortalCooldown"}, constant={@Constant(intValue=10)})
    private int getPortalCooldownHook(int n) {
        int intValue = n;
        Portal portal = ModuleManager.getModule(Portal.class);
        if (portal.isEnabled() && ((Boolean)portal.fastPortal.getValue()).booleanValue()) {
            intValue = (Integer)portal.cooldown.getValue();
        }
        return intValue;
    }

    @Inject(method={"jump"}, at={@At(value="HEAD")}, cancellable=true)
    public void onJump(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().player.getName() == this.getName()) {
            LemonClient.EVENT_BUS.post(new PlayerJumpEvent());
        }
    }

    @Inject(method={"isPushedByWater"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPushedByWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        WaterPushEvent event = new WaterPushEvent();
        LemonClient.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
