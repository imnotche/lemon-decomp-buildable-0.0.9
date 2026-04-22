package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.PlacementUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="AntiVoid", category=Category.Movement)
public class AntiVoid
        extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Freeze", "Glitch", "Catch"), "Freeze");
    DoubleSetting height = this.registerDouble("Height", 2.0, 0.0, 5.0);
    BooleanSetting chorus = this.registerBoolean("Chorus", false, () -> ((String)this.mode.getValue()).equals("Freeze"));
    BooleanSetting packetFly = this.registerBoolean("PacketFly", false, () -> ((String)this.mode.getValue()).equals("Catch"));
    boolean chorused;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        try {
            if (AntiVoid.mc.player.posY < (Double)this.height.getValue() + 0.1 && ((String)this.mode.getValue()).equalsIgnoreCase("Freeze") && AntiVoid.mc.world.getBlockState(new BlockPos(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ)).getMaterial().isReplaceable()) {
                switch ((String)this.mode.getValue()) {
                    case "Freeze": {
                        AntiVoid.mc.player.posY = (Double)this.height.getValue();
                        event.setY(0.0);
                        if (AntiVoid.mc.player.getRidingEntity() != null) {
                            AntiVoid.mc.player.ridingEntity.setVelocity(0.0, 0.0, 0.0);
                        }
                        if (!((Boolean)this.chorus.getValue()).booleanValue()) break;
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = AntiVoid.mc.player.inventory.getStackInSlot(i);
                            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemChorusFruit)) continue;
                            newSlot = i;
                            break;
                        }
                        if (newSlot == -1) {
                            newSlot = 1;
                            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Out of chorus!", Notification.Type.ERROR);
                            this.chorused = false;
                        } else {
                            this.chorused = true;
                        }
                        if (!this.chorused) break;
                        AntiVoid.mc.player.inventory.currentItem = newSlot;
                        if (!AntiVoid.mc.player.canEat(true)) break;
                        AntiVoid.mc.player.setActiveHand(EnumHand.MAIN_HAND);
                        break;
                    }
                    case "Glitch": {
                        AntiVoid.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(AntiVoid.mc.player.posX, AntiVoid.mc.player.posY + 69.0, AntiVoid.mc.player.posZ, AntiVoid.mc.player.onGround));
                        break;
                    }
                    case "Catch": {
                        int oldSlot = AntiVoid.mc.player.inventory.currentItem;
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = AntiVoid.mc.player.inventory.getStackInSlot(i);
                            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem((Item)stack.getItem()).getDefaultState().isFullBlock() || ((ItemBlock)stack.getItem()).getBlock() instanceof BlockFalling) continue;
                            newSlot = i;
                            break;
                        }
                        if (newSlot == -1) {
                            newSlot = 1;
                            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Out of valid blocks. Disabling!", Notification.Type.DISABLE);
                            this.disable();
                        }
                        AntiVoid.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(newSlot));
                        PlacementUtil.place(new BlockPos(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ), EnumHand.MAIN_HAND, true);
                        if (AntiVoid.mc.world.getBlockState(new BlockPos(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ)).getMaterial().isReplaceable() && ((Boolean)this.packetFly.getValue()).booleanValue()) {
                            AntiVoid.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(AntiVoid.mc.player.posX + AntiVoid.mc.player.motionX, AntiVoid.mc.player.posY + 0.0624, AntiVoid.mc.player.posZ + AntiVoid.mc.player.motionZ, AntiVoid.mc.player.rotationYaw, AntiVoid.mc.player.rotationPitch, false));
                            AntiVoid.mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(AntiVoid.mc.player.posX, AntiVoid.mc.player.posY + 69420.0, AntiVoid.mc.player.posZ, AntiVoid.mc.player.rotationYaw, AntiVoid.mc.player.rotationPitch, false));
                        }
                        AntiVoid.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(oldSlot));
                        break;
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }, new Predicate[0]);
}
