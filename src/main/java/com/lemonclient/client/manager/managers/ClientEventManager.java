// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.manager.managers;

import com.lemonclient.api.event.events.SendMessageEvent;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import org.lwjgl.input.Mouse;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiChat;
import com.lemonclient.client.command.CommandManager;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import com.lemonclient.api.util.chat.NotificationManager;
import com.lemonclient.api.event.events.Render3DEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.util.render.RenderUtil;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import com.lemonclient.api.util.world.TimerUtils;
import net.minecraft.inventory.IInventory;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemShulkerBox;
import com.lemonclient.client.module.modules.misc.ShulkerBypass;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraft.entity.Entity;
import com.lemonclient.client.PeekCmd;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import com.lemonclient.client.module.modules.dev.AntiPush;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import com.lemonclient.client.module.modules.qwq.AntiUnicdoe;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.GlStateManager;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.api.event.events.Render2DEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import java.util.Iterator;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import com.lemonclient.api.event.events.PlayerLeaveEvent;
import com.lemonclient.api.event.events.PlayerJoinEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.player.NameUtil;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import java.util.function.Predicate;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketCustomPayload;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import java.util.HashSet;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.Set;
import com.lemonclient.client.manager.Manager;

public enum ClientEventManager implements Manager
{
    INSTANCE;
    
