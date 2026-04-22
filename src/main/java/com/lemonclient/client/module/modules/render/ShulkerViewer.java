// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import com.lemonclient.client.clickgui.LemonClientGUI;
import java.awt.Point;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.NonNullList;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.client.renderer.GlStateManager;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.item.ItemStack;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ShulkerViewer", category = Category.Render)
public class ShulkerViewer extends Module
{
    public ColorSetting outlineColor;
    public ColorSetting fillColor;
    
    public ShulkerViewer() {
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        this.fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 255));
    }
    
    public void renderShulkerPreview(final ItemStack itemStack, final int posX, final int posY, final int width, final int height) {
        final GSColor outline = new GSColor(this.outlineColor.getValue(), 255);
        final GSColor fill = new GSColor(this.fillColor.getValue(), 200);
        RenderUtil.draw2DRect(posX + 1, posY + 1, width - 2, height - 2, 1000, fill);
        RenderUtil.draw2DRect(posX, posY, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY + height - 1, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY, 1, height, 1000, outline);
        RenderUtil.draw2DRect(posX + width - 1, posY, 1, height, 1000, outline);
        GlStateManager.disableDepth();
        FontUtil.drawStringWithShadow(ModuleManager.getModule(ColorMain.class).customFont.getValue(), itemStack.getDisplayName(), (float)(posX + 3), (float)(posY + 3), new GSColor(255, 255, 255, 255));
        GlStateManager.enableDepth();
        final NonNullList<ItemStack> contentItems = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"), contentItems);
        for (int i = 0; i < contentItems.size(); ++i) {
            final int finalX = posX + 1 + i % 9 * 18;
            final int finalY = posY + 31 + (i / 9 - 1) * 18;
            LemonClientGUI.renderItemTest(contentItems.get(i), new Point(finalX, finalY));
        }
    }
}
