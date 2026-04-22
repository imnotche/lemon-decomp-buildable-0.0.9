// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBed;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.BlockBed;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import java.util.List;
import java.util.Comparator;
import net.minecraft.util.NonNullList;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.client.module.modules.combat.AntiBurrow;
import com.lemonclient.client.module.modules.combat.AntiRegear;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.init.Items;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.EnumFacing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BedCev", category = Category.Dev)
public class BedCevBreaker extends Module
{
    public static BedCevBreaker INSTANCE;
    IntegerSetting slotS;
    IntegerSetting delay;
    BooleanSetting helpBlock;
    DoubleSetting maxRange;
    BooleanSetting down;
    BooleanSetting packet;
    BooleanSetting rotate;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting instantMine;
    BooleanSetting pickBypass;
    BooleanSetting strict;
    public boolean working;
    boolean offhand;
    boolean start;
    boolean anyBed;
    int blockSlot;
    int bedSlot;
    int pickSlot;
    long time;
    EnumFacing facing;
    Vec2f rotation;
    Timing timer;
    BlockPos[] side;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    BlockPos placePos;
    int lastSlot;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    
    public BedCevBreaker() {
        this.slotS = this.registerInteger("Slot", 1, 1, 9);
        this.delay = this.registerInteger("Delay", 50, 0, 1000);
        this.helpBlock = this.registerBoolean("Help Block", true);
        this.maxRange = this.registerDouble("Max Range", 5.0, 0.0, 10.0, () -> this.helpBlock.getValue());
        this.down = this.registerBoolean("Down Block", true, () -> this.helpBlock.getValue());
        this.packet = this.registerBoolean("Packet Place", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.swing = this.registerBoolean("Swing", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.instantMine = this.registerBoolean("Instant Mine", true);
        this.pickBypass = this.registerBoolean("Pick Bypass", false);
        this.strict = this.registerBoolean("Strict", false);
        this.timer = new Timing();
        this.side = new BlockPos[] { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (this.rotation == null || event.getPhase() != Phase.PRE) {
            }
            else {
                new PlayerPacket(this, new Vec2f(this.rotation.x, PlayerPacketManager.INSTANCE.getServerSideRotation().y));
                final PlayerPacket playerPacket = null;
                final PlayerPacket packet = playerPacket;
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.rotation != null) {
                if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                    ((CPacketPlayer.Rotation)event.getPacket()).yaw = this.rotation.x;
                }
                if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    ((CPacketPlayer.PositionRotation)event.getPacket()).yaw = this.rotation.x;
                }
                if (event.getPacket() instanceof CPacketVehicleMove) {
                    ((AccessorCPacketVehicleMove)event.getPacket()).setYaw(this.rotation.x);
                }
            }
        }, new Predicate[0]);
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (BedCevBreaker.mc.world == null || BedCevBreaker.mc.player == null) {
            }
            else {
                if (event.getPacket() instanceof CPacketHeldItemChange) {
                    final int slot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
                    if (slot != this.lastSlot) {
                        this.lastSlot = slot;
                        if (this.strict.getValue()) {
                            final EnumFacing facing = BlockUtil.getRayTraceFacing(this.placePos, this.facing);
                            BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.placePos, facing));
                            BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, facing));
                            if (this.swing.getValue()) {
                                BedCevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                            this.time = System.currentTimeMillis() + this.calcBreakTime();
                        }
                    }
                }
            }
        }, new Predicate[0]);
        BedCevBreaker.INSTANCE = this;
    }
    
    public void onDisable() {
        this.working = false;
    }
    
    public void refill_bed() {
        if (!(BedCevBreaker.mc.currentScreen instanceof GuiContainer) || BedCevBreaker.mc.currentScreen instanceof GuiInventory) {
            final int airSlot = this.isSpace();
            if (airSlot != -1) {
                for (int i = 9; i < 36; ++i) {
                    if (BedCevBreaker.mc.player.inventory.getStackInSlot(i).getItem() == Items.BED) {
                        BedCevBreaker.mc.playerController.windowClick(0, i, airSlot, ClickType.SWAP, BedCevBreaker.mc.player);
                    }
                }
            }
        }
    }
    
    private int isSpace() {
        int slot = -1;
        final int slot2 = this.slotS.getValue() - 1;
        if (BedCevBreaker.mc.player.inventory.getStackInSlot(slot2).getItem() != Items.BED) {
            slot = slot2;
        }
        return slot;
    }
    
    public void onEnable() {
        if (BedCevBreaker.mc.objectMouseOver == null || BedCevBreaker.mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || BedCevBreaker.mc.world.getBlockState(BedCevBreaker.mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.BEDROCK) {
            this.disable();
            return;
        }
        this.placePos = BedCevBreaker.mc.objectMouseOver.getBlockPos();
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
        if (BedCevBreaker.mc.world == null || BedCevBreaker.mc.player == null || this.placePos == null || BedCevBreaker.mc.player.isDead) {
            this.disable();
            return;
        }
        if (!this.canPlaceBedWithoutBase() || !this.space(this.placePos)) {
            this.disable();
            return;
        }
        this.refill_bed();
        this.getItem();
        if (!this.anyBed || this.blockSlot == -1 || this.pickSlot == -1) {
            this.disable();
            return;
        }
        if (this.bedSlot == -1) {
            return;
        }
        if (BedCevBreaker.mc.world.isAirBlock(this.placePos.north()) && BedCevBreaker.mc.world.isAirBlock(this.placePos.west()) && BedCevBreaker.mc.world.isAirBlock(this.placePos.east()) && BedCevBreaker.mc.world.isAirBlock(this.placePos.south())) {
            this.helpBlock(this.placePos);
            this.rotation = null;
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
            if (instantPos.equals(new BlockPos(BedCevBreaker.mc.player.posX, BedCevBreaker.mc.player.posY + 2.0, BedCevBreaker.mc.player.posZ))) {
                return;
            }
            if (instantPos.equals(new BlockPos(BedCevBreaker.mc.player.posX, BedCevBreaker.mc.player.posY - 1.0, BedCevBreaker.mc.player.posZ))) {
                return;
            }
            if (BedCevBreaker.mc.world.getBlockState(instantPos).getBlock() == Blocks.WEB) {
                return;
            }
            this.doBreak();
        }
        this.working = true;
        if (!this.start && BedCevBreaker.mc.world.isAirBlock(this.placePos)) {
            this.time = System.currentTimeMillis() + (this.instantMine.getValue() ? 0 : this.calcBreakTime());
            this.start = true;
        }
        if (this.time > System.currentTimeMillis()) {
            return;
        }
        if (this.start && this.timer.passedMs(this.delay.getValue())) {
            if (BlockUtil.isAir(this.placePos)) {
                this.run(this.blockSlot, false, () -> BurrowUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
            }
            BlockPos basePos;
            if (this.block(this.placePos.east())) {
                this.rotation = new Vec2f(90.0f, 90.0f);
                basePos = this.placePos.add(1, 0, 0);
            }
            else if (this.block(this.placePos.north())) {
                this.rotation = new Vec2f(0.0f, 90.0f);
                basePos = this.placePos.add(0, 0, -1);
            }
            else if (this.block(this.placePos.west())) {
                this.rotation = new Vec2f(-90.0f, 90.0f);
                basePos = this.placePos.add(-1, 0, 0);
            }
            else {
                if (!this.block(this.placePos.south())) {
                    this.rotation = null;
                    return;
                }
                this.rotation = new Vec2f(180.0f, 90.0f);
                basePos = this.placePos.add(0, 0, 1);
            }
            if (PlayerPacketManager.INSTANCE.getServerSideRotation().x != this.rotation.x) {
                return;
            }
            final EnumHand hand = this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            final EnumFacing opposite = EnumFacing.DOWN.getOpposite();
            final Vec3d hitVec = new Vec3d(basePos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            if (BlockUtil.blackList.contains(BedCevBreaker.mc.world.getBlockState(basePos).getBlock()) && !ColorMain.INSTANCE.sneaking) {
                BedCevBreaker.mc.player.connection.sendPacket(new CPacketEntityAction(BedCevBreaker.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            this.run(this.bedSlot, false, () -> {
                if (this.packet.getValue()) {
                    BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(basePos, EnumFacing.UP, hand, 0.5f, 1.0f, 0.5f));
                }
                else {
                    BedCevBreaker.mc.playerController.processRightClickBlock(BedCevBreaker.mc.player, BedCevBreaker.mc.world, basePos, EnumFacing.UP, hitVec, hand);
                }
                if (this.swing.getValue()) {
                    BedCevBreaker.mc.player.swingArm(hand);
                }
            });
            this.run(this.pickSlot, this.pickBypass.getValue(), () -> {
                this.facing = BlockUtil.getRayTraceFacing(this.placePos, EnumFacing.UP);
                BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                if (!this.instantMine.getValue()) {
                    BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.placePos, this.facing));
                    BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.placePos, this.facing));
                    this.time = System.currentTimeMillis() + this.calcBreakTime();
                }
                if (this.swing.getValue()) {
                    BedCevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            });
            final EnumFacing side = EnumFacing.UP;
            final Vec3d vec = this.getHitVecOffset(side);
            BedCevBreaker.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placePos.up(), side, hand, (float)vec.x, (float)vec.y, (float)vec.z));
            if (this.swing.getValue()) {
                BedCevBreaker.mc.player.swingArm(hand);
            }
            this.timer.reset();
        }
    }
    
    private Vec3d getHitVecOffset(final EnumFacing face) {
        final Vec3i vec = face.getDirectionVec();
        return new Vec3d(vec.x * 0.5f + 0.5f, vec.y * 0.5f + 0.5f, vec.z * 0.5f + 0.5f);
    }
    
    private void helpBlock(final BlockPos pos) {
        final List<BlockPos> blocks = NonNullList.create();
        for (final BlockPos side : this.side) {
            blocks.add(pos.add(side));
        }
        if (this.down.getValue()) {
            blocks.add(pos.down());
        }
        final BlockPos finalPos = blocks.stream().filter(p -> BedCevBreaker.mc.player.getDistanceSq(p) <= this.maxRange.getValue() * this.maxRange.getValue()).filter(this::canPlaceBase).max(Comparator.comparing(p -> BedCevBreaker.mc.player.getDistanceSq(p))).orElse(null);
        this.run(this.blockSlot, false, () -> BurrowUtil.placeBlock(finalPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
    }
    
    private boolean canPlaceBase(final BlockPos pos) {
        return !ColorMain.INSTANCE.breakList.contains(pos) && BurrowUtil.getBedFacing(pos) != null && this.space(pos) && !this.intersectsWithEntity(pos);
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : BedCevBreaker.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean canPlaceBedWithoutBase() {
        return this.space(this.placePos) && (this.space(this.placePos.east()) || this.space(this.placePos.north()) || this.space(this.placePos.west()) || this.space(this.placePos.south()));
    }
    
    private boolean block(final BlockPos pos) {
        return !BlockUtil.canReplace(pos) && this.space(pos) && this.solid(pos);
    }
    
    private boolean solid(final BlockPos pos) {
        return !BlockUtil.isBlockUnSolid(pos) && !(BedCevBreaker.mc.world.getBlockState(pos).getBlock() instanceof BlockBed) && BedCevBreaker.mc.world.getBlockState(pos).isSideSolid(BedCevBreaker.mc.world, pos, EnumFacing.UP) && BlockUtil.getBlock(pos).fullBlock;
    }
    
    private boolean space(final BlockPos pos) {
        return BedCevBreaker.mc.world.getBlockState(pos.up()).getBlock() == Blocks.BED || BedCevBreaker.mc.world.isAirBlock(pos.up());
    }
    
    private void getItem() {
        final int blockSlot = -1;
        this.pickSlot = blockSlot;
        this.bedSlot = blockSlot;
        this.blockSlot = blockSlot;
        this.anyBed = false;
        if (BedCevBreaker.mc.player.getHeldItemOffhand().getItem() instanceof ItemBed) {
            this.bedSlot = 36;
            this.offhand = true;
        }
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = BedCevBreaker.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBed) {
                    this.anyBed = true;
                    if (i < 9) {
                        this.bedSlot = i;
                        break;
                    }
                    break;
                }
            }
        }
        this.blockSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        this.pickSlot = this.findItem();
    }
    
    private void doBreak() {
        if (this.placePos == null || BedCevBreaker.mc.world.isAirBlock(this.placePos) || BedCevBreaker.mc.world.getBlockState(this.placePos).getBlock() == Blocks.BEDROCK) {
            return;
        }
        if (this.swing.getValue()) {
            BedCevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        BedCevBreaker.mc.playerController.onPlayerDamageBlock(this.placePos, BlockUtil.getRayTraceFacing(this.placePos, EnumFacing.UP));
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void run(int slot, final boolean bypass, final Runnable runnable) {
        final int oldslot = BedCevBreaker.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (bypass || slot > 8) {
            if (slot < 9) {
                slot += 36;
            }
            BedCevBreaker.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, BedCevBreaker.mc.player.inventory.currentItem, ClickType.SWAP, ItemStack.EMPTY, BedCevBreaker.mc.player.inventoryContainer.getNextTransactionID(BedCevBreaker.mc.player.inventory)));
            runnable.run();
            BedCevBreaker.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, BedCevBreaker.mc.player.inventory.currentItem, ClickType.SWAP, ItemStack.EMPTY, BedCevBreaker.mc.player.inventoryContainer.getNextTransactionID(BedCevBreaker.mc.player.inventory)));
        }
        else {
            if (this.packetSwitch.getValue()) {
                BedCevBreaker.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                BedCevBreaker.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (this.packetSwitch.getValue()) {
                BedCevBreaker.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                BedCevBreaker.mc.player.inventory.currentItem = oldslot;
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
        int result = BedCevBreaker.mc.player.inventory.currentItem;
        double speed = this.getSpeed(Blocks.OBSIDIAN.getBlockState().getBaseState(), BedCevBreaker.mc.player.getHeldItemMainhand());
        for (int i = 0; i < (this.pickBypass.getValue() ? 36 : 9); ++i) {
            final ItemStack stack = BedCevBreaker.mc.player.inventory.getStackInSlot(i);
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
        final ItemStack itemStack = BedCevBreaker.mc.player.inventory.getStackInSlot(this.pickSlot);
        float digSpeed = BedCevBreaker.mc.player.inventory.getStackInSlot(this.pickSlot).getDestroySpeed(blockState);
        final int efficiencyModifier;
        if (!itemStack.isEmpty() && digSpeed > 1.0 && (efficiencyModifier = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack)) > 0) {
            digSpeed += (float)(StrictMath.pow(efficiencyModifier, 2.0) + 1.0);
        }
        if (BedCevBreaker.mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1.0f + (BedCevBreaker.mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (BedCevBreaker.mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float fatigueScale = 0.0f;
            switch (BedCevBreaker.mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
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
        if (BedCevBreaker.mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(BedCevBreaker.mc.player)) {
            digSpeed /= 5.0f;
        }
        return digSpeed;
    }
}
