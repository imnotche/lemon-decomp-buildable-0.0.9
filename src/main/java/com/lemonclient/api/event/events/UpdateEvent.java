// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateEvent extends Event
{
    private final Stage stage;
    
    public UpdateEvent(final Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return this.stage;
    }
    
    public enum Stage
    {
        PRE, 
        POST
    }
}
