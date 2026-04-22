// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import me.zero.alpine.listener.Listener;
import java.util.Collection;

public interface Subscriber
{
    Collection<Listener<?>> getListeners();
}
