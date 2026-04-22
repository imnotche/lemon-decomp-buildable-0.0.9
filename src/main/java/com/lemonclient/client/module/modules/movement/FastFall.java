// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "FastFall", category = Category.Movement)
public class FastFall extends Module
{
    DoubleSetting dist;
    DoubleSetting speed;
    
    public FastFall() {
        this.dist = this.registerDouble("Min Distance", 3.0, 0.0, 25.0);
        this.speed = this.registerDouble("Multiplier", 3.0, 0.0, 10.0);
    }
    
    @Override
    public void onUpdate() {
        if (FastFall.mc.world.isAirBlock(new BlockPos(FastFall.mc.player.getPositionVector())) && FastFall.mc.player.onGround && (!FastFall.mc.player.isElytraFlying() || FastFall.mc.player.fallDistance < this.dist.getValue() || !FastFall.mc.player.capabilities.isFlying)) {
            final EntityPlayerSP player = FastFall.mc.player;
            player.motionY -= this.speed.getValue();
        }
    }
}
