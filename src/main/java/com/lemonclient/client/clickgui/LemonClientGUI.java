// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.clickgui;

import com.lukflug.panelstudio.container.GUI;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import com.lemonclient.client.LemonClient;
import net.minecraft.item.ItemStack;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import java.util.Iterator;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.layout.CSGOLayout;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import java.awt.Rectangle;
import com.lukflug.panelstudio.layout.PanelLayout;
import com.lukflug.panelstudio.layout.ChildUtil;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.widget.TextField;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.widget.ColorPickerComponent;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.layout.IComponentAdder;
import java.util.function.Supplier;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import java.util.function.IntPredicate;
import com.lukflug.panelstudio.layout.ComponentGenerator;
import com.lukflug.panelstudio.component.IResizable;
import java.util.function.UnaryOperator;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponentProxy;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.base.Animation;
import com.lemonclient.client.module.HUDModule;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.base.Context;
import java.util.function.BiFunction;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lemonclient.api.setting.Setting;
import org.lwjgl.input.Keyboard;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lemonclient.api.setting.SettingsManager;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.base.IToggleable;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.setting.IModule;
import java.util.stream.Stream;
import com.lukflug.panelstudio.setting.ICategory;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Arrays;
import com.lemonclient.client.module.Category;
import com.lukflug.panelstudio.theme.IColorScheme;
import net.minecraft.util.text.TextFormatting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.font.FontUtil;
import net.minecraft.client.renderer.GlStateManager;
import java.awt.Color;
import java.awt.Point;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;

public class LemonClientGUI extends MinecraftHUDGUI
{
    public static LemonClientGUI INSTANCE;
    public static final int WIDTH = 100;
    public static final int HEIGHT = 12;
    public static final int FONT_HEIGHT = 9;
    public static final int DISTANCE = 10;
    public static final int HUD_BORDER = 2;
    public static IClient client;
    public static GUIInterface guiInterface;
    public static HUDGUI gui;
    private final ITheme theme;
    private ITheme clearTheme;
    
