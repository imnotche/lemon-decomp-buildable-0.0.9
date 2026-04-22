// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.util.render.RenderUtil;
import java.util.function.BiConsumer;
import com.lemonclient.api.event.events.RenderEvent;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.google.common.collect.Sets;
import java.util.Arrays;
import com.lemonclient.api.util.render.GSColor;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.concurrent.ConcurrentHashMap;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "HoleESP", category = Category.Render)
public class HoleESP extends Module
{
    public IntegerSetting range;
    IntegerSetting Yrange;
    BooleanSetting single;
    BooleanSetting Double;
    BooleanSetting fourBlocks;
    BooleanSetting custom;
    ModeSetting type;
    ModeSetting mode;
    BooleanSetting hideOwn;
    BooleanSetting flatOwn;
    BooleanSetting fov;
    DoubleSetting slabHeight;
    DoubleSetting outslabHeight;
    IntegerSetting width;
    ColorSetting bedrockColor;
    ColorSetting obsidianColor;
    ColorSetting twobedrockColor;
    ColorSetting twoobsidianColor;
    ColorSetting fourColor;
    ColorSetting customColor;
    IntegerSetting alpha;
    IntegerSetting ufoAlpha;
    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;
    
    public HoleESP() {
        this.range = this.registerInteger("Range", 5, 1, 20);
        this.Yrange = this.registerInteger("Y Range", 5, 1, 20);
        this.single = this.registerBoolean("1x1", true);
        this.Double = this.registerBoolean("2x1", true);
        this.fourBlocks = this.registerBoolean("2x2", true);
        this.custom = this.registerBoolean("Custom", true);
        this.type = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.mode = this.registerMode("Mode", Arrays.asList("Air", "Ground", "Flat", "Slab", "Double"), "Air");
        this.hideOwn = this.registerBoolean("Hide Own", false);
        this.flatOwn = this.registerBoolean("Flat Own", false);
        this.fov = this.registerBoolean("In Fov", false);
        this.slabHeight = this.registerDouble("Slab Height", 0.5, 0.0, 2.0);
        this.outslabHeight = this.registerDouble("Outline Height", 0.5, 0.0, 2.0);
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.bedrockColor = this.registerColor("Bedrock Color", new GSColor(0, 255, 0));
        this.obsidianColor = this.registerColor("Obsidian Color", new GSColor(255, 0, 0));
        this.twobedrockColor = this.registerColor("2x1 Bedrock Color", new GSColor(0, 255, 0));
        this.twoobsidianColor = this.registerColor("2x1 Obsidian Color", new GSColor(255, 0, 0));
        this.fourColor = this.registerColor("2x2 Color", new GSColor(255, 0, 0));
        this.customColor = this.registerColor("Custom Color", new GSColor(0, 0, 255));
        this.alpha = this.registerInteger("Alpha", 50, 0, 255);
        this.ufoAlpha = this.registerInteger("UFOAlpha", 255, 0, 255);
    }
    
    @Override
    public void onUpdate() {
        if (HoleESP.mc.player == null || HoleESP.mc.world == null) {
            return;
        }
        if (this.holes == null) {
            this.holes = new ConcurrentHashMap<AxisAlignedBB, GSColor>();
        }
        else {
            this.holes.clear();
        }
        final HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        final List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)this.range.getValue(), (double)this.Yrange.getValue(), false, false, 0);
        final Iterator<BlockPos> iterator = blockPosList.iterator();
        BlockPos pos = null;
        while (iterator.hasNext()) {
            pos = iterator.next();
            if (this.fov.getValue() && !RotationUtil.isInFov(pos)) {
                continue;
            }
            if (!HoleESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (HoleESP.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            possibleHoles.add(pos);
        }
        possibleHoles.forEach(holePos -> {
            final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(holePos, false, false, true);
            final HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                final HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                final AxisAlignedBB centreBlocks = holeInfo.getCentre();
                if (centreBlocks != null) {
                    if (this.fourBlocks.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                        final GSColor colour = new GSColor(this.fourColor.getValue(), 255);
                        this.holes.put(centreBlocks, colour);
                    }
                    else if (this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                        final GSColor colour2 = new GSColor(this.customColor.getValue(), 255);
                        this.holes.put(centreBlocks, colour2);
                    }
                    else if (this.Double.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                        GSColor colour3;
                        if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                            colour3 = new GSColor(this.twobedrockColor.getValue(), 255);
                        }
                        else {
                            colour3 = new GSColor(this.twoobsidianColor.getValue(), 255);
                        }
                        this.holes.put(centreBlocks, colour3);
                    }
                    else if (this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                        GSColor colour4;
                        if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                            colour4 = new GSColor(this.bedrockColor.getValue(), 255);
                        }
                        else {
                            colour4 = new GSColor(this.obsidianColor.getValue(), 255);
                        }
                        this.holes.put(centreBlocks, colour4);
                    }
                }
            }
        });
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (HoleESP.mc.player == null || HoleESP.mc.world == null || this.holes == null || this.holes.isEmpty()) {
            return;
        }
        this.holes.forEach(this::renderHoles);
    }
    
    private void renderHoles(final AxisAlignedBB hole, final GSColor color) {
        final String s = this.type.getValue();
        switch (s) {
            case "Outline": {
                this.renderOutline(hole, color);
                break;
            }
            case "Fill": {
                this.renderFill(hole, color);
                break;
            }
            case "Both": {
                this.renderOutline(hole, color);
                this.renderFill(hole, color);
                break;
            }
        }
    }
    
    private void renderFill(final AxisAlignedBB hole, final GSColor color) {
        final GSColor fillColor = new GSColor(color, this.alpha.getValue());
        final int ufoAlpha = this.ufoAlpha.getValue() * 50 / 255;
        if (this.hideOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
            return;
        }
        final String s = this.mode.getValue();
        switch (s) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 63);
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0.0, -1.0, 0.0), true, 1.0, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), 63);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, false, this.slabHeight.getValue(), fillColor, ufoAlpha, 63);
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1.0), true, 2.0, fillColor, ufoAlpha, 63);
                break;
            }
        }
    }
    
    private void renderOutline(final AxisAlignedBB hole, final GSColor color) {
        final GSColor outlineColor = new GSColor(color, 255);
        if (this.hideOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
            return;
        }
        final String s = this.mode.getValue();
        switch (s) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0.0, -1.0, 0.0), this.width.getValue(), new GSColor(outlineColor, this.ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + this.outslabHeight.getValue()), this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersects(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1.0), this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
        }
    }
}
