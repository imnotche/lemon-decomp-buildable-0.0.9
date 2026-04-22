// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.Minecraft;

public class ServerUtil
{
    static Minecraft mc;
    private static String serverBrand;
    private static Timing timer;
    private static float[] tpsCounts;
    private static long lastUpdate;
    private static String format;
    private static float TPS;
    
    public static float getTpsFactor() {
        return 20.0f / ServerUtil.TPS;
    }
    
    public static long serverRespondingTime() {
        return ServerUtil.timer.getPassedTimeMs();
    }
    
    public static String getServerBrand() {
        return ServerUtil.serverBrand;
    }
    
    public boolean isServerNotResponding() {
        return ServerUtil.timer.passedMs(500L);
    }
    
    public ServerUtil() {
        ServerUtil.tpsCounts = new float[10];
        ServerUtil.format = "%.3f";
        ServerUtil.timer = new Timing();
        ServerUtil.TPS = 20.0f;
        ServerUtil.lastUpdate = -1L;
        ServerUtil.serverBrand = "";
    }
    
    public void update() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (ServerUtil.lastUpdate == -1L) {
            ServerUtil.lastUpdate = currentTimeMillis;
            return;
        }
        float n = (currentTimeMillis - ServerUtil.lastUpdate) / 20.0f;
        if (n == 0.0f) {
            n = 50.0f;
        }
        float n2;
        if ((n2 = 1000.0f / n) > 20.0f) {
            n2 = 20.0f;
        }
        System.arraycopy(ServerUtil.tpsCounts, 0, ServerUtil.tpsCounts, 1, ServerUtil.tpsCounts.length - 1);
        ServerUtil.tpsCounts[0] = n2;
        double n3 = 0.0;
        final float[] tpsCounts = ServerUtil.tpsCounts;
        for (int length = tpsCounts.length, i = 0; i < length; ++i) {
            n3 += tpsCounts[i];
        }
        double number;
        if ((number = n3 / tpsCounts.length) > 20.0) {
            number = 20.0;
        }
        ServerUtil.TPS = Float.parseFloat(String.format(ServerUtil.format, number));
        ServerUtil.lastUpdate = currentTimeMillis;
    }
    
    public static int getPing() {
        if (ServerUtil.mc.world == null || ServerUtil.mc.player == null) {
            return 0;
        }
        try {
            return Objects.requireNonNull(ServerUtil.mc.getConnection()).getPlayerInfo(ServerUtil.mc.getConnection().getGameProfile().getId()).getResponseTime();
        }
        catch (final Exception ex) {
            return 0;
        }
    }
    
    public static float getTPS() {
        return ServerUtil.TPS;
    }
    
    public void setServerBrand(String serverBrand) {
        serverBrand = serverBrand;
    }
    
    public static void reset() {
        Arrays.fill(ServerUtil.tpsCounts, 20.0f);
        ServerUtil.TPS = 20.0f;
    }
    
    public static void onPacketReceived() {
        ServerUtil.timer.reset();
    }
    
    static {
        ServerUtil.mc = Minecraft.getMinecraft();
        ServerUtil.format = "%.3f";
    }
}
