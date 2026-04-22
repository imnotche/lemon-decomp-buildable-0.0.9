// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiContainer", category = Category.Misc)
public class AntiContainer extends Module
{
    BooleanSetting Chest;
    BooleanSetting EnderChest;
    BooleanSetting Trapped_Chest;
    BooleanSetting Hopper;
    BooleanSetting Dispenser;
    BooleanSetting Furnace;
    BooleanSetting Beacon;
    BooleanSetting Crafting_Table;
    BooleanSetting Anvil;
    BooleanSetting Enchanting_table;
    BooleanSetting Brewing_Stand;
    BooleanSetting ShulkerBox;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public AntiContainer() {
        this.Chest = this.registerBoolean("Chest", true);
        this.EnderChest = this.registerBoolean("EnderChest", true);
        this.Trapped_Chest = this.registerBoolean("Trapped_Chest", true);
        this.Hopper = this.registerBoolean("Hopper", true);
        this.Dispenser = this.registerBoolean("Dispenser", true);
        this.Furnace = this.registerBoolean("Furnace", true);
        this.Beacon = this.registerBoolean("Beacon", true);
        this.Crafting_Table = this.registerBoolean("Crafting_Table", true);
        this.Anvil = this.registerBoolean("Anvil", true);
        this.Enchanting_table = this.registerBoolean("Enchanting_table", true);
        this.Brewing_Stand = this.registerBoolean("Brewing_Stand", true);
        this.ShulkerBox = this.registerBoolean("ShulkerBox", true);
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final BlockPos pos = ((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getPos();
                if (this.check(pos)) {
                    event.cancel();
                }
            }
        }, new Predicate[0]);
    }
    
    public boolean check(final BlockPos pos) {
        return (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.CHEST && this.Chest.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST && this.EnderChest.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.TRAPPED_CHEST && this.Trapped_Chest.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.HOPPER && this.Hopper.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.DISPENSER && this.Dispenser.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.FURNACE && this.Furnace.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.BEACON && this.Beacon.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE && this.Crafting_Table.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL && this.Anvil.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE && this.Enchanting_table.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() == Blocks.BREWING_STAND && this.Brewing_Stand.getValue()) || (AntiContainer.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox && this.ShulkerBox.getValue());
    }
}
