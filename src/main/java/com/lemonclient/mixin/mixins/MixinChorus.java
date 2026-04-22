// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.ChorusEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemChorusFruit;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.item.ItemFood;

@Mixin({ ItemChorusFruit.class })
public class MixinChorus extends ItemFood
{
    public MixinChorus(final int amount, final float saturation) {
        super(amount, saturation, false);
    }
    
    @Inject(method = { "onItemUseFinish" }, at = { @At("HEAD") })
    public void attemptTeleportHook(final ItemStack stack, final World worldIn, final EntityLivingBase entityLiving, final CallbackInfoReturnable<ItemStack> cir) {
        final ChorusEvent event = new ChorusEvent();
        LemonClient.EVENT_BUS.post(event);
    }
}
