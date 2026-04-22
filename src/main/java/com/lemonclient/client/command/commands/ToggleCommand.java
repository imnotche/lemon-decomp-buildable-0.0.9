// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.client.module.Module;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Toggle", syntax = "toggle [module]", alias = { "toggle", "t", "enable", "disable" })
public class ToggleCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        String string;
        if (module == null) {
            string = this.getSyntax();
        }
        else {
            module.toggle();
            if (module.isEnabled()) {
                string = "Module " + module.getName() + " set to: ENABLED!";
            }
            else {
                string = "Module " + module.getName() + " set to: DISABLED!";
            }
        }
        if (none) {
            MessageBus.sendServerMessage(string);
        }
        else {
            MessageBus.sendCommandMessage(string, true);
        }
    }
}
