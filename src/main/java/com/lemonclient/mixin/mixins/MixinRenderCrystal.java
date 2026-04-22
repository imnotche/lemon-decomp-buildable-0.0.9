// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.NewRenderEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderEnderCrystal.class })
public class MixinRenderCrystal
{
    @Redirect(method = { "doRender" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(final ModelBase modelBase, final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        final NewRenderEntityEvent event = new NewRenderEntityEvent(modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        LemonClient.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            modelBase.render(event.entityIn, 0.0f, event.limbSwingAmount, event.ageInTicks, 0.0f, 0.0f, event.scale);
        }
    }
}
