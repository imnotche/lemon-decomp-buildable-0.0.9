// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.api.event.events.RenderEvent;
import java.util.Iterator;
import java.util.List;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import io.netty.util.internal.ConcurrentSet;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "VoidESP", category = Category.Render)
public class VoidESP extends Module
{
    IntegerSetting renderDistance;
    IntegerSetting activeYValue;
    ModeSetting renderType;
    ModeSetting renderMode;
    IntegerSetting width;
    ColorSetting color;
    private ConcurrentSet<BlockPos> voidHoles;
    
    public VoidESP() {
        this.renderDistance = this.registerInteger("Distance", 10, 1, 40);
        this.activeYValue = this.registerInteger("Activate Y", 20, 0, 256);
        this.renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.renderMode = this.registerMode("Mode", Arrays.asList("Box", "Flat"), "Flat");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(255, 255, 0));
    }
    
    @Override
    public void onUpdate() {
        if (VoidESP.mc.player.dimension == 1) {
            return;
        }
        if (VoidESP.mc.player.getPosition().getY() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles == null) {
            this.voidHoles = (ConcurrentSet<BlockPos>)new ConcurrentSet();
        }
        else {
            this.voidHoles.clear();
        }
        final List<BlockPos> blockPosList = BlockUtil.getCircle(getPlayerPos(), 0, this.renderDistance.getValue(), false);
        for (final BlockPos blockPos : blockPosList) {
            if (VoidESP.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (this.isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            this.voidHoles.add(blockPos);
        }
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (VoidESP.mc.player == null || this.voidHoles == null) {
            return;
        }
        if (VoidESP.mc.player.getPosition().getY() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles.isEmpty()) {
            return;
        }
        this.voidHoles.forEach(blockPos -> {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                this.drawBox(blockPos);
            }
            else {
                this.drawFlat(blockPos);
            }
            this.drawOutline(blockPos, this.width.getValue());
        });
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(VoidESP.mc.player.posX), Math.floor(VoidESP.mc.player.posY), Math.floor(VoidESP.mc.player.posZ));
    }
    
    private boolean isAnyBedrock(final BlockPos origin, final BlockPos[] offset) {
        for (final BlockPos pos : offset) {
            if (VoidESP.mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }
    
    private void drawFlat(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBox(blockPos, 1.0, c, 1);
            }
        }
    }
    
    private void drawBox(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            RenderUtil.drawBox(blockPos, 1.0, c, 63);
        }
    }
    
    private void drawOutline(final BlockPos blockPos, final int width) {
        if (this.renderType.getValue().equalsIgnoreCase("Outline") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                RenderUtil.drawBoundingBox(blockPos, 1.0, (float)width, this.color.getValue());
            }
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBoundingBoxWithSides(blockPos, width, this.color.getValue(), 1);
            }
        }
    }
    
    private static class Offsets
    {
        static final BlockPos[] center;
        
        static {
            center = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
        }
    }
}
