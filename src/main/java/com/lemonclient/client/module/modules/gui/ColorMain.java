// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.gui;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import org.lwjgl.opengl.Display;
import java.util.Iterator;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import java.util.regex.Matcher;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.lemonclient.client.module.modules.movement.SpeedPlus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import com.lemonclient.api.util.player.social.SocialManager;
import java.util.regex.Pattern;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.LemonClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketCustomPayload;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketEntityAction;
import java.util.ArrayList;
import com.lemonclient.api.util.misc.ColorUtil;
import java.util.Arrays;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.event.events.EntityUseTotemEvent;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.awt.Color;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Colors", category = Category.GUI, enabled = true, drawn = false, priority = 10000)
public class ColorMain extends Module
{
    public static ColorMain INSTANCE;
    public ColorSetting enabledColor;
    public DoubleSetting rainbowSpeed;
    public ModeSetting rainbowMode;
    public BooleanSetting customFont;
    public BooleanSetting textFont;
    public BooleanSetting highlightSelf;
    public ModeSetting selfColor;
    public ModeSetting friendColor;
    public ModeSetting enemyColor;
    public ModeSetting chatModuleColor;
    public ModeSetting chatEnableColor;
    public ModeSetting chatDisableColor;
    public ColorSetting Title;
    public ColorSetting Enabled;
    public ColorSetting Disabled;
    public ColorSetting Background;
    public ColorSetting Font;
    public ColorSetting ScrollBar;
    public ColorSetting Highlight;
    public ModeSetting colorModel;
    Color title;
    Color enable;
    Color disable;
    Color background;
    Color font;
    Color scrollBar;
    Color highlight;
    public boolean sneaking;
    public double velocityBoost;
    public List<BlockPos> breakList;
    HashMap<EntityPlayer, BlockPos> list;
    BlockPos lastBreak;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSend;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    public Listener<EntityUseTotemEvent> listListener;
    
