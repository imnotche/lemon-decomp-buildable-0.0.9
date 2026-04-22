// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world.combat;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.HashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import java.util.Set;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.Minecraft;

public class HoleFinder
{
    private static final Minecraft mc;
    private static final Vec3i[] OFFSETS_2x2;
    public static final Set<Block> NO_BLAST;
    public static final Set<Block> UNSAFE;
    
    public static boolean isAir(final BlockPos pos) {
        return HoleFinder.mc.world.isAirBlock(pos);
    }
    
    public static boolean[] isHole(final BlockPos pos, final boolean above) {
        final boolean[] result = { false, true };
        if (!isAir(pos) || !isAir(pos.up()) || (above && !isAir(pos.up(2)))) {
            return result;
        }
        return is1x1(pos, result);
    }
    
    public static boolean[] is1x1(final BlockPos pos) {
        return is1x1(pos, new boolean[] { false, true });
    }
    
    public static boolean[] is1x1(final BlockPos pos, final boolean[] result) {
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.UP) {
                final BlockPos offset = pos.offset(facing);
                final IBlockState state = HoleFinder.mc.world.getBlockState(offset);
                if (state.getBlock() != Blocks.BEDROCK) {
                    if (!HoleFinder.NO_BLAST.contains(state.getBlock())) {
                        return result;
                    }
                    result[1] = false;
                }
            }
        }
        result[0] = true;
        return result;
    }
    
    public static boolean is2x1(final BlockPos pos) {
        return is2x1(pos, true);
    }
    
    public static boolean is2x1(final BlockPos pos, final boolean upper) {
        if (upper && (!isAir(pos) || !isAir(pos.up()) || isAir(pos.down()))) {
            return false;
        }
        int airBlocks = 0;
        for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
            final BlockPos offset = pos.offset(facing);
            if (isAir(offset)) {
                if (!isAir(offset.up())) {
                    return false;
                }
                if (isAir(offset.down())) {
                    return false;
                }
                for (final EnumFacing offsetFacing : EnumFacing.HORIZONTALS) {
                    if (offsetFacing != facing.getOpposite()) {
                        final IBlockState state = HoleFinder.mc.world.getBlockState(offset.offset(offsetFacing));
                        if (!HoleFinder.NO_BLAST.contains(state.getBlock())) {
                            return false;
                        }
                    }
                }
                ++airBlocks;
            }
            if (airBlocks > 1) {
                return false;
            }
        }
        return airBlocks == 1;
    }
    
    public static boolean is2x2Partial(final BlockPos pos) {
        final Set<BlockPos> positions = new HashSet<BlockPos>();
        for (final Vec3i vec : HoleFinder.OFFSETS_2x2) {
            positions.add(pos.add(vec));
        }
        boolean airBlock = false;
        for (final BlockPos holePos : positions) {
            if (!isAir(holePos) || !isAir(holePos.up()) || isAir(holePos.down())) {
                return false;
            }
            if (isAir(holePos.up(2))) {
                airBlock = true;
            }
            for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
                final BlockPos offset = holePos.offset(facing);
                if (!positions.contains(offset)) {
                    final IBlockState state = HoleFinder.mc.world.getBlockState(offset);
                    if (!HoleFinder.NO_BLAST.contains(state.getBlock())) {
                        return false;
                    }
                }
            }
        }
        return airBlock;
    }
    
    public static boolean is2x2(final BlockPos pos) {
        return is2x2(pos, true);
    }
    
    public static boolean is2x2(final BlockPos pos, final boolean upper) {
        if (upper && !isAir(pos)) {
            return false;
        }
        if (is2x2Partial(pos)) {
            return true;
        }
        final BlockPos l = pos.add(-1, 0, 0);
        final boolean airL = isAir(l);
        if (airL && is2x2Partial(l)) {
            return true;
        }
        final BlockPos r = pos.add(0, 0, -1);
        final boolean airR = isAir(r);
        return (airR && is2x2Partial(r)) || ((airL || airR) && is2x2Partial(pos.add(-1, 0, -1)));
    }
    
    public static boolean is2x2single(final BlockPos pos, final boolean upper) {
        return (!upper || isAir(pos)) && is2x2Partial(pos);
    }
    
    static {
        mc = Minecraft.getMinecraft();
        OFFSETS_2x2 = new Vec3i[] { new Vec3i(0, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 1) };
        NO_BLAST = Sets.newHashSet(Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ANVIL, Blocks.ENDER_CHEST);
        UNSAFE = Sets.newHashSet(Blocks.OBSIDIAN, Blocks.ANVIL, Blocks.ENDER_CHEST);
    }
}
