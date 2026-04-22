// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.clickgui;

import com.lukflug.panelstudio.theme.StandardColorPicker;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.awt.Dimension;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import java.awt.Rectangle;
import java.awt.Point;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IColorScheme;
import java.awt.Color;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.theme.ThemeBase;

public class Theme extends ThemeBase
{
    protected IBoolean gradient;
    protected int height;
    protected int padding;
    protected int border;
    protected String separator;
    Color title;
    Color enable;
    Color disable;
    Color background;
    Color font;
    Color scrollBar;
    Color hgihlight;
    
    public Theme(final IColorScheme scheme, final Color title, final Color enable, final Color disable, final Color background, final Color font, final Color scrollBar, final Color hgihlight, final IBoolean gradient, final int height, final int padding, final int border, final String separator) {
        super(scheme);
        this.title = title;
        this.enable = enable;
        this.disable = disable;
        this.background = background;
        this.font = font;
        this.scrollBar = scrollBar;
        this.hgihlight = hgihlight;
        this.gradient = gradient;
        this.height = height;
        this.padding = padding;
        this.border = border;
        this.separator = separator;
    }
    
    protected void renderOverlay(final Context context) {
        final Color color = context.isHovered() ? new Color(0, 0, 0, 64) : new Color(0, 0, 0, 0);
        context.getInterface().fillRect(context.getRect(), color, color, color, color);
    }
    
    protected void renderBackground(final Context context, final boolean focus, final int graphicalLevel) {
        if (graphicalLevel == 0) {
            final Color color = this.getBackgroundColor(focus);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
        }
    }
    
