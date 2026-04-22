// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.List;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Collection;
import com.lemonclient.client.module.modules.dev.PistonAura;
import java.util.ArrayList;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BlockHead", category = Category.Combat)
public class BlockHead extends Module
{
    IntegerSetting delay;
    DoubleSetting range;
    IntegerSetting maxTarget;
    DoubleSetting maxSpeed;
    IntegerSetting bpt;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting pause;
    int ob;
    int waited;
    int placed;
    BlockPos[] block;
    BlockPos[] sides;
    
    public BlockHead() {
        this.delay = this.registerInteger("Delay", 0, 0, 20);
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.maxTarget = this.registerInteger("Max Target", 1, 1, 10);
        this.maxSpeed = this.registerDouble("Max Target Speed", 10.0, 0.0, 50.0);
        this.bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
        this.rotate = this.registerBoolean("Rotate", false);
        this.packet = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.pause = this.registerBoolean("BedrockHole", true);
        this.block = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 1, 0) };
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
    }
    
    public static boolean isPlayerInHole(final EntityPlayer target) {
        final BlockPos blockPos = getLocalPlayerPosFloored(target);
        final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(blockPos, true, true, false);
        final HoleUtil.HoleType holeType = holeInfo.getType();
        return holeType == HoleUtil.HoleType.SINGLE;
    }
    
    public static BlockPos getLocalPlayerPosFloored(final EntityPlayer target) {
        return new BlockPos(target.getPositionVector());
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : BlockHead.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (entity instanceof EntityArmorStand) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        if (BlockHead.mc.world == null || BlockHead.mc.player == null || BlockHead.mc.player.isDead) {
            return;
        }
        this.placed = 0;
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        this.waited = 0;
        this.ob = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (this.ob == -1) {
            return;
        }
        for (final EntityPlayer target : PlayerUtil.getNearPlayers(this.range.getValue(), this.maxTarget.getValue())) {
            if (target != null) {
                if (EntityUtil.isDead(target)) {
                    continue;
                }
                if (LemonClient.speedUtil.getPlayerSpeed(target) > this.maxSpeed.getValue()) {
                    continue;
                }
                if (!isPlayerInHole(target)) {
                    continue;
                }
                final BlockPos pos = new BlockPos(target.posX, target.posY + 0.5, target.posZ);
                int bedrock = 0;
                for (final BlockPos side : this.sides) {
                    if (BlockHead.mc.world.getBlockState(pos.add(side)).getBlock() == Blocks.BEDROCK) {
                        ++bedrock;
                    }
                }
                if (bedrock >= 4 && !this.pause.getValue()) {
                    continue;
                }
                final BlockPos placePos = pos.up(2);
                if (!BlockUtil.isAir(placePos) || this.intersectsWithEntity(placePos)) {
                    continue;
                }
                if (BurrowUtil.getFirstFacing(pos.up(2)) == null) {
                    final List<BlockPos> posList = new ArrayList<BlockPos>();
                    final List<BlockPos> list = new ArrayList<BlockPos>();
                    for (final BlockPos side2 : this.sides) {
                        final BlockPos crystalPos = pos.add(side2);
                        if (!PistonAura.INSTANCE.canPistonCrystal(crystalPos, pos)) {
                            posList.add(crystalPos);
                        }
                        list.add(crystalPos);
                    }
                    if (posList.isEmpty()) {
                        for (final BlockPos side2 : this.sides) {
                            final BlockPos crystalPos = pos.add(side2);
                            if (!PistonAura.INSTANCE.canPistonCrystal(crystalPos.up(), pos)) {
                                posList.add(crystalPos);
                            }
                            list.add(crystalPos);
                        }
                    }
                    if (posList.isEmpty()) {
                        posList.addAll(list);
                    }
                    final BlockPos side = posList.stream().max(Comparator.comparingDouble(PlayerUtil::getDistance)).orElse(null);
                    if (side == null) {
                        continue;
                    }
                    for (final BlockPos add : this.block) {
                        if (this.placed > this.bpt.getValue()) {
                            return;
                        }
                        final BlockPos obsi = side.up().add(add);
                        if (!this.intersectsWithEntity(obsi) && BlockUtil.canReplace(obsi)) {
                            InventoryUtil.run(this.ob, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(obsi, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
                            ++this.placed;
                        }
                    }
                }
                if (this.placed > this.bpt.getValue()) {
                    return;
                }
                InventoryUtil.run(this.ob, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
                ++this.placed;
            }
        }
    }
}
