// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.function.Predicate;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.util.MovementInputFromOptions;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BlockMove", category = Category.Dev, priority = 120)
public class BlockMove extends Module
{
    BooleanSetting middle;
    IntegerSetting delay;
    BooleanSetting only;
    BooleanSetting avoid;
    Timing timer;
    Vec3d[] sides;
    @EventHandler
    private final Listener<InputUpdateEvent> inputUpdateEventListener;
    
    public BlockMove() {
        this.middle = this.registerBoolean("Middle", true);
        this.delay = this.registerInteger("Delay", 250, 0, 2000);
        this.only = this.registerBoolean("Only In Block", true);
        this.avoid = this.registerBoolean("Avoid Out", true, () -> !this.only.getValue());
        this.timer = new Timing();
        this.sides = new Vec3d[] { new Vec3d(0.24, 0.0, 0.24), new Vec3d(-0.24, 0.0, 0.24), new Vec3d(0.24, 0.0, -0.24), new Vec3d(-0.24, 0.0, -0.24) };
        this.inputUpdateEventListener = new Listener<InputUpdateEvent>(event -> {
            if (BlockMove.mc.player == null || BlockMove.mc.world == null) {
                return;
            }
            Vec3d vec = BlockMove.mc.player.getPositionVector();
            boolean air = true;
            final AxisAlignedBB playerBox = BlockMove.mc.player.boundingBox;
            final Vec3d[] sides = this.sides;
            final int length = sides.length;
            int j = 0;
            while (j < length) {
                final Vec3d vec3d = sides[j];
                if (!air) {
                    break;
                }
                else {
                    for (int i = 0; i < 2; ++i) {
                        final BlockPos pos = new BlockPos(vec.add(vec3d).add(0.0, i, 0.0));
                        if (!BlockUtil.isAir(pos)) {
                            final AxisAlignedBB box = BlockUtil.getBoundingBox(pos);
                            if (box != null && playerBox.intersects(box)) {
                                air = false;
                                break;
                            }
                        }
                    }
                    ++j;
                }
            }
            if (!air) {
                if (event.getMovementInput() instanceof MovementInputFromOptions) {
                    if (this.timer.passedMs(this.delay.getValue())) {
                        BlockPos playerPos = null;
                        if (this.middle.getValue()) {
                            playerPos = PlayerUtil.getPlayerPos();
                        }
                        else {
                            playerPos = new BlockPos((double)Math.round(vec.x), vec.y, (double)Math.round(vec.z));
                        }
                        if (playerPos == null) {
                            return;
                        }
                        final BlockPos pos2 = playerPos;
                        final EnumFacing facing = BlockMove.mc.player.getHorizontalFacing();
                        final int x = pos2.offset(facing).x - pos2.x;
                        final int z = pos2.offset(facing).z - pos2.z;
                        final boolean addX = x != 0;
                        if (event.getMovementInput().forwardKeyDown) {
                            vec = this.add(pos2, addX, addX ? (x < 0) : (z < 0));
                        }
                        else if (event.getMovementInput().backKeyDown) {
                            vec = this.add(pos2, addX, addX ? (x > 0) : (z > 0));
                        }
                        else if (event.getMovementInput().leftKeyDown) {
                            vec = this.add(pos2, !addX, addX ? (x > 0) : (z < 0));
                        }
                        else if (event.getMovementInput().rightKeyDown) {
                            vec = this.add(pos2, !addX, addX ? (x < 0) : (z > 0));
                        }
                        if (vec != null) {
                            BlockMove.mc.player.setPosition(vec.x, vec.y, vec.z);
                            this.timer.reset();
                        }
                    }
                    event.getMovementInput().forwardKeyDown = false;
                    event.getMovementInput().backKeyDown = false;
                    event.getMovementInput().leftKeyDown = false;
                    event.getMovementInput().rightKeyDown = false;
                    event.getMovementInput().moveForward = 0.0f;
                    event.getMovementInput().moveStrafe = 0.0f;
                }
            }
        }, 200, new Predicate[0]);
    }
    
    private Vec3d add(final BlockPos pos, final boolean x, final boolean negative) {
        Vec3d vec;
        if (negative) {
            if (x) {
                vec = this.pos(pos.add(-1, 0, 0));
            }
            else {
                vec = this.pos(pos.add(0, 0, -1));
            }
        }
        else if (x) {
            vec = this.pos(pos.add(1, 0, 0));
        }
        else {
            vec = this.pos(pos.add(0, 0, 1));
        }
        return vec;
    }
    
    private Vec3d pos(final BlockPos pos) {
        if (BlockMove.mc.world == null) {
            return null;
        }
        if (this.middle.getValue()) {
            return new Vec3d(pos.x + 0.5, pos.y, pos.z + 0.5);
        }
        Vec3d lastVec;
        Vec3d vec = lastVec = new Vec3d(pos.x, pos.y, pos.z);
        boolean any = !BlockMove.mc.world.isAirBlock(pos) || !BlockMove.mc.world.isAirBlock(pos.up());
        vec = new Vec3d(pos.x - 1.0E-8, pos.y, pos.z);
        if (BlockMove.mc.world.isAirBlock(new BlockPos(vec)) && BlockMove.mc.world.isAirBlock(new BlockPos(vec).up())) {
            lastVec = vec;
        }
        else {
            any = true;
        }
        vec = new Vec3d(pos.x, pos.y, pos.z - 1.0E-8);
        if (BlockMove.mc.world.isAirBlock(new BlockPos(vec)) && BlockMove.mc.world.isAirBlock(new BlockPos(vec).up())) {
            lastVec = vec;
        }
        else {
            any = true;
        }
        vec = new Vec3d(pos.x - 1.0E-8, pos.y, pos.z - 1.0E-8);
        if (BlockMove.mc.world.isAirBlock(new BlockPos(vec)) && BlockMove.mc.world.isAirBlock(new BlockPos(vec).up())) {
            lastVec = vec;
        }
        else {
            any = true;
        }
        if (!this.only.getValue() && !any && this.avoid.getValue()) {
            return null;
        }
        return lastVec;
    }
}
