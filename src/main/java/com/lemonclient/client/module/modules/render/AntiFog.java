// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import java.util.Arrays;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiFog", category = Category.Render)
public class AntiFog extends Module
{
    public static String type;
    ModeSetting mode;
    
    public AntiFog() {
        this.mode = this.registerMode("Mode", Arrays.asList("NoFog", "Air"), "NoFog");
    }
    
    @Override
    public void onUpdate() {
        AntiFog.type = this.mode.getValue();
    }
}
