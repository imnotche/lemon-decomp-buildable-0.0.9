// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.player.Locks;
import net.minecraft.item.Item;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.inventory.ClickType;
import java.util.Set;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.util.Comparator;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemArmor;
import com.lemonclient.api.util.player.InvStack;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import com.lemonclient.client.module.ModuleManager;
import java.util.stream.Collectors;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.init.Items;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoArmor", category = Category.Combat)
public class AutoArmor extends Module
{
    IntegerSetting delay;
    BooleanSetting noDesync;
    BooleanSetting illegalSync;
    IntegerSetting checkDelay;
    BooleanSetting strict;
    BooleanSetting stackArmor;
    IntegerSetting slot;
    BooleanSetting packetSwitch;
    BooleanSetting armorSaver;
    BooleanSetting pauseWhenSafe;
    IntegerSetting depletion;
    BooleanSetting allowMend;
    IntegerSetting repair;
    Timing rightClickTimer;
    Timing timer;
    private boolean sleep;
    @EventHandler
    private final Listener<PlayerInteractEvent.RightClickItem> listener;
    
    public AutoArmor() {
        this.delay = this.registerInteger("Delay", 1, 1, 10);
        this.noDesync = this.registerBoolean("No Desync", true);
        this.illegalSync = this.registerBoolean("Illegal Sync", true);
        this.checkDelay = this.registerInteger("Check Delay", 1, 0, 20, () -> this.noDesync.getValue());
        this.strict = this.registerBoolean("Strict", false);
        this.stackArmor = this.registerBoolean("Stack Armor", false);
        this.slot = this.registerInteger("Swap Slot", 1, 1, 9, () -> this.stackArmor.getValue());
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.stackArmor.getValue());
        this.armorSaver = this.registerBoolean("Armor Saver", false);
        this.pauseWhenSafe = this.registerBoolean("Pause When Safe", false);
        this.depletion = this.registerInteger("Depletion", 20, 0, 99, () -> this.armorSaver.getValue());
        this.allowMend = this.registerBoolean("Allow Mend", false);
        this.repair = this.registerInteger("Repair", 80, 0, 100);
        this.rightClickTimer = new Timing();
        this.timer = new Timing();
        this.listener = new Listener<PlayerInteractEvent.RightClickItem>(event -> {
            if (event.getEntityPlayer() == AutoArmor.mc.player) {
                if (event.getItemStack().getItem() == Items.EXPERIENCE_BOTTLE) {
                    this.rightClickTimer.reset();
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (AutoArmor.mc.world == null || AutoArmor.mc.player == null || AutoArmor.mc.player.isDead) {
            return;
        }
        if (AutoArmor.mc.player.ticksExisted % this.delay.getValue() != 0 || this.checkDesync()) {
            return;
        }
        if (this.strict.getValue() && (AutoArmor.mc.player.motionX != 0.0 || AutoArmor.mc.player.motionZ != 0.0)) {
            return;
        }
        if (this.pauseWhenSafe.getValue()) {
            final List<Entity> proximity = AutoArmor.mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityPlayer && !e.equals(AutoArmor.mc.player) && AutoArmor.mc.player.getDistance(e) <= 6.0f) || (e instanceof EntityEnderCrystal && AutoArmor.mc.player.getDistance(e) <= 12.0f)).collect(Collectors.toList());
            if (proximity.isEmpty()) {
                return;
            }
        }
        final boolean isMending = ModuleManager.isModuleEnabled(AutoMend.class);
        if (this.allowMend.getValue() && !this.rightClickTimer.passedMs(500L)) {
            for (int i = 0; i < AutoArmor.mc.player.inventory.armorInventory.size(); ++i) {
                final ItemStack armorPiece = AutoArmor.mc.player.inventory.armorInventory.get(i);
                if (armorPiece.isEmpty) {
                    return;
                }
                boolean mending = false;
                for (final Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(armorPiece).entrySet()) {
                    if (entry.getKey().getName().contains("mending")) {
                        mending = true;
                        break;
                    }
                }
                if (mending) {
                    if (!armorPiece.isEmpty()) {
                        final long freeSlots = AutoArmor.mc.player.inventory.mainInventory.stream().filter(is -> is.isEmpty() || is.getItem() == Items.AIR).map(is -> AutoArmor.mc.player.inventory.getSlotFor(is)).count();
                        if (freeSlots <= 0L) {
                            return;
                        }
                        if (armorPiece.getItemDamage() != 0) {
                            this.shiftClickSpot(8 - i);
                            return;
                        }
                    }
                }
            }
            return;
        }
        if (AutoArmor.mc.currentScreen instanceof GuiContainer && !(AutoArmor.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        final AtomicBoolean hasSwapped = new AtomicBoolean(false);
        if (this.sleep) {
            this.sleep = false;
            return;
        }
        final Set<InvStack> replacements = new HashSet<InvStack>();
        InvStack candidateStack = null;
        for (int slot = 0; slot < 45; ++slot) {
            if (slot <= 4 || slot >= 9) {
                candidateStack = new InvStack(slot, AutoArmor.mc.player.inventoryContainer.getSlot(slot).getStack());
                if (candidateStack.stack.getItem() instanceof ItemArmor || candidateStack.stack.getItem() instanceof ItemElytra) {
                    replacements.add(candidateStack);
                }
            }
        }
        List<InvStack> armors = replacements.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).filter(stack -> !this.armorSaver.getValue() || stack.stack.getItem().getDurabilityForDisplay(stack.stack) < this.depletion.getValue()).sorted(Comparator.comparingInt(stack -> stack.slot)).sorted(Comparator.comparingInt(stack -> ((ItemArmor)stack.stack.getItem()).damageReduceAmount)).collect(Collectors.toList());
        final boolean wasEmpty = armors.isEmpty();
        if (wasEmpty) {
            armors = replacements.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).sorted(Comparator.comparingInt(stack -> stack.slot)).sorted(Comparator.comparingInt(stack -> ((ItemArmor)stack.stack.getItem()).damageReduceAmount)).collect(Collectors.toList());
        }
        final ItemStack currentHeadItem = AutoArmor.mc.player.inventory.getStackInSlot(39);
        final ItemStack currentChestItem = AutoArmor.mc.player.inventory.getStackInSlot(38);
        final ItemStack currentLegsItem = AutoArmor.mc.player.inventory.getStackInSlot(37);
        final ItemStack currentFeetItem = AutoArmor.mc.player.inventory.getStackInSlot(36);
        final boolean saveHead = !wasEmpty && currentHeadItem.getCount() == 1 && this.armorSaver.getValue() && this.getItemDamage(5) <= this.depletion.getValue();
        final boolean saveChest = !wasEmpty && currentChestItem.getCount() == 1 && this.armorSaver.getValue() && this.getItemDamage(6) <= this.depletion.getValue();
        final boolean saveLegs = !wasEmpty && currentLegsItem.getCount() == 1 && this.armorSaver.getValue() && this.getItemDamage(7) <= this.depletion.getValue();
        final boolean saveFeet = !wasEmpty && currentFeetItem.getCount() == 1 && this.armorSaver.getValue() && this.getItemDamage(8) <= this.depletion.getValue();
        final boolean replaceHead = currentHeadItem.isEmpty || saveHead || (isMending && this.getItemDamage(5) >= this.repair.getValue());
        final boolean replaceChest = currentChestItem.isEmpty || saveChest || (isMending && this.getItemDamage(6) >= this.repair.getValue());
        final boolean replaceLegs = currentLegsItem.isEmpty || saveLegs || (isMending && this.getItemDamage(7) >= this.repair.getValue());
        final boolean replaceFeet = currentFeetItem.isEmpty || saveFeet || (isMending && this.getItemDamage(8) >= this.repair.getValue());
        if (replaceHead && !hasSwapped.get()) {
            armors.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).filter(stack -> ((ItemArmor)stack.stack.getItem()).armorType.equals(EntityEquipmentSlot.HEAD)).filter(stack -> !saveHead || this.getItemDamage(stack.slot) > this.depletion.getValue()).filter(stack -> !isMending || this.getItemDamage(stack.slot) <= this.repair.getValue()).findFirst().ifPresent(stack -> {
                this.swapSlot(stack.slot, 5);
                hasSwapped.set(true);
            });
        }
        if (replaceChest || (currentChestItem.getItem() instanceof ItemElytra && !hasSwapped.get())) {
            armors.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).filter(stack -> ((ItemArmor)stack.stack.getItem()).armorType.equals(EntityEquipmentSlot.CHEST)).filter(stack -> !saveChest || this.getItemDamage(stack.slot) > this.depletion.getValue()).filter(stack -> !isMending || this.getItemDamage(stack.slot) <= this.repair.getValue()).findFirst().ifPresent(stack -> {
                this.swapSlot(stack.slot, 6);
                hasSwapped.set(true);
            });
        }
        if (replaceLegs && !hasSwapped.get()) {
            armors.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).filter(stack -> ((ItemArmor)stack.stack.getItem()).armorType.equals(EntityEquipmentSlot.LEGS)).filter(stack -> !saveLegs || this.getItemDamage(stack.slot) > this.depletion.getValue()).filter(stack -> !isMending || this.getItemDamage(stack.slot) <= this.repair.getValue()).findFirst().ifPresent(stack -> {
                this.swapSlot(stack.slot, 7);
                hasSwapped.set(true);
            });
        }
        if (replaceFeet && !hasSwapped.get()) {
            armors.stream().filter(stack -> stack.stack.getItem() instanceof ItemArmor).filter(stack -> ((ItemArmor)stack.stack.getItem()).armorType.equals(EntityEquipmentSlot.FEET)).filter(stack -> !saveFeet || this.getItemDamage(stack.slot) > this.depletion.getValue()).filter(stack -> !isMending || this.getItemDamage(stack.slot) <= this.repair.getValue()).findFirst().ifPresent(stack -> {
                this.swapSlot(stack.slot, 8);
                hasSwapped.set(true);
            });
        }
    }
    
    private int getItemDamage(final int slot) {
        final ItemStack itemStack = AutoArmor.mc.player.inventoryContainer.getSlot(slot).getStack();
        final float green = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
        final float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }
    
    private void swapSlot(final int source, final int target) {
        final ItemStack sourceStack = AutoArmor.mc.player.inventoryContainer.getSlot(source).getStack();
        final boolean stacked = sourceStack.getCount() > 1;
        if (stacked) {
            this.swapStack(source, target);
        }
        else {
            this.swap(source, target);
        }
        this.sleep = true;
    }
    
    private void swapStack(final int slotFrom, final int slotTo) {
        if (!this.stackArmor.getValue()) {
            return;
        }
        if (AutoArmor.mc.player.inventoryContainer.getSlot(slotTo).getStack() != ItemStack.EMPTY) {
            AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotTo, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
        }
        int slot = this.slot.getValue() - 1;
        if (slotFrom < 36) {
            this.swapToHotbar(slotFrom);
        }
        else {
            slot = slotFrom - 36;
        }
        InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> AutoArmor.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
        if (slotFrom < 36) {
            this.swapToHotbar(slotFrom);
        }
    }
    
    private boolean checkDesync() {
        if ((this.noDesync.getValue() && !(AutoArmor.mc.currentScreen instanceof GuiContainer)) || (AutoArmor.mc.currentScreen instanceof GuiInventory && this.timer.passedMs(this.checkDelay.getValue() * 50))) {
            int bestSlot = -1;
            int clientValue = 0;
            boolean foundType = false;
            final int armorValue = AutoArmor.mc.player.getTotalArmorValue();
            for (int i = 5; i < 9; ++i) {
                final ItemStack stack = AutoArmor.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack.isEmpty() && !foundType) {
                    bestSlot = i;
                    foundType = true;
                }
                else if (stack.getItem() instanceof ItemArmor) {
                    final ItemArmor itemArmor = (ItemArmor)stack.getItem();
                    clientValue += itemArmor.damageReduceAmount;
                }
            }
            if (clientValue != armorValue && this.timer.passedMs(this.delay.getValue() * 50)) {
                if (this.illegalSync.getValue()) {
                    InventoryUtil.illegalSync();
                }
                else if (bestSlot != -1 && getSlot(AutoArmor.mc.player.inventory.getItemStack()) == fromSlot(bestSlot)) {
                    final Item j = get(bestSlot).getItem();
                    clickLocked(bestSlot, bestSlot, j, j);
                }
                else {
                    final Item j = get(20).getItem();
                    clickLocked(20, 20, j, j);
                }
                this.timer.reset();
                return true;
            }
        }
        return false;
    }
    
    public static void clickLocked(final int slot, final int to, final Item inSlot, final Item inTo) {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if ((slot == -1 || get(slot).getItem() == inSlot) && get(to).getItem() == inTo) {
                final boolean multi = slot >= 0;
                if (multi) {
                    click(slot);
                }
                click(to);
            }
        });
    }
    
    public static void click(final int slot) {
        AutoArmor.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, AutoArmor.mc.player);
    }
    
    public static ItemStack get(final int slot) {
        if (slot == -2) {
            return AutoArmor.mc.player.inventory.getItemStack();
        }
        return AutoArmor.mc.player.inventoryContainer.getInventory().get(slot);
    }
    
    public static EntityEquipmentSlot fromSlot(final int slot) {
        switch (slot) {
            case 5: {
                return EntityEquipmentSlot.HEAD;
            }
            case 6: {
                return EntityEquipmentSlot.CHEST;
            }
            case 7: {
                return EntityEquipmentSlot.LEGS;
            }
            case 8: {
                return EntityEquipmentSlot.FEET;
            }
            default: {
                final ItemStack stack = get(slot);
                return getSlot(stack);
            }
        }
    }
    
    public static EntityEquipmentSlot getSlot(final ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor)stack.getItem();
                return armor.getEquipmentSlot();
            }
            if (stack.getItem() instanceof ItemElytra) {
                return EntityEquipmentSlot.CHEST;
            }
        }
        return null;
    }
    
    private void swapToHotbar(final int InvSlot) {
        AutoArmor.mc.playerController.windowClick(0, InvSlot, this.slot.getValue() - 1, ClickType.SWAP, AutoArmor.mc.player);
        AutoArmor.mc.playerController.updateController();
    }
    
    private void swap(final int slotFrom, final int slotTo) {
        if (AutoArmor.mc.player.inventoryContainer.getSlot(slotTo).getStack().isEmpty) {
            AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotFrom, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
        }
        else {
            boolean hasEmpty = false;
            for (int l_I = 0; l_I < 36; ++l_I) {
                final ItemStack l_Stack = AutoArmor.mc.player.inventory.getStackInSlot(l_I);
                if (l_Stack.isEmpty) {
                    hasEmpty = true;
                    break;
                }
            }
            if (hasEmpty) {
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotTo, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotFrom, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
            }
            else {
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotFrom, 0, ClickType.PICKUP, AutoArmor.mc.player);
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotTo, 0, ClickType.PICKUP, AutoArmor.mc.player);
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, slotFrom, 0, ClickType.PICKUP, AutoArmor.mc.player);
            }
        }
        AutoArmor.mc.playerController.updateController();
    }
    
    private void shiftClickSpot(final int source) {
        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, source, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
    }
}
