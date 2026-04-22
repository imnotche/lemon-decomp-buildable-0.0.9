// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class RenderEvent extends LemonClientEvent
{
    private final float partialTicks;
    
    public RenderEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
    
    @Override
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
