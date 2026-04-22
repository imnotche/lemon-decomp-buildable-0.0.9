// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.event.events.RenderEvent;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.ArrayList;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ChorusViewer", category = Category.Render)
public class ChorusViewer extends Module
{
    ModeSetting render;
    IntegerSetting life;
    DoubleSetting circleRange;
    ColorSetting color;
    BooleanSetting desyncCircle;
    IntegerSetting stepRainbowCircle;
    BooleanSetting increaseHeight;
    DoubleSetting speedIncrease;
    ArrayList<renderClass> toRender;
    @EventHandler
    private final Listener<PacketEvent.Receive> sendListener;
    
    public ChorusViewer() {
        this.render = this.registerMode("Render", Arrays.asList("None", "Rectangle", "Circle"), "None");
        this.life = this.registerInteger("Life", 300, 0, 1000);
        this.circleRange = this.registerDouble("Circle Range", 1.0, 0.0, 3.0);
        this.color = this.registerColor("Color", new GSColor(255, 255, 255, 150), true);
        this.desyncCircle = this.registerBoolean("Desync Circle", false);
        this.stepRainbowCircle = this.registerInteger("Step Rainbow Circle", 1, 1, 100);
        this.increaseHeight = this.registerBoolean("Increase Height", true);
        this.speedIncrease = this.registerDouble("Speed Increase", 0.01, 0.3, 0.001);
        this.toRender = new ArrayList<renderClass>();
        this.sendListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect soundPacket = (SPacketSoundEffect)event.getPacket();
                if (soundPacket.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
                    final ArrayList<renderClass> toRender = this.toRender;
                    new renderClass(new Vec3d(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ()), this.life.getValue(), this.render.getValue(), this.color.getValue(), this.circleRange.getValue(), this.desyncCircle.getValue(), this.stepRainbowCircle.getValue(), this.circleRange.getValue(), this.stepRainbowCircle.getValue(), this.increaseHeight.getValue(), this.speedIncrease.getValue());
                    final renderClass e = null;
                    toRender.add(e);
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (ChorusViewer.mc.world == null || ChorusViewer.mc.player == null) {
            return;
        }
        for (int i = 0; i < this.toRender.size(); ++i) {
            if (this.toRender.get(i).update()) {
                this.toRender.remove(i);
                --i;
            }
        }
        this.toRender.forEach(renderClass::render);
    }
    
    static class renderClass
    {
        final Vec3d center;
        long start;
        final long life;
        final String mode;
        final double circleRange;
        final GSColor color;
        final boolean desyncCircle;
        final int stepRainbowCircle;
        final double range;
        final int desync;
        final boolean increaseHeight;
        final double speedIncrease;
        double nowHeigth;
        boolean up;
        
        public renderClass(final Vec3d center, final long life, final String mode, final GSColor color, final double circleRange, final boolean desyncCircle, final int stepRainbowCircle, final double range, final int desync, final boolean increaseHeight, final double speedIncrease) {
            this.nowHeigth = 0.0;
            this.up = true;
            this.center = center;
            this.increaseHeight = increaseHeight;
            this.speedIncrease = speedIncrease;
            this.range = range;
            this.start = System.currentTimeMillis();
            this.life = life;
            this.mode = mode;
            this.desync = desync;
            this.circleRange = circleRange;
            this.color = color;
            this.desyncCircle = desyncCircle;
            this.stepRainbowCircle = stepRainbowCircle;
        }
        
        boolean update() {
            return System.currentTimeMillis() - this.start > this.life;
        }
        
        void render() {
            final String mode = this.mode;
            switch (mode) {
                case "Rectangle": {
                    RenderUtil.drawBox(new BlockPos(this.center.x, this.center.y, this.center.z), 1.8, this.color, 63);
                    break;
                }
                case "Circle": {
                    double inc = 0.0;
                    if (this.increaseHeight) {
                        this.nowHeigth += this.speedIncrease * (this.up ? 1 : -1);
                        if (this.nowHeigth > 1.8) {
                            this.up = false;
                        }
                        else if (this.nowHeigth < 0.0) {
                            this.up = true;
                        }
                        inc = this.nowHeigth;
                    }
                    if (this.desyncCircle) {
                        RenderUtil.drawCircle((float)this.center.x, (float)(this.center.y + inc), (float)this.center.z, this.range, this.desync, this.color.getAlpha());
                        break;
                    }
                    RenderUtil.drawCircle((float)this.center.x, (float)(this.center.y + inc), (float)this.center.z, this.range, this.color);
                    break;
                }
            }
        }
    }
}
