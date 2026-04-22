// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.config;

import com.lukflug.panelstudio.config.IConfigList;
import com.lemonclient.client.clickgui.GuiConfig;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.google.gson.JsonArray;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.font.CFontRenderer;
import java.awt.Font;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.command.CommandManager;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import java.io.Reader;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.io.IOException;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;

public class LoadConfig
{
    private static final String fileName = "LemonClient/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";
    
    public static void init() {
        try {
            loadModules();
            loadEnabledModules();
            loadModuleKeybinds();
            loadDrawnModules();
            loadToggleMessageModules();
            loadCommandPrefix();
            loadCustomFont();
            loadFriendsList();
            loadIgnoressList();
            loadEnemiesList();
            loadClickGUIPositions();
        }
        catch (final Exception ex) {}
    }
    
    private static void loadModules() {
        final String moduleLocation = "LemonClient/Modules/";
        for (final Module module : ModuleManager.getModules()) {
            try {
                loadModuleDirect(moduleLocation, module);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void loadModuleDirect(final String moduleLocation, final Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        }
        catch (final IllegalStateException e) {
            return;
        }
        if (moduleObject.get("Module") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (final Setting setting : SettingsManager.getSettingsForModule(module)) {
            final JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject == null || !dataObject.isJsonPrimitive()) {
                    continue;
                }
                if (setting instanceof BooleanSetting) {
                    setting.setValue(dataObject.getAsBoolean());
                }
                else if (setting instanceof IntegerSetting) {
                    setting.setValue(dataObject.getAsInt());
                }
                else if (setting instanceof DoubleSetting) {
                    setting.setValue(dataObject.getAsDouble());
                }
                else if (setting instanceof ColorSetting) {
                    ((ColorSetting)setting).fromLong(dataObject.getAsLong());
                }
                else if (setting instanceof ModeSetting) {
                    setting.setValue(dataObject.getAsString());
                }
                else {
                    if (!(setting instanceof StringSetting)) {
                        continue;
                    }
                    setting.setValue(dataObject.getAsString());
                    ((StringSetting)setting).setText(dataObject.getAsString());
                }
            }
            catch (final NumberFormatException ex) {}
        }
        inputStream.close();
    }
    
    private static void loadEnabledModules() throws IOException {
        final String enabledLocation = "LemonClient/Main/";
        final Path path = Paths.get(enabledLocation + "Toggle.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive() && dataObject.getAsBoolean()) {
                try {
                    module.enable();
                }
                catch (final NullPointerException ex) {}
            }
        }
        inputStream.close();
    }
    
    private static void loadModuleKeybinds() throws IOException {
        final String bindLocation = "LemonClient/Main/";
        final Path path = Paths.get(bindLocation + "Bind.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setBind(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }
    
    private static void loadDrawnModules() throws IOException {
        final String drawnLocation = "LemonClient/Main/";
        final Path path = Paths.get(drawnLocation + "Drawn.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setDrawn(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
    
    private static void loadToggleMessageModules() throws IOException {
        final String toggleMessageLocation = "LemonClient/Main/";
        final Path path = Paths.get(toggleMessageLocation + "ToggleMessages.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject toggleObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = toggleObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setToggleMsg(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
    
    private static void loadCommandPrefix() throws IOException {
        final String prefixLocation = "LemonClient/Main/";
        final Path path = Paths.get(prefixLocation + "CommandPrefix.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Prefix") == null) {
            return;
        }
        final JsonElement prefixObject = mainObject.get("Prefix");
        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            CommandManager.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }
    
    private static void loadCustomFont() throws IOException {
        final String fontLocation = "LemonClient/Misc/";
        final Path path = Paths.get(fontLocation + "CustomFont.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null) {
            return;
        }
        final JsonElement fontNameObject = mainObject.get("Font Name");
        String name = null;
        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }
        final JsonElement fontSizeObject = mainObject.get("Font Size");
        int size = -1;
        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }
        final JsonElement antiAliasObject = mainObject.get("Anti Alias");
        boolean alias = true;
        if (antiAliasObject != null && antiAliasObject.isJsonPrimitive()) {
            alias = antiAliasObject.getAsBoolean();
        }
        final JsonElement MetricsObject = mainObject.get("Fractional Metrics");
        boolean metrics = false;
        if (MetricsObject != null && MetricsObject.isJsonPrimitive()) {
            metrics = MetricsObject.getAsBoolean();
        }
        if (name != null && size != -1) {
            (LemonClient.INSTANCE.cFontRenderer = new CFontRenderer(new Font(name, 0, size), false, true)).setFont(new Font(name, 0, size));
            LemonClient.INSTANCE.cFontRenderer.setAntiAlias(alias);
            LemonClient.INSTANCE.cFontRenderer.setFractionalMetrics(metrics);
            LemonClient.INSTANCE.cFontRenderer.setFontName(name);
            LemonClient.INSTANCE.cFontRenderer.setFontSize(size);
        }
        inputStream.close();
    }
    
    private static void loadFriendsList() throws IOException {
        final String friendLocation = "LemonClient/Misc/";
        final Path path = Paths.get(friendLocation + "Friends.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Friends") == null) {
            return;
        }
        final JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addFriend(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadIgnoressList() throws IOException {
        final String friendLocation = "LemonClient/Misc/";
        final Path path = Paths.get(friendLocation + "Ignores.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Ignores") == null) {
            return;
        }
        final JsonArray friendObject = mainObject.get("Ignores").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addIgnore(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadEnemiesList() throws IOException {
        final String enemyLocation = "LemonClient/Misc/";
        final Path path = Paths.get(enemyLocation + "Enemies.json");
        if (!Files.exists(path)) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Enemies") == null) {
            return;
        }
        final JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();
        enemyObject.forEach(object -> SocialManager.addEnemy(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadClickGUIPositions() {
        LemonClientGUI.gui.loadConfig(new GuiConfig("LemonClient/Main/"));
    }
}
