// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.awt.Color;
import net.minecraft.util.text.TextFormatting;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.player.social.SocialManager;
import net.minecraft.entity.player.EntityPlayer;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.util.Arrays;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "TextRadar", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 50)
public class TextRadar extends HUDModule
{
    ModeSetting display;
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    IntegerSetting range;
    private final PlayerList list;
    
    public TextRadar() {
        this.display = this.registerMode("Display", Arrays.asList("All", "Friend", "Enemy"), "All");
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.range = this.registerInteger("Range", 100, 1, 260);
        this.list = new PlayerList();
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }
    
    @Override
    public void onRender() {
        this.list.players.clear();
        TextRadar.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != TextRadar.mc.player).forEach(e -> {
            if (TextRadar.mc.player.getDistance(e) <= this.range.getValue()) {
                if (!this.display.getValue().equalsIgnoreCase("Friend") || SocialManager.isFriend(e.getName())) {
                    if (!this.display.getValue().equalsIgnoreCase("Enemy") || SocialManager.isEnemy(e.getName())) {
                        this.list.players.add((EntityPlayer)e);
                    }
                }
            }
        });
    }
    
    private class PlayerList implements HUDList
    {
        public List<EntityPlayer> players;
        
        private PlayerList() {
            this.players = new ArrayList<EntityPlayer>();
        }
        
        @Override
        public int getSize() {
            return this.players.size();
        }
        
        @Override
        public String getItem(final int index) {
            final EntityPlayer e = this.players.get(index);
            TextFormatting friendcolor;
            if (SocialManager.isFriend(e.getName())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getFriendColor();
            }
            else if (SocialManager.isEnemy(e.getName())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getEnemyColor();
            }
            else {
                friendcolor = TextFormatting.GRAY;
            }
            final float health = e.getHealth() + e.getAbsorptionAmount();
            TextFormatting healthcolor;
            if (health <= 5.0f) {
                healthcolor = TextFormatting.RED;
            }
            else if (health > 5.0f && health < 15.0f) {
                healthcolor = TextFormatting.YELLOW;
            }
            else {
                healthcolor = TextFormatting.GREEN;
            }
            final float distance = TextRadar.mc.player.getDistance(e);
            TextFormatting distancecolor;
            if (distance < 20.0f) {
                distancecolor = TextFormatting.RED;
            }
            else if (distance >= 20.0f && distance < 50.0f) {
                distancecolor = TextFormatting.YELLOW;
            }
            else {
                distancecolor = TextFormatting.GREEN;
            }
            return TextFormatting.GRAY + "[" + healthcolor + (int)health + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)distance + TextFormatting.GRAY + "]";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return TextRadar.this.sortUp.getValue();
        }
        
        @Override
        public boolean sortRight() {
            return TextRadar.this.sortRight.getValue();
        }
    }
}
