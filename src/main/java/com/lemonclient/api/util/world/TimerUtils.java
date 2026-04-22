// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world;

import java.util.Iterator;
import java.lang.reflect.Field;
import com.lemonclient.api.util.misc.Mapping;
import net.minecraft.util.Timer;
import net.minecraft.client.Minecraft;
import java.util.HashMap;

public class TimerUtils
{
    private static int counter;
    private static final HashMap<Integer, Float> multipliers;
    
    public static void setTickLength(final float speed) {
        setTickLength0(resolveTimer(), speed);
    }
    
    public static float getTickLength() {
        return getTickLength0(resolveTimer());
    }
    
    public static void setSpeed(final float speed) {
        setTickLength0(resolveTimer(), 50.0f / speed);
    }
    
    public static float getTimer() {
        return 50.0f / getTickLength0(resolveTimer());
    }
    
    public static void setTimerSpeed(final float speed) {
        try {
            setTickLength0(resolveTimer(), 50.0f / speed);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static Timer resolveTimer() {
        try {
            final Field timerField = findField(Minecraft.class, Mapping.timer, "timer", "field_71428_T");
            return (Timer)timerField.get(Minecraft.getMinecraft());
        }
        catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to resolve Minecraft timer", e);
        }
    }

    private static float getTickLength0(final Timer timer) {
        try {
            return findField(Timer.class, Mapping.tickLength, "tickLength", "field_194149_e").getFloat(timer);
        }
        catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read Timer.tickLength", e);
        }
    }

    private static void setTickLength0(final Timer timer, final float tickLength) {
        try {
            findField(Timer.class, Mapping.tickLength, "tickLength", "field_194149_e").setFloat(timer, tickLength);
        }
        catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to write Timer.tickLength", e);
        }
    }

    private static Field findField(final Class<?> owner, final String... candidates) throws NoSuchFieldException {
        for (final String candidate : candidates) {
            try {
                final Field field = owner.getDeclaredField(candidate);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(owner.getName());
    }
    
    private static float getMultiplier() {
        float multiplier = 1.0f;
        for (final float f : TimerUtils.multipliers.values()) {
            multiplier *= f;
        }
        return multiplier;
    }
    
    public static int push(final float multiplier) {
        TimerUtils.multipliers.put(++TimerUtils.counter, multiplier);
        setSpeed(getMultiplier());
        return TimerUtils.counter;
    }
    
    public static void pop(final int counter) {
        TimerUtils.multipliers.remove(counter);
        setSpeed(getMultiplier());
    }
    
    static {
        multipliers = new HashMap<Integer, Float>();
    }
}
