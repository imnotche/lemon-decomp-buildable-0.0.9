// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world;

import java.util.Iterator;
import java.lang.reflect.Field;
import com.lemonclient.api.util.misc.Mapping;
import net.minecraft.util.Timer;
import com.lemonclient.mixin.mixins.accessor.AccessorTimer;
import net.minecraft.client.Minecraft;
import com.lemonclient.mixin.mixins.accessor.AccessorMinecraft;
import java.util.HashMap;

public class TimerUtils
{
    private static int counter;
    private static final HashMap<Integer, Float> multipliers;
    
    public static void setTickLength(final float speed) {
        final Timer timer = ((AccessorMinecraft)Minecraft.getMinecraft()).getTimer();
        ((AccessorTimer)timer).setTickLength(speed);
    }
    
    public static float getTickLength() {
        final Timer timer = ((AccessorMinecraft)Minecraft.getMinecraft()).getTimer();
        return ((AccessorTimer)timer).getTickLength();
    }
    
    public static void setSpeed(final float speed) {
        final Timer timer = ((AccessorMinecraft)Minecraft.getMinecraft()).getTimer();
        ((AccessorTimer)timer).setTickLength(50.0f / speed);
    }
    
    public static float getTimer() {
        final Timer timer = ((AccessorMinecraft)Minecraft.getMinecraft()).getTimer();
        return 50.0f / ((AccessorTimer)timer).getTickLength();
    }
    
    public static void setTimerSpeed(final float speed) {
        try {
            final Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            final Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(Minecraft.getMinecraft()), 50.0f / speed);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
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
