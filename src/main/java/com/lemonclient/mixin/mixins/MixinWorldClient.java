// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.api.event.events.EntityRemovedEvent;
import com.lemonclient.client.LemonClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ WorldClient.class })
public abstract class MixinWorldClient
{
    @Inject(method = { "onEntityRemoved" }, at = { @At("HEAD") })
    public void onEntityRemoved(final Entity entity, final CallbackInfo info) {
        LemonClient.EVENT_BUS.post(new EntityRemovedEvent(entity));
    }
}
