package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

@Module.Declaration(name="ClipFlight", category=Category.Exploits)
public class ClipFlight
        extends Module {
    ModeSetting flight = this.registerMode("Mode", Arrays.asList("Flight", "Clip"), "Clip");
    IntegerSetting packets = this.registerInteger("Packets", 80, 1, 300);
    IntegerSetting speed = this.registerInteger("XZ Speed", 7, -99, 99, () -> ((String)this.flight.getValue()).equalsIgnoreCase("Flight"));
    IntegerSetting speedY = this.registerInteger("Y Speed", 7, -99, 99, () -> !((String)this.flight.getValue()).equalsIgnoreCase("Relative"));
    BooleanSetting bypass = this.registerBoolean("Bypass", false);
    IntegerSetting interval = this.registerInteger("Interval", 25, 1, 100, () -> ((String)this.flight.getValue()).equalsIgnoreCase("Clip"));
    BooleanSetting update = this.registerBoolean("Update Position Client Side", false);
    int num = 0;
    double startFlat = 0.0;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        double[] dir = MotionUtil.forward(((Integer)this.speed.getValue()).intValue());
        switch ((String)this.flight.getValue()) {
            case "Flight": {
                double xPos = ClipFlight.mc.player.posX;
                double yPos = ClipFlight.mc.player.posY;
                double zPos = ClipFlight.mc.player.posZ;
                if (ClipFlight.mc.gameSettings.keyBindJump.isKeyDown() && !ClipFlight.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    yPos += (double)((Integer)this.speedY.getValue()).intValue();
                } else if (ClipFlight.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    yPos -= (double)((Integer)this.speedY.getValue()).intValue();
                }
                ClipFlight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(xPos += dir[0], yPos, zPos += dir[1], false));
                if (((Boolean)this.update.getValue()).booleanValue()) {
                    ClipFlight.mc.player.setPosition(xPos, yPos, zPos);
                }
                if (!((Boolean)this.bypass.getValue()).booleanValue()) break;
                ClipFlight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ClipFlight.mc.player.posX, ClipFlight.mc.player.posY + 0.05, ClipFlight.mc.player.posZ, true));
                break;
            }
            case "Clip": {
                if (!ClipFlight.mc.gameSettings.keyBindSprint.isKeyDown() && ClipFlight.mc.player.ticksExisted % (Integer)this.interval.getValue() != 0) break;
                for (int i = 0; i < (Integer)this.packets.getValue(); ++i) {
                    double yposition = ClipFlight.mc.player.posY + (double)((Integer)this.speedY.getValue()).intValue();
                    ClipFlight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ClipFlight.mc.player.posX, yposition, ClipFlight.mc.player.posZ, false));
                    if (((Boolean)this.update.getValue()).booleanValue()) {
                        ClipFlight.mc.player.setPosition(ClipFlight.mc.player.posX, yposition, ClipFlight.mc.player.posZ);
                    }
                    if (!((Boolean)this.bypass.getValue()).booleanValue()) continue;
                    ClipFlight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(ClipFlight.mc.player.posX, ClipFlight.mc.player.posY + 0.05, ClipFlight.mc.player.posZ, true));
                }
                break;
            }
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.startFlat = ClipFlight.mc.player.posY;
        this.num = 0;
    }
}
