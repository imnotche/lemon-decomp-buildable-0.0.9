package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketUseEntity.class})
public interface AccessorCPacketAttack {
    @Accessor(value="entityId")
    public int getId();

    @Accessor(value="entityId")
    public void setId(int var1);

    @Accessor(value="action")
    public void setAction(CPacketUseEntity.Action var1);
}
