// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import java.util.Iterator;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.modules.misc.AntiSpam;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.config.SaveConfig;
import net.minecraft.crash.CrashReport;
import com.lemonclient.mixin.mixins.accessor.AccessorEntityPlayerSP;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.MultiTask;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.util.player.Locks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Unique;
import club.minnced.discord.rpc.DiscordRPC;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Minecraft.class })
public class MixinMinecraft
{
    @Unique
    private final DiscordRPC lemonclient$discordRPC;
    @Shadow
    public EntityPlayerSP player;
    @Shadow
    public PlayerControllerMP playerController;
    @Unique
    private boolean lemonclient$handActive;
    @Unique
    private boolean lemonclient$isHittingBlock;
    
    public MixinMinecraft() {
        this.lemonclient$discordRPC = DiscordRPC.INSTANCE;
        this.lemonclient$handActive = false;
        this.lemonclient$isHittingBlock = false;
    }
    
    @Redirect(method = { "rightClickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClick(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult processRightClickHook(final PlayerControllerMP pCMP, final EntityPlayer player, final World worldIn, final EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            return pCMP.processRightClick(player, worldIn, hand);
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
    
    @Redirect(method = { "rightClickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClickBlock(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult processRightClickBlockHook(final PlayerControllerMP pCMP, final EntityPlayerSP player, final WorldClient worldIn, final BlockPos pos, final EnumFacing direction, final Vec3d vec, final EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            return pCMP.processRightClickBlock(player, worldIn, pos, direction, vec, hand);
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
    
    @Redirect(method = { "rightClickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult interactWithEntityHook(final PlayerControllerMP pCMP, final EntityPlayer player, Entity target, final EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            return pCMP.interactWithEntity(player, target, hand);
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
    
    @Redirect(method = { "rightClickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;interactWithEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult interactWithEntity2Hook(final PlayerControllerMP pCMP, final EntityPlayer player, Entity target, final RayTraceResult ray, final EnumHand hand) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            return pCMP.interactWithEntity(player, target, ray, hand);
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
    
    @Redirect(method = { "clickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"))
    public void attackEntityHook(final PlayerControllerMP playerControllerMP, final EntityPlayer playerIn, final Entity targetEntity) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> playerControllerMP.attackEntity(playerIn, targetEntity));
    }
    
    @Redirect(method = { "runTickMouse" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"))
    public void changeCurrentItemHook(final InventoryPlayer inventoryPlayer, final int direction) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.changeCurrentItem(direction));
    }
    
    @Redirect(method = { "sendClickBlockToController" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"))
    public boolean onPlayerDamageBlockHook(final PlayerControllerMP pCMP, final BlockPos posBlock, final EnumFacing directionFacing) {
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            return pCMP.onPlayerDamageBlock(posBlock, directionFacing);
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("HEAD") })
    public void rightClickMousePre(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.lemonclient$isHittingBlock = this.playerController.getIsHittingBlock();
            this.playerController.isHittingBlock = false;
        }
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("RETURN") })
    public void rightClickMousePost(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.playerController.getIsHittingBlock()) {
            this.playerController.isHittingBlock = this.lemonclient$isHittingBlock;
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("HEAD") })
    public void sendClickBlockToControllerPre(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.lemonclient$handActive = this.player.isHandActive();
            ((AccessorEntityPlayerSP)this.player).gsSetHandActive(false);
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("RETURN") })
    public void sendClickBlockToControllerPost(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.player.isHandActive()) {
            ((AccessorEntityPlayerSP)this.player).gsSetHandActive(this.lemonclient$handActive);
        }
    }
    
    @Redirect(method = { "run" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReportHook(final Minecraft minecraft, final CrashReport crashReport) {
        this.lemonclient$removeIgnore();
        SaveConfig.init();
        this.lemonclient$discordRPC.Discord_Shutdown();
        this.lemonclient$discordRPC.Discord_ClearPresence();
        LemonClient.shutdown();
    }
    
    @Inject(method = { "shutdownMinecraftApplet" }, at = { @At("HEAD") })
    private void stopClient(final CallbackInfo callbackInfo) {
        this.lemonclient$removeIgnore();
        SaveConfig.init();
        this.lemonclient$discordRPC.Discord_Shutdown();
        this.lemonclient$discordRPC.Discord_ClearPresence();
        LemonClient.shutdown();
    }
    
    @Inject(method = { "crashed" }, at = { @At("HEAD") })
    public void crashed(final CrashReport crash, final CallbackInfo callbackInfo) {
        this.lemonclient$removeIgnore();
        SaveConfig.init();
        this.lemonclient$discordRPC.Discord_Shutdown();
        this.lemonclient$discordRPC.Discord_ClearPresence();
        LemonClient.shutdown();
    }
    
    @Inject(method = { "shutdown" }, at = { @At("HEAD") })
    public void shutdown(final CallbackInfo callbackInfo) {
        this.lemonclient$removeIgnore();
        SaveConfig.init();
        this.lemonclient$discordRPC.Discord_Shutdown();
        this.lemonclient$discordRPC.Discord_ClearPresence();
        LemonClient.shutdown();
    }
    
    @Unique
    public void lemonclient$removeIgnore() {
        final AntiSpam antiSpam = ModuleManager.getModule(AntiSpam.class);
        for (final String name : antiSpam.ignoredList) {
            SocialManager.delIgnore(name);
        }
    }
}
