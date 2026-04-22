// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.World;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import java.util.Arrays;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.event.events.DrawBlockDamageEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BreakHighlight", category = Category.Render)
public class BreakHighlight extends Module
{
    public static BreakHighlight INSTANCE;
    BooleanSetting cancelAnimation;
    IntegerSetting range;
    IntegerSetting playerRange;
    BooleanSetting showProgress;
    IntegerSetting decimal;
    BooleanSetting doubleMine;
    ColorSetting nameColor;
    ModeSetting renderType;
    ColorSetting color;
    ColorSetting dColor;
    IntegerSetting alpha;
    IntegerSetting outAlpha;
    IntegerSetting width;
    DoubleSetting scale;
    HashMap<EntityPlayer, renderBlock> list;
    BlockPos lastBreak;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<DrawBlockDamageEvent> drawBlockDamageEventListener;
    @EventHandler
    private final Listener<PacketEvent.PostSend> listener;
    
    public BreakHighlight() {
        this.cancelAnimation = this.registerBoolean("No Animation", true);
        this.range = this.registerInteger("Range", 64, 0, 256);
        this.playerRange = this.registerInteger("Player Range", 16, 0, 64);
        this.showProgress = this.registerBoolean("Show Progress", false);
        this.decimal = this.registerInteger("Decimal", 2, 0, 2, () -> this.showProgress.getValue());
        this.doubleMine = this.registerBoolean("Double Mine", true);
        this.nameColor = this.registerColor("Name Color", new GSColor(255, 255, 255));
        this.renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
        this.dColor = this.registerColor("Double Color", new GSColor(0, 255, 0, 255), () -> this.doubleMine.getValue());
        this.alpha = this.registerInteger("Alpha", 100, 0, 255);
        this.outAlpha = this.registerInteger("Outline Alpha", 255, 0, 255);
        this.width = this.registerInteger("Width", 1, 0, 5);
        this.scale = this.registerDouble("Text Scale", 0.025, 0.01, 0.05);
        this.list = new HashMap<EntityPlayer, renderBlock>();
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (BreakHighlight.mc.world == null || BreakHighlight.mc.player == null) {
            }
            else {
                if (event.getPacket() instanceof SPacketBlockBreakAnim) {
                    final SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
                    final BlockPos blockPos = packet.getPosition();
                    if (BreakHighlight.mc.player.getDistanceSq(blockPos) <= this.range.getValue() * this.range.getValue()) {
                        final EntityPlayer entityPlayer = (EntityPlayer)BreakHighlight.mc.world.getEntityByID(packet.getBreakerId());
                        if (entityPlayer != null) {
                            if (this.list.containsKey(entityPlayer)) {
                                if (!this.isPos2(this.list.get(entityPlayer).pos.pos, blockPos)) {
                                    this.list.get(entityPlayer).pos.updatePos(blockPos);
                                }
                            }
                            else {
                                final HashMap<EntityPlayer, renderBlock> list = this.list;
                                new renderBlock(new breakPos(blockPos), entityPlayer);
                                final renderBlock value = null;
                                final Object key = new Object();
                                list.put((EntityPlayer)key, value);
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.drawBlockDamageEventListener = new Listener<DrawBlockDamageEvent>(event -> {
            if (this.cancelAnimation.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
        this.listener = new Listener<PacketEvent.PostSend>(event -> {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging packet2 = (CPacketPlayerDigging)event.getPacket();
                if (packet2.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    this.lastBreak = packet2.getPosition();
                }
            }
        }, new Predicate[0]);
        BreakHighlight.INSTANCE = this;
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (BreakHighlight.mc.player == null || BreakHighlight.mc.world == null) {
            this.list.clear();
            return;
        }
        final List<EntityPlayer> playerList = BreakHighlight.mc.world.playerEntities;
        for (final EntityPlayer player : playerList) {
            if (this.list.containsKey(player)) {
                final BlockPos pos = this.list.get(player).pos.pos;
                BlockPos dPos = this.list.get(player).pos.dPos;
                if (pos != null && BreakHighlight.mc.world.getBlockState(pos).getBlockHardness(BreakHighlight.mc.world, pos) < 0.0f) {
                    this.list.get(player).pos.remove();
                }
                if (dPos != null && BreakHighlight.mc.world.getBlockState(dPos).getBlockHardness(BreakHighlight.mc.world, dPos) < 0.0f) {
                    this.list.get(player).pos.removeDouble();
                }
                if (this.isPos2(pos, dPos)) {
                    dPos = null;
                }
                if (pos == null && dPos == null) {
                    continue;
                }
                final int rangeSq = this.range.getValue() * this.range.getValue();
                final int playerSq = this.playerRange.getValue() * this.playerRange.getValue();
                if (pos != null && BreakHighlight.mc.player.getDistanceSq(pos) > rangeSq && dPos != null && BreakHighlight.mc.player.getDistanceSq(dPos) > rangeSq) {
                    continue;
                }
                if (pos != null && player.getDistanceSq(pos) > playerSq && dPos != null && player.getDistanceSq(dPos) > playerSq) {
                    this.list.remove(player);
                }
                else {
                    this.list.get(player).update();
                }
            }
        }
    }
    
    public static GSColor getRainbowColor(final int damage) {
        return GSColor.fromHSB((1 + damage * 32) % 11520 / 11520.0f, 1.0f, 1.0f);
    }
    
    private void renderBox(final breakPos pos, final EntityPlayer player) {
        final String[] name = { player.getName() };
        final BlockPos blockPos = pos.pos;
        if (blockPos != null) {
            float mineDamage = (System.currentTimeMillis() - pos.start) / (float)pos.time;
            if (mineDamage > 1.0f) {
                mineDamage = 1.0f;
            }
            final AxisAlignedBB getSelectedBoundingBox = new AxisAlignedBB(blockPos);
            final Vec3d getCenter = getSelectedBoundingBox.getCenter();
            final float prognum = mineDamage * 100.0f;
            if (this.showProgress.getValue()) {
                String[] progress = { String.format("%.0f", prognum) };
                if (this.decimal.getValue() == 1) {
                    progress = new String[] { String.format("%.1f", prognum) };
                }
                else if (this.decimal.getValue() == 2) {
                    progress = new String[] { String.format("%.2f", prognum) };
                }
                RenderUtil.drawNametag(blockPos.getX() + 0.5, blockPos.getY() + 0.39, blockPos.getZ() + 0.5, progress, getRainbowColor((int)prognum), 1, this.scale.getValue(), 0.0);
                RenderUtil.drawNametag(blockPos.getX() + 0.5, blockPos.getY() + 0.61, blockPos.getZ() + 0.5, name, new GSColor(this.nameColor.getColor(), 255), 1, this.scale.getValue(), 0.0);
            }
            else {
                RenderUtil.drawNametag(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, name, new GSColor(this.nameColor.getColor(), 255), 1, this.scale.getValue(), 0.0);
            }
            this.renderESP(new AxisAlignedBB(getCenter.x, getCenter.y, getCenter.z, getCenter.x, getCenter.y, getCenter.z).grow((getSelectedBoundingBox.minX - getSelectedBoundingBox.maxX) * 0.5 * MathHelper.clamp(mineDamage, 0.0f, 1.0f), (getSelectedBoundingBox.minY - getSelectedBoundingBox.maxY) * 0.5 * MathHelper.clamp(mineDamage, 0.0f, 1.0f), (getSelectedBoundingBox.minZ - getSelectedBoundingBox.maxZ) * 0.5 * MathHelper.clamp(mineDamage, 0.0f, 1.0f)), false);
        }
        if (!this.doubleMine.getValue()) {
            return;
        }
        final BlockPos doubleBlockPos = pos.dPos;
        if (doubleBlockPos != null) {
            float doubleMineDamage = (System.currentTimeMillis() - pos.dStart) / (float)pos.dTime;
            if (doubleMineDamage > 1.0f) {
                doubleMineDamage = 1.0f;
            }
            final AxisAlignedBB getDoubleSelectedBoundingBox = new AxisAlignedBB(doubleBlockPos);
            final Vec3d getDoubleCenter = getDoubleSelectedBoundingBox.getCenter();
            final float doublePrognum = doubleMineDamage * 100.0f;
            if (this.showProgress.getValue()) {
                String[] progress2 = { String.format("%.0f", doublePrognum) };
                if (this.decimal.getValue() == 1) {
                    progress2 = new String[] { String.format("%.1f", doublePrognum) };
                }
                else if (this.decimal.getValue() == 2) {
                    progress2 = new String[] { String.format("%.2f", doublePrognum) };
                }
                RenderUtil.drawNametag(doubleBlockPos.getX() + 0.5, doubleBlockPos.getY() + 0.39, doubleBlockPos.getZ() + 0.5, progress2, getRainbowColor((int)doublePrognum), 1, this.scale.getValue(), 0.0);
                RenderUtil.drawNametag(doubleBlockPos.getX() + 0.5, doubleBlockPos.getY() + 0.61, doubleBlockPos.getZ() + 0.5, name, new GSColor(this.nameColor.getColor(), 255), 1, this.scale.getValue(), 0.0);
            }
            else {
                RenderUtil.drawNametag(doubleBlockPos.getX() + 0.5, doubleBlockPos.getY() + 0.5, doubleBlockPos.getZ() + 0.5, name, new GSColor(this.nameColor.getColor(), 255), 1, this.scale.getValue(), 0.0);
            }
            this.renderESP(new AxisAlignedBB(getDoubleCenter.x, getDoubleCenter.y, getDoubleCenter.z, getDoubleCenter.x, getDoubleCenter.y, getDoubleCenter.z).grow((getDoubleSelectedBoundingBox.minX - getDoubleSelectedBoundingBox.maxX) * 0.5 * MathHelper.clamp(doubleMineDamage, 0.0f, 1.0f), (getDoubleSelectedBoundingBox.minY - getDoubleSelectedBoundingBox.maxY) * 0.5 * MathHelper.clamp(doubleMineDamage, 0.0f, 1.0f), (getDoubleSelectedBoundingBox.minZ - getDoubleSelectedBoundingBox.maxZ) * 0.5 * MathHelper.clamp(doubleMineDamage, 0.0f, 1.0f)), true);
        }
    }
    
    private void renderESP(final AxisAlignedBB axisAlignedBB, final boolean dm) {
        final GSColor fillColor = new GSColor(dm ? this.dColor.getValue() : this.color.getValue(), this.alpha.getValue());
        final GSColor outlineColor = new GSColor(dm ? this.dColor.getValue() : this.color.getValue(), this.outAlpha.getValue());
        final String s = this.renderType.getValue();
        switch (s) {
            case "Fill": {
                RenderUtil.drawBox(axisAlignedBB, true, 0.0, fillColor, 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(axisAlignedBB, this.width.getValue(), outlineColor);
                break;
            }
            default: {
                RenderUtil.drawBox(axisAlignedBB, true, 0.0, fillColor, 63);
                RenderUtil.drawBoundingBox(axisAlignedBB, this.width.getValue(), outlineColor);
                break;
            }
        }
    }
    
    private int calcBreakTime(final BlockPos pos) {
        if (pos == null) {
            return -1;
        }
        final IBlockState blockState = BreakHighlight.mc.world.getBlockState(pos);
        final float hardness = blockState.getBlockHardness(BreakHighlight.mc.world, pos);
        final float breakSpeed = this.getBreakSpeed(pos, blockState);
        if (breakSpeed == -1.0f) {
            return -1;
        }
        final float relativeDamage = breakSpeed / hardness / 30.0f;
        final int ticks = (int)Math.ceil(0.7f / relativeDamage);
        return ticks * 50;
    }
    
    private float getBreakSpeed(final BlockPos pos, final IBlockState blockState) {
        float maxSpeed = 1.0f;
        final int slot = this.findItem(pos);
        float speed = BreakHighlight.mc.player.inventory.getStackInSlot(slot).getDestroySpeed(blockState);
        if (speed <= 1.0f) {
            return maxSpeed;
        }
        final int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, BreakHighlight.mc.player.inventory.getStackInSlot(slot));
        if (efficiency > 0) {
            speed += efficiency * efficiency + 1.0f;
        }
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        return maxSpeed;
    }
    
    public int findItem(final BlockPos pos) {
        if (pos == null) {
            return BreakHighlight.mc.player.inventory.currentItem;
        }
        return findBestTool(pos, BreakHighlight.mc.world.getBlockState(pos));
    }
    
    public static int findBestTool(final BlockPos pos, final IBlockState state) {
        int result = BreakHighlight.mc.player.inventory.currentItem;
        if (state.getBlockHardness(BreakHighlight.mc.world, pos) > 0.0f) {
            double speed = getSpeed(state, BreakHighlight.mc.player.getHeldItemMainhand());
            for (int i = 0; i < 36; ++i) {
                final ItemStack stack = BreakHighlight.mc.player.inventory.getStackInSlot(i);
                final double stackSpeed = getSpeed(state, stack);
                if (stackSpeed > speed) {
                    speed = stackSpeed;
                    result = i;
                }
            }
        }
        return result;
    }
    
    public static double getSpeed(final IBlockState state, final ItemStack stack) {
        final double str = stack.getDestroySpeed(state);
        final int effect = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        return Math.max(str + ((str > 1.0) ? (effect * effect + 1.0) : 0.0), 0.0);
    }
    
    class renderBlock
    {
        private final breakPos pos;
        private final EntityPlayer player;
        
        public renderBlock(final breakPos pos, final EntityPlayer player) {
            this.pos = pos;
            this.player = player;
        }
        
        void update() {
            this.pos.update();
            BreakHighlight.this.renderBox(this.pos, this.player);
        }
    }
    
    public static class breakPos
    {
        private BlockPos pos;
        private BlockPos dPos;
        private long start;
        private long dStart;
        private long time;
        private long dTime;
        
        public breakPos(final BlockPos pos) {
            this.dPos = null;
            this.pos = pos;
            this.start = System.currentTimeMillis();
            this.time = BreakHighlight.INSTANCE.calcBreakTime(pos);
        }
        
        public void updatePos(final BlockPos pos) {
            if (this.dPos == null) {
                this.dPos = this.pos;
                this.dStart = this.start;
                this.dTime = (long)(this.time * 1.4);
            }
            this.pos = pos;
            this.start = System.currentTimeMillis();
            this.time = BreakHighlight.INSTANCE.calcBreakTime(pos);
        }
        
        public long getEnd() {
            return this.start + this.time;
        }
        
        public void update() {
            this.time = BreakHighlight.INSTANCE.calcBreakTime(this.pos);
            if (this.dPos != null && BlockUtil.airBlocks.contains(BreakHighlight.mc.world.getBlockState(this.dPos).getBlock())) {
                this.removeDouble();
            }
        }
        
        public void remove() {
            this.pos = null;
        }
        
        public void removeDouble() {
            this.dPos = null;
        }
    }
}
