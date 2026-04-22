// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.util.math.AxisAlignedBB;
import com.lemonclient.api.event.LemonClientEvent;

public class StepEvent extends LemonClientEvent
{
    AxisAlignedBB BB;
    
    public StepEvent(final AxisAlignedBB bb) {
        this.BB = bb;
    }
    
    public AxisAlignedBB getBB() {
        return this.BB;
    }
}
