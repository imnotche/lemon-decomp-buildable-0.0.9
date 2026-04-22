package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={PlayerControllerMP.class})
public interface AccessorPlayerControllerMP {
    @Accessor(value="blockHitDelay")
    public int getBlockHitDelay();

    @Accessor(value="blockHitDelay")
    public void setBlockHitDelay(int var1);

    @Accessor(value="isHittingBlock")
    public void setIsHittingBlock(boolean var1);

    @Accessor(value="currentPlayerItem")
    public int getCurrentPlayerItem();

    @Invoker(value="syncCurrentPlayItem")
    public void invokeSyncCurrentPlayItem();
}
