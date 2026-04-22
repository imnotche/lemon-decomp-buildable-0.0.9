// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.awt.Color;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.util.Iterator;
import com.lemonclient.api.util.world.TimerUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.zero.alpine.listener.Listener;
import java.util.ArrayDeque;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "Speedometer", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 70)
public class Speedometer extends HUDModule
{
    private static final String MPS = "m/s";
    private static final String KMH = "km/h";
    private static final String MPH = "mph";
    ModeSetting speedUnit;
    BooleanSetting averageSpeed;
    IntegerSetting averageSpeedTicks;
    private final ArrayDeque<Double> speedDeque;
    private String speedString;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener;
    
    public Speedometer() {
        this.speedUnit = this.registerMode("Unit", Arrays.asList("m/s", "km/h", "mph"), "km/h");
        this.averageSpeed = this.registerBoolean("Average Speed", true);
        this.averageSpeedTicks = this.registerInteger("Average Time", 20, 5, 100);
        this.speedDeque = new ArrayDeque<Double>();
        this.speedString = "";
        this.listener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase == TickEvent.Phase.END) {
                final EntityPlayerSP player = Speedometer.mc.player;
                if (player != null) {
                    final String unit = this.speedUnit.getValue();
                    double displaySpeed;
                    final double speed = displaySpeed = this.calcSpeed(player, unit);
                    if (this.averageSpeed.getValue()) {
                        if (speed > 0.0 || player.ticksExisted % 4 == 0) {
                            this.speedDeque.add(speed);
                        }
                        else {
                            this.speedDeque.pollFirst();
                        }
                        while (!this.speedDeque.isEmpty() && this.speedDeque.size() > this.averageSpeedTicks.getValue()) {
                            this.speedDeque.poll();
                        }
                        displaySpeed = this.average(this.speedDeque);
                    }
                    this.speedString = String.format("%.2f", displaySpeed) + ' ' + unit;
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    protected void onDisable() {
        this.speedDeque.clear();
        this.speedString = "";
    }
    
    private double calcSpeed(final EntityPlayerSP player, final String unit) {
        final double tps = 1000.0 / TimerUtils.getTickLength();
        final double xDiff = player.posX - player.prevPosX;
        final double zDiff = player.posZ - player.prevPosZ;
        double speed = Math.hypot(xDiff, zDiff) * tps;
        switch (unit) {
            case "km/h": {
                speed *= 3.6;
                break;
            }
            case "mph": {
                speed *= 2.237;
                break;
            }
        }
        return speed;
    }
    
    private double average(final Collection<Double> collection) {
        if (collection.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int size = 0;
        for (final double element : collection) {
            sum += element;
            ++size;
        }
        return sum / size;
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new SpeedLabel(), 9, 1);
    }
    
    private class SpeedLabel implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return Speedometer.this.speedString;
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
