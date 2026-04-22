// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.manager;

import net.minecraft.profiler.Profiler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;
import me.zero.alpine.listener.Listenable;

public interface Manager extends Listenable
{
    default Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
    
    default EntityPlayerSP getPlayer() {
        return this.getMinecraft().player;
    }
    
    default WorldClient getWorld() {
        return this.getMinecraft().world;
    }
    
    default Profiler getProfiler() {
        return this.getMinecraft().profiler;
    }
}
