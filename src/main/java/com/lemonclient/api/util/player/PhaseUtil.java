// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import java.util.Arrays;
import net.minecraft.network.Packet;
import com.lemonclient.api.util.world.MotionUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.client.Minecraft;
import java.util.List;

public class PhaseUtil
{
    public static List<String> bound;
    public static String normal;
    private static final Minecraft mc;
    
    public static CPacketPlayer doBounds(final String mode, final boolean send) {
        CPacketPlayer packet = new CPacketPlayer.PositionRotation(0.0, 0.0, 0.0, 0.0f, 0.0f, false);
        switch (mode) {
            case "Up": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Down": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY - 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Zero": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, 0.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Min": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 100.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Alternate": {
                if (PhaseUtil.mc.player.ticksExisted % 2 == 0) {
                    packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                    break;
                }
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY - 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Forward": {
                final double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY + 33.4, PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Flat": {
                final double[] dir = MotionUtil.forward(100.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY, PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Constrict": {
                final double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY + ((PhaseUtil.mc.player.posY > 64.0) ? -33.4 : 33.4), PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
        }
        PhaseUtil.mc.player.connection.sendPacket(packet);
        return packet;
    }
    
    public static CPacketPlayer doBounds(final String mode, final int c) {
        CPacketPlayer packet = new CPacketPlayer.PositionRotation(0.0, 0.0, 0.0, 0.0f, 0.0f, false);
        switch (mode) {
            case "Up": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Down": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY - 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Zero": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, 0.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Min": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 100.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Alternate": {
                if (PhaseUtil.mc.player.ticksExisted % 2 == 0) {
                    packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY + 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                    break;
                }
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX, PhaseUtil.mc.player.posY - 69420.0, PhaseUtil.mc.player.posZ, PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Forward": {
                final double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY + 33.4, PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Flat": {
                final double[] dir = MotionUtil.forward(100.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY, PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
            case "Constrict": {
                final double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.player.posX + dir[0], PhaseUtil.mc.player.posY + ((PhaseUtil.mc.player.posY > 64.0) ? -33.4 : 33.4), PhaseUtil.mc.player.posZ + dir[1], PhaseUtil.mc.player.rotationYaw, PhaseUtil.mc.player.rotationPitch, false);
                break;
            }
        }
        for (int i = 1; i < c; ++i) {
            PhaseUtil.mc.player.connection.sendPacket(packet);
        }
        return packet;
    }
    
    static {
        PhaseUtil.bound = Arrays.asList("Up", "Alternate", "Down", "Zero", "Min", "Forward", "Flat", "LimitJitter", "Constrict", "None");
        PhaseUtil.normal = "Forward";
        mc = Minecraft.getMinecraft();
    }
}
