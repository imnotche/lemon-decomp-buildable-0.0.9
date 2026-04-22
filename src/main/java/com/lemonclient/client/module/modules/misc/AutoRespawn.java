// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.network.play.client.CPacketChatMessage;
import com.lemonclient.api.util.misc.MessageBus;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClientStatus;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiGameOver;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoRespawn", category = Category.Misc)
public class AutoRespawn extends Module
{
    BooleanSetting respawn;
    BooleanSetting coords;
    BooleanSetting respawnMessage;
    StringSetting message;
    IntegerSetting respawnMessageDelay;
    private boolean isDead;
    private boolean sentRespawnMessage;
    long timeSinceRespawn;
    BlockPos deathPos;
    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener;
    
    public AutoRespawn() {
        this.respawn = this.registerBoolean("Respawn", false);
        this.coords = this.registerBoolean("Death Coords", false);
        this.respawnMessage = this.registerBoolean("Respawn Message", false);
        this.message = this.registerString("Message", "/kit name");
        this.respawnMessageDelay = this.registerInteger("Msg Delay(ms)", 0, 0, 5000);
        this.sentRespawnMessage = true;
        this.livingDeathEventListener = new Listener<GuiOpenEvent>(event -> {
            if (this.isEnabled()) {
                if (event.getGui() instanceof GuiGameOver) {
                    this.isDead = true;
                    this.deathPos = PlayerUtil.getPlayerPos();
                    this.sentRespawnMessage = true;
                    if (this.respawn.getValue()) {
                        event.setCanceled(true);
                        AutoRespawn.mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (AutoRespawn.mc.player == null) {
            return;
        }
        if (this.isDead && AutoRespawn.mc.player.isEntityAlive()) {
            if (this.coords.getValue()) {
                MessageBus.sendMessage("You died at X:" + this.deathPos.getX() + ", Y:" + this.deathPos.getY() + ", Z:" + this.deathPos.getZ() + ".", false);
            }
            if (this.respawnMessage.getValue()) {
                this.sentRespawnMessage = false;
                this.timeSinceRespawn = System.currentTimeMillis();
            }
            this.isDead = false;
        }
        if (!this.sentRespawnMessage && System.currentTimeMillis() - this.timeSinceRespawn > this.respawnMessageDelay.getValue()) {
            AutoRespawn.mc.player.connection.sendPacket(new CPacketChatMessage(this.message.getText()));
            this.sentRespawnMessage = true;
        }
    }
}
