// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.item.ItemStack;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.TransformSideFirstPersonEvent;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.RenderTweaks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ItemRenderer.class })
public class MixinItemRenderer
{
    @Final
    @Shadow
    public Minecraft mc;
    
    @Inject(method = { "updateEquippedItem" }, at = { @At("RETURN") })
    public void updateEquippedItem(final CallbackInfo ci) {
        final RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && renderTweaks.noAnimation.getValue()) {
            this.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            this.mc.entityRenderer.itemRenderer.itemStackMainHand = this.mc.player.getHeldItemMainhand();
            this.mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
            this.mc.entityRenderer.itemRenderer.itemStackOffHand = this.mc.player.getHeldItemOffhand();
        }
    }
    
    @Inject(method = { "resetEquippedProgress" }, at = { @At("HEAD") }, cancellable = true)
    public void resetEquippedProgress(final CallbackInfo ci) {
        final RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && renderTweaks.noAnimation.getValue()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "transformSideFirstPerson" }, at = { @At("HEAD") })
    public void transformSideFirstPerson(final EnumHandSide hand, final float p_187459_2_, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        LemonClient.EVENT_BUS.post(event);
    }
    
    @Inject(method = { "transformEatFirstPerson" }, at = { @At("HEAD") }, cancellable = true)
    public void transformEatFirstPerson(final float p_187454_1_, final EnumHandSide hand, final ItemStack stack, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        LemonClient.EVENT_BUS.post(event);
        final RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && renderTweaks.noEat.getValue()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "transformFirstPerson" }, at = { @At("HEAD") })
    public void transformFirstPerson(final EnumHandSide hand, final float p_187453_2_, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        LemonClient.EVENT_BUS.post(event);
    }
    
    @Inject(method = { "renderOverlays" }, at = { @At("HEAD") }, cancellable = true)
    public void renderOverlays(final float partialTicks, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noOverlay.getValue()) {
            callbackInfo.cancel();
        }
    }
}
