// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.manager;

import java.util.ArrayList;
import net.minecraftforge.common.MinecraftForge;
import me.zero.alpine.listener.Listenable;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.TotemPopManager;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.manager.managers.ClientEventManager;
import java.util.List;

public class ManagerLoader
{
    private static final List<Manager> managers;
    
    public static void init() {
        register(ClientEventManager.INSTANCE);
        register(PlayerPacketManager.INSTANCE);
        register(TotemPopManager.INSTANCE);
    }
    
    private static void register(final Manager manager) {
        ManagerLoader.managers.add(manager);
        LemonClient.EVENT_BUS.subscribe(manager);
        MinecraftForge.EVENT_BUS.register(manager);
    }
    
    static {
        managers = new ArrayList<Manager>();
    }
}
