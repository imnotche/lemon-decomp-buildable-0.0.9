// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.event.events;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import com.lemonclient.api.event.LemonClientEvent;

public class NewRenderEntityEvent extends LemonClientEvent
{
    public ModelBase modelBase;
    public Entity entityIn;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scale;
    
    public NewRenderEntityEvent(final ModelBase modelBase, final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        this.modelBase = modelBase;
        this.entityIn = entityIn;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
    
    public boolean isCancelable() {
        return true;
    }
}
