// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.Minecraft;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.movement.PlayerTweaks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.util.MovementInput;

@Mixin(value = { MovementInputFromOptions.class }, priority = 10000)
public abstract class MixinMovementInputFromOptions extends MovementInput
{
    @Redirect(method = { "updatePlayerMoveState" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean isKeyPressed(final KeyBinding keyBinding) {
        final int keyCode = keyBinding.getKeyCode();
        if (keyCode > 0 && keyCode < 256) {
            final PlayerTweaks playerTweaks = ModuleManager.getModule(PlayerTweaks.class);
            if (playerTweaks.isEnabled() && playerTweaks.guiMove.getValue() && Minecraft.getMinecraft().currentScreen != null && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
                return Keyboard.isKeyDown(keyCode);
            }
        }
        return keyBinding.isKeyDown();
    }
}
