// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.AutoGear;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.lemonclient.api.util.misc.MessageBus;
import java.util.HashMap;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "AutoGear", syntax = "gear set/save/del/list [name]", alias = { "gear", "gr", "kit" })
public class AutoGearCommand extends Command
{
    private static final String pathSave = "LemonClient/Misc/AutoGear.json";
    private static final HashMap<String, String> errorMessage;
    
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String lowerCase = message[0].toLowerCase();
        switch (lowerCase) {
            case "list": {
                if (message.length == 1) {
                    this.listMessage();
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "set": {
                if (message.length == 2) {
                    this.set(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "save":
            case "add":
            case "create": {
                if (message.length == 2) {
                    this.save(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "del": {
                if (message.length == 2) {
                    this.delete(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            default: {
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
                break;
            }
        }
    }
    
    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        String string = "";
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            for (int lenghtJson = completeJson.entrySet().size(), i = 0; i < lenghtJson; ++i) {
                final String item = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (!item.equals("pointer")) {
                    if (string.equals("")) {
                        string = item;
                    }
                    else {
                        string = string + ", " + item;
                    }
                }
            }
            MessageBus.sendCommandMessage("Kit avaible: " + string, true);
        }
        catch (final IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void delete(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.remove(name);
                if (completeJson.get("pointer").getAsString().equals(name)) {
                    completeJson.addProperty("pointer", "none");
                }
                this.saveFile(completeJson, name, "deleted");
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (final IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void set(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.addProperty("pointer", name);
                this.saveFile(completeJson, name, "selected");
                ModuleManager.getModule(AutoGear.class).onEnable();
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (final IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void save(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                errorMessage("Exist");
                return;
            }
        }
        catch (final IOException e) {
            completeJson.addProperty("pointer", "none");
        }
        final StringBuilder jsonInventory = new StringBuilder();
        for (final ItemStack item : AutoGearCommand.mc.player.inventory.mainInventory) {
            jsonInventory.append(item.getItem().getRegistryName().toString() + item.getMetadata()).append(" ");
        }
        completeJson.addProperty(name, jsonInventory.toString());
        this.saveFile(completeJson, name, "saved");
    }
    
    private void saveFile(final JsonObject completeJson, final String name, final String operation) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter("LemonClient/Misc/AutoGear.json"));
            bw.write(completeJson.toString());
            bw.close();
            MessageBus.printDebug("Kit " + name + " " + operation, false);
        }
        catch (final IOException e) {
            errorMessage("Saving");
        }
    }
    
    private static void errorMessage(final String e) {
        MessageBus.printDebug("Error: " + AutoGearCommand.errorMessage.get(e), true);
    }
    
    public static String getCurrentSet() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none")) {
                return completeJson.get("pointer").getAsString();
            }
        }
        catch (final IOException ex) {}
        errorMessage("NoEx");
        return "";
    }
    
    public static String getInventoryKit(final String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse(new FileReader("LemonClient/Misc/AutoGear.json")).getAsJsonObject();
            return completeJson.get(kit).getAsString();
        }
        catch (final IOException ex) {
            errorMessage("NoEx");
            return "";
        }
    }
    
    static {
        errorMessage = new HashMap<String, String>() {
            {
                this.put("NoPar", "Not enough parameters");
                this.put("Exist", "This kit arleady exist");
                this.put("Saving", "Error saving the file");
                this.put("NoEx", "Kit not found");
            }
        };
    }
}
