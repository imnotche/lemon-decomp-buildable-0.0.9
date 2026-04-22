// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemBlock;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.util.player.PredictUtil;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.misc.MessageBus;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import java.util.HashMap;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import java.util.Map;
import java.util.ArrayList;
import net.minecraft.item.Item;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Offhand", category = Category.Dev)
public class OffHand extends Module
{
    public static OffHand INSTANCE;
    public boolean autoCrystal;
    ModeSetting defaultItem;
    ModeSetting nonDefaultItem;
    ModeSetting noPlayerItem;
    ModeSetting swordMode;
    ModeSetting gappleMode;
    ModeSetting pickaxeMode;
    ModeSetting shiftPickaxeMode;
    ModeSetting potionChoose;
    IntegerSetting healthSwitch;
    IntegerSetting swordHealth;
    IntegerSetting tickDelay;
    IntegerSetting fallDistance;
    IntegerSetting maxSwitchPerSecond;
    DoubleSetting biasDamage;
    DoubleSetting playerDistance;
    BooleanSetting rightGap;
    BooleanSetting shiftPot;
    BooleanSetting swordCheck;
    BooleanSetting crystalGap;
    BooleanSetting fallDistanceBol;
    BooleanSetting crystalCheck;
    IntegerSetting predict;
    BooleanSetting noHotBar;
    BooleanSetting onlyHotBar;
    BooleanSetting antiWeakness;
    BooleanSetting hotBarTotem;
    BooleanSetting refill;
    BooleanSetting check;
    IntegerSetting totemSlot;
    ModeSetting HudMode;
    BooleanSetting debug;
    String ItemName;
    String itemCheck;
    int prevSlot;
    int tickWaited;
    int counts;
    int totems;
    boolean returnBack;
    boolean stepChanging;
    boolean firstChange;
    Item item;
    private final ArrayList<Long> switchDone;
    Map<String, Item> allowedItemsItem;
    Map<String, Block> allowedItemsBlock;
    int nowSlot;
    @EventHandler
    private final Listener<PacketEvent.Send> postSendListener;
    
