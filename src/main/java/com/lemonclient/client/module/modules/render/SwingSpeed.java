// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "SwingSpeed", category = Category.Render)
public class SwingSpeed extends Module
{
    public static SwingSpeed INSTANCE;
    public IntegerSetting speed;
    
    public SwingSpeed() {
        this.speed = this.registerInteger("Speed", 6, 1, 50);
        SwingSpeed.INSTANCE = this;
    }
}
