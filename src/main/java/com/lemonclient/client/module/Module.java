// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.setting.values.ModeSetting;
import java.util.List;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.StringSetting;
import java.util.function.Supplier;
import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.IntegerSetting;
import net.minecraft.util.text.TextFormatting;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.event.events.Render3DEvent;
import com.lemonclient.api.event.events.Render2DEvent;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.client.Minecraft;
import me.zero.alpine.listener.Listenable;

public abstract class Module implements Listenable
{
    protected static final Minecraft mc;
    private final String name;
    private final Category category;
    private final int priority;
    private int bind;
    private boolean enabled;
    private boolean drawn;
    private boolean toggleMsg;
    public float remainingAnimation;
    public int onUpdateTimer;
    public int onTickTimer;
    public int fastTimer;
    private String disabledMessage;
    
    public Module() {
        this.name = this.getDeclaration().name();
        this.category = this.getDeclaration().category();
        this.priority = this.getDeclaration().priority();
        this.bind = this.getDeclaration().bind();
        this.enabled = this.getDeclaration().enabled();
        this.drawn = this.getDeclaration().drawn();
        this.toggleMsg = this.getDeclaration().toggleMsg();
        this.disabledMessage = "";
    }
    
    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }
    
    public void onTick() {
    }
    
    public void fast() {
    }
    
    protected void onEnable() {
    }
    
    protected void onDisable() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender() {
    }
    
    public void onWorldRender(final RenderEvent event) {
    }
    
    public void onRender2D(final Render2DEvent event) {
        ++this.remainingAnimation;
    }
    
    public void onRender3D(final Render3DEvent event) {
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setDisabledMessage(final String message) {
        this.disabledMessage = message;
    }
    
    public void enable() {
        this.setEnabled(true);
        LemonClient.EVENT_BUS.subscribe(this);
        try {
            this.onEnable();
        }
        catch (final Exception e) {
            MessageBus.sendClientPrefixMessage("Disabled " + this.getName() + " due to " + e, Notification.Type.ERROR);
            for (final StackTraceElement stack : e.getStackTrace()) {
                System.out.println(stack.toString());
            }
        }
        if (this.toggleMsg && Module.mc.world != null && Module.mc.player != null) {
            MessageBus.sendClientDeleteMessage(ModuleManager.getModule(ColorMain.class).getModuleColor() + this.name + ChatFormatting.GRAY + " turned " + ModuleManager.getModule(ColorMain.class).getEnabledColor() + "ON" + ChatFormatting.GRAY + ".", Notification.Type.SUCCESS, this.getName(), 0);
        }
    }
    
    public void disable() {
        this.setEnabled(false);
        LemonClient.EVENT_BUS.unsubscribe(this);
        try {
            this.onDisable();
        }
        catch (final Exception e) {
            MessageBus.sendClientPrefixMessage("Failed to Disable " + this.getName() + "properly due to " + e, Notification.Type.ERROR);
            for (final StackTraceElement stack : e.getStackTrace()) {
                System.out.println(stack.toString());
            }
        }
        if (this.toggleMsg && Module.mc.world != null && Module.mc.player != null) {
            MessageBus.sendClientDeleteMessage(this.disabledMessage.isEmpty() ? (ModuleManager.getModule(ColorMain.class).getModuleColor() + this.name + ChatFormatting.GRAY + " turned " + ModuleManager.getModule(ColorMain.class).getDisabledColor() + "OFF" + TextFormatting.GRAY + ".") : this.disabledMessage, Notification.Type.DISABLE, this.getName(), 0);
        }
        this.disabledMessage = "";
    }
    
    public static int getIdFromString(String name) {
        StringBuilder s = new StringBuilder();
        name = name.replace("\u79ae", "e");
        final String blacklist = "[^a-z]";
        for (int i = 0; i < name.length(); ++i) {
            s.append(Integer.parseInt(String.valueOf(name.charAt(i)).replaceAll(blacklist, "e"), 36));
        }
        try {
            s = new StringBuilder(s.toString());
        }
        catch (final StringIndexOutOfBoundsException ignored) {
            s = new StringBuilder(String.valueOf(Integer.MAX_VALUE));
        }
        return Integer.MAX_VALUE - Integer.parseInt(s.toString().toLowerCase());
    }
    
    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        }
        else {
            this.enable();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public int getBind() {
        return this.bind;
    }
    
    public void setBind(final int bind) {
        if (bind >= 0 && bind <= 255) {
            this.bind = bind;
        }
    }
    
    public String getHudInfo() {
        return "";
    }
    
    public boolean isDrawn() {
        return this.drawn;
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn = drawn;
    }
    
    public boolean isToggleMsg() {
        return this.toggleMsg;
    }
    
    public void setToggleMsg(final boolean toggleMsg) {
        this.toggleMsg = toggleMsg;
    }
    
    protected IntegerSetting registerInteger(final String name, final int value, final int min, final int max) {
        final IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
        SettingsManager.addSetting(integerSetting);
        return integerSetting;
    }
    
    protected IntegerSetting registerInteger(final String name, final int value, final int min, final int max, final Supplier<Boolean> dipendent) {
        final IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
        integerSetting.setVisible(dipendent);
        SettingsManager.addSetting(integerSetting);
        return integerSetting;
    }
    
    protected StringSetting registerString(final String name, final String value) {
        final StringSetting stringSetting = new StringSetting(name, this, value);
        SettingsManager.addSetting(stringSetting);
        return stringSetting;
    }
    
    protected StringSetting registerString(final String name, final String value, final Supplier<Boolean> dipendent) {
        final StringSetting stringSetting = new StringSetting(name, this, value);
        stringSetting.setVisible(dipendent);
        SettingsManager.addSetting(stringSetting);
        return stringSetting;
    }
    
    protected DoubleSetting registerDouble(final String name, final double value, final double min, final double max) {
        final DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
        SettingsManager.addSetting(doubleSetting);
        return doubleSetting;
    }
    
    protected DoubleSetting registerDouble(final String name, final double value, final double min, final double max, final Supplier<Boolean> dipendent) {
        final DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
        doubleSetting.setVisible(dipendent);
        SettingsManager.addSetting(doubleSetting);
        return doubleSetting;
    }
    
    protected BooleanSetting registerBoolean(final String name, final boolean value) {
        final BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
        SettingsManager.addSetting(booleanSetting);
        return booleanSetting;
    }
    
    protected BooleanSetting registerBoolean(final String name, final boolean value, final Supplier<Boolean> dipendent) {
        final BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
        booleanSetting.setVisible(dipendent);
        SettingsManager.addSetting(booleanSetting);
        return booleanSetting;
    }
    
    protected ModeSetting registerMode(final String name, final List<String> modes, final String value) {
        final ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
        SettingsManager.addSetting(modeSetting);
        return modeSetting;
    }
    
    protected ModeSetting registerMode(final String name, final List<String> modes, final String value, final Supplier<Boolean> dipendent) {
        final ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
        modeSetting.setVisible(dipendent);
        SettingsManager.addSetting(modeSetting);
        return modeSetting;
    }
    
    protected ColorSetting registerColor(final String name, final GSColor color) {
        final ColorSetting colorSetting = new ColorSetting(name, this, false, color);
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }
    
    protected ColorSetting registerColor(final String name, final GSColor color, final Supplier<Boolean> dipendent) {
        final ColorSetting colorSetting = new ColorSetting(name, this, false, color);
        colorSetting.setVisible(dipendent);
        colorSetting.alphaEnabled();
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }
    
    protected ColorSetting registerColor(final String name, final GSColor color, final Boolean alphaEnabled) {
        final ColorSetting colorSetting = new ColorSetting(name, this, false, color, alphaEnabled);
        colorSetting.alphaEnabled();
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }
    
    protected ColorSetting registerColor(final String name, final GSColor color, final Supplier<Boolean> dipendent, final Boolean alphaEnabled) {
        final ColorSetting colorSetting = new ColorSetting(name, this, false, color, alphaEnabled);
        colorSetting.setVisible(dipendent);
        colorSetting.alphaEnabled();
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }
    
    protected ColorSetting registerColor(final String name) {
        return this.registerColor(name, new GSColor(90, 145, 240));
    }
    
    protected ColorSetting registerColor(final String name, final Supplier<Boolean> dipendent) {
        final ColorSetting color = this.registerColor(name, new GSColor(90, 145, 240));
        color.setVisible(dipendent);
        return color;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        String name();
        
        Category category();
        
        int priority() default 0;
        
        int bind() default 0;
        
        boolean enabled() default false;
        
        boolean drawn() default true;
        
        boolean toggleMsg() default false;
    }
}
