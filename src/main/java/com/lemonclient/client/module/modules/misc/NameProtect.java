// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "NameProtect", category = Category.Misc)
public class NameProtect extends Module
{
    public static NameProtect INSTANCE;
    StringSetting name;
    
    public NameProtect() {
        this.name = this.registerString("Name", "");
        NameProtect.INSTANCE = this;
    }
    
    public String replaceName(final String string) {
        if (string != null && this.isEnabled()) {
            final String username = NameProtect.mc.getSession().getUsername();
            return string.replace(username, this.name.getText()).replace(username.toLowerCase(), this.name.getText().toLowerCase()).replace(username.toUpperCase(), this.name.getText().toUpperCase());
        }
        return string;
    }
    
    public String getName(final String original) {
        if (this.name.getText().length() > 0) {
            return this.name.getText();
        }
        return original;
    }
}
