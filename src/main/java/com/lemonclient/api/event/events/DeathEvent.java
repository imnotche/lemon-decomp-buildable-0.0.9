// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.event.LemonClientEvent;

public class DeathEvent extends LemonClientEvent
{
    public EntityPlayer player;
    
    public DeathEvent(final EntityPlayer player) {
        this.player = player;
    }
}