    final String LAG_MESSAGE = "\u0101\u0201\u0301\u0401\u0601\u0701\u0801\u0901\u0a01\u0b01\u0e01\u0f01\u1001\u1101\u1201\u1301\u1401\u1501\u1601\u1701\u1801\u1901\u1a01\u1b01\u1c01\u1d01\u1e01\u1f01 \u2101\u2201\u2301\u2401\u2501\u2701\u2801\u2901\u2a01\u2b01\u2c01\u2d01\u2e01\u2f01\u3001\u3101\u3201\u3301\u3401\u3501\u3601\u3701\u3801\u3901\u3a01\u3b01\u3c01\u3d01\u3e01\u3f01\u4001\u4101\u4201\u4301\u4401\u4501\u4601\u4701\u4801\u4901\u4a01\u4b01\u4c01\u4d01\u4e01\u4f01\u5001\u5101\u5201\u5301\u5401\u5501\u5601\u5701\u5801\u5901\u5a01\u5b01\u5c01\u5d01\u5e01\u5f01\u6001\u6101\u6201\u6301\u6401\u6501\u6601\u6701\u6801\u6901\u6a01\u6b01\u6c01\u6d01\u6e01\u6f01\u7001\u7101\u7201\u7301\u7401\u7501\u7601\u7701\u7801\u7901\u7a01\u7b01\u7c01\u7d01\u7e01\u7f01\u8001\u8101\u8201\u8301\u8401\u8501\u8601\u8701\u8801\u8901\u8a01\u8b01\u8c01\u8d01\u8e01\u8f01\u9001\u9101\u9201\u9301\u9401\u9501\u9601\u9701\u9801\u9901\u9a01\u9b01\u9c01\u9d01\u9e01\u9f01\ua001\ua101\ua201\ua301\ua401\ua501\ua601\ua701\ua801\ua901\uaa01\uab01\uac01\uad01\uae01\uaf01\ub001\ub101\ub201\ub301\ub401\ub501\ub601\ub701\ub801\ub901\uba01\ubb01\ubc01\ubd01";
    final Set<Character> lagMessageSet;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSend;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    ClientEventManager() {
        this.lagMessageSet = new HashSet<Character>();
        this.packetSend = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof FMLProxyPacket && !Minecraft.getMinecraft().isSingleplayer()) {
                event.cancel();
            }
            if (event.getPacket() instanceof CPacketCustomPayload) {
                final CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
                if (packet.getChannelName().equalsIgnoreCase("MC|Brand")) {
                    ((AccessorCPacketCustomPayload)packet).setData(new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
                }
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                final SPacketPlayerListItem packet2 = (SPacketPlayerListItem)event.getPacket();
                SPacketPlayerListItem.AddPlayerData playerData = null;
                if (packet2.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    for (final SPacketPlayerListItem.AddPlayerData entry : packet2.getEntries()) {
                        final SPacketPlayerListItem.AddPlayerData playerDataEntry = entry;
                        if (playerDataEntry.getProfile().getId() != this.getMinecraft().session.getProfile().getId()) {
                            new Thread(() -> {
                                final String name2 = NameUtil.resolveName(playerDataEntry.getProfile().getId().toString());
                                if (name2 != null && this.getPlayer() != null && this.getPlayer().ticksExisted >= 1000) {
                                    LemonClient.EVENT_BUS.post(new PlayerJoinEvent(name2));
                                }
                            }).start();
                        }
                    }
                }
                if (packet2.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    for (final SPacketPlayerListItem.AddPlayerData playerData2 : packet2.getEntries()) {
                        if (playerData2.getProfile().getId() != this.getMinecraft().session.getProfile().getId()) {
                            new Thread(() -> {
                                final String name3 = NameUtil.resolveName(playerData2.getProfile().getId().toString());
                                if (name3 != null && this.getPlayer() != null && this.getPlayer().ticksExisted >= 1000) {
                                    LemonClient.EVENT_BUS.post(new PlayerLeaveEvent(name3));
                                }
                            }).start();
                        }
                    }
                }
            }
            if (event.getPacket() instanceof SPacketTimeUpdate) {
                LemonClient.serverUtil.update();
            }
        }, new Predicate[0]);
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            final Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            for (final Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) {
                    continue;
                }
                this.getProfiler().startSection(module.getName());
                module.onRender2D(render2DEvent);
                this.getProfiler().endSection();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        if (this.lagMessageSet.isEmpty()) {
            for (int i = 0; i < "\u0101\u0201\u0301\u0401\u0601\u0701\u0801\u0901\u0a01\u0b01\u0e01\u0f01\u1001\u1101\u1201\u1301\u1401\u1501\u1601\u1701\u1801\u1901\u1a01\u1b01\u1c01\u1d01\u1e01\u1f01 \u2101\u2201\u2301\u2401\u2501\u2701\u2801\u2901\u2a01\u2b01\u2c01\u2d01\u2e01\u2f01\u3001\u3101\u3201\u3301\u3401\u3501\u3601\u3701\u3801\u3901\u3a01\u3b01\u3c01\u3d01\u3e01\u3f01\u4001\u4101\u4201\u4301\u4401\u4501\u4601\u4701\u4801\u4901\u4a01\u4b01\u4c01\u4d01\u4e01\u4f01\u5001\u5101\u5201\u5301\u5401\u5501\u5601\u5701\u5801\u5901\u5a01\u5b01\u5c01\u5d01\u5e01\u5f01\u6001\u6101\u6201\u6301\u6401\u6501\u6601\u6701\u6801\u6901\u6a01\u6b01\u6c01\u6d01\u6e01\u6f01\u7001\u7101\u7201\u7301\u7401\u7501\u7601\u7701\u7801\u7901\u7a01\u7b01\u7c01\u7d01\u7e01\u7f01\u8001\u8101\u8201\u8301\u8401\u8501\u8601\u8701\u8801\u8901\u8a01\u8b01\u8c01\u8d01\u8e01\u8f01\u9001\u9101\u9201\u9301\u9401\u9501\u9601\u9701\u9801\u9901\u9a01\u9b01\u9c01\u9d01\u9e01\u9f01\ua001\ua101\ua201\ua301\ua401\ua501\ua601\ua701\ua801\ua901\uaa01\uab01\uac01\uad01\uae01\uaf01\ub001\ub101\ub201\ub301\ub401\ub501\ub601\ub701\ub801\ub901\uba01\ubb01\ubc01\ubd01".length(); ++i) {
                this.lagMessageSet.add("\u0101\u0201\u0301\u0401\u0601\u0701\u0801\u0901\u0a01\u0b01\u0e01\u0f01\u1001\u1101\u1201\u1301\u1401\u1501\u1601\u1701\u1801\u1901\u1a01\u1b01\u1c01\u1d01\u1e01\u1f01 \u2101\u2201\u2301\u2401\u2501\u2701\u2801\u2901\u2a01\u2b01\u2c01\u2d01\u2e01\u2f01\u3001\u3101\u3201\u3301\u3401\u3501\u3601\u3701\u3801\u3901\u3a01\u3b01\u3c01\u3d01\u3e01\u3f01\u4001\u4101\u4201\u4301\u4401\u4501\u4601\u4701\u4801\u4901\u4a01\u4b01\u4c01\u4d01\u4e01\u4f01\u5001\u5101\u5201\u5301\u5401\u5501\u5601\u5701\u5801\u5901\u5a01\u5b01\u5c01\u5d01\u5e01\u5f01\u6001\u6101\u6201\u6301\u6401\u6501\u6601\u6701\u6801\u6901\u6a01\u6b01\u6c01\u6d01\u6e01\u6f01\u7001\u7101\u7201\u7301\u7401\u7501\u7601\u7701\u7801\u7901\u7a01\u7b01\u7c01\u7d01\u7e01\u7f01\u8001\u8101\u8201\u8301\u8401\u8501\u8601\u8701\u8801\u8901\u8a01\u8b01\u8c01\u8d01\u8e01\u8f01\u9001\u9101\u9201\u9301\u9401\u9501\u9601\u9701\u9801\u9901\u9a01\u9b01\u9c01\u9d01\u9e01\u9f01\ua001\ua101\ua201\ua301\ua401\ua501\ua601\ua701\ua801\ua901\uaa01\uab01\uac01\uad01\uae01\uaf01\ub001\ub101\ub201\ub301\ub401\ub501\ub601\ub701\ub801\ub901\uba01\ubb01\ubc01\ubd01".charAt(i));
            }
        }
        if (event.getMessage().getFormattedText().contains("{") || event.getMessage().getFormattedText().contains("}")) {
            event.setCanceled(true);
            final TextComponentString string = new TextComponentString(event.getMessage().getFormattedText().replace("{", "").replace("}", "").replace("$", "").replace("ldap", ""));
            Minecraft.getMinecraft().player.sendMessage(string);
            return;
        }
        if (ModuleManager.isModuleEnabled(AntiUnicdoe.class)) {
            int count = 0;
            final String text = event.getMessage().getFormattedText();
            for (int j = 0; j < text.length(); ++j) {
                if (this.lagMessageSet.contains(text.charAt(j))) {
                    ++count;
                }
            }
            if (count >= 25) {
                event.setCanceled(true);
                final TextComponentString string2 = new TextComponentString("(lag message)");
                Minecraft.getMinecraft().player.sendMessage(string2);
                return;
            }
        }
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onRenderBlockOverlay(final RenderBlockOverlayEvent event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(final LivingEntityUseItemEvent.Finish event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onInputUpdate(final InputUpdateEvent event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onPlayerPush(final PlayerSPPushOutOfBlocksEvent event) {
        if (ModuleManager.isModuleEnabled(AntiPush.class)) {
            event.setCanceled(true);
        }
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onEntitySpawn(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof EntityItem) {
            PeekCmd.drop = (EntityItem)entity;
            PeekCmd.metadataTicks = 0;
        }
    }
    
    @SubscribeEvent
    public void onWorldLoad(final WorldEvent.Load event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFogDensity(final EntityViewRenderEvent.FogDensity event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFov(final EntityViewRenderEvent.FOVModifier event) {
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (ModuleManager.isModuleEnabled("Peek") && ShulkerBypass.shulkers) {
            if (event.phase == TickEvent.Phase.END) {
                if (PeekCmd.guiTicks > -1) {
                    ++PeekCmd.guiTicks;
                }
                if (PeekCmd.metadataTicks > -1) {
                    ++PeekCmd.metadataTicks;
                }
            }
            if (PeekCmd.metadataTicks >= ShulkerBypass.delay) {
                PeekCmd.metadataTicks = -1;
                if (PeekCmd.drop.getItem().getItem() instanceof ItemShulkerBox) {
                    MessageBus.sendClientDeleteMessage("New shulker found. use /peek to view its content " + TextFormatting.GREEN + "(" + PeekCmd.drop.getItem().getDisplayName() + ")", Notification.Type.INFO, "Peek", 3);
                    PeekCmd.shulker = PeekCmd.drop.getItem();
                }
            }
            if (PeekCmd.guiTicks == 20) {
                PeekCmd.guiTicks = -1;
                Minecraft.getMinecraft().player.displayGUIChest(PeekCmd.toOpen);
            }
        }
        if (this.getMinecraft().player != null && this.getMinecraft().world != null) {
            final int timerSpeed = (int)TimerUtils.getTimer();
            for (final Module module : ModuleManager.getModules()) {
                try {
                    if (!module.isEnabled()) {
                        continue;
                    }
                    final Module module2 = module;
                    ++module2.onTickTimer;
                    if (module.onTickTimer < timerSpeed) {
                        continue;
                    }
                    module.onTick();
                    module.onTickTimer = 0;
                }
                catch (final Exception e) {
                    if (this.getWorld() != null && this.getPlayer() != null) {
                        MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + e, Notification.Type.ERROR);
                    }
                    module.setEnabled(false);
                    for (final StackTraceElement stack : e.getStackTrace()) {
                        System.out.println(stack.toString());
                    }
                }
            }
        }
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (this.getMinecraft().player == null || this.getMinecraft().world == null) {
            return;
        }
        if (event.getEntity().getEntityWorld().isRemote && event.getEntityLiving() == this.getPlayer()) {
            final int timerSpeed = (int)TimerUtils.getTimer();
            for (final Module module : ModuleManager.getModules()) {
                try {
                    if (!module.isEnabled()) {
                        continue;
                    }
                    final Module module2 = module;
                    ++module2.onUpdateTimer;
                    if (module.onUpdateTimer < timerSpeed) {
                        continue;
                    }
                    module.onUpdate();
                    module.onUpdateTimer = 0;
                }
                catch (final Exception e) {
                    if (this.getWorld() != null && this.getPlayer() != null) {
                        MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + e, Notification.Type.ERROR);
                    }
                    module.setEnabled(false);
                    for (final StackTraceElement stack : e.getStackTrace()) {
                        System.out.println(stack.toString());
                    }
                }
            }
            LemonClient.EVENT_BUS.post(event);
        }
    }
    
    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (this.getMinecraft().player == null || this.getMinecraft().world == null) {
            return;
        }
        this.getProfiler().startSection("lemonclient");
        this.getProfiler().startSection("setup");
        RenderUtil.prepare();
        final RenderEvent event2 = new RenderEvent(event.getPartialTicks());
        this.getProfiler().endSection();
        for (final Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            this.getProfiler().startSection(module.getName());
            module.onWorldRender(event2);
            this.getProfiler().endSection();
        }
        this.getProfiler().startSection("release");
        RenderUtil.release();
        this.getProfiler().endSection();
        this.getProfiler().endSection();
    }
    
    @SubscribeEvent
    public void onRender3D(final RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (this.getMinecraft().player == null || this.getMinecraft().world == null) {
            return;
        }
        this.getProfiler().startSection("lemonclient");
        this.getProfiler().startSection("setup");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0f);
        final Render3DEvent event2 = new Render3DEvent(event.getPartialTicks());
        this.getProfiler().endSection();
        for (final Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            this.getProfiler().startSection(module.getName());
            module.onRender3D(event2);
            this.getProfiler().endSection();
        }
        this.getProfiler().startSection("release");
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        this.getProfiler().endSection();
        this.getProfiler().endSection();
    }
    
    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        if (this.getMinecraft().player == null || this.getMinecraft().world == null) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            for (final Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) {
                    continue;
                }
                module.onRender();
                NotificationManager.draw();
            }
            LemonClient.INSTANCE.gameSenseGUI.render();
        }
        LemonClient.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState() || Keyboard.getEventKey() == 0) {
            return;
        }
        final EntityPlayerSP player = this.getPlayer();
        if (player != null && !player.isSneaking()) {
            final String prefix = CommandManager.getCommandPrefix();
            final char typedChar = Keyboard.getEventCharacter();
            if (prefix.length() == 1 && prefix.charAt(0) == typedChar) {
                this.getMinecraft().displayGuiScreen(new GuiChat(prefix));
            }
        }
        final int key = Keyboard.getEventKey();
        if (key != 0) {
            for (final Module module : ModuleManager.getModules()) {
                if (module.getBind() != key) {
                    continue;
                }
                module.toggle();
            }
        }
        LemonClient.INSTANCE.gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
    }
    
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            LemonClient.EVENT_BUS.post(event);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(final ClientChatEvent event) {
        if (event.getMessage().startsWith(CommandManager.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                this.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                CommandManager.callCommand(event.getMessage().substring(1), false);
            }
            catch (final Exception e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + e.getMessage(), true);
            }
        }
        else {
            final SendMessageEvent eventNow = new SendMessageEvent(event.getMessage());
            LemonClient.EVENT_BUS.post(eventNow);
            if (eventNow.isCancelled()) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void init(final TickEvent.ClientTickEvent event) {
        this.fastest();
    }
    
    @SubscribeEvent
    public void init(final TickEvent.ServerTickEvent event) {
        this.fastest();
    }
    
    @SubscribeEvent
    public void init(final TickEvent.PlayerTickEvent event) {
        this.fastest();
    }
    
    @SubscribeEvent
    public void init(final TickEvent.WorldTickEvent event) {
        this.fastest();
    }
    
    public void fastest() {
        if (this.getMinecraft().player != null && this.getMinecraft().world != null) {
            final int timerSpeed = (int)TimerUtils.getTimer();
            for (final Module module : ModuleManager.getModules()) {
                try {
                    if (!module.isEnabled()) {
                        continue;
                    }
                    final Module module2 = module;
                    ++module2.fastTimer;
                    if (module.fastTimer < timerSpeed) {
                        continue;
                    }
                    module.fast();
                    module.fastTimer = 0;
                }
                catch (final Exception e) {
                    if (this.getWorld() != null && this.getPlayer() != null) {
                        MessageBus.sendClientPrefixMessage("Disabled " + module.getName() + " due to " + e, Notification.Type.ERROR);
                    }
                    module.setEnabled(false);
                    for (final StackTraceElement stack : e.getStackTrace()) {
                        System.out.println(stack.toString());
                    }
                }
            }
        }
    }
}
