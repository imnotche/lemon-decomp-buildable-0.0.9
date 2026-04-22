// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.util.Iterator;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "DisableAll", syntax = "disableall", alias = { "disableall", "stop" })
public class DisableAllCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        int count = 0;
        for (final Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                module.disable();
                ++count;
            }
        }
        MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
    }
}
