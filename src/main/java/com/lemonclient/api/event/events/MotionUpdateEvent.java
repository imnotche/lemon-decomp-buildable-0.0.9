// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class MotionUpdateEvent extends LemonClientEvent
{
    public int stage;
    
    public MotionUpdateEvent(final int stage) {
        this.stage = stage;
    }
}
