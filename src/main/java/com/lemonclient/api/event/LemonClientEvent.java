// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event;

import net.minecraft.client.Minecraft;
import me.zero.alpine.event.type.Cancellable;

public class LemonClientEvent extends Cancellable
{
    private final Era era;
    private final float partialTicks;
    
    public LemonClientEvent() {
        this.era = Era.PRE;
        this.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }
    
    public Era getEra() {
        return this.era;
    }
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
    
    public enum Era
    {
        PRE, 
        PERI, 
        POST
    }
}
