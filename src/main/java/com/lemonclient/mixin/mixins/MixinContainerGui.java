// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.lemonclient.api.util.misc.MapPeek;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.GuiScreen;

@Mixin({ GuiContainer.class })
public class MixinContainerGui extends GuiScreen
{
    MapPeek peek;
    
    public MixinContainerGui() {
        this.peek = new MapPeek();
    }
    
    @Inject(method = { "drawScreen(IIF)V" }, at = { @At("RETURN") })
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo info) {
        try {
            this.peek.draw(mouseX, mouseY, (GuiContainer)this.mc.currentScreen);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
