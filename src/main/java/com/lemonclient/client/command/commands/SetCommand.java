// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.client.module.Module;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "Set", syntax = "set [module] [setting] value (no color support)", alias = { "set", "setmodule", "changesetting", "setting" })
public class SetCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        final String[] string = { null };
        if (module == null) {
            string[0] = this.getSyntax();
            return;
        }
        SettingsManager.getSettingsForModule(module).stream().filter(setting -> setting.getConfigName().equalsIgnoreCase(message[1])).forEach(setting -> {
            if (setting instanceof BooleanSetting) {
                final BooleanSetting booleanSetting = (BooleanSetting)setting;
                if (message[2].equalsIgnoreCase("true") || message[2].equalsIgnoreCase("false")) {
                    booleanSetting.setValue(Boolean.parseBoolean(message[2]));
                    string[0] = module.getName() + " " + booleanSetting.getConfigName() + " set to: " + booleanSetting.getValue() + "!";
                }
                else {
                    string[0] = this.getSyntax();
                }
            }
            else if (setting instanceof IntegerSetting) {
                final IntegerSetting integerSetting = (IntegerSetting)setting;
                if (Integer.parseInt(message[2]) > integerSetting.getMax()) {
                    integerSetting.setValue(integerSetting.getMax());
                }
                if (Integer.parseInt(message[2]) < integerSetting.getMin()) {
                    integerSetting.setValue(integerSetting.getMin());
                }
                if (Integer.parseInt(message[2]) < integerSetting.getMax() && Integer.parseInt(message[2]) > integerSetting.getMin()) {
                    integerSetting.setValue(Integer.parseInt(message[2]));
                }
                string[0] = module.getName() + " " + integerSetting.getConfigName() + " set to: " + integerSetting.getValue() + "!";
            }
            else if (setting instanceof DoubleSetting) {
                final DoubleSetting doubleSetting = (DoubleSetting)setting;
                if (Double.parseDouble(message[2]) > doubleSetting.getMax()) {
                    doubleSetting.setValue(doubleSetting.getMax());
                }
                if (Double.parseDouble(message[2]) < doubleSetting.getMin()) {
                    doubleSetting.setValue(doubleSetting.getMin());
                }
                if (Double.parseDouble(message[2]) < doubleSetting.getMax() && Double.parseDouble(message[2]) > doubleSetting.getMin()) {
                    doubleSetting.setValue(Double.parseDouble(message[2]));
                }
                string[0] = module.getName() + " " + doubleSetting.getConfigName() + " set to: " + doubleSetting.getValue() + "!";
            }
            else if (setting instanceof ModeSetting) {
                final ModeSetting modeSetting = (ModeSetting)setting;
                if (!modeSetting.getModes().contains(message[2])) {
                    string[0] = this.getSyntax();
                }
                else {
                    modeSetting.setValue(message[2]);
                    string[0] = module.getName() + " " + modeSetting.getConfigName() + " set to: " + modeSetting.getValue() + "!";
                }
            }
            else {
                string[0] = this.getSyntax();
            }
        });
        if (none) {
            MessageBus.sendServerMessage(string[0]);
        }
        else {
            MessageBus.sendCommandMessage(string[0], true);
        }
    }
}
