// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.util.Iterator;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "FixHUD", syntax = "fixhud", alias = { "fixhud", "hud", "resethud" })
public class FixHUDCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).resetPosition();
            }
        }
        MessageBus.sendCommandMessage("HUD positions reset!", true);
    }
}
