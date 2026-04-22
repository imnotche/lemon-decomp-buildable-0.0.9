// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client;

import java.util.ArrayList;
import me.zero.alpine.bus.EventManager;
import org.apache.logging.log4j.LogManager;
import java.io.InputStream;
import com.lemonclient.api.util.misc.IconUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import com.lemonclient.client.manager.ManagerLoader;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.api.config.LoadConfig;
import java.awt.Font;
import org.lwjgl.opengl.Display;
import com.lemonclient.api.util.render.CapeUtil;
import com.lemonclient.api.util.chat.notification.NotificationsManager;
import com.lemonclient.api.util.chat.notification.notifications.BottomRightNotification;
import com.lemonclient.api.util.chat.notification.NotificationType;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.lemonclient.api.util.log4j.Fixer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.api.util.font.CFontRenderer;
import com.lemonclient.api.util.player.SpeedUtil;
import com.lemonclient.api.util.misc.ServerUtil;
import com.lemonclient.api.util.player.PositionUtil;
import java.util.List;
import me.zero.alpine.bus.EventBus;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "lemonclient", name = "Lemon Client", version = "v0.0.9")
public class LemonClient
{
    public static final String MODNAME = "Lemon Client";
    public static final String MODID = "lemonclient";
    public static final String MODVER = "v0.0.9";
    public static String Ver;
    public static String KEY;
    public static final Logger LOGGER;
    public static final EventBus EVENT_BUS;
    public static PositionUtil positionUtil;
    public static ServerUtil serverUtil;
    public static SpeedUtil speedUtil;
    public static Runtime runtime;
    @Mod.Instance
    public static LemonClient INSTANCE;
    public CFontRenderer cFontRenderer;
    public LemonClientGUI gameSenseGUI;
    
    @Mod.EventHandler
    public void construct(final FMLConstructionEvent event) {
        try {
            Fixer.disableJndiManager();
        }
        catch (final Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        Fixer.doRuntimeTest(event.getModLog());
    }
    
    public LemonClient() {
        LemonClient.INSTANCE = this;
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        LemonClient.LOGGER.info("Starting up Lemon Client v0.0.9!");
        this.startClient();
        LemonClient.LOGGER.info("Finished initialization for Lemon Client v0.0.9!");
        final NotificationType type = NotificationType.WELCOME;
        final int length = 20;
        final String msg = "You are on the latest version";
        NotificationsManager.show(new BottomRightNotification(type, "LemonClient", msg, length));
        CapeUtil.init();
        Display.setTitle("Lemon Client v0.0.9");
        setWindowIcon();
    }
    
    private void startClient() {
        this.cFontRenderer = new CFontRenderer(new Font("Comic Sans Ms", 0, 17), false, true);
        LoadConfig.init();
        ModuleManager.init();
        CommandManager.init();
        ManagerLoader.init();
        this.gameSenseGUI = new LemonClientGUI();
        LoadConfig.init();
        LemonClient.positionUtil = new PositionUtil();
        LemonClient.serverUtil = new ServerUtil();
        LemonClient.speedUtil = new SpeedUtil();
        LemonClient.INSTANCE.gameSenseGUI.refresh();
    }
    
    public static void shutdown() {
    }
    
    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (final InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/lemonclient/icons/icon-16x.png");
                 final InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/lemonclient/icons/icon-32x.png")) {
                final ByteBuffer[] icons = { IconUtil.INSTANCE.readImageToBuffer(inputStream32x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x) };
                Display.setIcon(icons);
            }
            catch (final Exception ex) {}
        }
    }
    
    static {
        LemonClient.Ver = "009";
        LemonClient.KEY = "vMQtVc69qr";
        LOGGER = LogManager.getLogger("Lemon Client");
        EVENT_BUS = new EventManager();
        LemonClient.runtime = Runtime.getRuntime();
    }
}
