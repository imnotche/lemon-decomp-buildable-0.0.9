// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Wrapper
{
    public static EntityPlayerSP getPlayer() {
        final EntityPlayerSP player = Minecraft.getMinecraft().player;
        return player;
    }
    
    public static Minecraft getMinecraft() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft;
    }
    
    public static World getWorld() {
        final World world = Minecraft.getMinecraft().world;
        return world;
    }
}