    protected void renderSmallButton(final Context context, final String title, final int symbol, final boolean focus) {
        final Point[] points = new Point[3];
        final int padding = (context.getSize().height <= 8) ? 2 : 4;
        final Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
        if (title == null) {
            final Rectangle rectangle = rect;
            rectangle.x += context.getSize().width / 2 - context.getSize().height / 2;
        }
        final Color color = this.getFontColor(focus);
        switch (symbol) {
            case 1: {
                context.getInterface().drawLine(new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color, color);
                context.getInterface().drawLine(new Point(rect.x, rect.y + rect.height), new Point(rect.x + rect.width, rect.y), color, color);
                break;
            }
            case 2: {
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 2), color, color, color, color);
                break;
            }
            case 3: {
                if (rect.width % 2 == 1) {
                    final Rectangle rectangle2 = rect;
                    --rectangle2.width;
                }
                if (rect.height % 2 == 1) {
                    final Rectangle rectangle3 = rect;
                    --rectangle3.height;
                }
                context.getInterface().fillRect(new Rectangle(rect.x + rect.width / 2 - 1, rect.y, 2, rect.height), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height / 2 - 1, rect.width, 2), color, color, color, color);
                break;
            }
            case 4: {
                if (rect.height % 2 == 1) {
                    final Rectangle rectangle4 = rect;
                    --rectangle4.height;
                }
                points[2] = new Point(rect.x + rect.width, rect.y);
                points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[0] = new Point(rect.x, rect.y + rect.height / 2);
                break;
            }
            case 5: {
                if (rect.height % 2 == 1) {
                    final Rectangle rectangle5 = rect;
                    --rectangle5.height;
                }
                points[0] = new Point(rect.x, rect.y);
                points[1] = new Point(rect.x, rect.y + rect.height);
                points[2] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
                break;
            }
            case 6: {
                if (rect.width % 2 == 1) {
                    final Rectangle rectangle6 = rect;
                    --rectangle6.width;
                }
                points[0] = new Point(rect.x, rect.y + rect.height);
                points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[2] = new Point(rect.x + rect.width / 2, rect.y);
                break;
            }
            case 7: {
                if (rect.width % 2 == 1) {
                    final Rectangle rectangle7 = rect;
                    --rectangle7.width;
                }
                points[2] = new Point(rect.x, rect.y);
                points[1] = new Point(rect.x + rect.width, rect.y);
                points[0] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
                break;
            }
        }
        if (symbol >= 4 && symbol <= 7) {
            context.getInterface().fillTriangle(points[0], points[1], points[2], color, color, color);
        }
        if (title != null) {
            context.getInterface().drawString(new Point(context.getPos().x + ((symbol == 0) ? padding : context.getSize().height), context.getPos().y + padding), this.height, title, this.getFontColor(focus));
        }
    }
    
    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        return new IDescriptionRenderer() {
            @Override
            public void renderDescription(final IInterface inter, final Point pos, final String text) {
                final Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(Theme.this.height, text) + 2, Theme.this.height + 2));
                final Color color = Theme.this.getBackgroundColor(true);
                inter.fillRect(rect, color, color, color, color);
                inter.drawString(new Point(pos.x + 1, pos.y + 1), Theme.this.height, text, Theme.this.getFontColor(true));
            }
        };
    }
    
    @Override
    public IContainerRenderer getContainerRenderer(final int logicalLevel, final int graphicalLevel, final boolean horizontal) {
        return new IContainerRenderer() {
            @Override
            public void renderBackground(final Context context, final boolean focus) {
                Theme.this.renderBackground(context, focus, graphicalLevel);
            }
            
            @Override
            public int getBorder() {
                return horizontal ? 0 : Theme.this.border;
            }
            
            @Override
            public int getTop() {
                return horizontal ? 0 : Theme.this.border;
            }
        };
    }
    
    @Override
    public <T> IPanelRenderer<T> getPanelRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel) {
        return new IPanelRenderer<T>() {
            @Override
            public void renderPanelOverlay(final Context context, final boolean focus, final T state, final boolean open) {
            }
            
            @Override
            public void renderTitleOverlay(final Context context, final boolean focus, final T state, final boolean open) {
                if (graphicalLevel > 0) {
                    Rectangle rect = context.getRect();
                    rect = new Rectangle(rect.width - rect.height, 0, rect.height, rect.height);
                    if (rect.width % 2 != 0) {
                        final Rectangle rectangle = rect;
                        --rectangle.width;
                        final Rectangle rectangle2 = rect;
                        --rectangle2.height;
                        final Rectangle rectangle3 = rect;
                        ++rectangle3.x;
                    }
                    final Context subContext = new Context(context, rect.width, rect.getLocation(), true, true);
                    subContext.setHeight(rect.height);
                    if (open) {
                        Theme.this.renderSmallButton(subContext, null, 7, focus);
                    }
                    else {
                        Theme.this.renderSmallButton(subContext, null, 5, focus);
                    }
                }
            }
        };
    }
    
    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel) {
        return new IScrollBarRenderer<T>() {
            @Override
            public int renderScrollBar(final Context context, final boolean focus, final T state, final boolean horizontal, final int height, final int position) {
                Theme.this.renderBackground(context, focus, graphicalLevel);
                final Color color = ITheme.combineColors(Theme.this.scrollBar, Theme.this.getBackgroundColor(focus));
                if (horizontal) {
                    final int a = (int)(position / (double)height * context.getSize().width);
                    final int b = (int)((position + context.getSize().width) / (double)height * context.getSize().width);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + a + 1, context.getPos().y + 1, b - a - 2, 2), color, color, color, color);
                    context.getInterface().drawRect(new Rectangle(context.getPos().x + a + 1, context.getPos().y + 1, b - a - 2, 2), color, color, color, color);
                }
                else {
                    final int a = (int)(position / (double)height * context.getSize().height);
                    final int b = (int)((position + context.getSize().height) / (double)height * context.getSize().height);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 1, context.getPos().y + a + 1, 2, b - a - 2), color, color, color, color);
                    context.getInterface().drawRect(new Rectangle(context.getPos().x + 1, context.getPos().y + a + 1, 2, b - a - 2), color, color, color, color);
                }
                if (horizontal) {
                    return (int)((context.getInterface().getMouse().x - context.getPos().x) * height / (double)context.getSize().width - context.getSize().width / 2.0);
                }
                return (int)((context.getInterface().getMouse().y - context.getPos().y) * height / (double)context.getSize().height - context.getSize().height / 2.0);
            }
            
            @Override
            public int getThickness() {
                return 4;
            }
        };
    }
    
    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IEmptySpaceRenderer<T>() {
            @Override
            public void renderSpace(final Context context, final boolean focus, final T state) {
                Theme.this.renderBackground(context, focus, graphicalLevel);
            }
        };
    }
    
    @Override
    public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<T>() {
            @Override
            public void renderButton(final Context context, final String title, final boolean focus, final T state) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                if (container && graphicalLevel <= 0) {
                    final Color colorA = Theme.this.title;
                    final Color colorB = Theme.this.gradient.isOn() ? Theme.this.getBackgroundColor(effFocus) : colorA;
                    context.getInterface().fillRect(context.getRect(), colorA, colorA, colorB, colorB);
                }
                else {
                    Theme.this.renderBackground(context, effFocus, graphicalLevel);
                }
                Color color = Theme.this.getFontColor(effFocus);
                if (type == Boolean.class && state instanceof Boolean && (Boolean)state) {
                    color = Theme.this.getMainColor(effFocus, true);
                }
                else if (type == Color.class) {
                    color = (Color)state;
                }
                if (graphicalLevel > 0) {
                    Theme.this.renderOverlay(context);
                }
                if (type == String.class) {
                    context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, color);
                }
                else {
                    context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title, color);
                }
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<Void>() {
            @Override
            public void renderButton(final Context context, final String title, final boolean focus, final Void state) {
                Theme.this.renderBackground(context, focus, graphicalLevel);
                Theme.this.renderOverlay(context);
                if (!container || logicalLevel <= 0) {
                    Theme.this.renderSmallButton(context, title, symbol, focus);
                }
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public IButtonRenderer<String> getKeybindRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<String>() {
            @Override
            public void renderButton(final Context context, final String title, final boolean focus, final String state) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                if (container && graphicalLevel <= 0) {
                    final Color colorA = Theme.this.title;
                    final Color colorB = Theme.this.gradient.isOn() ? Theme.this.getBackgroundColor(effFocus) : colorA;
                    context.getInterface().fillRect(context.getRect(), colorA, colorA, colorB, colorB);
                }
                else {
                    Theme.this.renderBackground(context, effFocus, graphicalLevel);
                }
                Color color = Theme.this.getFontColor(effFocus);
                if (effFocus) {
                    color = Theme.this.getMainColor(effFocus, true);
                }
                Theme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + (focus ? "..." : state), color);
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public ISliderRenderer getSliderRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISliderRenderer() {
            @Override
            public void renderSlider(final Context context, final String title, final String state, final boolean focus, final double value) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                Theme.this.renderBackground(context, effFocus, graphicalLevel);
                final Color color = Theme.this.getFontColor(effFocus);
                final Color colorA = Theme.this.getMainColor(effFocus, true);
                final Rectangle rect = this.getSlideArea(context, title, state);
                final int divider = (int)(rect.width * value);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y, divider, rect.height), colorA, colorA, colorA, colorA);
                Theme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, color);
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public IRadioRenderer getRadioRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IRadioRenderer() {
            @Override
            public void renderItem(final Context context, final ILabeled[] items, final boolean focus, final int target, final double state, final boolean horizontal) {
                Theme.this.renderBackground(context, focus, graphicalLevel);
                for (int i = 0; i < items.length; ++i) {
                    final Rectangle rect = this.getItemRect(context, items, i, horizontal);
                    final Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
                    subContext.setHeight(rect.height);
                    Theme.this.renderOverlay(subContext);
                    context.getInterface().drawString(new Point(rect.x + Theme.this.padding, rect.y + Theme.this.padding), Theme.this.height, items[i].getDisplayName(), (i == target) ? Theme.this.getMainColor(focus, true) : Theme.this.getFontColor(focus));
                }
            }
            
            @Override
            public int getDefaultHeight(final ILabeled[] items, final boolean horizontal) {
                return (horizontal ? 1 : items.length) * Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        return new IResizeBorderRenderer() {
            @Override
            public void drawBorder(final Context context, final boolean focus) {
                final Color color = Theme.this.getBackgroundColor(focus);
                final Rectangle rect = context.getRect();
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y, rect.width, this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - this.getBorder(), rect.width, this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x + rect.width - this.getBorder(), rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
            }
            
            @Override
            public int getBorder() {
                return 2;
            }
        };
    }
    
    @Override
    public ITextFieldRenderer getTextRenderer(final boolean embed, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ITextFieldRenderer() {
            @Override
            public int renderTextField(final Context context, final String title, final boolean focus, final String content, final int position, final int select, int boxPosition, final boolean insertMode) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                Theme.this.renderBackground(context, effFocus, graphicalLevel);
                final Color textColor = Theme.this.getFontColor(effFocus);
                final Color highlightColor = Theme.this.hgihlight;
                final Rectangle rect = this.getTextArea(context, title);
                final int strlen = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, position));
                context.getInterface().fillRect(rect, new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64));
                if (boxPosition < position) {
                    int minPosition;
                    for (minPosition = boxPosition; minPosition < position && context.getInterface().getFontWidth(Theme.this.height, content.substring(0, minPosition)) + rect.width - Theme.this.padding < strlen; ++minPosition) {}
                    if (boxPosition < minPosition) {
                        boxPosition = minPosition;
                    }
                }
                else if (boxPosition > position) {
                    boxPosition = position - 1;
                }
                int maxPosition;
                for (maxPosition = content.length(); maxPosition > 0; --maxPosition) {
                    if (context.getInterface().getFontWidth(Theme.this.height, content.substring(maxPosition)) >= rect.width - Theme.this.padding) {
                        ++maxPosition;
                        break;
                    }
                }
                if (boxPosition > maxPosition) {
                    boxPosition = maxPosition;
                }
                else if (boxPosition < 0) {
                    boxPosition = 0;
                }
                final int offset = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, boxPosition));
                final int x1 = rect.x + Theme.this.padding / 2 - offset + strlen;
                int x2 = rect.x + Theme.this.padding / 2 - offset;
                if (position < content.length()) {
                    x2 += context.getInterface().getFontWidth(Theme.this.height, content.substring(0, position + 1));
                }
                else {
                    x2 += context.getInterface().getFontWidth(Theme.this.height, content + "X");
                }
                Theme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding / 2), Theme.this.height, title + Theme.this.separator, textColor);
                context.getInterface().window(rect);
                if (select >= 0) {
                    final int x3 = rect.x + Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Theme.this.height, content.substring(0, select));
                    context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + Theme.this.padding / 2, Math.abs(x3 - x1), Theme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
                }
                context.getInterface().drawString(new Point(rect.x + Theme.this.padding / 2 - offset, rect.y + Theme.this.padding / 2), Theme.this.height, content, textColor);
                if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
                    if (insertMode) {
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + Theme.this.padding / 2 + Theme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
                    }
                    else {
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + Theme.this.padding / 2, 1, Theme.this.height), textColor, textColor, textColor, textColor);
                    }
                }
                context.getInterface().restore();
                return boxPosition;
            }
            
            @Override
            public int getDefaultHeight() {
                int height = Theme.this.getBaseHeight() - Theme.this.padding;
                if (height % 2 == 1) {
                    ++height;
                }
                return height;
            }
            
            @Override
            public Rectangle getTextArea(final Context context, final String title) {
                final Rectangle rect = context.getRect();
                final int length = Theme.this.padding + context.getInterface().getFontWidth(Theme.this.height, title + Theme.this.separator);
                return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
            }
            
            @Override
            public int transformToCharPos(final Context context, final String title, final String content, final int boxPosition) {
                final Rectangle rect = this.getTextArea(context, title);
                final Point mouse = context.getInterface().getMouse();
                final int offset = context.getInterface().getFontWidth(Theme.this.height, content.substring(0, boxPosition));
                if (rect.contains(mouse)) {
                    for (int i = 1; i <= content.length(); ++i) {
                        if (rect.x + Theme.this.padding / 2 - offset + context.getInterface().getFontWidth(Theme.this.height, content.substring(0, i)) > mouse.x) {
                            return i - 1;
                        }
                    }
                    return content.length();
                }
                return -1;
            }
        };
    }
    
    @Override
    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<Boolean>() {
            @Override
            public void renderButton(final Context context, final String title, final boolean focus, final Boolean state) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                Theme.this.renderBackground(context, effFocus, graphicalLevel);
                Theme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + (state ? "On" : "Off"), Theme.this.getFontColor(focus));
                final Color color = state ? Theme.this.enable : Theme.this.disable;
                final Color fillColor = ITheme.combineColors(color, Theme.this.getBackgroundColor(effFocus));
                Rectangle rect = state ? this.getOnField(context) : this.getOffField(context);
                context.getInterface().fillRect(rect, fillColor, fillColor, fillColor, fillColor);
                rect = context.getRect();
                rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * Theme.this.padding, rect.y + Theme.this.padding, 2 * rect.height - 4 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
                context.getInterface().drawRect(rect, color, color, color, color);
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
            
            @Override
            public Rectangle getOnField(final Context context) {
                final Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height + Theme.this.padding, rect.y + Theme.this.padding, rect.height - 2 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
            }
            
            @Override
            public Rectangle getOffField(final Context context) {
                final Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * Theme.this.padding, rect.y + Theme.this.padding, rect.height - 2 * Theme.this.padding, rect.height - 2 * Theme.this.padding);
            }
        };
    }
    
    @Override
    public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<String>() {
            @Override
            public void renderButton(final Context context, final String title, final boolean focus, final String state) {
                final boolean effFocus = container ? context.hasFocus() : focus;
                Theme.this.renderBackground(context, effFocus, graphicalLevel);
                Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
                subContext.setHeight(context.getSize().height);
                Theme.this.renderOverlay(subContext);
                final Color textColor = Theme.this.getFontColor(effFocus);
                context.getInterface().drawString(new Point(context.getPos().x + Theme.this.padding, context.getPos().y + Theme.this.padding), Theme.this.height, title + Theme.this.separator + state, textColor);
                Rectangle rect = this.getOnField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                Theme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, null, effFocus, null);
                rect = this.getOffField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                Theme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, null, effFocus, null);
            }
            
            @Override
            public int getDefaultHeight() {
                return Theme.this.getBaseHeight();
            }
            
            @Override
            public Rectangle getOnField(final Context context) {
                final Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height, rect.y, rect.height, rect.height);
            }
            
            @Override
            public Rectangle getOffField(final Context context) {
                final Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, rect.height, rect.height);
            }
        };
    }
    
    @Override
    public IColorPickerRenderer getColorPickerRenderer() {
        return new StandardColorPicker() {
            @Override
            public int getPadding() {
                return Theme.this.padding;
            }
            
            @Override
            public int getBaseHeight() {
                return Theme.this.getBaseHeight();
            }
        };
    }
    
    @Override
    public int getBaseHeight() {
        return this.height + 2 * this.padding;
    }
    
    @Override
    public Color getMainColor(final boolean focus, final boolean active) {
        if (active) {
            return this.enable;
        }
        return new Color(0, 0, 0, 0);
    }
    
    @Override
    public Color getBackgroundColor(final boolean focus) {
        return this.background;
    }
    
    @Override
    public Color getFontColor(final boolean focus) {
        return this.font;
    }
}
