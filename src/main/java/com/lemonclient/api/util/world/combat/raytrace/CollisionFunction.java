// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world.combat.raytrace;

import net.minecraft.block.state.IBlockProperties;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface CollisionFunction
{
    CollisionFunction DEFAULT = (state, world, pos, start, end) -> state.collisionRayTrace(world, pos, start, end);
    
    RayTraceResult collisionRayTrace(final IBlockState p0, final World p1, final BlockPos p2, final Vec3d p3, final Vec3d p4);
}
