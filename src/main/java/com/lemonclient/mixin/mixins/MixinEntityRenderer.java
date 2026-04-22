package com.lemonclient.mixin.mixins;

import com.lemonclient.api.util.chat.notification.NotificationsManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.AntiFog;
import com.lemonclient.client.module.modules.render.NoRender;
import com.lemonclient.client.module.modules.render.RenderTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer {
    @Inject(method={"updateCameraAndRender"}, at={@At(value="RETURN")})
    public void updateCameraAndRender$Inject$RETURN(float partialTicks, long nanoTime, CallbackInfo ci) {
        NotificationsManager.render();
        NotificationsManager.drawNotifications();
    }

    @Redirect(method={"orientCamera"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(WorldClient world, Vec3d start, Vec3d end) {
        RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && ((Boolean)renderTweaks.viewClip.getValue()).booleanValue()) {
            return null;
        }
        return world.rayTraceBlocks(start, end);
    }

    @Shadow
    public abstract void disableLightmap();

    @Inject(method={"setupFog"}, at={@At(value="HEAD")}, cancellable=true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled("AntiFog") && AntiFog.type.equals("NoFog")) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"setupFog"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpoint(World worldIn, Entity entityIn, float p_186703_2_) {
        if (ModuleManager.isModuleEnabled("AntiFog") && AntiFog.type.equals("Air")) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint((World)worldIn, (Entity)entityIn, (float)p_186703_2_);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffect(float ticks, CallbackInfo callbackInfo) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.hurtCam.getValue()).booleanValue()) {
            callbackInfo.cancel();
        }
    }
}
