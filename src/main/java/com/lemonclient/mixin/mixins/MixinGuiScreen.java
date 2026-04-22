// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.item.ItemShulkerBox;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.ShulkerViewer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiScreen.class })
public class MixinGuiScreen
{
    @Inject(method = { "renderToolTip" }, at = { @At("HEAD") }, cancellable = true)
    public void renderToolTip(final ItemStack stack, final int x, final int y, final CallbackInfo callbackInfo) {
        final ShulkerViewer shulkerViewer = ModuleManager.getModule(ShulkerViewer.class);
        if (shulkerViewer.isEnabled() && stack.getItem() instanceof ItemShulkerBox && stack.getTagCompound() != null && stack.getTagCompound().hasKey("BlockEntityTag", 10) && stack.getTagCompound().getCompoundTag("BlockEntityTag").hasKey("Items", 9)) {
            callbackInfo.cancel();
            shulkerViewer.renderShulkerPreview(stack, x + 6, y - 33, 162, 66);
        }
    }
}
