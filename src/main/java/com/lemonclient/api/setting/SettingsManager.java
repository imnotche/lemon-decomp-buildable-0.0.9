// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting;

import java.util.stream.Collectors;
import java.util.List;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;

public class SettingsManager
{
    private static final ArrayList<Setting<?>> settings;
    
    public static void addSetting(final Setting<?> setting) {
        SettingsManager.settings.add(setting);
    }
    
    public static ArrayList<Setting<?>> getSettings() {
        return SettingsManager.settings;
    }
    
    public static List<Setting<?>> getSettingsForModule(final Module module) {
        return SettingsManager.settings.stream().filter(setting -> setting.getModule().equals(module)).collect(Collectors.toList());
    }
    
    static {
        settings = new ArrayList<Setting<?>>();
    }
}
