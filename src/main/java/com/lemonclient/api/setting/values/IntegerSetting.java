// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import java.util.function.Supplier;
import com.lemonclient.client.module.Module;
import com.lemonclient.api.setting.Setting;

public class IntegerSetting extends Setting<Integer>
{
    private final int min;
    private final int max;
    
    public IntegerSetting(final String name, final Module module, final int value, final int min, final int max) {
        super(value, name, module);
        this.min = min;
        this.max = max;
    }
    
    public IntegerSetting(final String name, final String configName, final Module module, final Supplier<Boolean> isVisible, final int value, final int min, final int max) {
        super(value, name, configName, module, isVisible);
        this.min = min;
        this.max = max;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public int getMax() {
        return this.max;
    }
}
