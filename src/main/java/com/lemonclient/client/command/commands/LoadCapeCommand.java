// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.render.CapeUtil;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "LoadCape", syntax = "loadcape", alias = { "loadcape", "capeload", "reloadcape", "capereload" })
public class LoadCapeCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        MessageBus.sendCommandMessage("Reloaded Cape UUID", true);
        CapeUtil.init();
    }
}
