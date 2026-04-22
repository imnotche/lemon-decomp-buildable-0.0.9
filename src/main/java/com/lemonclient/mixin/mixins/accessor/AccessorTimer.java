// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Timer.class })
public interface AccessorTimer
{
    @Accessor("tickLength")
    float getTickLength();
    
    @Accessor("tickLength")
    void setTickLength(final float p0);
}
