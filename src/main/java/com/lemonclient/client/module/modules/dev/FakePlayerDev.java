// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.init.Enchantments;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.network.Packet;
import java.util.function.Predicate;
import com.lemonclient.api.event.events.TotemPopEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.combat.DamageUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import java.util.Collection;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import java.util.Arrays;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.ArrayList;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.StringSetting;
import net.minecraft.item.ItemStack;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "FakePlayerDev", category = Category.Dev)
public class FakePlayerDev extends Module
{
    private final ItemStack[] armors;
    StringSetting nameFakePlayer;
    BooleanSetting copyInventory;
    BooleanSetting playerStacked;
    BooleanSetting onShift;
    BooleanSetting simulateDamage;
    IntegerSetting vulnerabilityTick;
    IntegerSetting resetHealth;
    IntegerSetting tickRegenVal;
    IntegerSetting startHealth;
    ModeSetting moving;
    DoubleSetting speed;
    DoubleSetting range;
    BooleanSetting followPlayer;
    BooleanSetting resistance;
    BooleanSetting pop;
    int incr;
    boolean beforePressed;
    ArrayList<playerInfo> listPlayers;
    movingManager manager;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener;
    
    public FakePlayerDev() {
        this.armors = new ItemStack[] { new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND_HELMET) };
        this.nameFakePlayer = this.registerString("Name FakePlayer", "NotLazyOfLazys");
        this.copyInventory = this.registerBoolean("Copy Inventory", false);
        this.playerStacked = this.registerBoolean("Player Stacked", false);
        this.onShift = this.registerBoolean("On Shift", false);
        this.simulateDamage = this.registerBoolean("Simulate Damage", false);
        this.vulnerabilityTick = this.registerInteger("Vulnerability Tick", 4, 0, 10);
        this.resetHealth = this.registerInteger("Reset Health", 10, 0, 36);
        this.tickRegenVal = this.registerInteger("Tick Regen", 4, 0, 30);
        this.startHealth = this.registerInteger("Start Health", 20, 0, 30);
        this.moving = this.registerMode("Moving", Arrays.asList("None", "Line", "Circle", "Random"), "None");
        this.speed = this.registerDouble("Speed", 0.36, 0.0, 4.0);
        this.range = this.registerDouble("Range", 3.0, 0.0, 14.0);
        this.followPlayer = this.registerBoolean("Follow Player", true);
        this.resistance = this.registerBoolean("Resistance", true);
        this.pop = this.registerBoolean("Pop", true);
        this.listPlayers = new ArrayList<playerInfo>();
        this.manager = new movingManager();
        this.packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.simulateDamage.getValue()) {
                final Packet packet = event.getPacket();
                if (packet instanceof SPacketSoundEffect) {
                    final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect)packet;
                    if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                        for (final Entity entity : new ArrayList<Entity>(FakePlayerDev.mc.world.loadedEntityList)) {
                            if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0) {
                                for (final EntityPlayer entityPlayer : FakePlayerDev.mc.world.playerEntities) {
                                    if (entityPlayer.getName().split(this.nameFakePlayer.getText()).length == 2) {
                                        final Optional<playerInfo> temp = this.listPlayers.stream().filter(e -> e.name.equals(entityPlayer.getName())).findAny();
                                        if (temp.isPresent()) {
                                            if (!temp.get().canPop()) {
                                                continue;
                                            }
                                            else {
                                                final float damage = DamageUtil.calculateDamage(entityPlayer, entityPlayer.getPositionVector(), entityPlayer.getEntityBoundingBox(), packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ(), 6.0f, "Default");
                                                if (damage > entityPlayer.getHealth()) {
                                                    entityPlayer.setHealth((float)this.resetHealth.getValue());
                                                    if (this.pop.getValue()) {
                                                        FakePlayerDev.mc.effectRenderer.emitParticleAtEntity(entityPlayer, EnumParticleTypes.TOTEM, 30);
                                                        FakePlayerDev.mc.world.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
                                                    }
                                                    LemonClient.EVENT_BUS.post(new TotemPopEvent(entityPlayer));
                                                }
                                                else {
                                                    entityPlayer.setHealth(entityPlayer.getHealth() - damage);
                                                }
                                                temp.get().tickPop = 0;
                                            }
                                        }
                                        else {
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        this.incr = 0;
        this.beforePressed = false;
        if (FakePlayerDev.mc.player == null || FakePlayerDev.mc.player.isDead) {
            this.disable();
            return;
        }
        if (!this.onShift.getValue()) {
            this.spawnPlayer();
        }
    }
    
    void spawnPlayer() {
        final EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(FakePlayerDev.mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), this.nameFakePlayer.getText()));
        clonedPlayer.copyLocationAndAnglesFrom(FakePlayerDev.mc.player);
        clonedPlayer.rotationYawHead = FakePlayerDev.mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = FakePlayerDev.mc.player.rotationYaw;
        clonedPlayer.rotationPitch = FakePlayerDev.mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth((float)this.startHealth.getValue());
        FakePlayerDev.mc.world.addEntityToWorld(-1234 + this.incr, clonedPlayer);
        ++this.incr;
        if (this.copyInventory.getValue()) {
            clonedPlayer.inventory.copyInventory(FakePlayerDev.mc.player.inventory);
        }
        else if (this.playerStacked.getValue()) {
            for (int i = 0; i < 4; ++i) {
                final ItemStack item = this.armors[i];
                item.addEnchantment((i == 3) ? Enchantments.BLAST_PROTECTION : Enchantments.PROTECTION, 4);
                clonedPlayer.inventory.armorInventory.set(i, item);
            }
        }
        if (this.resistance.getValue()) {
            clonedPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(11), 123456789, 0));
        }
        clonedPlayer.onEntityUpdate();
        this.listPlayers.add(new playerInfo(clonedPlayer.getName()));
        if (!this.moving.getValue().equals("None")) {
            this.manager.addPlayer(clonedPlayer.entityId, this.moving.getValue(), this.speed.getValue(), this.moving.getValue().equals("Line") ? this.getDirection() : -1, this.range.getValue(), this.followPlayer.getValue());
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.onShift.getValue() && FakePlayerDev.mc.gameSettings.keyBindSneak.isPressed() && !this.beforePressed) {
            this.beforePressed = true;
            this.spawnPlayer();
        }
        else {
            this.beforePressed = false;
        }
        for (int i = 0; i < this.listPlayers.size(); ++i) {
            if (this.listPlayers.get(i).update()) {
                final int finalI = i;
                final Optional<EntityPlayer> temp = FakePlayerDev.mc.world.playerEntities.stream().filter(e -> e.getName().equals(this.listPlayers.get(finalI).name)).findAny();
                if (temp.isPresent() && temp.get().getHealth() < 20.0f) {
                    temp.get().setHealth(temp.get().getHealth() + 1.0f);
                }
            }
        }
        this.manager.update();
    }
    
    int getDirection() {
        int yaw = (int)RotationUtil.normalizeAngle(FakePlayerDev.mc.player.getPitchYaw().y);
        if (yaw < 0) {
            yaw += 360;
        }
        yaw += 22;
        yaw %= 360;
        return yaw / 45;
    }
    
    public void onDisable() {
        if (FakePlayerDev.mc.world != null) {
            for (int i = 0; i < this.incr; ++i) {
                FakePlayerDev.mc.world.removeEntityFromWorld(-1234 + i);
            }
        }
        this.listPlayers.clear();
        this.manager.remove();
    }
    
    class playerInfo
    {
        final String name;
        int tickPop;
        int tickRegen;
        
        public playerInfo(final String name) {
            this.tickPop = -1;
            this.tickRegen = 0;
            this.name = name;
        }
        
        boolean update() {
            if (this.tickPop != -1 && ++this.tickPop >= FakePlayerDev.this.vulnerabilityTick.getValue()) {
                this.tickPop = -1;
            }
            if (++this.tickRegen >= FakePlayerDev.this.tickRegenVal.getValue()) {
                this.tickRegen = 0;
                return true;
            }
            return false;
        }
        
        boolean canPop() {
            return this.tickPop == -1;
        }
    }
    
    static class movingPlayer
    {
        private final int id;
        private final String type;
        private final double speed;
        private final int direction;
        private final double range;
        private final boolean follow;
        int rad;
        
        public movingPlayer(final int id, final String type, final double speed, final int direction, final double range, final boolean follow) {
            this.rad = 0;
            this.id = id;
            this.type = type;
            this.speed = speed;
            this.direction = Math.abs(direction);
            this.range = range;
            this.follow = follow;
        }
        
        void move() {
            final Entity player = FakePlayerDev.mc.world.getEntityByID(this.id);
            if (player != null) {
                final String type = this.type;
                switch (type) {
                    case "Line": {
                        double posX = this.follow ? FakePlayerDev.mc.player.posX : player.posX;
                        double posY = this.follow ? FakePlayerDev.mc.player.posY : player.posY;
                        double posZ = this.follow ? FakePlayerDev.mc.player.posZ : player.posZ;
                        switch (this.direction) {
                            case 0: {
                                posZ += this.speed;
                                break;
                            }
                            case 1: {
                                posX -= this.speed / 2.0;
                                posZ += this.speed / 2.0;
                                break;
                            }
                            case 2: {
                                posX -= this.speed / 2.0;
                                break;
                            }
                            case 3: {
                                posZ -= this.speed / 2.0;
                                posX -= this.speed / 2.0;
                                break;
                            }
                            case 4: {
                                posZ -= this.speed;
                                break;
                            }
                            case 5: {
                                posX += this.speed / 2.0;
                                posZ -= this.speed / 2.0;
                                break;
                            }
                            case 6: {
                                posX += this.speed;
                                break;
                            }
                            case 7: {
                                posZ += this.speed / 2.0;
                                posX += this.speed / 2.0;
                                break;
                            }
                        }
                        if (BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir) {
                            for (int i = 0; i < 5 && BlockUtil.getBlock(posX, posY - 1.0, posZ) instanceof BlockAir; --posY, ++i) {}
                        }
                        else {
                            for (int i = 0; i < 5 && !(BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir); ++posY, ++i) {}
                        }
                        player.setPositionAndUpdate(posX, posY, posZ);
                        break;
                    }
                    case "Circle": {
                        final double posXCir = Math.cos(this.rad / 100.0) * this.range + FakePlayerDev.mc.player.posX;
                        final double posZCir = Math.sin(this.rad / 100.0) * this.range + FakePlayerDev.mc.player.posZ;
                        double posYCir = FakePlayerDev.mc.player.posY;
                        if (BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir) {
                            for (int j = 0; j < 5 && BlockUtil.getBlock(posXCir, posYCir - 1.0, posZCir) instanceof BlockAir; --posYCir, ++j) {}
                        }
                        else {
                            for (int j = 0; j < 5 && !(BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir); ++posYCir, ++j) {}
                        }
                        player.setPositionAndUpdate(posXCir, posYCir, posZCir);
                        this.rad += (int)(this.speed * 10.0);
                        break;
                    }
                }
            }
        }
    }
    
    static class movingManager
    {
        private final ArrayList<movingPlayer> players;
        
        movingManager() {
            this.players = new ArrayList<movingPlayer>();
        }
        
        void addPlayer(final int id, final String type, final double speed, final int direction, final double range, final boolean follow) {
            this.players.add(new movingPlayer(id, type, speed, direction, range, follow));
        }
        
        void update() {
            this.players.forEach(movingPlayer::move);
        }
        
        void remove() {
            this.players.clear();
        }
    }
}
