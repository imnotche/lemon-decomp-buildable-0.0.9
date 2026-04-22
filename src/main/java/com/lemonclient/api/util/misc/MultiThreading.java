// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreading
{
    private static final AtomicInteger threadCounter;
    private static final ExecutorService SERVICE;
    
    public static void runAsync(final Runnable task) {
        MultiThreading.SERVICE.execute(task);
    }
    
    static {
        threadCounter = new AtomicInteger(0);
        SERVICE = Executors.newCachedThreadPool(task -> {
            return new Thread(task, "Lemon Thread " + MultiThreading.threadCounter.getAndIncrement());
        });
    }
}
