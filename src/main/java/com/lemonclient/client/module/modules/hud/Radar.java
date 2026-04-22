// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import com.lukflug.panelstudio.base.IInterface;
import net.minecraft.util.EnumFacing;
import java.awt.Rectangle;
import java.awt.Dimension;
import net.minecraft.entity.EntityCreature;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import java.awt.Point;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.Entity;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.player.social.SocialManager;
import java.awt.Color;
import net.minecraft.entity.player.EntityPlayer;
import com.lukflug.panelstudio.theme.ITheme;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "Radar", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 200)
public class Radar extends HUDModule
{
    BooleanSetting renderPlayer;
    BooleanSetting renderMobs;
    ColorSetting playerColor;
    ColorSetting outlineColor;
    ColorSetting fillColor;
    
    public Radar() {
        this.renderPlayer = this.registerBoolean("Player", true);
        this.renderMobs = this.registerBoolean("Mobs", true);
        this.playerColor = this.registerColor("Player Color", new GSColor(0, 0, 255, 255));
        this.outlineColor = this.registerColor("Outline Color", new GSColor(255, 0, 0, 255));
        this.fillColor = this.registerColor("Fill Color", new GSColor(0, 0, 0, 255));
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new RadarComponent(theme);
    }
    
    private Color getPlayerColor(final EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.getName())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(entityPlayer.getName())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(this.playerColor.getValue(), 255);
    }
    
    private Color getEntityColor(final Entity entity) {
        if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            return new GSColor(255, 0, 0, 255);
        }
        if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            return new GSColor(0, 255, 0, 255);
        }
        return new GSColor(255, 165, 0, 255);
    }
    
    private class RadarComponent extends HUDComponent
    {
        private final int maxRange = 50;
        
        public RadarComponent(final ITheme theme) {
            super(new Labeled(Radar.this.getName(), null, () -> true), Radar.this.position, Radar.this.getName());
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (Radar.mc.player != null && Radar.mc.player.ticksExisted >= 10) {
                if (Radar.this.renderPlayer.getValue()) {
                    Radar.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != Radar.mc.player).forEach(entityPlayer -> this.renderEntityPoint(entityPlayer, Radar.this.getPlayerColor(entityPlayer), context));
                }
                if (Radar.this.renderMobs.getValue()) {
                    Radar.mc.world.loadedEntityList.stream().filter(entity -> !(entity instanceof EntityPlayer)).forEach(entity -> {
                        if (entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) {
                            this.renderEntityPoint(entity, Radar.this.getEntityColor(entity), context);
                        }
                    });
                }
                final Color background = new GSColor(Radar.this.fillColor.getValue(), 100);
                context.getInterface().fillRect(context.getRect(), background, background, background, background);
                final Color outline = new GSColor(Radar.this.outlineColor.getValue(), 255);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                final boolean isNorth = this.isFacing(EnumFacing.NORTH);
                final boolean isSouth = this.isFacing(EnumFacing.SOUTH);
                final boolean isEast = this.isFacing(EnumFacing.EAST);
                final boolean isWest = this.isFacing(EnumFacing.WEST);
                final Color selfColor = new Color(255, 255, 255, 255);
                final int distanceToCenter = context.getSize().height / 2;
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 3, context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter + (isEast ? 1 : 0), context.getPos().y + distanceToCenter), isEast ? outline : selfColor, isEast ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + 3), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + (isSouth ? 1 : 0)), isSouth ? outline : selfColor, isSouth ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter - (isWest ? 1 : 0), context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter - 3, context.getPos().y + distanceToCenter), isWest ? outline : selfColor, isWest ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter - (isNorth ? 1 : 0)), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter - 3), isNorth ? outline : selfColor, isNorth ? outline : selfColor);
            }
        }
        
        private boolean isFacing(final EnumFacing enumFacing) {
            return Radar.mc.player.getHorizontalFacing().equals(enumFacing);
        }
        
        private void renderEntityPoint(final Entity entity, final Color color, final Context context) {
            final int distanceX = this.findDistance1D(Radar.mc.player.posX, entity.posX);
            final int distanceY = this.findDistance1D(Radar.mc.player.posZ, entity.posZ);
            final int distanceToCenter = context.getSize().height / 2;
            if (distanceX > 50 || distanceY > 50 || distanceX < -50 || distanceY < -50) {
                return;
            }
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), new Point(context.getPos().x + distanceToCenter - 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), color, color);
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter + 1 + distanceY), new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter - 1 + distanceY), color, color);
        }
        
        private int findDistance1D(final double player, final double entity) {
            double player2 = player;
            double entity2 = entity;
            if (player2 < 0.0) {
                player2 *= -1.0;
            }
            if (entity2 < 0.0) {
                entity2 *= -1.0;
            }
            int value = (int)(entity2 - player2);
            if ((player > 0.0 && entity < 0.0) || (player < 0.0 && entity > 0.0)) {
                value = (int)(-1.0 * player + entity);
            }
            if ((player > 0.0 || player < 0.0) && entity < 0.0 && entity2 != player2) {
                value = (int)(-1.0 * player + entity);
            }
            if ((player < 0.0 && entity == 0.0) || (player == 0.0 && entity < 0.0)) {
                value = (int)(-1.0 * (entity2 - player2));
            }
            return value;
        }
        
        @Override
        public Dimension getSize(final IInterface anInterface) {
            return new Dimension(103, 103);
        }
    }
}
