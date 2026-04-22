// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.hud;

import com.lukflug.panelstudio.base.IInterface;
import net.minecraft.util.NonNullList;
import java.awt.Color;
import com.lemonclient.client.clickgui.LemonClientGUI;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import java.awt.Rectangle;
import java.awt.Dimension;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.hud.HUDComponent;
import java.awt.Point;
import com.lukflug.panelstudio.theme.ITheme;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.HUDModule;

@Module.Declaration(name = "InventoryViewer", category = Category.HUD, drawn = false)
@HUDModule.Declaration(posX = 0, posZ = 10)
public class InventoryViewer extends HUDModule
{
    ColorSetting fillColor;
    IntegerSetting fill;
    ColorSetting outlineColor;
    IntegerSetting outline;
    
    public InventoryViewer() {
        this.fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 100));
        this.fill = this.registerInteger("Fill Alpha", 100, 0, 255);
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        this.outline = this.registerInteger("Outline Alpha", 255, 0, 255);
    }
    
    @Override
    public void populate(final ITheme theme) {
        this.component = new InventoryViewerComponent(theme);
    }
    
    private class InventoryViewerComponent extends HUDComponent
    {
        public InventoryViewerComponent(final ITheme theme) {
            super(new Labeled(InventoryViewer.this.getName(), null, () -> true), InventoryViewer.this.position, InventoryViewer.this.getName());
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            final Color bgcolor = new GSColor(InventoryViewer.this.fillColor.getValue(), InventoryViewer.this.fill.getValue());
            context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
            final Color color = new GSColor(InventoryViewer.this.outlineColor.getValue(), InventoryViewer.this.outline.getValue());
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
            final NonNullList<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory;
            for (int size = items.size(), item = 9; item < size; ++item) {
                final int slotX = context.getPos().x + item % 9 * 18;
                final int slotY = context.getPos().y + 2 + (item / 9 - 1) * 18;
                LemonClientGUI.renderItem(items.get(item), new Point(slotX, slotY));
            }
        }
        
        @Override
        public Dimension getSize(final IInterface inter) {
            return new Dimension(162, 56);
        }
    }
}
