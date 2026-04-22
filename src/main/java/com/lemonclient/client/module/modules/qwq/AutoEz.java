// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import java.util.Objects;
import com.lemonclient.api.util.misc.MessageBus;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.ArrayList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.List;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoEz", category = Category.qwq)
public class AutoEz extends Module
{
    public static AutoEz INSTANCE;
    public BooleanSetting hi;
    StringSetting msg;
    IntegerSetting delay;
    List<Target> targetedPlayers;
    int waited;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<LivingDeathEvent> livingDeathEventListener;
    
    public AutoEz() {
        this.hi = this.registerBoolean("Use {name} for target name", true);
        this.msg = this.registerString("Msg", ">Ez");
        this.delay = this.registerInteger("Delay", 0, 0, 20);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (AutoEz.mc.player != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ArrayList<Target>();
                }
                if (this.waited <= 0) {
                    if (event.getPacket() instanceof CPacketUseEntity) {
                        final CPacketUseEntity cPacketUseEntity = (CPacketUseEntity)event.getPacket();
                        if (cPacketUseEntity.getAction().equals(CPacketUseEntity.Action.ATTACK)) {
                            final Entity targetEntity = cPacketUseEntity.getEntityFromWorld(AutoEz.mc.world);
                            if (targetEntity instanceof EntityPlayer) {
                                this.addTargetedPlayer(targetEntity.getName());
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.livingDeathEventListener = new Listener<LivingDeathEvent>(event -> {
            if (AutoEz.mc.player != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ArrayList<Target>();
                }
                if (this.waited <= 0) {
                    final EntityLivingBase entity = event.getEntityLiving();
                    if (entity != null && entity instanceof EntityPlayer) {
                        final EntityPlayer player = (EntityPlayer)entity;
                        if (player.getHealth() <= 0.0f) {
                            final String name = player.getName();
                            this.doAnnounce(name);
                        }
                    }
                }
            }
        }, new Predicate[0]);
        AutoEz.INSTANCE = this;
    }
    
    public void onEnable() {
        this.targetedPlayers = new ArrayList<Target>();
    }
    
    public void onDisable() {
        this.targetedPlayers = null;
    }
    
    @Override
    public void onUpdate() {
        if (this.targetedPlayers == null) {
            this.targetedPlayers = new ArrayList<Target>();
        }
        --this.waited;
        if (this.waited > 0) {
            return;
        }
        final List<String> nameList = new ArrayList<String>();
        for (final EntityPlayer player : AutoEz.mc.world.playerEntities) {
            final String name = player.getName();
            nameList.add(name);
            if (this.inList(name) && player.getHealth() <= 0.0f) {
                this.doAnnounce(name);
            }
        }
        this.targetedPlayers.removeIf(target -> {
            if (!nameList.contains(target.name) || target.name.equals("")) {
                return true;
            }
            else {
                target.updateTime();
                return target.time <= 0;
            }
        });
    }
    
    private void doAnnounce(final String name) {
        if (name.equals(AutoEz.mc.player.getName())) {
            return;
        }
        boolean in = false;
        for (final Target target : this.targetedPlayers) {
            if (target.name.equals(name)) {
                this.targetedPlayers.remove(target);
                in = true;
                break;
            }
        }
        if (!in) {
            return;
        }
        final String message = this.msg.getText();
        String messageSanitized = message.replace("{name}", name);
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }
        MessageBus.sendServerMessage(messageSanitized);
        this.waited = this.delay.getValue();
    }
    
    public void addTargetedPlayer(final String name) {
        if (!Objects.equals(name, AutoEz.mc.player.getName())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ArrayList<Target>();
            }
            boolean added = false;
            for (final Target target : this.targetedPlayers) {
                if (target.name.equals(name)) {
                    target.update();
                    added = true;
                    break;
                }
            }
            if (!added) {
                this.targetedPlayers.add(new Target(name));
            }
        }
    }
    
    private boolean inList(final String name) {
        for (final Target target : this.targetedPlayers) {
            if (target.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    static class Target
    {
        String name;
        int time;
        
        public Target(final String name) {
            this.name = name;
            this.time = 20;
        }
        
        void updateTime() {
            --this.time;
        }
        
        void update() {
            this.time = 20;
        }
    }
}
