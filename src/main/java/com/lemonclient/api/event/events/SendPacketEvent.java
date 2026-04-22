// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SendPacketEvent extends Event
{
    private final Packet packet;
    
    public SendPacketEvent(final Packet packet) {
        this.packet = packet;
    }
    
    public Packet getPacket() {
        return this.packet;
    }
    
    public static class Receive extends SendPacketEvent
    {
        public Receive(final Packet packet) {
            super(packet);
        }
    }
    
    public static class Send extends SendPacketEvent
    {
        public Send(final Packet packet) {
            super(packet);
        }
    }
}
