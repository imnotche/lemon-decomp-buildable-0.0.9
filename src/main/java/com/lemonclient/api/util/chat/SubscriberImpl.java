// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import java.util.Collection;
import java.util.ArrayList;
import me.zero.alpine.listener.Listener;
import java.util.List;

public class SubscriberImpl implements Subscriber
{
    protected final List<Listener<?>> listeners;
    
    public SubscriberImpl() {
        this.listeners = new ArrayList<Listener<?>>();
    }
    
    @Override
    public Collection<Listener<?>> getListeners() {
        return this.listeners;
    }
}
