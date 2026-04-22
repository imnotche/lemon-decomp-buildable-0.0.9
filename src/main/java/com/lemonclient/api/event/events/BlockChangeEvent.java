// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.event.LemonClientEvent;

public class BlockChangeEvent extends LemonClientEvent
{
    private final BlockPos position;
    private final Block block;
    
    public BlockChangeEvent(final BlockPos position, final Block block) {
        this.position = position;
        this.block = block;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
}
