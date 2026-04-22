// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.util.Iterator;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Commands", syntax = "commands", alias = { "commands", "cmd", "command", "commandlist", "help" })
public class CmdListCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        for (final Command command2 : CommandManager.getCommands()) {
            MessageBus.sendMessage(command2.getName() + ": \"" + command2.getSyntax() + "\"!", true);
        }
    }
}
