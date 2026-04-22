// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import net.minecraft.init.MobEffects;
import net.minecraft.block.material.Material;
import java.util.function.Predicate;
import com.lemonclient.api.event.events.BossbarEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "NoRender", category = Category.Render)
public class NoRender extends Module
{
    public BooleanSetting armor;
    BooleanSetting fire;
    BooleanSetting blind;
    BooleanSetting nausea;
    public BooleanSetting hurtCam;
    public BooleanSetting noSkylight;
    public BooleanSetting noOverlay;
    BooleanSetting noBossBar;
    public BooleanSetting nameTag;
    public BooleanSetting noCluster;
    IntegerSetting maxNoClusterRender;
    public int currentClusterAmount;
    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener;
    @EventHandler
    private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener;
    @EventHandler
    private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener;
    @EventHandler
    private final Listener<BossbarEvent> bossbarEventListener;
    
    public NoRender() {
        this.armor = this.registerBoolean("Armor", false);
        this.fire = this.registerBoolean("Fire", false);
        this.blind = this.registerBoolean("Blind", false);
        this.nausea = this.registerBoolean("Nausea", false);
        this.hurtCam = this.registerBoolean("HurtCam", false);
        this.noSkylight = this.registerBoolean("Skylight", false);
        this.noOverlay = this.registerBoolean("No Overlay", false);
        this.noBossBar = this.registerBoolean("No Boss Bar", false);
        this.nameTag = this.registerBoolean("No NameTag", false);
        this.noCluster = this.registerBoolean("No Cluster", false);
        this.maxNoClusterRender = this.registerInteger("No Cluster Max", 5, 1, 25);
        this.currentClusterAmount = 0;
        this.blockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
            if (this.fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) {
                event.setCanceled(true);
            }
        }, new Predicate[0]);
        this.fogDensityListener = new Listener<EntityViewRenderEvent.FogDensity>(event -> {
            if (this.noOverlay.getValue() && (event.getState().getMaterial().equals(Material.WATER) || event.getState().getMaterial().equals(Material.LAVA))) {
                event.setDensity(0.0f);
                event.setCanceled(true);
            }
        }, new Predicate[0]);
        this.renderBlockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
            if (this.noOverlay.getValue()) {
                event.setCanceled(true);
            }
        }, new Predicate[0]);
        this.renderGameOverlayEventListener = new Listener<RenderGameOverlayEvent>(event -> {
            if (this.noOverlay.getValue()) {
                if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)) {
                    event.setCanceled(true);
                }
                if (event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL)) {
                    event.setCanceled(true);
                }
            }
        }, new Predicate[0]);
        this.bossbarEventListener = new Listener<BossbarEvent>(event -> {
            if (this.noBossBar.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.blind.getValue() && NoRender.mc.player.isPotionActive(MobEffects.BLINDNESS)) {
            NoRender.mc.player.removePotionEffect(MobEffects.BLINDNESS);
        }
        if (this.nausea.getValue() && NoRender.mc.player.isPotionActive(MobEffects.NAUSEA)) {
            NoRender.mc.player.removePotionEffect(MobEffects.NAUSEA);
        }
    }
    
    @Override
    public void onRender() {
        this.currentClusterAmount = 0;
    }
    
    public boolean incrementNoClusterRender() {
        ++this.currentClusterAmount;
        return this.currentClusterAmount > this.maxNoClusterRender.getValue();
    }
    
    public boolean getNoClusterRender() {
        return this.currentClusterAmount <= this.maxNoClusterRender.getValue();
    }
}
