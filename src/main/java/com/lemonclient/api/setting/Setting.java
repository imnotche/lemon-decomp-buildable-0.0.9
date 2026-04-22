// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting;

import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.lemonclient.client.module.Module;

public abstract class Setting<T>
{
    private T value;
    private final String name;
    private final String configName;
    private final Module module;
    private Supplier<Boolean> isVisible;
    private final List<Setting<?>> subSettings;
    
    public Setting(final T value, final String name, final String configName, final Module module, final Supplier<Boolean> isVisible) {
        this.subSettings = new ArrayList<Setting<?>>();
        this.value = value;
        this.name = name;
        this.configName = configName;
        this.module = module;
        this.isVisible = isVisible;
    }
    
    public void setVisible(final Supplier<Boolean> vis) {
        this.isVisible = vis;
    }
    
    public Setting(final T value, final String name, final Module module) {
        this(value, name, name.replace(" ", ""), module, () -> true);
    }
    
    public T getValue() {
        return this.value;
    }
    
    @SuppressWarnings("unchecked")
    public void setValue(final Object value) {
        this.value = (T)value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getConfigName() {
        return this.configName;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    public boolean isVisible() {
        return this.isVisible.get();
    }
    
    public Stream<Setting<?>> getSubSettings() {
        return this.subSettings.stream();
    }
    
    public void addSubSetting(final Setting<?> setting) {
        this.subSettings.add(setting);
    }
}
