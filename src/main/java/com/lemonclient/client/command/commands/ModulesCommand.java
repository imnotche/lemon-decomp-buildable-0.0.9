// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.util.Iterator;
import java.util.Collection;
import com.lemonclient.client.command.CommandManager;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.Style;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import net.minecraft.util.text.TextComponentString;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Modules", syntax = "modules (click to toggle)", alias = { "modules", "module", "modulelist", "mod", "mods" })
public class ModulesCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final TextComponentString msg = new TextComponentString("§7Modules: §f ");
        final Collection<Module> modules = ModuleManager.getModules();
        final int size = modules.size();
        int index = 0;
        for (final Module module : modules) {
            msg.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "§7" + ((index == size - 1) ? "" : ", ")).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(module.getCategory().name()))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandManager.getCommandPrefix() + "toggle " + module.getName()))));
            ++index;
        }
        msg.appendSibling(new TextComponentString(ChatFormatting.GRAY + "!"));
        ModulesCommand.mc.ingameGUI.getChatGUI().printChatMessage(msg);
    }
}
