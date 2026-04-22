// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

import net.minecraft.client.Minecraft;

public class Interpolation
{
    public static Minecraft mc;
    
    public static double getRenderPosX() {
        return Interpolation.mc.getRenderManager().renderPosX;
    }
    
    public static double getRenderPosY() {
        return Interpolation.mc.getRenderManager().renderPosY;
    }
    
    public static double getRenderPosZ() {
        return Interpolation.mc.getRenderManager().renderPosZ;
    }
    
    static {
        Interpolation.mc = Minecraft.getMinecraft();
    }
}
