// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import net.minecraft.entity.item.EntityFallingBlock;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoFallingBlocks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.renderer.culling.ICamera;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityEnderPearl;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.RenderEntityEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderManager.class })
public class MixinRenderManager
{
    @Inject(method = { "renderEntity" }, at = { @At("HEAD") }, cancellable = true)
    public void renderEntityHead(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks, final boolean p_188391_10_, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.TEXTURE);
        LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            final RenderEntityEvent.Head renderEntityEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);
            LemonClient.EVENT_BUS.post(renderEntityEvent);
            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "shouldRender" }, at = { @At("HEAD") }, cancellable = true)
    private void onShouldRender(final Entity entity, final ICamera camera, final double camX, final double camY, final double camZ, final CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isModuleEnabled(NoFallingBlocks.class) && entity instanceof EntityFallingBlock) {
            cir.setReturnValue(false);
        }
    }
    
    @Inject(method = { "renderEntity" }, at = { @At("RETURN") }, cancellable = true)
    public void renderEntityReturn(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks, final boolean p_188391_10_, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.TEXTURE);
        LemonClient.EVENT_BUS.post(renderEntityReturnEvent);
        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            final RenderEntityEvent.Return renderEntityEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.COLOR);
            LemonClient.EVENT_BUS.post(renderEntityEvent);
            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
        if (renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
