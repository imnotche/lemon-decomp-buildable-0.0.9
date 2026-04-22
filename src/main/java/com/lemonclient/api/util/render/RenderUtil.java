// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

import net.minecraft.world.World;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.modules.render.Nametags;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.util.glu.Sphere;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.ColorSetting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import net.minecraft.client.Minecraft;

public class RenderUtil
{
    private static final Minecraft mc;
    
    public static void drawLine(final double posx, final double posy, final double posz, final double posx2, final double posy2, final double posz2, final GSColor color) {
        drawLine(posx, posy, posz, posx2, posy2, posz2, color, 1.0f);
    }
    
    public static void drawRectOutline(final double x, final double y, final double width, final double height, final Color color) {
        drawGradientRectOutline(x, y, width, height, GradientDirection.Normal, color, color);
    }
    
    public static void drawGradientRectOutline(final double x, final double y, final double width, final double height, final GradientDirection direction, final Color startColor, final Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        final Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(2);
        GL11.glColor4f(result[2].getRed() / 255.0f, result[2].getGreen() / 255.0f, result[2].getBlue() / 255.0f, result[2].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y);
        GL11.glColor4f(result[3].getRed() / 255.0f, result[3].getGreen() / 255.0f, result[3].getBlue() / 255.0f, result[3].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(result[0].getRed() / 255.0f, result[0].getGreen() / 255.0f, result[0].getBlue() / 255.0f, result[0].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y + height);
        GL11.glColor4f(result[1].getRed() / 255.0f, result[1].getGreen() / 255.0f, result[1].getBlue() / 255.0f, result[1].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }
    
    public static void drawTriangle(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final Color color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glBegin(6);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    public static void drawRect(final double x, final double y, final double width, final double height, final Color color) {
        drawGradientRect(x, y, width, height, GradientDirection.Normal, color, color);
    }
    
    public static void setColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
    
    private static Color[] checkColorDirection(final GradientDirection direction, final Color start, final Color end) {
        final Color[] dir = new Color[4];
        if (direction == GradientDirection.Normal) {
            for (int a = 0; a < dir.length; ++a) {
                dir[a] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            }
        }
        else if (direction == GradientDirection.DownToUp) {
            dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
        }
        else if (direction == GradientDirection.UpToDown) {
            dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
        }
        else if (direction == GradientDirection.RightToLeft) {
            dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
        }
        else if (direction == GradientDirection.LeftToRight) {
            dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
        }
        else {
            for (int a = 0; a < dir.length; ++a) {
                dir[a] = new Color(255, 255, 255);
            }
        }
        return dir;
    }
    
    public static void drawGradientRect(final double x, final double y, final double width, final double height, final GradientDirection direction, final Color startColor, final Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        final Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(7);
        setColor(result[0]);
        GL11.glVertex2d(x + width, y);
        setColor(result[1]);
        GL11.glVertex2d(x, y);
        setColor(result[2]);
        GL11.glVertex2d(x, y + height);
        setColor(result[3]);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }
    
    public static void drawRect(final float x1, final float y1, final float x2, final float y2, final int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        Gui.drawRect(0, 0, 0, 0, 0);
    }
    
    public static void drawRectSOutline(final double x, final double y, final double x2, final double y2, final Color color) {
        drawGradientRectSOutline(x, y, x2, y2, GradientDirection.Normal, color, color);
    }
    
    public static void drawGradientRectSOutline(final double x, final double y, final double x2, final double y2, final GradientDirection direction, final Color startColor, final Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        final Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(2);
        GL11.glColor4f(result[2].getRed() / 255.0f, result[2].getGreen() / 255.0f, result[2].getBlue() / 255.0f, result[2].getAlpha() / 255.0f);
        GL11.glVertex2d(x2, y);
        GL11.glColor4f(result[3].getRed() / 255.0f, result[3].getGreen() / 255.0f, result[3].getBlue() / 255.0f, result[3].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(result[0].getRed() / 255.0f, result[0].getGreen() / 255.0f, result[0].getBlue() / 255.0f, result[0].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y2);
        GL11.glColor4f(result[1].getRed() / 255.0f, result[1].getGreen() / 255.0f, result[1].getBlue() / 255.0f, result[1].getAlpha() / 255.0f);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }
    
    public static void drawRectS(final double x1, final double y1, final float x2, final float y2, final int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        Gui.drawRect(0, 0, 0, 0, 0);
    }
    
    public static void color(final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }
    
    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(Float.intBitsToFloat(Float.floatToIntBits(5.0675106f) ^ 0x7F22290C));
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(Float.intBitsToFloat(Float.floatToIntBits(11.925059f) ^ 0x7EBECD0B), Float.intBitsToFloat(Float.floatToIntBits(18.2283f) ^ 0x7E11D38F), Float.intBitsToFloat(Float.floatToIntBits(9.73656f) ^ 0x7E9BC8F3));
    }
    
    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(Float.intBitsToFloat(Float.floatToIntBits(12.552789f) ^ 0x7EC8D839), Float.intBitsToFloat(Float.floatToIntBits(7.122752f) ^ 0x7F63ED96), Float.intBitsToFloat(Float.floatToIntBits(5.4278784f) ^ 0x7F2DB12E));
        GL11.glColor4f(Float.intBitsToFloat(Float.floatToIntBits(10.5715685f) ^ 0x7EA92525), Float.intBitsToFloat(Float.floatToIntBits(4.9474883f) ^ 0x7F1E51D3), Float.intBitsToFloat(Float.floatToIntBits(4.9044757f) ^ 0x7F1CF177), Float.intBitsToFloat(Float.floatToIntBits(9.482457f) ^ 0x7E97B825));
    }
    
    public static void draw2DGradientRect(final float left, final float top, final float right, final float bottom, final int leftBottomColor, final int leftTopColor, final int rightBottomColor, final int rightTopColor) {
        final float lba = (leftBottomColor >> 24 & 0xFF) / 255.0f;
        final float lbr = (leftBottomColor >> 16 & 0xFF) / 255.0f;
        final float lbg = (leftBottomColor >> 8 & 0xFF) / 255.0f;
        final float lbb = (leftBottomColor & 0xFF) / 255.0f;
        final float rba = (rightBottomColor >> 24 & 0xFF) / 255.0f;
        final float rbr = (rightBottomColor >> 16 & 0xFF) / 255.0f;
        final float rbg = (rightBottomColor >> 8 & 0xFF) / 255.0f;
        final float rbb = (rightBottomColor & 0xFF) / 255.0f;
        final float lta = (leftTopColor >> 24 & 0xFF) / 255.0f;
        final float ltr = (leftTopColor >> 16 & 0xFF) / 255.0f;
        final float ltg = (leftTopColor >> 8 & 0xFF) / 255.0f;
        final float ltb = (leftTopColor & 0xFF) / 255.0f;
        final float rta = (rightTopColor >> 24 & 0xFF) / 255.0f;
        final float rtr = (rightTopColor >> 16 & 0xFF) / 255.0f;
        final float rtg = (rightTopColor >> 8 & 0xFF) / 255.0f;
        final float rtb = (rightTopColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0).color(rtr, rtg, rtb, rta).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(ltr, ltg, ltb, lta).endVertex();
        bufferbuilder.pos(left, bottom, 0.0).color(lbr, lbg, lbb, lba).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(rbr, rbg, rbb, rba).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public static void drawLine(final double posx, final double posy, final double posz, final double posx2, final double posy2, final double posz2, final GSColor color, final float width) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth(width);
        color.glColor();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION);
        vertex(posx, posy, posz, bufferbuilder);
        vertex(posx2, posy2, posz2, bufferbuilder);
        tessellator.draw();
    }
    
    public static void draw2DRect(final int posX, final int posY, final int width, final int height, final int zHeight, final GSColor color) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        color.glColor();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(posX, posY + height, zHeight).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zHeight).endVertex();
        bufferbuilder.pos(posX + width, posY, zHeight).endVertex();
        bufferbuilder.pos(posX, posY, zHeight).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    private static void drawBorderedRect(final double x, final double y, final double x1, final GSColor inside, final GSColor border) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        inside.glColor();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, 1.0, 0.0).endVertex();
        bufferbuilder.pos(x1, 1.0, 0.0).endVertex();
        bufferbuilder.pos(x1, y, 0.0).endVertex();
        bufferbuilder.pos(x, y, 0.0).endVertex();
        tessellator.draw();
        border.glColor();
        GlStateManager.glLineWidth(1.8f);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, y, 0.0).endVertex();
        bufferbuilder.pos(x, 1.0, 0.0).endVertex();
        bufferbuilder.pos(x1, 1.0, 0.0).endVertex();
        bufferbuilder.pos(x1, y, 0.0).endVertex();
        bufferbuilder.pos(x, y, 0.0).endVertex();
        tessellator.draw();
    }
    
    public static void drawCircle(final float x, final float y, final float z, final Double radius, final GSColor colour) {
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        int alpha = 255 - colour.getAlpha();
        if (alpha == 0) {
            alpha = 1;
        }
        for (int i = 0; i < 361; ++i) {
            bufferbuilder.pos(x + Math.sin(Math.toRadians(i)) * radius - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z + Math.cos(Math.toRadians(i)) * radius - RenderUtil.mc.getRenderManager().viewerPosZ).color(colour.getRed() / 255.0f, colour.getGreen() / 255.0f, colour.getBlue() / 255.0f, (float)alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
    }
    
    public static void drawCircle(final float x, final float y, final float z, final Double radius, final int stepCircle, final int alphaVal) {
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        int alpha = 255 - alphaVal;
        if (alpha == 0) {
            alpha = 1;
        }
        for (int i = 0; i < 361; ++i) {
            final GSColor colour = ColorSetting.getRainbowColor(i % 180 * stepCircle);
            bufferbuilder.pos(x + Math.sin(Math.toRadians(i)) * radius - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z + Math.cos(Math.toRadians(i)) * radius - RenderUtil.mc.getRenderManager().viewerPosZ).color(colour.getRed() / 255.0f, colour.getGreen() / 255.0f, colour.getBlue() / 255.0f, (float)alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
    }
    
    public static void drawBox(final BlockPos blockPos, final double height, final GSColor color, final int sides) {
        drawBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0, height, 1.0, color, color.getAlpha(), sides);
    }
    
    public static void drawBox(final AxisAlignedBB bb, final boolean check, final double height, final GSColor color, final int sides) {
        drawBox(bb, check, height, color, color.getAlpha(), sides);
    }
    
    public static void drawBox(final AxisAlignedBB bb, final boolean check, final double height, final GSColor color, final int alpha, final int sides) {
        if (check) {
            drawBox(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ, color, alpha, sides);
        }
        else {
            drawBox(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, height, bb.maxZ - bb.minZ, color, alpha, sides);
        }
    }
    
    public static void drawBox(final double x, final double y, final double z, final double w, final double h, final double d, final GSColor color, final int alpha, final int sides) {
        GlStateManager.disableAlpha();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        color.glColor();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides, false);
        tessellator.draw();
        GlStateManager.enableAlpha();
    }
    
    public static void drawBoxDire(final AxisAlignedBB bb, final double height, final GSColor color, final int alpha, final int sides) {
        drawBoxDire(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, height, bb.maxZ - bb.minZ, color, alpha, sides);
        drawFixBoxDire(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, height, bb.maxZ - bb.minZ, color, alpha, sides);
    }
    
    public static void drawBoxDire(final double x, final double y, final double z, final double w, final double h, final double d, final GSColor color, final int alpha, final int sides) {
        GlStateManager.disableAlpha();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        color.glColor();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides);
        tessellator.draw();
        GlStateManager.enableAlpha();
    }
    
    public static void drawFixBoxDire(final double x, final double y, final double z, final double w, final double h, final double d, final GSColor color, final int alpha, final int sides) {
        GlStateManager.disableAlpha();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        color.glColor();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        doFixVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides);
        tessellator.draw();
        GlStateManager.enableAlpha();
    }
    
    public static void drawBoundingBoxDire(final BlockPos pos, final double height, final double width, final GSColor color, final int alpha, final int sides) {
        drawBoundingBoxDire(new AxisAlignedBB(pos), height, width, color, alpha, sides);
    }
    
    public static void drawBoundingBoxDire(final AxisAlignedBB bb, final double height, final double width, final GSColor color, final int alpha, final int sides) {
        drawBoundingBoxDire(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, height, bb.maxZ - bb.minZ, width, color, alpha, sides);
    }
    
    public static void drawBoundingBoxDire(final double x, final double y, final double z, final double w, final double h, final double d, final double width, final GSColor color, final int alpha, final int sides) {
        GlStateManager.disableAlpha();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float)width);
        color.glColor();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        final AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + w, y + h, z + d);
        if ((sides & 0x20) != 0x0) {
            colorVertex(bb.minX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(bb.minX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.minY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
            colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        }
        tessellator.draw();
        GlStateManager.enableAlpha();
    }
    
    public static void drawBoundingBox(final AxisAlignedBB bb, final double width, final GSColor[] otherPos) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float)width);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        colorVertex(bb.minX, bb.minY, bb.minZ, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.minZ, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
        tessellator.draw();
    }
    
    public static void drawBoundingBox(final AxisAlignedBB axisAlignedBB, final double width, final GSColor[] color, final boolean five, final int sides) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float)width);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[6], color[6].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[4], color[4].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[6], color[6].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x2) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[6], color[6].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x1) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
        tessellator.draw();
    }
    
    public static void drawBoundingBox(final BlockPos bp, final double height, final float width, final GSColor color) {
        drawBoundingBox(getBoundingBox(bp, height), width, color, color.getAlpha());
    }
    
    public static void drawBoundingBox(final AxisAlignedBB bb, final double width, final GSColor color) {
        drawBoundingBox(bb, width, color, color.getAlpha());
    }
    
    public static void drawBoundingBox(final AxisAlignedBB bb, final double width, final GSColor color, final int alpha) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float)width);
        color.glColor();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, color, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, color, alpha, bufferbuilder);
        tessellator.draw();
    }
    
    public static void drawBoundingBoxWithSides(final BlockPos blockPos, final double high, final int width, final GSColor color, final int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, high), width, color, color.getAlpha(), sides);
    }
    
    public static void drawBoundingBoxWithSides(final BlockPos blockPos, final int width, final GSColor color, final int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0), width, color, color.getAlpha(), sides);
    }
    
    public static void drawBoundingBoxWithSides(final BlockPos blockPos, final int width, final GSColor color, final int alpha, final int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0), width, color, alpha, sides);
    }
    
    public static void drawBoundingBoxWithSides(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final int sides) {
        drawBoundingBoxWithSides(axisAlignedBB, width, color, color.getAlpha(), sides);
    }
    
    public static void drawBoundingBoxWithSides(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final int alpha, final int sides) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float)width);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        doVerticies(axisAlignedBB, color, alpha, bufferbuilder, sides, true);
        tessellator.draw();
    }
    
    public static void drawBoxProva2(final AxisAlignedBB bb, final GSColor[] color, final int sides) {
        drawBoxProva(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ, color, sides);
    }
    
    public static void drawBoxProva(final double x, final double y, final double z, final double w, final double h, final double d, final GSColor[] color, final int sides) {
        GlStateManager.disableAlpha();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        doVerticiesProva(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, bufferbuilder, sides);
        tessellator.draw();
        GlStateManager.enableAlpha();
    }
    
    private static void doVerticiesProva(final AxisAlignedBB axisAlignedBB, final GSColor[] color, final BufferBuilder bufferbuilder, final int sides) {
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[6], color[6].getAlpha(), bufferbuilder);
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[4], color[4].getAlpha(), bufferbuilder);
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[4], color[4].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[6], color[6].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
        }
        if ((sides & 0x2) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[7], color[7].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color[6], color[6].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[5], color[5].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color[4], color[4].getAlpha(), bufferbuilder);
        }
        if ((sides & 0x1) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color[3], color[3].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[2], color[2].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color[1], color[1].getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color[0], color[0].getAlpha(), bufferbuilder);
        }
    }
    
    public static void drawBoxWithDirection(final AxisAlignedBB bb, final GSColor color, final float rotation, final float width, final int mode) {
        final double xCenter = bb.minX + (bb.maxX - bb.minX) / 2.0;
        final double zCenter = bb.minZ + (bb.maxZ - bb.minZ) / 2.0;
        final Points square = new Points(bb.minY, bb.maxY, xCenter, zCenter, rotation);
        if (mode == 0) {
            square.addPoints(bb.minX, bb.minZ);
            square.addPoints(bb.minX, bb.maxZ);
            square.addPoints(bb.maxX, bb.maxZ);
            square.addPoints(bb.maxX, bb.minZ);
        }
        if (mode == 0) {
            drawDirection(square, color, width);
        }
    }
    
    public static void drawDirection(final Points square, final GSColor color, final float width) {
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMin, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMax, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], color, width);
        }
    }
    
    public static void drawSphere(final double x, final double y, final double z, final float size, final int slices, final int stacks, final float lineWidth, final GSColor color) {
        final Sphere sphere = new Sphere();
        GlStateManager.glLineWidth(lineWidth);
        color.glColor();
        sphere.setDrawStyle(100013);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ);
        sphere.draw(size, slices, stacks);
        GlStateManager.popMatrix();
    }
    
    public static void drawNametag(final Entity entity, final String[] text, final GSColor color, final int type) {
        final Vec3d pos = EntityUtil.getInterpolatedPos(entity, RenderUtil.mc.getRenderPartialTicks());
        drawNametag(pos.x, pos.y + entity.height, pos.z, text, color, type, 0.0, 0.0);
    }
    
    public static double getDistance(final double x, final double y, final double z) {
        Entity viewEntity = RenderUtil.mc.getRenderViewEntity();
        if (viewEntity == null) {
            viewEntity = RenderUtil.mc.player;
        }
        final double d0 = viewEntity.posX - x;
        final double d2 = viewEntity.posY - y;
        final double d3 = viewEntity.posZ - z;
        return MathHelper.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
    }
    
    public static void drawNametag(final double x, final double y, final double z, final String[] text, final GSColor color, final int type, final double customScale, final double maxSize) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        final double dist = getDistance(x, y, z);
        double scale = 1.0;
        double offset = 0.0;
        int start = 0;
        switch (type) {
            case 0: {
                scale = dist / 20.0 * Math.pow(1.2589254, 0.1 / ((dist < 25.0) ? 0.5 : 2.0));
                scale = Math.min(Math.max(scale, 0.5), 5.0);
                offset = ((scale > 2.0) ? (scale / 2.0) : scale);
                scale /= 40.0;
                start = 10;
                break;
            }
            case 1: {
                scale = customScale;
                break;
            }
            case 2: {
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) {
                    scale = 0.0245;
                }
                start = -8;
                break;
            }
        }
        if (maxSize != 0.0 && scale > maxSize) {
            scale = maxSize;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - RenderUtil.mc.getRenderManager().viewerPosX, y + offset - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ);
        GlStateManager.rotate(-RenderUtil.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(RenderUtil.mc.getRenderManager().playerViewX, (RenderUtil.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        if (type == 2) {
            final Nametags nametags = ModuleManager.getModule(Nametags.class);
            double width = 0.0;
            GSColor bcolor = new GSColor(0, 0, 0, 0);
            if (nametags.outline.getValue()) {
                bcolor = color;
                if (nametags.customColor.getValue()) {
                    bcolor = nametags.borderColor.getValue();
                }
            }
            for (final String s : text) {
                final double w = FontUtil.getStringWidth(colorMain.customFont.getValue(), s) / 2.0;
                if (w > width) {
                    width = w;
                }
            }
            drawBorderedRect(-width - 1.0, -RenderUtil.mc.fontRenderer.FONT_HEIGHT, width + 2.0, new GSColor(0, 4, 0, nametags.border.getValue() ? 85 : 0), bcolor);
        }
        GlStateManager.enableTexture2D();
        for (int i = 0; i < text.length; ++i) {
            FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), text[i], (float)(-FontUtil.getStringWidth(colorMain.customFont.getValue(), text[i]) / 2), (float)(i * (RenderUtil.mc.fontRenderer.FONT_HEIGHT + 1) + start), color);
        }
        GlStateManager.disableTexture2D();
        if (type != 2) {
            GlStateManager.popMatrix();
        }
    }
    
    private static void vertex(final double x, final double y, final double z, final BufferBuilder bufferbuilder) {
        bufferbuilder.pos(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ).endVertex();
    }
    
    private static void colorVertex(final double x, final double y, final double z, final GSColor color, final int alpha, final BufferBuilder bufferbuilder) {
        bufferbuilder.pos(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
    }
    
    private static AxisAlignedBB getBoundingBox(final BlockPos bp, final double height) {
        final double x = bp.getX();
        final double y = bp.getY();
        final double z = bp.getZ();
        return new AxisAlignedBB(x, y, z, x + 1.0, y + height, z + 1.0);
    }
    
    private static void doVerticies(final AxisAlignedBB axisAlignedBB, final GSColor color, final int alpha, final BufferBuilder bufferbuilder, final int sides, final boolean five) {
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x2) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            }
        }
        if ((sides & 0x1) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            }
        }
    }
    
    public static void doVerticies(final AxisAlignedBB axisAlignedBB, final GSColor color, final int alpha, final BufferBuilder bufferbuilder, final int sides) {
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
        }
    }
    
    public static void doFixVerticies(final AxisAlignedBB axisAlignedBB, final GSColor color, final int alpha, final BufferBuilder bufferbuilder, final int sides) {
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
        }
    }
    
    public static void prepare() {
        GL11.glHint(3154, 4354);
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GL11.glEnable(2848);
        GL11.glEnable(34383);
    }
    
    public static void release() {
        GL11.glDisable(34383);
        GL11.glDisable(2848);
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(7424);
        GL11.glHint(3154, 4352);
    }
    
    public static Vec3d getInterpolatedPos(final Entity entity, final float partialTicks, final boolean wrap) {
        final Vec3d amount = new Vec3d((entity.posX - entity.lastTickPosX) * partialTicks, (entity.posY - entity.lastTickPosY) * partialTicks, (entity.posZ - entity.lastTickPosZ) * partialTicks);
        final Vec3d vec = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(amount);
        if (wrap) {
            return vec.subtract(RenderUtil.mc.getRenderManager().renderPosX, RenderUtil.mc.getRenderManager().renderPosY, RenderUtil.mc.getRenderManager().renderPosZ);
        }
        return vec;
    }
    
    public static AxisAlignedBB getAxisAlignedBB(final BlockPos pos, final double size) {
        final AxisAlignedBB bb = RenderUtil.mc.world.getBlockState(pos).getSelectedBoundingBox(RenderUtil.mc.world, pos);
        final Vec3d center = bb.getCenter();
        return new AxisAlignedBB(center.x - (bb.maxX - bb.minX) * size, center.y - (bb.maxY - bb.minX) * size, center.z - (bb.maxZ - bb.minZ) * size, center.x + (bb.maxX - bb.minX) * size, center.y + (bb.maxY - bb.minY) * size, center.z + (bb.maxZ - bb.minZ) * size);
    }
    
    public static AxisAlignedBB getInterpolatedAxis(final AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }
    
    public static Vec3d getInterpolatedRenderPos(final Entity entity, final float ticks) {
        return interpolateEntity(entity, ticks).subtract(RenderUtil.mc.getRenderManager().renderPosX, RenderUtil.mc.getRenderManager().renderPosY, RenderUtil.mc.getRenderManager().renderPosZ);
    }
    
    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
    
    public static double getInterpolatedDouble(final double pre, final double current, final float partialTicks) {
        return pre + (current - pre) * partialTicks;
    }
    
    public static float getInterpolatedFloat(final float pre, final float current, final float partialTicks) {
        return pre + (current - pre) * partialTicks;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    public enum GradientDirection
    {
        LeftToRight, 
        RightToLeft, 
        UpToDown, 
        DownToUp, 
        Normal
    }
    
    private static class Points
    {
        double[][] point;
        private int count;
        private final double xCenter;
        private final double zCenter;
        public final double yMin;
        public final double yMax;
        private final float rotation;
        
        public Points(final double yMin, final double yMax, final double xCenter, final double zCenter, final float rotation) {
            this.point = new double[10][2];
            this.count = 0;
            this.yMin = yMin;
            this.yMax = yMax;
            this.xCenter = xCenter;
            this.zCenter = zCenter;
            this.rotation = rotation;
        }
        
        public void addPoints(double x, double z) {
            x -= this.xCenter;
            z -= this.zCenter;
            double rotateX = x * Math.cos(this.rotation) - z * Math.sin(this.rotation);
            double rotateZ = x * Math.sin(this.rotation) + z * Math.cos(this.rotation);
            rotateX += this.xCenter;
            rotateZ += this.zCenter;
            this.point[this.count++] = new double[] { rotateX, rotateZ };
        }
        
        public double[] getPoint(final int index) {
            return this.point[index];
        }
    }
}
