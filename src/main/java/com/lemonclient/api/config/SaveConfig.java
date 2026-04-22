// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.config;

import com.lukflug.panelstudio.config.IConfigList;
import com.lemonclient.client.clickgui.GuiConfig;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.api.util.player.social.Ignore;
import com.lemonclient.api.util.player.social.Enemy;
import com.lemonclient.api.util.player.social.Friend;
import com.lemonclient.api.util.player.social.SocialManager;
import com.google.gson.JsonArray;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.command.CommandManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import com.google.gson.GsonBuilder;
import java.util.Iterator;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.io.IOException;

public class SaveConfig
{
    public static final String fileName = "LemonClient/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";
    
    public static void init() {
        try {
            saveConfig();
            saveModules();
            saveEnabledModules();
            saveModuleKeyBinds();
            saveDrawnModules();
            saveToggleMessagesModules();
            saveCommandPrefix();
            saveCustomFont();
            saveFriendsList();
            saveEnemiesList();
            saveIgnoresList();
            saveClickGUIPositions();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void saveConfig() throws IOException {
        final Path path = Paths.get("LemonClient/");
        if (!Files.exists(path)) {
            Files.createDirectories(path, new FileAttribute[0]);
        }
        final Path path2 = Paths.get("LemonClient/Modules/");
        if (!Files.exists(path2)) {
            Files.createDirectories(path2, new FileAttribute[0]);
        }
        final Path path3 = Paths.get("LemonClient/Main/");
        if (!Files.exists(path3)) {
            Files.createDirectories(path3, new FileAttribute[0]);
        }
        final Path path4 = Paths.get("LemonClient/Misc/");
        if (!Files.exists(path4)) {
            Files.createDirectories(path4, new FileAttribute[0]);
        }
    }
    
    private static void registerFiles(final String location, final String name) throws IOException {
        final Path path = Paths.get("LemonClient/" + location + name + ".json");
        if (Files.exists(path)) {
            final File file = new File("LemonClient/" + location + name + ".json");
            file.delete();
        }
        Files.createFile(path, new FileAttribute[0]);
    }
    
    private static void saveModules() throws IOException {
        for (final Module module : ModuleManager.getModules()) {
            try {
                saveModuleDirect(module);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void saveModuleDirect(final Module module) throws IOException {
        registerFiles("Modules/", module.getName());
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Modules/" + module.getName() + ".json")), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(module.getName()));
        for (final Setting<?> setting : SettingsManager.getSettingsForModule(module)) {
            if (setting instanceof BooleanSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive((Boolean)setting.getValue()));
            }
            else if (setting instanceof IntegerSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive((Number)setting.getValue()));
            }
            else if (setting instanceof DoubleSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive((Number)setting.getValue()));
            }
            else if (setting instanceof ColorSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((ColorSetting)setting).toLong()));
            }
            else if (setting instanceof ModeSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive((String)setting.getValue()));
            }
            else {
                if (!(setting instanceof StringSetting)) {
                    continue;
                }
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((StringSetting)setting).getText()));
            }
        }
        moduleObject.add("Settings", settingObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveEnabledModules() throws IOException {
        registerFiles("Main/", "Toggle");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Toggle.json")), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject enabledObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            enabledObject.add(module.getName(), new JsonPrimitive(Boolean.valueOf(module.isEnabled())));
        }
        moduleObject.add("Modules", enabledObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveModuleKeyBinds() throws IOException {
        registerFiles("Main/", "Bind");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Bind.json")), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject bindObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            bindObject.add(module.getName(), new JsonPrimitive(module.getBind()));
        }
        moduleObject.add("Modules", bindObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveDrawnModules() throws IOException {
        registerFiles("Main/", "Drawn");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Drawn.json")), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject drawnObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            drawnObject.add(module.getName(), new JsonPrimitive(Boolean.valueOf(module.isDrawn())));
        }
        moduleObject.add("Modules", drawnObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveToggleMessagesModules() throws IOException {
        registerFiles("Main/", "ToggleMessages");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/ToggleMessages.json")), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject toggleMessagesObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            toggleMessagesObject.add(module.getName(), new JsonPrimitive(Boolean.valueOf(module.isToggleMsg())));
        }
        moduleObject.add("Modules", toggleMessagesObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveCommandPrefix() throws IOException {
        registerFiles("Main/", "CommandPrefix");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/CommandPrefix.json")), StandardCharsets.UTF_8);
        final JsonObject prefixObject = new JsonObject();
        prefixObject.add("Prefix", new JsonPrimitive(CommandManager.getCommandPrefix()));
        final String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveCustomFont() throws IOException {
        registerFiles("Misc/", "CustomFont");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/CustomFont.json")), StandardCharsets.UTF_8);
        final JsonObject fontObject = new JsonObject();
        fontObject.add("Font Name", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getFontName()));
        fontObject.add("Font Size", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getFontSize()));
        fontObject.add("Anti Alias", new JsonPrimitive(Boolean.valueOf(LemonClient.INSTANCE.cFontRenderer.getAntiAlias())));
        fontObject.add("Fractional Metrics", new JsonPrimitive(Boolean.valueOf(LemonClient.INSTANCE.cFontRenderer.getFractionalMetrics())));
        final String jsonString = gson.toJson(new JsonParser().parse(fontObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveFriendsList() throws IOException {
        registerFiles("Misc/", "Friends");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Friends.json")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray friendArray = new JsonArray();
        for (final Friend friend : SocialManager.getFriends()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("Friends", friendArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveEnemiesList() throws IOException {
        registerFiles("Misc/", "Enemies");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Enemies.json")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray enemyArray = new JsonArray();
        for (final Enemy enemy : SocialManager.getEnemies()) {
            enemyArray.add(enemy.getName());
        }
        mainObject.add("Enemies", enemyArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveIgnoresList() throws IOException {
        registerFiles("Misc/", "Ignores");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Ignores.json")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray ignoreArray = new JsonArray();
        for (final Ignore ignore : SocialManager.getIgnores()) {
            ignoreArray.add(ignore.getName());
        }
        mainObject.add("Ignores", ignoreArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveClickGUIPositions() throws IOException {
        registerFiles("Main/", "ClickGUI");
        LemonClientGUI.gui.saveConfig(new GuiConfig("LemonClient/Main/"));
    }
}
