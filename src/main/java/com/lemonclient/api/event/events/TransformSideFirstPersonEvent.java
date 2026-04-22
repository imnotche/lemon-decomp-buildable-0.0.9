// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.util.EnumHandSide;
import com.lemonclient.api.event.LemonClientEvent;

public class TransformSideFirstPersonEvent extends LemonClientEvent
{
    private final EnumHandSide enumHandSide;
    
    public TransformSideFirstPersonEvent(final EnumHandSide enumHandSide) {
        this.enumHandSide = enumHandSide;
    }
    
    public EnumHandSide getEnumHandSide() {
        return this.enumHandSide;
    }
}