    public OffHand() {
        this.defaultItem = this.registerMode("Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "EChest", "Pot", "Exp", "Bed"), "Totem");
        this.nonDefaultItem = this.registerMode("Non Default", Arrays.asList("Totem", "Crystal", "Gapple", "Obby", "EChest", "Pot", "Exp", "Plates", "String", "Skull", "Bed"), "Crystal");
        this.noPlayerItem = this.registerMode("No Player", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "EChest", "Pot", "Exp", "Bed"), "Gapple");
        this.swordMode = this.registerMode("Sword Switch", Arrays.asList("Gapple", "Crystal", "Pot", "None"), "Gapple");
        this.gappleMode = this.registerMode("Gap Switch", Arrays.asList("Totem", "Gapple", "Crystal", "None"), "Crystal");
        this.pickaxeMode = this.registerMode("Pick Switch", Arrays.asList("Obsidian", "EChest", "Gapple", "Crystal", "None"), "Gapple");
        this.shiftPickaxeMode = this.registerMode("Shift Pick", Arrays.asList("Obsidian", "EChest", "Gapple", "Crystal", "None"), "Gapple");
        this.potionChoose = this.registerMode("Potion", Arrays.asList("first", "strength", "swiftness"), "first");
        this.healthSwitch = this.registerInteger("Health Switch", 14, 0, 36);
        this.swordHealth = this.registerInteger("Sword Health", 14, 0, 36);
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.fallDistance = this.registerInteger("Fall Distance", 12, 0, 30);
        this.maxSwitchPerSecond = this.registerInteger("Max Switch", 6, 2, 10);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 30.0);
        this.rightGap = this.registerBoolean("Right Click Gap", false);
        this.shiftPot = this.registerBoolean("Shift Pot", false);
        this.swordCheck = this.registerBoolean("Only Sword", true);
        this.crystalGap = this.registerBoolean("Crystal Gap", false);
        this.fallDistanceBol = this.registerBoolean("Fall Distance", true);
        this.crystalCheck = this.registerBoolean("Crystal Check", false);
        this.predict = this.registerInteger("Predict Tick", 1, 0, 20);
        this.noHotBar = this.registerBoolean("No HotBar", false);
        this.onlyHotBar = this.registerBoolean("Only HotBar", false);
        this.antiWeakness = this.registerBoolean("AntiWeakness", false);
        this.hotBarTotem = this.registerBoolean("Switch HotBar Totem", false);
        this.refill = this.registerBoolean("ReFill", true, () -> this.hotBarTotem.getValue());
        this.check = this.registerBoolean("Check", true, () -> this.hotBarTotem.getValue() && this.refill.getValue());
        this.totemSlot = this.registerInteger("Totem Slot", 1, 1, 9, () -> this.hotBarTotem.getValue() && this.refill.getValue());
        this.HudMode = this.registerMode("Hud Mode", Arrays.asList("Totem", "Offhand"), "Offhand");
        this.debug = this.registerBoolean("Debug Msg", false);
        this.itemCheck = "";
        this.switchDone = new ArrayList<Long>();
        this.allowedItemsItem = new HashMap<String, Item>() {
            {
                this.put("Totem", Items.TOTEM_OF_UNDYING);
                this.put("Crystal", Items.END_CRYSTAL);
                this.put("Gapple", Items.GOLDEN_APPLE);
                this.put("Pot", Items.POTIONITEM);
                this.put("Exp", Items.EXPERIENCE_BOTTLE);
                this.put("Bed", Items.BED);
                this.put("String", Items.STRING);
            }
        };
        this.allowedItemsBlock = new HashMap<String, Block>() {
            {
                this.put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
                this.put("EChest", Blocks.ENDER_CHEST);
                this.put("Skull", Blocks.SKULL);
                this.put("Obby", Blocks.OBSIDIAN);
            }
        };
        this.postSendListener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketHeldItemChange) {
                this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
            }
        }, new Predicate[0]);
        OffHand.INSTANCE = this;
    }
    
    public void onEnable() {
        this.autoCrystal = false;
        this.firstChange = true;
        this.returnBack = false;
    }
    
    public void onDisable() {
    }
    
    @Override
    public void onTick() {
        if (OffHand.mc.world == null || OffHand.mc.player == null || OffHand.mc.player.isDead || (OffHand.mc.currentScreen instanceof GuiContainer && !(OffHand.mc.currentScreen instanceof GuiInventory))) {
            return;
        }
        if (this.hotBarTotem.getValue() && this.refill.getValue()) {
            boolean hasTotem = false;
            for (int i = 0; i < 9; ++i) {
                if (OffHand.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    hasTotem = true;
                }
            }
            if (!hasTotem || !this.check.getValue()) {
                for (int i = 9; i < 36; ++i) {
                    if (OffHand.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        OffHand.mc.playerController.windowClick(0, i, this.totemSlot.getValue() - 1, ClickType.SWAP, OffHand.mc.player);
                        break;
                    }
                }
            }
        }
        if (this.stepChanging) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.tickWaited = 0;
            this.stepChanging = false;
            OffHand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffHand.mc.player);
            this.switchDone.add(System.currentTimeMillis());
        }
        this.totems = OffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.returnBack) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.changeBack();
        }
        this.itemCheck = this.getItem(false);
        if (this.offHandSame(this.itemCheck)) {
            if (this.hotBarTotem.getValue() && this.itemCheck.equals("Totem")) {
                this.itemCheck = this.getItem(this.switchItemTotemHot());
            }
            if (this.offHandSame(this.itemCheck)) {
                this.switchItemNormal(this.itemCheck);
            }
        }
        this.GetOffhand();
    }
    
    private void GetOffhand() {
        if (this.HudMode.getValue().equals("Offhand")) {
            this.item = OffHand.mc.player.getHeldItemOffhand().getItem();
            final int items = OffHand.mc.player.getHeldItemOffhand().getCount();
            this.ItemName = OffHand.mc.player.getHeldItemOffhand().getDisplayName();
            this.counts = OffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == this.item).mapToInt(ItemStack::getCount).sum() + items;
        }
    }
    
    private void changeBack() {
        if (this.prevSlot == -1 || !OffHand.mc.player.inventory.getStackInSlot(this.prevSlot).isEmpty()) {
            this.prevSlot = this.findEmptySlot();
        }
        if (this.prevSlot != -1) {
            OffHand.mc.playerController.windowClick(0, (this.prevSlot < 9) ? (this.prevSlot + 36) : this.prevSlot, 0, ClickType.PICKUP, OffHand.mc.player);
        }
        else if (this.debug.getValue()) {
            MessageBus.printDebug("Your inventory is full.", true);
        }
        this.returnBack = false;
        this.tickWaited = 0;
    }
    
    private boolean switchItemTotemHot() {
        final int slot = InventoryUtil.findTotemSlot(0, 8);
        if (slot != -1) {
            if (this.nowSlot != slot) {
                OffHand.mc.player.inventory.currentItem = slot;
                OffHand.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            return true;
        }
        return false;
    }
    
    private void switchItemNormal(final String itemCheck) {
        final int t = this.getInventorySlot(itemCheck);
        if (t == -1) {
            return;
        }
        if (!itemCheck.equals("Totem") && this.canSwitch()) {
            return;
        }
        this.toOffHand(t);
    }
    
    private String getItem(final boolean mainTotem) {
        String itemCheck = "";
        boolean normalOffHand = true;
        if (!mainTotem && ((this.fallDistanceBol.getValue() && OffHand.mc.player.fallDistance >= this.fallDistance.getValue() && OffHand.mc.player.prevPosY != OffHand.mc.player.posY && !OffHand.mc.player.isElytraFlying()) || (this.crystalCheck.getValue() && this.crystalDamage()))) {
            normalOffHand = false;
            itemCheck = "Totem";
        }
        final Item mainHandItem = OffHand.mc.player.getHeldItemMainhand().getItem();
        if (mainHandItem instanceof ItemSword) {
            boolean can = true;
            if (OffHand.mc.gameSettings.keyBindUseItem.isKeyDown() && this.swordCheck.getValue()) {
                if (this.shiftPot.getValue() && OffHand.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    can = false;
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
                else if (this.rightGap.getValue() && !this.swordMode.getValue().equals("Gapple")) {
                    can = false;
                    itemCheck = "Gapple";
                    normalOffHand = false;
                }
            }
            if (can) {
                final String s = this.swordMode.getValue();
                switch (s) {
                    case "Gapple": {
                        itemCheck = "Gapple";
                        normalOffHand = false;
                        break;
                    }
                    case "Crystal": {
                        itemCheck = "Crystal";
                        normalOffHand = false;
                        break;
                    }
                    case "Pot": {
                        itemCheck = "Pot";
                        normalOffHand = false;
                        break;
                    }
                }
            }
        }
        else if (!this.swordCheck.getValue()) {
            if (this.shiftPot.getValue() && OffHand.mc.gameSettings.keyBindSneak.isKeyDown()) {
                itemCheck = "Pot";
                normalOffHand = false;
            }
            else if (this.rightGap.getValue() && !this.swordMode.getValue().equals("Gapple")) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }
        if (mainHandItem == Items.DIAMOND_PICKAXE) {
            if (!OffHand.mc.gameSettings.keyBindSneak.isKeyDown() || OffHand.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final String s2 = this.pickaxeMode.getValue();
                switch (s2) {
                    case "Obsidian": {
                        itemCheck = "Obby";
                        normalOffHand = false;
                        break;
                    }
                    case "EChest": {
                        itemCheck = "EChest";
                        normalOffHand = false;
                        break;
                    }
                    case "Gapple": {
                        itemCheck = "Gapple";
                        normalOffHand = false;
                        break;
                    }
                    case "Crystal": {
                        itemCheck = "Crystal";
                        normalOffHand = false;
                        break;
                    }
                }
            }
            if (OffHand.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final String s3 = this.shiftPickaxeMode.getValue();
                switch (s3) {
                    case "Obsidian": {
                        itemCheck = "Obby";
                        normalOffHand = false;
                        break;
                    }
                    case "EChest": {
                        itemCheck = "EChest";
                        normalOffHand = false;
                        break;
                    }
                    case "Gapple": {
                        itemCheck = "Gapple";
                        normalOffHand = false;
                        break;
                    }
                    case "Crystal": {
                        itemCheck = "Crystal";
                        normalOffHand = false;
                        break;
                    }
                }
            }
        }
        if (mainHandItem == Items.GOLDEN_APPLE) {
            final String s4 = this.gappleMode.getValue();
            switch (s4) {
                case "Totem": {
                    itemCheck = "Totem";
                    normalOffHand = false;
                    break;
                }
                case "Gapple": {
                    itemCheck = "Gapple";
                    normalOffHand = false;
                    break;
                }
                case "Crystal": {
                    itemCheck = "Crystal";
                    normalOffHand = false;
                    break;
                }
            }
        }
        if (this.crystalGap.getValue() && mainHandItem == Items.END_CRYSTAL) {
            itemCheck = "Gapple";
            normalOffHand = false;
        }
        if (normalOffHand && this.antiWeakness.getValue() && OffHand.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }
        if (this.autoCrystal) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (normalOffHand && !this.nearPlayer()) {
            itemCheck = this.noPlayerItem.getValue();
        }
        itemCheck = this.getItemToCheck(itemCheck, mainTotem);
        return itemCheck;
    }
    
    private boolean canSwitch() {
        final long now = System.currentTimeMillis();
        for (int i = 0; i < this.switchDone.size() && now - this.switchDone.get(i) > 1000L; ++i) {
            this.switchDone.remove(i);
        }
        if (this.switchDone.size() / 2 >= this.maxSwitchPerSecond.getValue()) {
            return true;
        }
        this.switchDone.add(now);
        return false;
    }
    
    private boolean nearPlayer() {
        if (this.playerDistance.getValue().intValue() == 0) {
            return true;
        }
        for (final EntityPlayer pl : OffHand.mc.world.playerEntities) {
            if (pl != OffHand.mc.player && OffHand.mc.player.getDistance(pl) < this.playerDistance.getValue()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean crystalDamage() {
        final PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings(this.predict.getValue(), true, 39, 2, 2, 1, true, true, true, true, 2, 0.15);
        for (final Entity t : OffHand.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && OffHand.mc.player.getDistance(t) <= 12.0f) {
                final EntityPlayer player = PredictUtil.predictPlayer(OffHand.mc.player, settings);
                if (DamageUtil.calculateCrystalDamage(OffHand.mc.player, player.getPositionVector(), player.getEntityBoundingBox(), t.posX, t.posY, t.posZ) * this.biasDamage.getValue() >= EntityUtil.getHealth(OffHand.mc.player) || (DamageUtil.calculateCrystalDamage(OffHand.mc.player, player.getPositionVector(), player.getEntityBoundingBox(), t.posX, t.posY, t.posZ) * this.biasDamage.getValue() >= EntityUtil.getHealth(OffHand.mc.player) && this.totems > 0)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private int findEmptySlot() {
        for (int i = 35; i > -1; --i) {
            if (OffHand.mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean offHandSame(final String itemCheck) {
        final Item offHandItem = OffHand.mc.player.getHeldItemOffhand().getItem();
        if (!this.allowedItemsBlock.containsKey(itemCheck)) {
            final Item item = this.allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        final Block item2 = this.allowedItemsBlock.get(itemCheck);
        if (offHandItem instanceof ItemBlock) {
            return ((ItemBlock)offHandItem).getBlock() != item2;
        }
        return true;
    }
    
    private String getItemToCheck(final String str, final boolean mainTotem) {
        if (mainTotem) {
            return str.isEmpty() ? this.nonDefaultItem.getValue() : str;
        }
        if (OffHand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
            return (PlayerUtil.getHealth() > this.swordHealth.getValue()) ? (str.isEmpty() ? this.nonDefaultItem.getValue() : str) : this.defaultItem.getValue();
        }
        return (PlayerUtil.getHealth() > this.healthSwitch.getValue()) ? (str.isEmpty() ? this.nonDefaultItem.getValue() : str) : this.defaultItem.getValue();
    }
    
    private int getInventorySlot(final String itemName) {
        boolean blockBool = false;
        Object item;
        if (this.allowedItemsItem.containsKey(itemName)) {
            item = this.allowedItemsItem.get(itemName);
        }
        else {
            item = this.allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        if (!this.firstChange && this.prevSlot != -1) {
            final int res = this.isCorrect(this.prevSlot, blockBool, item, itemName);
            if (res != -1) {
                return res;
            }
        }
        for (int i = this.onlyHotBar.getValue() ? 8 : 35; i > (this.noHotBar.getValue() ? 9 : -1); --i) {
            final int res = this.isCorrect(i, blockBool, item, itemName);
            if (res != -1) {
                return res;
            }
        }
        return -1;
    }
    
    private int isCorrect(final int i, final boolean blockBool, final Object item, final String itemName) {
        final Item temp = OffHand.mc.player.inventory.getStackInSlot(i).getItem();
        if (blockBool) {
            if (temp instanceof ItemBlock) {
                if (((ItemBlock)temp).getBlock() == item) {
                    return i;
                }
            }
            else if (temp instanceof ItemSkull && item == Blocks.SKULL) {
                return i;
            }
        }
        else if (item == temp) {
            if (itemName.equals("Pot") && !this.potionChoose.getValue().equalsIgnoreCase("first") && !OffHand.mc.player.inventory.getStackInSlot(i).stackTagCompound.toString().split(":")[2].contains(this.potionChoose.getValue())) {
                return -1;
            }
            return i;
        }
        return -1;
    }
    
    private void toOffHand(final int t) {
        if (!OffHand.mc.player.getHeldItemOffhand().isEmpty()) {
            if (this.firstChange) {
                this.prevSlot = t;
            }
            this.returnBack = true;
            this.firstChange = !this.firstChange;
        }
        else {
            this.prevSlot = -1;
        }
        OffHand.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffHand.mc.player);
        if (this.tickDelay.getValue() == 0) {
            OffHand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffHand.mc.player);
            this.switchDone.add(System.currentTimeMillis());
        }
        else {
            this.stepChanging = true;
        }
        this.tickWaited = 0;
    }
    
    @Override
    public String getHudInfo() {
        if (this.HudMode.getValue().equals("Totem")) {
            this.counts = this.totems;
            if (OffHand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
                ++this.counts;
            }
            return "[" + ChatFormatting.WHITE + "Totem " + this.counts + ChatFormatting.GRAY + "]";
        }
        if (this.itemCheck.isEmpty()) {
            return "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]";
        }
        return "[" + ChatFormatting.WHITE + this.itemCheck + " " + this.counts + ChatFormatting.GRAY + "]";
    }
}
