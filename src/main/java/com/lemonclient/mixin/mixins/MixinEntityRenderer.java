// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import com.lemonclient.client.module.modules.render.AntiFog;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.RenderTweaks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.api.util.chat.notification.NotificationsManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityRenderer.class })
public abstract class MixinEntityRenderer
{
    @Inject(method = { "updateCameraAndRender" }, at = { @At("RETURN") })
    public void updateCameraAndRender$Inject$RETURN(final float partialTicks, final long nanoTime, final CallbackInfo ci) {
        NotificationsManager.render();
        NotificationsManager.drawNotifications();
    }
    
    @Redirect(method = { "orientCamera" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(final WorldClient world, final Vec3d start, final Vec3d end) {
        final RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && renderTweaks.viewClip.getValue()) {
            return null;
        }
        return world.rayTraceBlocks(start, end);
    }
    
    @Shadow
    public abstract void disableLightmap();
    
    @Inject(method = { "setupFog" }, at = { @At("HEAD") }, cancellable = true)
    public void setupFog(final int startCoords, final float partialTicks, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled("AntiFog") && AntiFog.type.equals("NoFog")) {
            callbackInfo.cancel();
        }
    }
    
    @Redirect(method = { "setupFog" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpoint(final World worldIn, final Entity entityIn, final float p_186703_2_) {
        if (ModuleManager.isModuleEnabled("AntiFog") && AntiFog.type.equals("Air")) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
    }
    
    @Inject(method = { "hurtCameraEffect" }, at = { @At("HEAD") }, cancellable = true)
    public void hurtCameraEffect(final float ticks, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.hurtCam.getValue()) {
            callbackInfo.cancel();
        }
    }
}
