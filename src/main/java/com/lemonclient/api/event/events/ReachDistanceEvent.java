// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class ReachDistanceEvent extends LemonClientEvent
{
    private float distance;
    
    public ReachDistanceEvent(final float distance) {
        this.distance = distance;
    }
    
    public float getDistance() {
        return this.distance;
    }
    
    public void setDistance(final float distance) {
        this.distance = distance;
    }
}
