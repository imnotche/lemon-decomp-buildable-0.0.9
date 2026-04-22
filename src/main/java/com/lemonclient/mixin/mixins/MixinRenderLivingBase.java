package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.NewRenderEntityEvent;
import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    @Shadow
    protected ModelBase mainModel;
    protected final Minecraft mc = Minecraft.getMinecraft();
    private boolean isClustered;

    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
    }

    protected MixinRenderLivingBase() {
        super(null);
    }

    @Inject(method={"renderModel"}, at={@At(value="HEAD")}, cancellable=true)
    void doRender(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        NewRenderEntityEvent event = new NewRenderEntityEvent(this.mainModel, (Entity)entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (!this.bindEntityTexture((T) entityIn)) {
            return;
        }
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.noCluster.getValue()).booleanValue() && this.mc.player.getDistance(entityIn) < 1.0f && entityIn != this.mc.player) {
            GlStateManager.enableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
                ci.cancel();
            }
        } else {
            this.isClustered = false;
        }
        RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head((Entity)entityIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
        GlStateManager.enableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        LemonClient.EVENT_BUS.post(event);
        GlStateManager.disableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"renderModel"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
        if (!this.bindEntityTexture((T) entitylivingbaseIn)) {
            return;
        }
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.noCluster.getValue()).booleanValue() && this.mc.player.getDistance(entitylivingbaseIn) < 1.0f && entitylivingbaseIn != this.mc.player) {
            GlStateManager.enableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
                callbackInfo.cancel();
            }
        } else {
            this.isClustered = false;
        }
        RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"renderModel"}, at={@At(value="RETURN")}, cancellable=true)
    protected void renderModelReturn(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
        RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        LemonClient.EVENT_BUS.post(renderEntityReturnEvent);
        if (!renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"renderLayers"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo callbackInfo) {
        if (this.isClustered && !ModuleManager.getModule(NoRender.class).getNoClusterRender()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=6))
    protected void glTexEnvi0(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi((int)target, (int)parameterName, (int)parameter);
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=7))
    protected void glTexEnvi1(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi((int)target, (int)parameterName, (int)parameter);
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=8))
    protected void glTexEnvi2(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi((int)target, (int)parameterName, (int)parameter);
        }
    }
}