    public LemonClientGUI() {
        LemonClientGUI.INSTANCE = this;
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        LemonClientGUI.guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(final Point pos, final int height, final String s, final Color c) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)pos.x, (float)pos.y, 0.0f);
                final double scale = height / (double)(FontUtil.getFontHeight(colorMain.customFont.getValue()) + (colorMain.customFont.getValue() ? 1 : 0));
                this.end(false);
                FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), s, 0.0f, 0.0f, new GSColor(c));
                this.begin(false);
                GlStateManager.scale(scale, scale, 1.0);
                GlStateManager.popMatrix();
            }
            
            @Override
            public int getFontWidth(final int height, final String s) {
                final double scale = height / (double)(FontUtil.getFontHeight(colorMain.customFont.getValue()) + (colorMain.customFont.getValue() ? 1 : 0));
                return (int)Math.round(FontUtil.getStringWidth(colorMain.customFont.getValue(), s) * scale);
            }
            
            public double getScreenWidth() {
                return super.getScreenWidth();
            }
            
            public double getScreenHeight() {
                return super.getScreenHeight();
            }
            
            public String getResourcePrefix() {
                return "lemonclient:gui/";
            }
        };
        this.clearTheme = new Theme(new GSColorScheme("clear", () -> true), colorMain.Title.getValue(), colorMain.Enabled.getValue(), colorMain.Disabled.getValue(), colorMain.Background.getValue(), colorMain.Font.getValue(), colorMain.ScrollBar.getValue(), colorMain.Highlight.getValue(), () -> clickGuiModule.gradient.getValue(), 9, 3, 1, ": " + TextFormatting.GRAY);
        this.theme = this.clearTheme;
        LemonClientGUI.client = () -> Arrays.stream(Category.values()).sorted(Comparator.comparing(Enum::toString)).map(category -> new ICategory() {
            @Override
            public String getDisplayName() {
                return category.toString();
            }
            
            @Override
            public Stream<IModule> getModules() {
                return ModuleManager.getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).map(module -> new IModule() {
                    @Override
                    public String getDisplayName() {
                        return module.getName();
                    }
                    
                    @Override
                    public IToggleable isEnabled() {
                        return new IToggleable() {
                            @Override
                            public boolean isOn() {
                                return module.isEnabled();
                            }
                            
                            @Override
                            public void toggle() {
                                module.toggle();
                            }
                        };
                    }
                    
                    @Override
                    public Stream<ISetting<?>> getSettings() {
                        final Stream<ISetting<?>> moduleSettings = SettingsManager.getSettingsForModule(module).stream().map(LemonClientGUI.this::createSetting);
                        final Stream<ISetting<?>> extraSettings = Stream.of(new IBooleanSetting() {
                            @Override
                            public String getDisplayName() {
                                return "Toggle Msgs";
                            }
                            
                            @Override
                            public void toggle() {
                                module.setToggleMsg(!module.isToggleMsg());
                            }
                            
                            @Override
                            public boolean isOn() {
                                return module.isToggleMsg();
                            }
                        }, new IKeybindSetting() {
                            @Override
                            public String getDisplayName() {
                                return "Keybind";
                            }
                            
                            @Override
                            public int getKey() {
                                return module.getBind();
                            }
                            
                            @Override
                            public void setKey(final int key) {
                                module.setBind(key);
                            }
                            
                            @Override
                            public String getKeyName() {
                                return Keyboard.getKeyName(module.getBind());
                            }
                        });
                        return Stream.concat(moduleSettings, extraSettings);
                    }
                });
            }
        });
        final IToggleable guiToggle = new SimpleToggleable(false);
        final IToggleable hudToggle = new SimpleToggleable(false) {
            @Override
            public boolean isOn() {
                if (guiToggle.isOn() && super.isOn()) {
                    return clickGuiModule.showHUD.getValue();
                }
                return super.isOn();
            }
        };
        LemonClientGUI.gui = new HUDGUI(LemonClientGUI.guiInterface, this.theme.getDescriptionRenderer(), new MousePositioner(new Point(10, 10)), guiToggle, hudToggle);
        final BiFunction<Context, Integer, Integer> scrollHeight = (context, componentHeight) -> {
            if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                return componentHeight;
            }
            else {
                return Integer.valueOf(Math.min(componentHeight, Math.max(48, this.height - context.getPos().y - 12)));
            }
        };
        final Supplier<Animation> animation = () -> new SettingsAnimation(() -> clickGuiModule.animationSpeed.getValue(), () -> LemonClientGUI.guiInterface.getTime());
        final PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize() {
            @Override
            public int getScrollHeight(final Context context, final int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).populate(this.theme);
                LemonClientGUI.gui.addHUDComponent(((HUDModule)module).getComponent(), new IToggleable() {
                    @Override
                    public boolean isOn() {
                        return module.isEnabled();
                    }
                    
                    @Override
                    public void toggle() {
                        module.toggle();
                    }
                }, animation.get(), this.theme, 2);
            }
        }
        final IComponentAdder classicPanelAdder = new PanelAdder(new IContainer<IFixedComponent>() {
            @Override
            public boolean addComponent(final IFixedComponent component) {
                return LemonClientGUI.gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
                    @Override
                    public void handleScroll(final Context context, final int diff) {
                        if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                            final Point p = this.getPosition(LemonClientGUI.guiInterface);
                            p.translate(0, -diff);
                            this.setPosition(LemonClientGUI.guiInterface, p);
                        }
                    }
                    
                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                });
            }
            
            @Override
            public boolean addComponent(final IFixedComponent component, final IBoolean visible) {
                return LemonClientGUI.gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
                    @Override
                    public void handleScroll(final Context context, final int diff) {
                        if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                            final Point p = this.getPosition(LemonClientGUI.guiInterface);
                            p.translate(0, -diff);
                            this.setPosition(LemonClientGUI.guiInterface, p);
                        }
                    }
                    
                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                }, visible);
            }
            
            @Override
            public boolean removeComponent(final IFixedComponent component) {
                return LemonClientGUI.gui.removeComponent(component);
            }
        }, false, () -> !clickGuiModule.csgoLayout.getValue(), title -> title) {
            @Override
            protected IScrollSize getScrollSize(final IResizable size) {
                return new IScrollSize() {
                    @Override
                    public int getScrollHeight(final Context context, final int componentHeight) {
                        return scrollHeight.apply(context, componentHeight);
                    }
                };
            }
        };
        final IComponentGenerator generator = new ComponentGenerator(scancode -> scancode == 211, character -> character >= 32, new TextFieldKeys()) {
            @Override
            public IComponent getColorComponent(final IColorSetting setting, final Supplier<Animation> animation, final IComponentAdder adder, final ThemeTuple theme, final int colorLevel, final boolean isContainer) {
                return new ColorPickerComponent(setting, theme);
            }
            
            @Override
            public IComponent getStringComponent(final IStringSetting setting, final Supplier<Animation> animation, final IComponentAdder adder, final ThemeTuple theme, final int colorLevel, final boolean isContainer) {
                return new TextField(setting, this.keys, 0, new SimpleToggleable(false), theme.getTextRenderer(false, isContainer)) {
                    @Override
                    public boolean allowCharacter(final char character) {
                        return character >= ' ' && character != '\u007f';
                    }
                };
            }
        };
        final ILayout classicPanelLayout = new PanelLayout(100, new Point(10, 10), 55, 22, animation, level -> ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.DOWN, popupType);
        classicPanelLayout.populateGUI(classicPanelAdder, generator, LemonClientGUI.client, this.theme);
        final PopupTuple colorPopup = new PopupTuple(new CenteredPositioner(() -> new Rectangle(new Point(0, 0), LemonClientGUI.guiInterface.getWindowSize())), true, new IScrollSize() {});
        final IComponentAdder horizontalCSGOAdder = new PanelAdder(LemonClientGUI.gui, true, () -> clickGuiModule.csgoLayout.getValue(), title -> title);
        final ILayout horizontalCSGOLayout = new CSGOLayout(new Labeled("Lemon", null, () -> true), new Point(100, 100), 480, 100, animation, "Enabled", true, true, 2, ChildUtil.ChildMode.DOWN, colorPopup) {
            @Override
            public int getScrollHeight(final Context context, final int componentHeight) {
                return 320;
            }
            
            @Override
            protected boolean isUpKey(final int key) {
                return key == 200;
            }
            
            @Override
            protected boolean isDownKey(final int key) {
                return key == 208;
            }
            
            @Override
            protected boolean isLeftKey(final int key) {
                return key == 203;
            }
            
            @Override
            protected boolean isRightKey(final int key) {
                return key == 205;
            }
        };
        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder, generator, LemonClientGUI.client, this.theme);
    }
    
    @Override
    protected HUDGUI getGUI() {
        return LemonClientGUI.gui;
    }
    
    private ISetting<?> createSetting(final Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            final BooleanSetting booleanSetting = (BooleanSetting)setting;
            return new IBooleanSetting() {
                @Override
                public String getDisplayName() {
                    return booleanSetting.getName();
                }
                
                @Override
                public IBoolean isVisible() {
                    return booleanSetting::isVisible;
                }
                
                @Override
                public void toggle() {
                    booleanSetting.setValue(!booleanSetting.getValue());
                }
                
                @Override
                public boolean isOn() {
                    return booleanSetting.getValue();
                }
                
                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (booleanSetting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return booleanSetting.getSubSettings().map(LemonClientGUI.this::createSetting);
                }
            };
        }
        if (setting instanceof IntegerSetting) {
            final IntegerSetting integerSetting = (IntegerSetting)setting;
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return integerSetting.getName();
                }
                
                @Override
                public IBoolean isVisible() {
                    return integerSetting::isVisible;
                }
                
                @Override
                public double getNumber() {
                    return integerSetting.getValue();
                }
                
                @Override
                public void setNumber(final double value) {
                    integerSetting.setValue((int)Math.round(value));
                }
                
                @Override
                public double getMaximumValue() {
                    return integerSetting.getMax();
                }
                
                @Override
                public double getMinimumValue() {
                    return integerSetting.getMin();
                }
                
                @Override
                public int getPrecision() {
                    return 0;
                }
                
                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (integerSetting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return integerSetting.getSubSettings().map(LemonClientGUI.this::createSetting);
                }
            };
        }
        if (setting instanceof DoubleSetting) {
            final DoubleSetting doubleSetting = (DoubleSetting)setting;
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return doubleSetting.getName();
                }
                
                @Override
                public IBoolean isVisible() {
                    return doubleSetting::isVisible;
                }
                
                @Override
                public double getNumber() {
                    return doubleSetting.getValue();
                }
                
                @Override
                public void setNumber(final double value) {
                    doubleSetting.setValue(value);
                }
                
                @Override
                public double getMaximumValue() {
                    return doubleSetting.getMax();
                }
                
                @Override
                public double getMinimumValue() {
                    return doubleSetting.getMin();
                }
                
                @Override
                public int getPrecision() {
                    return 2;
                }
                
                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (doubleSetting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return doubleSetting.getSubSettings().map(LemonClientGUI.this::createSetting);
                }
            };
        }
        if (setting instanceof ModeSetting) {
            final ModeSetting modeSetting = (ModeSetting)setting;
            return new IEnumSetting() {
                private final ILabeled[] states = modeSetting.getModes().stream().map(mode -> new Labeled(mode, null, () -> true)).toArray(ILabeled[]::new);
                
                @Override
                public String getDisplayName() {
                    return modeSetting.getName();
                }
                
                @Override
                public IBoolean isVisible() {
                    return modeSetting::isVisible;
                }
                
                @Override
                public void increment() {
                    modeSetting.increment();
                }
                
                @Override
                public void decrement() {
                    modeSetting.decrement();
                }
                
                @Override
                public String getValueName() {
                    return modeSetting.getValue();
                }
                
                @Override
                public int getValueIndex() {
                    return modeSetting.getModes().indexOf(this.getValueName());
                }
                
                @Override
                public void setValueIndex(final int index) {
                    modeSetting.setValue(modeSetting.getModes().get(index));
                }
                
                @Override
                public ILabeled[] getAllowedValues() {
                    return this.states;
                }
                
                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (modeSetting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return modeSetting.getSubSettings().map(LemonClientGUI.this::createSetting);
                }
            };
        }
        if (setting instanceof ColorSetting) {
            final ColorSetting colorSetting = (ColorSetting)setting;
            return new IColorSetting() {
                @Override
                public String getDisplayName() {
                    return TextFormatting.BOLD + colorSetting.getName();
                }
                
                @Override
                public IBoolean isVisible() {
                    return colorSetting::isVisible;
                }
                
                @Override
                public Color getValue() {
                    return colorSetting.getValue();
                }
                
                @Override
                public void setValue(final Color value) {
                    colorSetting.setValue(new GSColor(value));
                }
                
                @Override
                public Color getColor() {
                    return colorSetting.getColor();
                }
                
                @Override
                public boolean getRainbow() {
                    return colorSetting.getRainbow();
                }
                
                @Override
                public void setRainbow(final boolean rainbow) {
                    colorSetting.setRainbow(rainbow);
                }
                
                @Override
                public boolean hasAlpha() {
                    return colorSetting.alphaEnabled();
                }
                
                @Override
                public boolean allowsRainbow() {
                    return colorSetting.rainbowEnabled();
                }
                
                @Override
                public boolean hasHSBModel() {
                    return ModuleManager.getModule(ColorMain.class).colorModel.getValue().equalsIgnoreCase("HSB");
                }
                
                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    final Stream<ISetting<?>> temp = colorSetting.getSubSettings().map(LemonClientGUI.this::createSetting);
                    return Stream.concat(temp, Stream.of(new IBooleanSetting() {
                        @Override
                        public String getDisplayName() {
                            return "Sync Color";
                        }
                        
                        @Override
                        public IBoolean isVisible() {
                            return () -> colorSetting != ModuleManager.getModule(ColorMain.class).enabledColor;
                        }
                        
                        @Override
                        public void toggle() {
                            colorSetting.setValue(ModuleManager.getModule(ColorMain.class).enabledColor.getColor());
                            colorSetting.setRainbow(ModuleManager.getModule(ColorMain.class).enabledColor.getRainbow());
                        }
                        
                        @Override
                        public boolean isOn() {
                            return ModuleManager.getModule(ColorMain.class).enabledColor.getColor().equals(colorSetting.getColor());
                        }
                    }));
                }
            };
        }
        if (setting instanceof StringSetting) {
            final StringSetting stringSetting = (StringSetting)setting;
            return new IStringSetting() {
                @Override
                public String getValue() {
                    return stringSetting.getText();
                }
                
                @Override
                public void setValue(final String string) {
                    stringSetting.setText(string);
                }
                
                @Override
                public String getDisplayName() {
                    return stringSetting.getName();
                }
            };
        }
        return new ISetting<Void>() {
            @Override
            public String getDisplayName() {
                return setting.getName();
            }
            
            @Override
            public IBoolean isVisible() {
                return setting::isVisible;
            }
            
            @Override
            public Void getSettingState() {
                return null;
            }
            
            @Override
            public Class<Void> getSettingClass() {
                return Void.class;
            }
            
            @Override
            public Stream<ISetting<?>> getSubSettings() {
                if (setting.getSubSettings().count() == 0L) {
                    return null;
                }
                return setting.getSubSettings().map(LemonClientGUI.this::createSetting);
            }
        };
    }
    
    public static void renderItem(final ItemStack item, final Point pos) {
        LemonClient.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, pos.x, pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        LemonClient.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }
    
    public static void renderItemTest(final ItemStack item, final Point pos) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, pos.x, pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }
    
    public static void renderEntity(final EntityLivingBase entity, final Point pos, final int scale) {
        LemonClient.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GuiInventory.drawEntityOnScreen(pos.x, pos.y, scale, 28.0f, 60.0f, entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        LemonClient.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }
    
    @Override
    protected GUIInterface getInterface() {
        return LemonClientGUI.guiInterface;
    }
    
    @Override
    protected int getScrollSpeed() {
        return ModuleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue();
    }
    
    public void refresh() {
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        this.clearTheme = new Theme(new GSColorScheme("clear", () -> true), colorMain.Title.getValue(), colorMain.Enabled.getValue(), colorMain.Disabled.getValue(), colorMain.Background.getValue(), colorMain.Font.getValue(), colorMain.ScrollBar.getValue(), colorMain.Highlight.getValue(), () -> clickGuiModule.gradient.getValue(), 9, 3, 1, ": " + TextFormatting.GRAY);
    }
    
    private static final class GSColorScheme implements IColorScheme
    {
        private final String configName;
        private final Supplier<Boolean> isVisible;
        
        public GSColorScheme(final String configName, final Supplier<Boolean> isVisible) {
            this.configName = configName;
            this.isVisible = isVisible;
        }
        
        @Override
        public void createSetting(final ITheme theme, final String name, final String description, final boolean hasAlpha, final boolean allowsRainbow, final Color color, final boolean rainbow) {
        }
        
        @Override
        public Color getColor(final String name) {
            return new Color(255, 255, 255);
        }
    }
}
