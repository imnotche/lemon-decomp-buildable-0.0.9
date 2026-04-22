// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import java.util.function.Predicate;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "NoKick", category = Category.Misc)
public class NoKick extends Module
{
    public BooleanSetting noPacketKick;
    BooleanSetting noSlimeCrash;
    BooleanSetting noOffhandCrash;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public NoKick() {
        this.noPacketKick = this.registerBoolean("Packet", true);
        this.noSlimeCrash = this.registerBoolean("Slime", false);
        this.noOffhandCrash = this.registerBoolean("Offhand", false);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.noOffhandCrash.getValue() && event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (NoKick.mc.world != null && this.noSlimeCrash.getValue()) {
            NoKick.mc.world.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntitySlime) {
                    final EntitySlime slime = (EntitySlime)entity;
                    if (slime.getSlimeSize() > 4) {
                        NoKick.mc.world.removeEntity(entity);
                    }
                }
            });
        }
    }
}
