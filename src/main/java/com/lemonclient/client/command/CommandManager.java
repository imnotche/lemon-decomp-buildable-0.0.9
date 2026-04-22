// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.client.command.commands.ToggleCommand;
import com.lemonclient.client.command.commands.SetCommand;
import com.lemonclient.client.command.commands.SaveConfigCommand;
import com.lemonclient.client.command.commands.RefreshGUICommand;
import com.lemonclient.client.command.commands.PrefixCommand;
import com.lemonclient.client.command.commands.OpenFolderCommand;
import com.lemonclient.client.command.commands.MsgsCommand;
import com.lemonclient.client.command.commands.ModulesCommand;
import com.lemonclient.client.command.commands.LoadConfigCommand;
import com.lemonclient.client.command.commands.LoadCapeCommand;
import com.lemonclient.client.command.commands.IgnoreCommand;
import com.lemonclient.client.command.commands.FriendCommand;
import com.lemonclient.client.command.commands.FontCommand;
import com.lemonclient.client.command.commands.FixHUDCommand;
import com.lemonclient.client.command.commands.FixGUICommand;
import com.lemonclient.client.command.commands.EnemyCommand;
import com.lemonclient.client.command.commands.DrawnCommand;
import com.lemonclient.client.command.commands.DisableAllCommand;
import com.lemonclient.client.command.commands.CoordsCommand;
import com.lemonclient.client.command.commands.CmdListCommand;
import com.lemonclient.client.command.commands.BindCommand;
import com.lemonclient.client.command.commands.BackupConfigCommand;
import com.lemonclient.client.command.commands.AutoGearCommand;
import java.util.ArrayList;

public class CommandManager
{
    private static String commandPrefix;
    public static final ArrayList<Command> commands;
    public static boolean isValidCommand;
    
    public static void init() {
        addCommand(new AutoGearCommand());
        addCommand(new BackupConfigCommand());
        addCommand(new BindCommand());
        addCommand(new CmdListCommand());
        addCommand(new CoordsCommand());
        addCommand(new DisableAllCommand());
        addCommand(new DrawnCommand());
        addCommand(new EnemyCommand());
        addCommand(new FixGUICommand());
        addCommand(new FixHUDCommand());
        addCommand(new FontCommand());
        addCommand(new FriendCommand());
        addCommand(new IgnoreCommand());
        addCommand(new LoadCapeCommand());
        addCommand(new LoadConfigCommand());
        addCommand(new ModulesCommand());
        addCommand(new MsgsCommand());
        addCommand(new OpenFolderCommand());
        addCommand(new PrefixCommand());
        addCommand(new RefreshGUICommand());
        addCommand(new SaveConfigCommand());
        addCommand(new SetCommand());
        addCommand(new ToggleCommand());
    }
    
    public static void addCommand(final Command command) {
        CommandManager.commands.add(command);
    }
    
    public static ArrayList<Command> getCommands() {
        return CommandManager.commands;
    }
    
    public static String getCommandPrefix() {
        return CommandManager.commandPrefix;
    }
    
    public static void setCommandPrefix(final String prefix) {
        CommandManager.commandPrefix = prefix;
    }
    
    public static void callCommand(final String input, final boolean none) {
        final String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String command1 = split[0];
        final String args = input.substring(command1.length()).trim();
        CommandManager.isValidCommand = false;
        CommandManager.commands.forEach(command -> {
            for (final String string : command.getAlias()) {
                if (string.equalsIgnoreCase(command1)) {
                    CommandManager.isValidCommand = true;
                    try {
                        command.onCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"), none);
                    }
                    catch (final Exception e) {
                        MessageBus.sendCommandMessage(command.getSyntax(), true);
                    }
                }
            }
        });
        if (!CommandManager.isValidCommand) {
            MessageBus.sendCommandMessage("Error! Invalid command!", true);
        }
    }
    
    static {
        CommandManager.commandPrefix = "-";
        commands = new ArrayList<Command>();
        CommandManager.isValidCommand = false;
    }
}
