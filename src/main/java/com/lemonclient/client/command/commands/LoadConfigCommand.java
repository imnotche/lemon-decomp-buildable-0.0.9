// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.config.LoadConfig;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "LoadConfig", syntax = "loadconfig", alias = { "config load", "loadconfig", "load" })
public class LoadConfigCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        LoadConfig.init();
        MessageBus.sendCommandMessage("Config loaded!", true);
        if (none) {
            MessageBus.sendServerMessage("Config loaded!");
        }
        else {
            MessageBus.sendCommandMessage("Config loaded!", true);
        }
        LemonClient.INSTANCE.gameSenseGUI.refresh();
    }
}
