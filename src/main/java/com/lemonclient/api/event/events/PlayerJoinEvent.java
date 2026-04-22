// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import com.lemonclient.api.event.LemonClientEvent;

public class PlayerJoinEvent extends LemonClientEvent
{
    private final String name;
    
    public PlayerJoinEvent(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
