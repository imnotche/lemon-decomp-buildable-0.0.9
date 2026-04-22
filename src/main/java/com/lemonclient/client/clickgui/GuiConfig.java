// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.clickgui;

import java.awt.Dimension;
import com.google.gson.JsonPrimitive;
import java.awt.Point;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.nio.file.Path;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.google.gson.JsonObject;
import com.lukflug.panelstudio.config.IConfigList;

public class GuiConfig implements IConfigList
{
    private final String fileLocation;
    private JsonObject panelObject;
    
    public GuiConfig(final String fileLocation) {
        this.panelObject = null;
        this.fileLocation = fileLocation;
    }
    
    @Override
    public void begin(final boolean loading) {
        if (loading) {
            final Path path = Paths.get(this.fileLocation + "ClickGUI.json");
            if (!Files.exists(path)) {
                return;
            }
            try {
                final InputStream inputStream = Files.newInputStream(path);
                final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
                if (mainObject.get("Panels") == null) {
                    return;
                }
                this.panelObject = mainObject.get("Panels").getAsJsonObject();
                inputStream.close();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        else {
            this.panelObject = new JsonObject();
        }
    }
    
    @Override
    public void end(final boolean loading) {
        if (this.panelObject == null) {
            return;
        }
        if (!loading) {
            try {
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(this.fileLocation + "ClickGUI.json")), StandardCharsets.UTF_8);
                final JsonObject mainObject = new JsonObject();
                mainObject.add("Panels", this.panelObject);
                final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
                fileOutputStreamWriter.write(jsonString);
                fileOutputStreamWriter.close();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        this.panelObject = null;
    }
    
    @Override
    public IPanelConfig addPanel(final String title) {
        if (this.panelObject == null) {
            return null;
        }
        final JsonObject valueObject = new JsonObject();
        this.panelObject.add(title, valueObject);
        return new GSPanelConfig(valueObject);
    }
    
    @Override
    public IPanelConfig getPanel(final String title) {
        if (this.panelObject == null) {
            return null;
        }
        final JsonElement configObject = this.panelObject.get(title);
        if (configObject != null && configObject.isJsonObject()) {
            return new GSPanelConfig(configObject.getAsJsonObject());
        }
        return null;
    }
    
    private static class GSPanelConfig implements IPanelConfig
    {
        private final JsonObject configObject;
        
        public GSPanelConfig(final JsonObject configObject) {
            this.configObject = configObject;
        }
        
        @Override
        public void savePositon(final Point position) {
            this.configObject.add("PosX", new JsonPrimitive(position.x));
            this.configObject.add("PosY", new JsonPrimitive(position.y));
        }
        
        @Override
        public void saveSize(final Dimension size) {
        }
        
        @Override
        public Point loadPosition() {
            final Point point = new Point();
            final JsonElement panelPosXObject = this.configObject.get("PosX");
            if (panelPosXObject == null || !panelPosXObject.isJsonPrimitive()) {
                return null;
            }
            point.x = panelPosXObject.getAsInt();
            final JsonElement panelPosYObject = this.configObject.get("PosY");
            if (panelPosYObject != null && panelPosYObject.isJsonPrimitive()) {
                point.y = panelPosYObject.getAsInt();
                return point;
            }
            return null;
        }
        
        @Override
        public Dimension loadSize() {
            return null;
        }
        
        @Override
        public void saveState(final boolean state) {
            this.configObject.add("State", new JsonPrimitive(Boolean.valueOf(state)));
        }
        
        @Override
        public boolean loadState() {
            final JsonElement panelOpenObject = this.configObject.get("State");
            return panelOpenObject != null && panelOpenObject.isJsonPrimitive() && panelOpenObject.getAsBoolean();
        }
    }
}
