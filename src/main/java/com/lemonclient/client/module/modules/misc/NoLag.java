// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityWitherSkull;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketParticles;
import com.lemonclient.api.event.events.RenderEntityEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiLag", category = Category.Misc)
public class NoLag extends Module
{
    BooleanSetting particles;
    BooleanSetting effect;
    BooleanSetting soundEffect;
    BooleanSetting skulls;
    BooleanSetting tnt;
    BooleanSetting parrots;
    BooleanSetting spawn;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<RenderEntityEvent> renderEntityEventListener;
    
    public NoLag() {
        this.particles = this.registerBoolean("Particles", true);
        this.effect = this.registerBoolean("Effect", true);
        this.soundEffect = this.registerBoolean("Sound Effect", true);
        this.skulls = this.registerBoolean("Skull", true);
        this.tnt = this.registerBoolean("Tnt", true);
        this.parrots = this.registerBoolean("Parrot", true);
        this.spawn = this.registerBoolean("Spawn", true);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketParticles && this.particles.getValue()) {
                event.cancel();
            }
            if (event.getPacket() instanceof SPacketEffect && this.effect.getValue()) {
                event.cancel();
            }
            if (event.getPacket() instanceof SPacketSoundEffect && this.soundEffect.getValue()) {
                final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
                if (packet.getCategory() == SoundCategory.PLAYERS && packet.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                    event.cancel();
                }
                if (packet.getCategory() == SoundCategory.WEATHER && packet.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER) {
                    event.cancel();
                }
            }
            if (event.getPacket() instanceof SPacketSpawnMob && this.spawn.getValue()) {
                final SPacketSpawnMob packet2 = (SPacketSpawnMob)event.getPacket();
                if (packet2.getEntityType() == 55) {
                    event.cancel();
                }
            }
        }, new Predicate[0]);
        this.renderEntityEventListener = new Listener<RenderEntityEvent>(event -> {
            if (this.skulls.getValue() && event.getEntity() instanceof EntityWitherSkull) {
                event.cancel();
            }
            if (this.tnt.getValue() && event.getEntity() instanceof EntityTNTPrimed) {
                event.cancel();
            }
            if (this.parrots.getValue() && event.getEntity() instanceof EntityParrot) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void onDisable() {
        NoLag.mc.renderGlobal.loadRenderers();
        super.onDisable();
    }
}
