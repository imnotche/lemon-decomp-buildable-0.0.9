// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import java.util.Iterator;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.block.BlockShulkerBox;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.ArrayList;
import com.lemonclient.api.util.render.GSColor;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ShulkerESP", category = Category.Render)
public class ShulkerESP extends Module
{
    IntegerSetting range;
    ColorSetting color;
    IntegerSetting alpha;
    IntegerSetting outlineAlpha;
    List<BlockPos> renderList;
    
    public ShulkerESP() {
        this.range = this.registerInteger("Range", 24, 0, 256);
        this.color = this.registerColor("Color", new GSColor(255, 255, 255));
        this.alpha = this.registerInteger("Alpha", 75, 0, 255);
        this.outlineAlpha = this.registerInteger("Outline Alpha", 125, 0, 255);
        this.renderList = new ArrayList<BlockPos>();
    }
    
    @Override
    public void onTick() {
        this.renderList = new ArrayList<BlockPos>();
        (this.renderList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)this.range.getValue(), (double)this.range.getValue(), false, false, 0)).removeIf(p -> !(BlockUtil.getBlock(p) instanceof BlockShulkerBox) || ShulkerESP.mc.player.getDistance(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) > this.range.getValue());
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        for (final BlockPos pos : this.renderList) {
            RenderUtil.drawBox(new AxisAlignedBB(pos), false, 1.0, new GSColor(this.color.getValue(), this.alpha.getValue()), 63);
            RenderUtil.drawBoundingBox(new AxisAlignedBB(pos), 1.0, new GSColor(this.color.getValue(), this.outlineAlpha.getValue()), this.outlineAlpha.getValue());
        }
    }
}
