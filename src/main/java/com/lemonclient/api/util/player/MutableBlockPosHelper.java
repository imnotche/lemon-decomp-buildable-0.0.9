// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.util.math.BlockPos;

public class MutableBlockPosHelper
{
    public BlockPos.MutableBlockPos mutablePos;
    
    public MutableBlockPosHelper() {
        this.mutablePos = new BlockPos.MutableBlockPos();
    }
    
    public static BlockPos.MutableBlockPos set(final BlockPos.MutableBlockPos mutablePos, final double x, final double y, final double z) {
        return mutablePos.setPos(x, y, z);
    }
    
    public static BlockPos.MutableBlockPos set(final BlockPos.MutableBlockPos mutablePos, final BlockPos pos) {
        return mutablePos.setPos(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static BlockPos.MutableBlockPos set(final BlockPos.MutableBlockPos mutablePos, final BlockPos pos, final double x, final double y, final double z) {
        return mutablePos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
    
    public static BlockPos.MutableBlockPos set(final BlockPos.MutableBlockPos mutablePos, final BlockPos pos, final int x, final int y, final int z) {
        return mutablePos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
    
    public static BlockPos.MutableBlockPos set(final BlockPos.MutableBlockPos mutablePos, final int x, final int y, final int z) {
        return mutablePos.setPos(x, y, z);
    }
    
    public static BlockPos.MutableBlockPos setAndAdd(final BlockPos.MutableBlockPos mutablePos, final int x, final int y, final int z) {
        return mutablePos.setPos(mutablePos.getX() + x, mutablePos.getY() + y, mutablePos.getZ() + z);
    }
    
    public static BlockPos.MutableBlockPos setAndAdd(final BlockPos.MutableBlockPos mutablePos, final double x, final double y, final double z) {
        return mutablePos.setPos(mutablePos.getX() + x, mutablePos.getY() + y, mutablePos.getZ() + z);
    }
    
    public static BlockPos.MutableBlockPos setAndAdd(final BlockPos.MutableBlockPos mutablePos, final BlockPos pos) {
        return mutablePos.setPos(mutablePos.getX() + pos.getX(), mutablePos.getY() + pos.getY(), mutablePos.getZ() + pos.getZ());
    }
    
    public static BlockPos.MutableBlockPos setAndAdd(final BlockPos.MutableBlockPos mutablePos, final BlockPos pos, final double x, final double y, final double z) {
        return mutablePos.setPos(mutablePos.getX() + pos.getX() + x, mutablePos.getY() + pos.getY() + y, mutablePos.getZ() + pos.getZ() + z);
    }
    
    public BlockPos.MutableBlockPos set(final double x, final double y, final double z) {
        return this.mutablePos.setPos(x, y, z);
    }
    
    public BlockPos.MutableBlockPos set(final BlockPos pos) {
        return this.mutablePos.setPos(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public BlockPos.MutableBlockPos set(final BlockPos pos, final double x, final double y, final double z) {
        return this.mutablePos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
    
    public BlockPos.MutableBlockPos set(final BlockPos pos, final int x, final int y, final int z) {
        return this.mutablePos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
    
    public BlockPos.MutableBlockPos set(final int x, final int y, final int z) {
        return this.mutablePos.setPos(x, y, z);
    }
    
    public BlockPos.MutableBlockPos setAndAdd(final int x, final int y, final int z) {
        return this.mutablePos.setPos(this.mutablePos.getX() + x, this.mutablePos.getY() + y, this.mutablePos.getZ() + z);
    }
    
    public BlockPos.MutableBlockPos setAndAdd(final double x, final double y, final double z) {
        return this.mutablePos.setPos(this.mutablePos.getX() + x, this.mutablePos.getY() + y, this.mutablePos.getZ() + z);
    }
    
    public BlockPos.MutableBlockPos setAndAdd(final BlockPos pos) {
        return this.mutablePos.setPos(this.mutablePos.getX() + pos.getX(), this.mutablePos.getY() + pos.getY(), this.mutablePos.getZ() + pos.getZ());
    }
    
    public BlockPos.MutableBlockPos setAndAdd(final BlockPos pos, final double x, final double y, final double z) {
        return this.mutablePos.setPos(this.mutablePos.getX() + pos.getX() + x, this.mutablePos.getY() + pos.getY() + y, this.mutablePos.getZ() + pos.getZ() + z);
    }
}
