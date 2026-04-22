// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event;

public interface MultiPhase<T extends LemonClientEvent>
{
    Phase getPhase();
    
    T nextPhase();
}
