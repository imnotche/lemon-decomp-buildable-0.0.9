// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import com.lemonclient.api.util.misc.Wrapper;
import net.minecraft.init.MobEffects;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemEndCrystal;
import java.util.List;
import java.util.Comparator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockAir;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import java.util.function.Predicate;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.EnumFacing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "CevBreaker", category = Category.Combat)
public class CevBreaker extends Module
{
    public static CevBreaker INSTANCE;
    ModeSetting page;
    IntegerSetting delay;
    BooleanSetting helpBlock;
    DoubleSetting maxRange;
    BooleanSetting down;
    BooleanSetting packet;
    BooleanSetting rotate;
    BooleanSetting strictFacing;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting bypassSwitch;
    BooleanSetting instantMine;
    BooleanSetting pickBypass;
    BooleanSetting strict;
    BooleanSetting packetCrystal;
    BooleanSetting crystalBypass;
    IntegerSetting breakDelay;
    ModeSetting breakCrystal;
    BooleanSetting airCheck;
    BooleanSetting antiWeakness;
    public boolean working;
    boolean offhand;
    boolean start;
    boolean anyCrystal;
    int blockSlot;
    int crystalSlot;
    int pickSlot;
    long time;
    EnumFacing facing;
    Timing timer;
    Timing breakTimer;
    BlockPos[] side;
    BlockPos placePos;
    int lastSlot;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    
    public CevBreaker() {
        this.page = this.registerMode("Page", Arrays.asList("General", "Place"), "General");
        this.delay = this.registerInteger("Delay", 50, 0, 1000, () -> this.page.getValue().equals("General"));
        this.helpBlock = this.registerBoolean("Help Block", true, () -> this.page.getValue().equals("General"));
        this.maxRange = this.registerDouble("Max Range", 5.0, 0.0, 10.0, () -> this.helpBlock.getValue() && this.page.getValue().equals("General"));
        this.down = this.registerBoolean("Down Block", true, () -> this.helpBlock.getValue() && this.page.getValue().equals("General"));
        this.packet = this.registerBoolean("Packet Place", true, () -> this.page.getValue().equals("General"));
        this.rotate = this.registerBoolean("Rotate", false, () -> this.page.getValue().equals("General"));
        this.strictFacing = this.registerBoolean("Strict Facing", false, () -> this.page.getValue().equals("General"));
        this.swing = this.registerBoolean("Swing", true, () -> this.page.getValue().equals("General"));
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("General"));
        this.bypassSwitch = this.registerBoolean("Bypass Switch", false, () -> this.page.getValue().equals("General"));
        this.instantMine = this.registerBoolean("Instant Mine", true, () -> this.page.getValue().equals("General"));
        this.pickBypass = this.registerBoolean("Pick Bypass", false, () -> this.page.getValue().equals("General"));
        this.strict = this.registerBoolean("Strict", false, () -> this.page.getValue().equals("General"));
        this.packetCrystal = this.registerBoolean("Packet Crystal", false, () -> this.page.getValue().equals("Place"));
        this.crystalBypass = this.registerBoolean("Crystal Bypass", false, () -> this.page.getValue().equals("Place"));
        this.breakDelay = this.registerInteger("Break Delay", 50, 0, 1000, () -> this.page.getValue().equals("Place"));
        this.breakCrystal = this.registerMode("Break Crystal", Arrays.asList("Vanilla", "Packet"), "Packet", () -> this.page.getValue().equals("Place"));
        this.airCheck = this.registerBoolean("Air Check", true, () -> this.page.getValue().equals("Place"));
        this.antiWeakness = this.registerBoolean("AntiWeakness", true, () -> this.page.getValue().equals("Place"));
        this.timer = new Timing();
        this.breakTimer = new Timing();
        this.side = new BlockPos[] { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (CevBreaker.mc.world == null || CevBreaker.mc.player == null) {
            }
            else {
                if (event.getPacket() instanceof CPacketHeldItemChange) {
                    final int slot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
                    if (slot != this.lastSlot) {
                        this.lastSlot = slot;
                        if (this.strict.getValue()) {
                            final EnumFacing facing = BlockUtil.getRayTraceFacing(this.placePos, this.facing);
                            CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.placePos, facing));
                            CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, facing));
                            if (this.swing.getValue()) {
                                CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                            this.time = System.currentTimeMillis() + this.calcBreakTime();
                        }
                    }
                }
            }
        }, new Predicate[0]);
        CevBreaker.INSTANCE = this;
    }
    
    public void onDisable() {
        this.working = false;
    }
    
    public void onEnable() {
        if (CevBreaker.mc.objectMouseOver == null || CevBreaker.mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || CevBreaker.mc.world.getBlockState(CevBreaker.mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.BEDROCK) {
            this.disable();
            return;
        }
        this.placePos = CevBreaker.mc.objectMouseOver.getBlockPos();
        final boolean b = false;
        this.offhand = b;
        this.start = b;
        this.getItem();
        this.doBreak();
        this.timer.reset();
    }
    
    @Override
    public void fast() {
        this.working = false;
        if (CevBreaker.mc.world == null || CevBreaker.mc.player == null || this.placePos == null || CevBreaker.mc.player.isDead) {
            this.disable();
            return;
        }
        if (!CevBreaker.mc.world.isAirBlock(this.placePos.up()) || !CevBreaker.mc.world.isAirBlock(this.placePos.up().up())) {
            this.disable();
            return;
        }
        this.getItem();
        if (!this.anyCrystal || this.blockSlot == -1 || this.pickSlot == -1) {
            this.disable();
            return;
        }
        if (this.crystalSlot == -1) {
            return;
        }
        if (CevBreaker.mc.world.isAirBlock(this.placePos.down()) && CevBreaker.mc.world.isAirBlock(this.placePos.north()) && CevBreaker.mc.world.isAirBlock(this.placePos.west()) && CevBreaker.mc.world.isAirBlock(this.placePos.east()) && CevBreaker.mc.world.isAirBlock(this.placePos.south())) {
            this.helpBlock(this.placePos);
            return;
        }
        if (AntiRegear.INSTANCE.working || AntiBurrow.INSTANCE.mining) {
            return;
        }
        BlockPos instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        if (instantPos != null && !this.isPos2(instantPos, this.placePos)) {
            if (instantPos.equals(new BlockPos(CevBreaker.mc.player.posX, CevBreaker.mc.player.posY + 2.0, CevBreaker.mc.player.posZ))) {
                return;
            }
            if (instantPos.equals(new BlockPos(CevBreaker.mc.player.posX, CevBreaker.mc.player.posY - 1.0, CevBreaker.mc.player.posZ))) {
                return;
            }
            if (CevBreaker.mc.world.getBlockState(instantPos).getBlock() == Blocks.WEB) {
                return;
            }
            this.doBreak();
        }
        this.working = true;
        if (!this.start && CevBreaker.mc.world.isAirBlock(this.placePos)) {
            this.time = System.currentTimeMillis() + (this.instantMine.getValue() ? 0 : this.calcBreakTime());
            this.start = true;
        }
        final Entity crystal = this.getCrystal();
        if (CevBreaker.mc.world.getBlockState(this.placePos).getBlock() instanceof BlockAir) {
            this.breakCrystalPiston(crystal);
            this.breakTimer.reset();
        }
        if (this.time > System.currentTimeMillis()) {
            return;
        }
        if (this.start && this.timer.passedMs(this.delay.getValue())) {
            this.run(this.blockSlot, this.bypassSwitch.getValue(), false, () -> BurrowUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
            this.run(this.crystalSlot, this.crystalBypass.getValue(), true, () -> this.placeCrystal(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            this.run(this.pickSlot, this.pickBypass.getValue(), false, () -> {
                this.facing = EnumFacing.UP;
                if (this.strictFacing.getValue()) {
                    this.facing = BlockUtil.getRayTraceFacing(this.placePos, this.facing);
                }
                CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                if (!this.instantMine.getValue()) {
                    CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.placePos, this.facing));
                    CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                    this.time = System.currentTimeMillis() + this.calcBreakTime();
                }
                if (this.swing.getValue()) {
                    CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            });
            if (!this.airCheck.getValue() || BlockUtil.isAir(this.placePos)) {
                this.breakCrystalPiston(this.getCrystal());
            }
            this.timer.reset();
        }
    }
    
    private void helpBlock(final BlockPos pos) {
        final List<BlockPos> blocks = NonNullList.create();
        for (final BlockPos side : this.side) {
            blocks.add(pos.add(side));
        }
        if (this.down.getValue()) {
            blocks.add(pos.down());
        }
        final BlockPos finalPos = blocks.stream().filter(p -> CevBreaker.mc.player.getDistanceSq(p) <= this.maxRange.getValue() * this.maxRange.getValue()).max(Comparator.comparing(p -> CevBreaker.mc.player.getDistanceSq(p))).orElse(null);
        this.run(this.blockSlot, this.bypassSwitch.getValue(), false, () -> BurrowUtil.placeBlock(finalPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
    }
    
    private void getItem() {
        final int blockSlot = -1;
        this.pickSlot = blockSlot;
        this.crystalSlot = blockSlot;
        this.blockSlot = blockSlot;
        this.anyCrystal = false;
        if (CevBreaker.mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            this.crystalSlot = 11;
            this.offhand = true;
        }
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = CevBreaker.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemEndCrystal) {
                    this.anyCrystal = true;
                    if (this.crystalBypass.getValue() || i < 9) {
                        this.crystalSlot = i;
                        break;
                    }
                    break;
                }
            }
        }
        this.blockSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        this.pickSlot = this.findItem();
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (crystal == null) {
            return;
        }
        if (!this.breakTimer.passedMs(this.breakDelay.getValue())) {
            return;
        }
        this.breakTimer.reset();
        int newSlot = -1;
        if (this.antiWeakness.getValue() && CevBreaker.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
                if (stack != ItemStack.EMPTY) {
                    if (stack.getItem() instanceof ItemSword) {
                        newSlot = i;
                        break;
                    }
                    if (stack.getItem() instanceof ItemTool) {
                        newSlot = i;
                    }
                }
            }
        }
        this.run(newSlot, this.pickBypass.getValue(), false, () -> {
            if (this.breakCrystal.getValue().equalsIgnoreCase("Vanilla")) {
                CrystalUtil.breakCrystal(crystal, this.swing.getValue());
            }
            else if (this.breakCrystal.getValue().equalsIgnoreCase("Packet")) {
                CrystalUtil.breakCrystalPacket(crystal, this.swing.getValue());
            }
        });
    }
    
    private Entity getCrystal() {
        for (final Entity t : CevBreaker.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && t.getDistance(this.placePos.x + 0.5, this.placePos.y + 1.5, this.placePos.z + 0.5) < 3.0) {
                return t;
            }
        }
        return null;
    }
    
    private void doBreak() {
        if (this.placePos == null || CevBreaker.mc.world.isAirBlock(this.placePos) || CevBreaker.mc.world.getBlockState(this.placePos).getBlock() == Blocks.BEDROCK) {
            return;
        }
        if (this.swing.getValue()) {
            CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        CevBreaker.mc.playerController.onPlayerDamageBlock(this.placePos, BlockUtil.getRayTraceFacing(this.placePos, EnumFacing.UP));
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void placeCrystal(final EnumHand hand) {
        if (this.packetCrystal.getValue()) {
            CevBreaker.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placePos, EnumFacing.UP, hand, 0.0f, 0.0f, 0.0f));
        }
        else {
            CevBreaker.mc.playerController.processRightClickBlock(CevBreaker.mc.player, CevBreaker.mc.world, this.placePos, EnumFacing.UP, new Vec3d(this.placePos).add(0.5, 0.5, 0.5).add(new Vec3d(EnumFacing.UP.getDirectionVec())), hand);
        }
        if (this.swing.getValue()) {
            CevBreaker.mc.player.swingArm(hand);
        }
    }
    
    private void run(int slot, final boolean bypass, final boolean update, final Runnable runnable) {
        final int oldslot = CevBreaker.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (bypass || slot > 8) {
            final ItemStack itemStack = CevBreaker.mc.player.inventory.getStackInSlot(slot);
            if (slot < 9) {
                slot += 36;
            }
            CevBreaker.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, CevBreaker.mc.player.inventory.currentItem, ClickType.SWAP, ItemStack.EMPTY, CevBreaker.mc.player.inventoryContainer.getNextTransactionID(CevBreaker.mc.player.inventory)));
            runnable.run();
            CevBreaker.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, CevBreaker.mc.player.inventory.currentItem, ClickType.SWAP, update ? itemStack : ItemStack.EMPTY, CevBreaker.mc.player.inventoryContainer.getNextTransactionID(CevBreaker.mc.player.inventory)));
        }
        else {
            if (this.packetSwitch.getValue()) {
                CevBreaker.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                CevBreaker.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (this.packetSwitch.getValue()) {
                CevBreaker.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                CevBreaker.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    private int calcBreakTime() {
        return this.getBreakTime() * 70;
    }
    
    private int getBreakTime() {
        final float hardness = 50.0f;
        final float breakSpeed = this.getSpeed(Blocks.OBSIDIAN.getBlockState().getBaseState());
        if (breakSpeed < 0.0f) {
            return -1;
        }
        final float relativeDamage = this.getSpeed(Blocks.OBSIDIAN.getBlockState().getBaseState()) / hardness / 30.0f;
        return (int)Math.ceil(0.7f / relativeDamage);
    }
    
    private int findItem() {
        int result = CevBreaker.mc.player.inventory.currentItem;
        double speed = this.getSpeed(Blocks.OBSIDIAN.getBlockState().getBaseState(), CevBreaker.mc.player.getHeldItemMainhand());
        for (int i = 0; i < (this.pickBypass.getValue() ? 36 : 9); ++i) {
            final ItemStack stack = CevBreaker.mc.player.inventory.getStackInSlot(i);
            final double stackSpeed = this.getSpeed(Blocks.OBSIDIAN.getBlockState().getBaseState(), stack);
            if (stackSpeed > speed) {
                speed = stackSpeed;
                result = i;
            }
        }
        return result;
    }
    
    private double getSpeed(final IBlockState state, final ItemStack stack) {
        final double str = stack.getDestroySpeed(state);
        final int effect = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        return Math.max(str + ((str > 1.0) ? (effect * effect + 1.0) : 0.0), 0.0);
    }
    
    private float getSpeed(final IBlockState blockState) {
        final ItemStack itemStack = CevBreaker.mc.player.inventory.getStackInSlot(this.pickSlot);
        float digSpeed = CevBreaker.mc.player.inventory.getStackInSlot(this.pickSlot).getDestroySpeed(blockState);
        final int efficiencyModifier;
        if (!itemStack.isEmpty() && digSpeed > 1.0 && (efficiencyModifier = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack)) > 0) {
            digSpeed += (float)(StrictMath.pow(efficiencyModifier, 2.0) + 1.0);
        }
        if (CevBreaker.mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1.0f + (CevBreaker.mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (CevBreaker.mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float fatigueScale = 0.0f;
            switch (CevBreaker.mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    fatigueScale = 0.3f;
                    break;
                }
                case 1: {
                    fatigueScale = 0.09f;
                    break;
                }
                case 2: {
                    fatigueScale = 0.0027f;
                    break;
                }
                default: {
                    fatigueScale = 8.1E-4f;
                    break;
                }
            }
            digSpeed *= fatigueScale;
        }
        if (CevBreaker.mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(CevBreaker.mc.player)) {
            digSpeed /= 5.0f;
        }
        return digSpeed;
    }
}
