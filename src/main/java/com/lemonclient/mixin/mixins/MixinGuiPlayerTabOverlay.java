// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.player.social.SocialManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiPlayerTabOverlay.class })
public class MixinGuiPlayerTabOverlay
{
    @Inject(method = { "getPlayerName" }, at = { @At("HEAD") }, cancellable = true)
    public void getPlayerNameHead(final NetworkPlayerInfo networkPlayerInfoIn, final CallbackInfoReturnable<String> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.getPlayerNameGS(networkPlayerInfoIn));
    }
    
    private String getPlayerNameGS(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String displayName = (networkPlayerInfoIn.getDisplayName() != null) ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (SocialManager.isFriend(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getFriendColor() + displayName;
        }
        if (SocialManager.isEnemy(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getEnemyColor() + displayName;
        }
        return displayName;
    }
}
