// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.config.SaveConfig;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "SaveConfig", syntax = "saveconfig", alias = { "config save", "saveconfig", "save" })
public class SaveConfigCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        SaveConfig.init();
        MessageBus.sendCommandMessage("Config saved!", true);
    }
}
