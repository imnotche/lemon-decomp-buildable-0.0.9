// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import java.util.function.Supplier;
import com.lemonclient.client.module.Module;
import java.util.List;
import com.lemonclient.api.setting.Setting;

public class ModeSetting extends Setting<String>
{
    private final List<String> modes;
    
    public ModeSetting(final String name, final Module module, final String value, final List<String> modes) {
        super(value, name, module);
        this.modes = modes;
    }
    
    public ModeSetting(final String name, final String configName, final Module module, final String value, final Supplier<Boolean> isVisible, final List<String> modes) {
        super(value, name, configName, module, isVisible);
        this.modes = modes;
    }
    
    public List<String> getModes() {
        return this.modes;
    }
    
    public void increment() {
        int modeIndex = this.modes.indexOf(this.getValue());
        modeIndex = (modeIndex + 1) % this.modes.size();
        this.setValue(this.modes.get(modeIndex));
    }
    
    public void decrement() {
        int modeIndex = this.modes.indexOf(this.getValue());
        if (--modeIndex < 0) {
            modeIndex = this.modes.size() - 1;
        }
        this.setValue(this.modes.get(modeIndex));
    }
}
