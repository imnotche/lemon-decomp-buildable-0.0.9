// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.item.ItemStack;

public class InvStack
{
    public final int slot;
    public final ItemStack stack;
    
    public InvStack(final int slot, final ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }
}
