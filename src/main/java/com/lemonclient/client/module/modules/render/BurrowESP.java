// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BurrowESP", category = Category.Render)
public class BurrowESP extends Module
{
    BooleanSetting self;
    ColorSetting selfColor;
    BooleanSetting friend;
    ColorSetting friendColor;
    BooleanSetting enemy;
    ColorSetting enemyColor;
    IntegerSetting ufoAlpha;
    IntegerSetting Alpha;
    
    public BurrowESP() {
        this.self = this.registerBoolean("Self", true);
        this.selfColor = this.registerColor("Self Color", new GSColor(0, 255, 0, 50));
        this.friend = this.registerBoolean("Friend", true);
        this.friendColor = this.registerColor("Friend Color", new GSColor(0, 0, 255, 50));
        this.enemy = this.registerBoolean("Enemy", true);
        this.enemyColor = this.registerColor("Enemy Color", new GSColor(255, 0, 0));
        this.ufoAlpha = this.registerInteger("Alpha", 120, 0, 255);
        this.Alpha = this.registerInteger("Outline Alpha", 255, 0, 255);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        for (final Entity entity : BurrowESP.mc.world.playerEntities) {
            final BlockPos pos = EntityUtil.getEntityPos(entity);
            if (BlockUtil.getBlock(pos) != Blocks.AIR) {
                final String name = entity.getName();
                if (entity == BurrowESP.mc.player) {
                    if (!this.self.getValue()) {
                        continue;
                    }
                    RenderUtil.drawBox(pos, 1.0, new GSColor(this.selfColor.getValue(), this.ufoAlpha.getValue()), 63);
                    RenderUtil.drawBoundingBox(pos, 1.0, 1.0f, new GSColor(this.selfColor.getValue(), this.Alpha.getValue()));
                }
                else if (SocialManager.isFriend(name)) {
                    if (!this.friend.getValue()) {
                        continue;
                    }
                    RenderUtil.drawBox(pos, 1.0, new GSColor(this.friendColor.getValue(), this.ufoAlpha.getValue()), 63);
                    RenderUtil.drawBoundingBox(pos, 1.0, 1.0f, new GSColor(this.friendColor.getValue(), this.Alpha.getValue()));
                }
                else {
                    if (!this.enemy.getValue()) {
                        continue;
                    }
                    RenderUtil.drawBox(pos, 1.0, new GSColor(this.enemyColor.getValue(), this.ufoAlpha.getValue()), 63);
                    RenderUtil.drawBoundingBox(pos, 1.0, 1.0f, new GSColor(this.enemyColor.getValue(), this.Alpha.getValue()));
                }
            }
        }
    }
}
