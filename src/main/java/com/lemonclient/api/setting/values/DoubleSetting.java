// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import java.util.function.Supplier;
import com.lemonclient.client.module.Module;
import com.lemonclient.api.setting.Setting;

public class DoubleSetting extends Setting<Double>
{
    private final double min;
    private final double max;
    
    public DoubleSetting(final String name, final Module module, final double value, final double min, final double max) {
        super(value, name, module);
        this.min = min;
        this.max = max;
    }
    
    public DoubleSetting(final String name, final String configName, final Module module, final Supplier<Boolean> isVisible, final double value, final double min, final double max) {
        super(value, name, configName, module, isVisible);
        this.min = min;
        this.max = max;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
}
