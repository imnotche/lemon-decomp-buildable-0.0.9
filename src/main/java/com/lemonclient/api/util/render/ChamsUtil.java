// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;

public class ChamsUtil
{
    private static final Minecraft mc;
    
    public static void createChamsPre() {
        ChamsUtil.mc.getRenderManager().setRenderShadow(false);
        ChamsUtil.mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GL11.glEnable(32823);
        GL11.glDepthRange(0.0, 0.01);
        GlStateManager.popMatrix();
    }
    
    public static void createChamsPost() {
        final boolean shadow = ChamsUtil.mc.getRenderManager().isRenderShadow();
        ChamsUtil.mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GL11.glDisable(32823);
        GL11.glDepthRange(0.0, 1.0);
        GlStateManager.popMatrix();
    }
    
    public static void createColorPre(final GSColor color, final boolean isPlayer) {
        ChamsUtil.mc.getRenderManager().setRenderShadow(false);
        ChamsUtil.mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GL11.glEnable(32823);
        GL11.glDepthRange(0.0, 0.01);
        GL11.glDisable(3553);
        if (!isPlayer) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        color.glColor();
        GlStateManager.popMatrix();
    }
    
    public static void createColorPost(final boolean isPlayer) {
        final boolean shadow = ChamsUtil.mc.getRenderManager().isRenderShadow();
        ChamsUtil.mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        if (!isPlayer) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glDisable(32823);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(3553);
        GlStateManager.popMatrix();
    }
    
    public static void createWirePre(final GSColor color, final int lineWidth, final boolean isPlayer) {
        ChamsUtil.mc.getRenderManager().setRenderShadow(false);
        ChamsUtil.mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GL11.glPolygonMode(1032, 6913);
        GL11.glEnable(10754);
        GL11.glDepthRange(0.0, 0.01);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        if (!isPlayer) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glLineWidth((float)lineWidth);
        color.glColor();
        GlStateManager.popMatrix();
    }
    
    public static void createWirePost(final boolean isPlayer) {
        final boolean shadow = ChamsUtil.mc.getRenderManager().isRenderShadow();
        ChamsUtil.mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        if (!isPlayer) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glPolygonMode(1032, 6914);
        GL11.glDisable(10754);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GlStateManager.popMatrix();
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
