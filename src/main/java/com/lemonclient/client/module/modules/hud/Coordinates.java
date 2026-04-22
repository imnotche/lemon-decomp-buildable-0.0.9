// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.Setting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "Coordinates", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 0)
public class Coordinates extends HUDModule
{
    BooleanSetting showNetherOverworld;
    BooleanSetting thousandsSeparator;
    IntegerSetting decimalPlaces;
    private final String[] coordinateString;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener;
    
    public Coordinates() {
        this.showNetherOverworld = this.registerBoolean("Show Nether", true);
        this.thousandsSeparator = this.registerBoolean("Thousands Separator", true);
        this.decimalPlaces = this.registerInteger("Decimal Places", 1, 0, 5);
        this.coordinateString = new String[] { "", "" };
        this.listener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase == TickEvent.Phase.END) {
                Entity viewEntity = Coordinates.mc.getRenderViewEntity();
                final EntityPlayerSP player = Coordinates.mc.player;
                if (viewEntity == null) {
                    if (player != null) {
                        viewEntity = player;
                    }
                    else {
                        return;
                    }
                }
                final int dimension = viewEntity.dimension;
                this.coordinateString[0] = "XYZ " + this.getFormattedCoords(viewEntity.posX, viewEntity.posY, viewEntity.posZ);
                switch (dimension) {
                    case -1: {
                        this.coordinateString[1] = "Overworld " + this.getFormattedCoords(viewEntity.posX * 8.0, viewEntity.posY, viewEntity.posZ * 8.0);
                        break;
                    }
                    case 0: {
                        this.coordinateString[1] = "Nether " + this.getFormattedCoords(viewEntity.posX / 8.0, viewEntity.posY, viewEntity.posZ / 8.0);
                        break;
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    private String getFormattedCoords(final double x, final double y, final double z) {
        return this.roundOrInt(x) + ", " + this.roundOrInt(y) + ", " + this.roundOrInt(z);
    }
    
    private String roundOrInt(final double input) {
        String separatorFormat;
        if (this.thousandsSeparator.getValue()) {
            separatorFormat = ",";
        }
        else {
            separatorFormat = "";
        }
        return String.format("%" + separatorFormat + "." + this.decimalPlaces.getValue() + "f", input);
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new CoordinateLabel(), 9, 1);
    }
    
    private class CoordinateLabel implements HUDList
    {
        @Override
        public int getSize() {
            final EntityPlayerSP player = Coordinates.mc.player;
            final int dimension = (player != null) ? player.dimension : 1;
            if (Coordinates.this.showNetherOverworld.getValue() && (dimension == -1 || dimension == 0)) {
                return 2;
            }
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return Coordinates.this.coordinateString[index];
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
