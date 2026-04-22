// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.Block;
import com.lemonclient.api.event.LemonClientEvent;

public class BoundingBoxEvent extends LemonClientEvent
{
    Block block;
    AxisAlignedBB bb;
    Vec3d pos;
    public boolean changed;
    
    public BoundingBoxEvent(final Block block, final Vec3d pos) {
        this.block = block;
        this.pos = pos;
    }
    
    public void setbb(final AxisAlignedBB BoundingBox) {
        this.bb = BoundingBox;
        this.changed = true;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public Vec3d getPos() {
        return this.pos;
    }
    
    public AxisAlignedBB getbb() {
        return this.bb;
    }
}
