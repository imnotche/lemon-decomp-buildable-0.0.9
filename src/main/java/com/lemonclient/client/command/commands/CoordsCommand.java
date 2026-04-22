// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Coords", syntax = "coords [module]", alias = { "coords", "position", "pos" })
public class CoordsCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        if (CoordsCommand.mc.player == null || CoordsCommand.mc.world == null) {
            return;
        }
        final String name = message[0];
        MessageBus.sendServerMessage("/msg " + name + " X:" + (int)CoordsCommand.mc.player.posX + ", Y:" + (int)CoordsCommand.mc.player.posY + ", Z:" + (int)CoordsCommand.mc.player.posZ);
    }
}
