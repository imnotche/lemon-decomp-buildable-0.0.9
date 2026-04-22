// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import java.util.Iterator;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.render.GSColor;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ArmorHUD", category = Category.HUD, drawn = false)
public class ArmorHUD extends Module
{
    @Override
    public void onRender() {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        final ScaledResolution resolution = new ScaledResolution(ArmorHUD.mc);
        final int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        final int y = resolution.getScaledHeight() - 55 - (ArmorHUD.mc.player.isInWater() ? 10 : 0);
        for (final ItemStack is : ArmorHUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            ArmorHUD.mc.getRenderItem().zLevel = 200.0f;
            ArmorHUD.mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            ArmorHUD.mc.getRenderItem().renderItemOverlayIntoGUI(ArmorHUD.mc.fontRenderer, is, x, y, "");
            ArmorHUD.mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            ArmorHUD.mc.fontRenderer.drawStringWithShadow(s, (float)(x + 19 - 2 - ArmorHUD.mc.fontRenderer.getStringWidth(s)), (float)(y + 9), new GSColor(255, 255, 255).getRGB());
            float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (green > 1.0f) {
                green = 1.0f;
            }
            else if (green < 0.0f) {
                green = 0.0f;
            }
            if (red > 1.0f) {
                red = 1.0f;
            }
            if (dmg < 0) {
                dmg = 0;
            }
            FontUtil.drawStringWithShadow(ModuleManager.getModule(ColorMain.class).customFont.getValue(), dmg + "", (float)(x + 8 - ArmorHUD.mc.fontRenderer.getStringWidth(dmg + "") / 2), (float)(y - 11), new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
}
