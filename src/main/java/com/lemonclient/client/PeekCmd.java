// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client;

import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.IClientCommand;
import net.minecraft.command.CommandBase;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.module.modules.misc.ShulkerBypass;
import com.lemonclient.client.module.ModuleManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "peek", name = "PeekBypass", version = "1", acceptedMinecraftVersions = "[1.12.2]")
public class PeekCmd
{
    public static int metadataTicks;
    public static int guiTicks;
    public static ItemStack shulker;
    public static EntityItem drop;
    public static InventoryBasic toOpen;
    public static Minecraft mc;
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new PeekCommand());
    }
    
    public static NBTTagCompound getShulkerNBT(final ItemStack stack) {
        if (PeekCmd.mc.player == null) {
            return null;
        }
        final NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag", 10)) {
            final NBTTagCompound tags = compound.getCompoundTag("BlockEntityTag");
            if (ModuleManager.getModule("Peek").isEnabled() && ShulkerBypass.shulkers) {
                if (tags.hasKey("Items", 9)) {
                    return tags;
                }
                MessageBus.sendMessage("Shulker is empty.", Notification.Type.INFO, "Peek", 3, ShulkerBypass.notification);
            }
        }
        return null;
    }
    
    static {
        PeekCmd.metadataTicks = -1;
        PeekCmd.guiTicks = -1;
        PeekCmd.shulker = ItemStack.EMPTY;
        PeekCmd.mc = Minecraft.getMinecraft();
    }
    
    public static class PeekCommand extends CommandBase implements IClientCommand
    {
        public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
            return false;
        }
        
        public String getName() {
            return "peek";
        }
        
        public String getUsage(final ICommandSender sender) {
            return null;
        }
        
        public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
            if (PeekCmd.mc.player != null && ModuleManager.getModule("Peek").isEnabled() && ShulkerBypass.shulkers) {
                if (!PeekCmd.shulker.isEmpty()) {
                    final NBTTagCompound shulkerNBT = PeekCmd.getShulkerNBT(PeekCmd.shulker);
                    if (shulkerNBT != null) {
                        final TileEntityShulkerBox fakeShulker = new TileEntityShulkerBox();
                        fakeShulker.loadFromNbt(shulkerNBT);
                        String customName = "container.shulkerBox";
                        boolean hasCustomName = false;
                        if (shulkerNBT.hasKey("CustomName", 8)) {
                            customName = shulkerNBT.getString("CustomName");
                            hasCustomName = true;
                        }
                        final InventoryBasic inv = new InventoryBasic(customName, hasCustomName, 27);
                        for (int i = 0; i < 27; ++i) {
                            inv.setInventorySlotContents(i, fakeShulker.getStackInSlot(i));
                        }
                        PeekCmd.toOpen = inv;
                        PeekCmd.guiTicks = 0;
                    }
                }
                else {
                    MessageBus.sendMessage("No shulker detected! please drop and pickup your shulker.", Notification.Type.ERROR, "Peek", 3, ShulkerBypass.notification);
                }
            }
        }
        
        public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
            return true;
        }
    }
}
