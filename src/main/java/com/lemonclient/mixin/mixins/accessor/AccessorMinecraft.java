// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.util.Timer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Minecraft.class })
public interface AccessorMinecraft
{
    @Accessor("timer")
    Timer getTimer();
    
    @Accessor("rightClickDelayTimer")
    int getRightClickDelayTimer();
    
    @Accessor("rightClickDelayTimer")
    void setRightClickDelayTimer(final int p0);
}
