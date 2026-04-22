// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import java.util.function.Supplier;
import com.lemonclient.client.module.Module;
import com.lemonclient.api.setting.Setting;

public class BooleanSetting extends Setting<Boolean>
{
    public BooleanSetting(final String name, final Module module, final boolean value) {
        super(value, name, module);
    }
    
    public BooleanSetting(final String name, final String configName, final Module module, final Supplier<Boolean> isVisible, final boolean value) {
        super(value, name, configName, module, isVisible);
    }
}
