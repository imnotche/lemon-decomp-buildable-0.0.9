// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import java.awt.Color;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.NewRenderEntityEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "CrystalChams", category = Category.Render)
public class CrystalChams extends Module
{
    IntegerSetting range;
    ModeSetting mode;
    BooleanSetting chams;
    BooleanSetting throughWalls;
    BooleanSetting wireframe;
    BooleanSetting wireWalls;
    DoubleSetting spinSpeed;
    DoubleSetting floatSpeed;
    ColorSetting color;
    ColorSetting wireFrameColor;
    DoubleSetting lineWidth;
    DoubleSetting lineWidthInterp;
    BooleanSetting show;
    @EventHandler
    private final Listener<NewRenderEntityEvent> renderEntityHeadEventListener;
    
    public CrystalChams() {
        this.range = this.registerInteger("Range", 32, 0, 256);
        this.mode = this.registerMode("Mode", Arrays.asList("Normal", "Gradient"), "Normal");
        this.chams = this.registerBoolean("Chams", false);
        this.throughWalls = this.registerBoolean("ThroughWalls", false);
        this.wireframe = this.registerBoolean("Wireframe", false);
        this.wireWalls = this.registerBoolean("WireThroughWalls", false);
        this.spinSpeed = this.registerDouble("SpinSpeed", 1.0, 0.0, 4.0);
        this.floatSpeed = this.registerDouble("FloatSpeed", 1.0, 0.0, 4.0);
        this.color = this.registerColor("Color", new GSColor(255, 255, 255, 255), true);
        this.wireFrameColor = this.registerColor("WireframeColor", new GSColor(255, 255, 255, 255), true);
        this.lineWidth = this.registerDouble("lineWidth", 1.0, 0.0, 4.0);
        this.lineWidthInterp = this.registerDouble("lineWidthInterp", 1.0, 0.1, 4.0);
        this.show = this.registerBoolean("ShowEntity ;;", false);
        this.renderEntityHeadEventListener = new Listener<NewRenderEntityEvent>(event -> {
            if (CrystalChams.mc.player != null && CrystalChams.mc.world != null && event.entityIn != null && event.entityIn.getName().length() != 0) {
                if (event.entityIn instanceof EntityEnderCrystal && CrystalChams.mc.player.getDistance(event.entityIn) <= this.range.getValue()) {
                    if (!this.show.getValue()) {
                        event.cancel();
                    }
                    this.prepare();
                    final float spinTicks = ((EntityEnderCrystal)event.entityIn).innerRotation + Minecraft.getMinecraft().getRenderPartialTicks();
                    final float floatTicks = MathHelper.sin(spinTicks * 0.2f * this.floatSpeed.getValue().floatValue()) / 2.0f + 0.5f;
                    final float spinSpeed = this.spinSpeed.getValue().floatValue();
                    final float scale = 0.0625f;
                    final float swingAmount = spinTicks * 3.0f * spinSpeed;
                    final float floatTicks2 = floatTicks * floatTicks + floatTicks;
                    final float floatTicks3 = floatTicks2 * 0.2f;
                    GlStateManager.glLineWidth(this.getInterpolatedLinWid(CrystalChams.mc.player.getDistance(event.entityIn) + 1.0f, this.lineWidth.getValue().floatValue(), this.lineWidthInterp.getValue().floatValue()));
                    GL11.glDisable(3553);
                    if (this.mode.getValue().equals("Gradient")) {
                        GL11.glPushAttrib(1048575);
                        GL11.glEnable(3042);
                        GL11.glDisable(2896);
                        GL11.glDisable(3553);
                        final float alpha = this.color.getValue().getAlpha() / 255.0f;
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
                        event.modelBase.render(event.entityIn, 0.0f, swingAmount, floatTicks3, 0.0f, 0.0f, scale);
                        GL11.glEnable(3553);
                        GL11.glBlendFunc(770, 771);
                        final float f = event.entityIn.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
                        CrystalChams.mc.getTextureManager().bindTexture(new ResourceLocation("textures/rainbow.png"));
                        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                        GlStateManager.enableBlend();
                        GlStateManager.depthFunc(514);
                        GlStateManager.depthMask(false);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                        for (int i = 0; i < 2; ++i) {
                            GlStateManager.disableLighting();
                            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                            GlStateManager.matrixMode(5890);
                            GlStateManager.loadIdentity();
                            GlStateManager.rotate(30.0f - i * 60.0f, 0.0f, 0.0f, 0.5f);
                            GlStateManager.translate(0.0f, f * (0.001f + i * 0.003f) * 20.0f, 0.0f);
                            GlStateManager.matrixMode(5888);
                            event.modelBase.render(event.entityIn, 0.0f, swingAmount, floatTicks3, 0.0f, 0.0f, scale);
                        }
                        GlStateManager.matrixMode(5890);
                        GlStateManager.loadIdentity();
                        GlStateManager.matrixMode(5888);
                        GlStateManager.enableLighting();
                        GlStateManager.depthMask(true);
                        GlStateManager.depthFunc(515);
                        GlStateManager.disableBlend();
                        CrystalChams.mc.entityRenderer.setupFogColor(false);
                        GL11.glPopAttrib();
                    }
                    else {
                        if (this.wireframe.getValue()) {
                            final Color wireColor = this.wireFrameColor.getValue();
                            GL11.glPushAttrib(1048575);
                            GL11.glEnable(3042);
                            GL11.glDisable(3553);
                            GL11.glDisable(2896);
                            GL11.glBlendFunc(770, 771);
                            GL11.glPolygonMode(1032, 6913);
                            if (this.wireWalls.getValue()) {
                                GL11.glDepthMask(false);
                                GL11.glDisable(2929);
                            }
                            GL11.glColor4f(wireColor.getRed() / 255.0f, wireColor.getGreen() / 255.0f, wireColor.getBlue() / 255.0f, wireColor.getAlpha() / 255.0f);
                            event.modelBase.render(event.entityIn, 0.0f, swingAmount, floatTicks3, 0.0f, 0.0f, scale);
                            GL11.glPopAttrib();
                        }
                        if (this.chams.getValue()) {
                            final Color chamsColor = this.color.getValue();
                            GL11.glPushAttrib(1048575);
                            GL11.glEnable(3042);
                            GL11.glDisable(3553);
                            GL11.glDisable(2896);
                            GL11.glDisable(3008);
                            GL11.glBlendFunc(770, 771);
                            GL11.glEnable(2960);
                            GL11.glEnable(10754);
                            if (this.throughWalls.getValue()) {
                                GL11.glDepthMask(false);
                                GL11.glDisable(2929);
                            }
                            GL11.glColor4f(chamsColor.getRed() / 255.0f, chamsColor.getGreen() / 255.0f, chamsColor.getBlue() / 255.0f, chamsColor.getAlpha() / 255.0f);
                            event.modelBase.render(event.entityIn, 0.0f, swingAmount, floatTicks3, 0.0f, 0.0f, scale);
                            GL11.glPopAttrib();
                        }
                    }
                    event.limbSwing = 0.0f;
                    event.limbSwingAmount = swingAmount;
                    event.ageInTicks = floatTicks3;
                    event.netHeadYaw = 0.0f;
                    event.headPitch = 0.0f;
                    event.scale = scale;
                    this.release();
                }
            }
        }, new Predicate[0]);
    }
    
    void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
    }
    
    void release() {
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glEnable(3553);
        GL11.glPolygonMode(1032, 6914);
        new GSColor(255, 255, 255, 255).glColor();
    }
    
    float getInterpolatedLinWid(final float distance, final float line, final float lineFactor) {
        return line * lineFactor / distance;
    }
}
