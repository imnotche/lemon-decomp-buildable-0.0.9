// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.HoleUtil;
import java.util.function.Predicate;
import net.minecraft.util.MovementInputFromOptions;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoSkull", category = Category.Combat)
public class AutoSkull extends Module
{
    BooleanSetting moving;
    IntegerSetting delay;
    BooleanSetting packet;
    BooleanSetting rotate;
    BooleanSetting swing;
    BooleanSetting onlyHoles;
    BooleanSetting packetSwitch;
    BooleanSetting disableAfter;
    BooleanSetting disable;
    Timing timer;
    double y;
    @EventHandler
    private final Listener<InputUpdateEvent> inputUpdateEventListener;
    
    public AutoSkull() {
        this.moving = this.registerBoolean("Moving", false);
        this.delay = this.registerInteger("Delay", 50, 0, 1000);
        this.packet = this.registerBoolean("Packet Place", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.swing = this.registerBoolean("Swing", true);
        this.onlyHoles = this.registerBoolean("Only Holes", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.disableAfter = this.registerBoolean("Disable After", true);
        this.disable = this.registerBoolean("Auto Disable", true);
        this.timer = new Timing();
        this.inputUpdateEventListener = new Listener<InputUpdateEvent>(event -> {
            if (this.disable.getValue()) {
                if (event.getMovementInput() instanceof MovementInputFromOptions) {
                    if (event.getMovementInput().jump) {
                        this.disable();
                    }
                    if (event.getMovementInput().forwardKeyDown || event.getMovementInput().backKeyDown || event.getMovementInput().leftKeyDown || event.getMovementInput().rightKeyDown) {
                        final double posY = AutoSkull.mc.player.posY - this.y;
                        if (posY * posY > 0.25) {
                            this.disable();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        if (AutoSkull.mc.world == null || AutoSkull.mc.player == null || AutoSkull.mc.player.isDead) {
            this.disable();
            return;
        }
        this.y = AutoSkull.mc.player.posY;
    }
    
    @Override
    public void fast() {
        if (AutoSkull.mc.world == null || AutoSkull.mc.player == null || AutoSkull.mc.player.isDead) {
            return;
        }
        if (this.onlyHoles.getValue() && !HoleUtil.isInHole(AutoSkull.mc.player, true, true, false)) {
            return;
        }
        if (!this.moving.getValue() && MotionUtil.isMoving(AutoSkull.mc.player)) {
            return;
        }
        final int slot = InventoryUtil.findSkullSlot();
        if (slot == -1) {
            return;
        }
        final BlockPos pos = PlayerUtil.getPlayerPos();
        if (BurrowUtil.getFirstFacing(pos) == null || !BlockUtil.isAir(pos)) {
            return;
        }
        if (this.timer.passedMs(this.delay.getValue())) {
            InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
            if (this.disableAfter.getValue()) {
                this.disable();
            }
            this.timer.reset();
        }
    }
}
