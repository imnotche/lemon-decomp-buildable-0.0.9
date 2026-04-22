// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Bind", syntax = "bind [module] key", alias = { "bind", "b", "setbind", "key" })
public class BindCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String main = message[0];
        final String value = message[1].toUpperCase();
        for (final Module module : ModuleManager.getModules()) {
            if (module.getName().equalsIgnoreCase(main)) {
                if (value.equalsIgnoreCase("none")) {
                    module.setBind(0);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else if (value.length() == 1) {
                    final int key = Keyboard.getKeyIndex(value);
                    module.setBind(key);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else {
                    if (value.length() <= 1) {
                        continue;
                    }
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
            }
        }
    }
}
