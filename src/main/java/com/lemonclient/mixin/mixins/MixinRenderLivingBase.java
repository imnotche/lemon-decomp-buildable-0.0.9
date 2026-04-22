// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.RenderEntityEvent;
import net.minecraft.client.renderer.GlStateManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.entity.Entity;
import com.lemonclient.api.event.events.NewRenderEntityEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;

@Mixin({ RenderLivingBase.class })
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T>
{
    @Shadow
    protected ModelBase mainModel;
    protected final Minecraft mc;
    private boolean isClustered;
    
    public MixinRenderLivingBase(final RenderManager renderManagerIn, final ModelBase modelBaseIn, final float shadowSizeIn) {
        super(renderManagerIn);
        this.mc = Minecraft.getMinecraft();
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
    }
    
    protected MixinRenderLivingBase() {
        super(null);
        this.mc = Minecraft.getMinecraft();
    }
    
    @Inject(method = { "renderModel" }, at = { @At("HEAD") }, cancellable = true)
    void doRender(final T entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo ci) {
        final NewRenderEntityEvent event = new NewRenderEntityEvent(this.mainModel, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (!this.bindEntityTexture(entityIn)) {
            return;
        }
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noCluster.getValue() && this.mc.player.getDistance(entityIn) < 1.0f && entityIn != this.mc.player) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
                ci.cancel();
            }
        }
        else {
            this.isClustered = false;
        }
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
        GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        LemonClient.EVENT_BUS.post(event);
        GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "renderModel" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderModel(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
        }
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noCluster.getValue() && this.mc.player.getDistance(entitylivingbaseIn) < 1.0f && entitylivingbaseIn != this.mc.player) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
                callbackInfo.cancel();
            }
        }
        else {
            this.isClustered = false;
        }
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderModel" }, at = { @At("RETURN") }, cancellable = true)
    protected void renderModelReturn(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityReturnEvent);
        if (!renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderLayers" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderLayers(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleIn, final CallbackInfo callbackInfo) {
        if (this.isClustered && !ModuleManager.getModule(NoRender.class).getNoClusterRender()) {
            callbackInfo.cancel();
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 6))
    protected void glTexEnvi0(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 7))
    protected void glTexEnvi1(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 8))
    protected void glTexEnvi2(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
}