    public ColorMain() {
        this.enabledColor = this.registerColor("Main Color", new GSColor(255, 0, 0, 255));
        this.rainbowSpeed = this.registerDouble("Rainbow Speed", 1.0, 0.1, 10.0);
        this.rainbowMode = this.registerMode("Rainbow Mode", Arrays.asList("Normal", "Sin", "Tan", "Sec", "CoTan", "CoSec"), "Normal");
        this.customFont = this.registerBoolean("Custom Font", true);
        this.textFont = this.registerBoolean("Custom Text", false);
        this.highlightSelf = this.registerBoolean("Highlight SelfName", false);
        this.selfColor = this.registerMode("Self Color", ColorUtil.colors, "Blue");
        this.friendColor = this.registerMode("Friend Color", ColorUtil.colors, "Green");
        this.enemyColor = this.registerMode("Enemy Color", ColorUtil.colors, "Red");
        this.chatModuleColor = this.registerMode("Msg Module", ColorUtil.colors, "Aqua");
        this.chatEnableColor = this.registerMode("Msg Enable", ColorUtil.colors, "Green");
        this.chatDisableColor = this.registerMode("Msg Disable", ColorUtil.colors, "Red");
        this.Title = this.registerColor("Title Color", new GSColor(90, 145, 240));
        this.Enabled = this.registerColor("Enabled Color", new GSColor(90, 145, 240));
        this.Disabled = this.registerColor("Disabled", new GSColor(64, 64, 64));
        this.Background = this.registerColor("BackGround Color", new GSColor(195, 195, 195, 150), true);
        this.Font = this.registerColor("Font Color", new GSColor(255, 255, 255));
        this.ScrollBar = this.registerColor("ScrollBar Color", new GSColor(90, 145, 240));
        this.Highlight = this.registerColor("Highlight Color", new GSColor(0, 0, 240));
        this.colorModel = this.registerMode("Color Model", Arrays.asList("RGB", "HSB"), "HSB");
        this.breakList = new ArrayList<BlockPos>();
        this.list = new HashMap<EntityPlayer, BlockPos>();
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (event.getPacket() instanceof CPacketEntityAction) {
                if (((CPacketEntityAction)event.getPacket()).getAction() == CPacketEntityAction.Action.START_SNEAKING) {
                    this.sneaking = true;
                }
                if (((CPacketEntityAction)event.getPacket()).getAction() == CPacketEntityAction.Action.STOP_SNEAKING) {
                    this.sneaking = false;
                }
            }
        }, new Predicate[0]);
        this.packetSend = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof FMLProxyPacket && !ColorMain.mc.isSingleplayer()) {
                event.cancel();
            }
            if (event.getPacket() instanceof CPacketCustomPayload) {
                final CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
                if (packet.getChannelName().equalsIgnoreCase("MC|Brand")) {
                    ((AccessorCPacketCustomPayload)packet).setData(new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
                }
            }
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging packet2 = (CPacketPlayerDigging)event.getPacket();
                if (packet2.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    this.lastBreak = packet2.getPosition();
                }
            }
        }, new Predicate[0]);
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (ColorMain.mc.world == null || ColorMain.mc.player == null) {
            }
            else {
                LemonClient.speedUtil.update();
                LemonClient.positionUtil.updatePosition();
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (ColorMain.mc.world == null || ColorMain.mc.player == null || EntityUtil.isDead(ColorMain.mc.player)) {
            }
            else {
                if (event.getPacket() instanceof SPacketChat) {
                    final String message = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    final Matcher matcher = Pattern.compile("<(.*?)>").matcher(message);
                    String username = "";
                    if (matcher.find()) {
                        username = matcher.group();
                    }
                    else if (message.contains(":")) {
                        final int spaceIndex = message.indexOf(" ");
                        if (spaceIndex != -1) {
                            username = message.substring(0, spaceIndex);
                        }
                    }
                    final String username2 = cleanColor(username);
                    if (SocialManager.isIgnore(username2)) {
                        event.cancel();
                    }
                }
                if (event.getPacket() instanceof SPacketBlockBreakAnim) {
                    final SPacketBlockBreakAnim packet3 = (SPacketBlockBreakAnim)event.getPacket();
                    final BlockPos blockPos = packet3.getPosition();
                    final EntityPlayer entityPlayer = (EntityPlayer)ColorMain.mc.world.getEntityByID(packet3.getBreakerId());
                    if (entityPlayer == null) {
                        return;
                    }
                    else {
                        this.list.put(entityPlayer, blockPos);
                    }
                }
                if (event.getPacket() instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity packet4 = (SPacketEntityVelocity)event.getPacket();
                    final Entity entity = ColorMain.mc.world.getEntityByID(packet4.entityID);
                    if (entity != null && entity == ColorMain.mc.player) {
                        this.velocityBoost = (SpeedPlus.INSTANCE.sum.getValue() ? (this.velocityBoost + Math.hypot(packet4.motionX / 8000.0f, packet4.motionZ / 8000.0f)) : Math.max(this.velocityBoost, Math.hypot(packet4.motionX / 8000.0f, packet4.motionZ / 8000.0f)));
                    }
                }
            }
        }, new Predicate[0]);
        this.listListener = new Listener<EntityUseTotemEvent>(event -> {
            if (event.getEntity() == ColorMain.mc.player && ColorMain.mc.currentScreen instanceof GuiContainer && !(ColorMain.mc.currentScreen instanceof GuiInventory)) {
                ColorMain.mc.player.closeScreen();
            }
        }, new Predicate[0]);
        ColorMain.INSTANCE = this;
    }
    
    public void onDisable() {
        this.enable();
    }
    
    @Override
    public void fast() {
        if (this.title != this.Title.getColor() || this.enable != this.Enabled.getColor() || this.disable != this.Disabled.getColor() || this.background != this.Background.getColor() || this.font != this.Font.getColor() || this.scrollBar != this.ScrollBar.getColor() || this.highlight != this.Highlight.getColor()) {
            this.title = this.Title.getColor();
            this.enable = this.Enabled.getColor();
            this.disable = this.Disabled.getColor();
            this.background = this.Background.getColor();
            this.font = this.Font.getColor();
            this.scrollBar = this.ScrollBar.getColor();
            this.highlight = this.Highlight.getColor();
            LemonClient.INSTANCE.gameSenseGUI.refresh();
        }
        if (!AutoEz.INSTANCE.hi.getValue()) {
            AutoEz.INSTANCE.hi.setValue(true);
        }
        (this.breakList = new ArrayList<BlockPos>()).add(this.lastBreak);
        final List<EntityPlayer> playerList = ColorMain.mc.world.playerEntities;
        for (final EntityPlayer player : playerList) {
            if (this.list.containsKey(player)) {
                final BlockPos pos = this.list.get(player);
                this.breakList.add(pos);
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (!Display.getTitle().equals("Lemon Client v0.0.9")) {
            Display.setTitle("Lemon Client v0.0.9");
            LemonClient.setWindowIcon();
        }
        if (!SpeedPlus.INSTANCE.isEnabled() && MotionUtil.moving(ColorMain.mc.player)) {
            this.velocityBoost = 0.0;
        }
    }
    
    public String highlight(final String string) {
        if (string != null && this.isEnabled()) {
            final String username = ColorMain.mc.getSession().getUsername();
            return string.replace(username, this.getSelfColor() + username).replace(username.toLowerCase(), this.getSelfColor() + username.toLowerCase()).replace(username.toUpperCase(), this.getSelfColor() + username.toUpperCase());
        }
        return string;
    }
    
    public static String cleanColor(final String input) {
        return input.replaceAll("(?i)\\u00A7.", "");
    }
    
    public TextFormatting getSelfColor() {
        return ColorUtil.settingToTextFormatting(this.selfColor);
    }
    
    public TextFormatting getFriendColor() {
        return ColorUtil.settingToTextFormatting(this.friendColor);
    }
    
    public TextFormatting getEnemyColor() {
        return ColorUtil.settingToTextFormatting(this.enemyColor);
    }
    
    public TextFormatting getModuleColor() {
        return ColorUtil.settingToTextFormatting(this.chatModuleColor);
    }
    
    public TextFormatting getEnabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatEnableColor);
    }
    
    public TextFormatting getDisabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatDisableColor);
    }
    
    public GSColor getFriendGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.friendColor));
    }
    
    public GSColor getEnemyGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.enemyColor));
    }
}
