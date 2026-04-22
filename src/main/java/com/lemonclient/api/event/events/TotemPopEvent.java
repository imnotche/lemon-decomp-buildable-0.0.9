// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.entity.Entity;
import com.lemonclient.api.event.LemonClientEvent;

public class TotemPopEvent extends LemonClientEvent
{
    private final Entity entity;
    
    public TotemPopEvent(final Entity entity) {
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
}
