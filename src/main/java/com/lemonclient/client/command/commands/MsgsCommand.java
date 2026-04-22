// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.client.module.Module;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Msgs", syntax = "msgs [player]", alias = { "msgs", "togglemsgs", "showmsgs", "messages" })
public class MsgsCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }
        if (module.isToggleMsg()) {
            module.setToggleMsg(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: FALSE!", true);
        }
        else {
            module.setToggleMsg(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: TRUE!", true);
        }
    }
}
