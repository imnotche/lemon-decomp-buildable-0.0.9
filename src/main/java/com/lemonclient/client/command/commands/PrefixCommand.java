// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Prefix", syntax = "prefix value (no letters or numbers)", alias = { "prefix", "setprefix", "cmdprefix", "commandprefix" })
public class PrefixCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String main = message[0].toUpperCase().replaceAll("[a-zA-Z0-9]", null);
        final int size = message[0].length();
        if (size == 1) {
            CommandManager.setCommandPrefix(main);
            MessageBus.sendCommandMessage("Prefix set: \"" + main + "\"!", true);
        }
        else if (size != 1) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
        }
    }
}
