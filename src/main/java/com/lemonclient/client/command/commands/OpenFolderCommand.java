// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.io.IOException;
import com.lemonclient.api.util.misc.MessageBus;
import java.io.File;
import java.awt.Desktop;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "OpenFolder", syntax = "openfolder", alias = { "openfolder", "open", "folder" })
public class OpenFolderCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        try {
            Desktop.getDesktop().open(new File("LemonClient/".replace("/", "")));
            MessageBus.sendCommandMessage("Opened config folder!", true);
        }
        catch (final IOException e) {
            MessageBus.sendCommandMessage("Could not open config folder!", true);
            e.printStackTrace();
        }
    }
}
