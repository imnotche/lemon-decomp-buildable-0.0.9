// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.setting.values;

import com.lemonclient.client.module.Module;
import com.lemonclient.api.setting.Setting;

public class StringSetting extends Setting<String>
{
    private String text;
    
    public StringSetting(final String name, final Module parent, final String text) {
        super(text, name, parent);
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
}
