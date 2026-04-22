// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.event.LemonClientEvent;

public class DestroyBlockEvent extends LemonClientEvent
{
    private BlockPos blockPos;
    
    public DestroyBlockEvent(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
