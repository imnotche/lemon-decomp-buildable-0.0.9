// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render.shaders.impl.fill;

import java.util.HashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import org.lwjgl.opengl.GL20;
import com.lemonclient.api.util.render.shaders.FramebufferShader;

public class FillShader extends FramebufferShader
{
    public static final FillShader INSTANCE;
    public float time;
    
    public FillShader() {
        super("fill.frag");
    }
    
    @Override
    public void setupUniforms() {
        this.setupUniform("color");
    }
    
    public void updateUniforms(final float red, final float green, final float blue, final float alpha) {
        GL20.glUniform4f(this.getUniform("color"), red, green, blue, alpha);
    }
    
    public void stopDraw(final Color color) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        GL11.glPushMatrix();
        this.startShader(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    
    public void startShader(final float red, final float green, final float blue, final float alpha) {
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(red, green, blue, alpha);
    }
    
    public void update(final double speed) {
        this.time += (float)speed;
    }
    
    static {
        INSTANCE = new FillShader();
    }
}
