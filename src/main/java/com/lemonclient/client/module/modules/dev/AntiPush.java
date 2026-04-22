// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.util.MovementInput;
import java.util.function.Predicate;
import net.minecraft.entity.MoverType;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiPush", category = Category.Dev, priority = 1000)
public class AntiPush extends Module
{
    BooleanSetting move;
    @EventHandler
    public final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public AntiPush() {
        this.move = this.registerBoolean("Move", false);
        this.playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
            final MoverType moverType = event.getType();
            if (moverType != MoverType.SELF && moverType != MoverType.PLAYER) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void fast() {
        if (AntiPush.mc.world == null || AntiPush.mc.player == null || AntiPush.mc.player.isDead || !this.move.getValue()) {
            return;
        }
        final MovementInput input = AntiPush.mc.player.movementInput;
        if (input.moveForward == 0.0 && input.moveStrafe == 0.0) {
            AntiPush.mc.player.motionX = 0.0;
            AntiPush.mc.player.motionZ = 0.0;
        }
    }
}
