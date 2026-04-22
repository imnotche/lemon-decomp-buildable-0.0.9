// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import java.util.function.Predicate;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.player.social.SocialManager;
import org.lwjgl.input.Mouse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "MCF", category = Category.Misc)
public class MCF extends Module
{
    @EventHandler
    private final Listener<InputEvent.MouseInputEvent> listener;
    
    public MCF() {
        this.listener = new Listener<InputEvent.MouseInputEvent>(event -> {
            if (MCF.mc.world != null && MCF.mc.player != null && !MCF.mc.player.isDead && MCF.mc.objectMouseOver != null) {
                if (MCF.mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.ENTITY) && MCF.mc.objectMouseOver.entityHit instanceof EntityPlayer && Mouse.isButtonDown(2)) {
                    if (SocialManager.isFriend(MCF.mc.objectMouseOver.entityHit.getName())) {
                        SocialManager.delFriend(MCF.mc.objectMouseOver.entityHit.getName());
                        MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Removed " + MCF.mc.objectMouseOver.entityHit.getName() + " from friends list", Notification.Type.SUCCESS);
                    }
                    else {
                        SocialManager.addFriend(MCF.mc.objectMouseOver.entityHit.getName());
                        MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Added " + MCF.mc.objectMouseOver.entityHit.getName() + " to friends list", Notification.Type.SUCCESS);
                    }
                }
            }
        }, new Predicate[0]);
    }
}
