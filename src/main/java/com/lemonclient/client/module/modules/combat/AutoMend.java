// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.combat.DamageUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.init.Items;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec2f;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoMend", category = Category.Combat)
public class AutoMend extends Module
{
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    IntegerSetting delay;
    IntegerSetting minDamage;
    IntegerSetting maxHeal;
    BooleanSetting takeOff;
    IntegerSetting takeOffDelay;
    BooleanSetting predict;
    BooleanSetting crystal;
    DoubleSetting biasDamage;
    BooleanSetting health;
    IntegerSetting minHealth;
    BooleanSetting player;
    DoubleSetting maxSpeed;
    int tookOff;
    Timing timer;
    Timing takeOffTimer;
    char toMend;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public AutoMend() {
        this.rotate = this.registerBoolean("Rotate", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.delay = this.registerInteger("Delay", 0, 0, 1000);
        this.minDamage = this.registerInteger("Min Damage", 50, 1, 100);
        this.maxHeal = this.registerInteger("Repair To", 90, 1, 100);
        this.takeOff = this.registerBoolean("TakeOff", true);
        this.takeOffDelay = this.registerInteger("TakeOff Delay", 0, 0, 1000);
        this.predict = this.registerBoolean("Predict", true);
        this.crystal = this.registerBoolean("Crystal Check", true);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.health = this.registerBoolean("Health Check", true);
        this.minHealth = this.registerInteger("Min Health", 16, 0, 36, () -> this.health.getValue());
        this.player = this.registerBoolean("Enemy Check", true);
        this.maxSpeed = this.registerDouble("Max Speed", 10.0, 0.0, 50.0, () -> this.player.getValue());
        this.timer = new Timing();
        this.takeOffTimer = new Timing();
        this.toMend = '\0';
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (!this.rotate.getValue()) {
            }
            else if (event.getPhase() != Phase.PRE) {
            }
            else {
                final PlayerPacket packet = new PlayerPacket(this, new Vec2f(PlayerPacketManager.INSTANCE.getServerSideRotation().x, 90.0f));
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.rotate.getValue()) {
                if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                    ((CPacketPlayer.Rotation)event.getPacket()).yaw = PlayerPacketManager.INSTANCE.getServerSideRotation().x;
                }
                if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    ((CPacketPlayer.PositionRotation)event.getPacket()).yaw = PlayerPacketManager.INSTANCE.getServerSideRotation().x;
                }
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        this.tookOff = 0;
    }
    
    @Override
    public void onTick() {
        if (AutoMend.mc.player == null || AutoMend.mc.world == null || AutoMend.mc.player.isDead || AutoMend.mc.player.ticksExisted < 10) {
            this.disable();
            return;
        }
        if (this.crystal.getValue() && this.crystalDamage()) {
            this.setDisabledMessage("Lethal crystal nearby");
            this.disable();
            return;
        }
        if (this.health.getValue() && AutoMend.mc.player.getHealth() + AutoMend.mc.player.getAbsorptionAmount() < this.minHealth.getValue()) {
            this.setDisabledMessage("Low health");
            this.disable();
            return;
        }
        if (this.player.getValue() && this.checkNearbyPlayers()) {
            this.setDisabledMessage("Players nearby");
            this.disable();
            return;
        }
        if (this.findXPSlot() == -1) {
            this.setDisabledMessage("No xp bottle found in hotbar");
            this.disable();
            return;
        }
        if (this.checkFinished()) {
            this.setDisabledMessage("Finished mending armors");
            this.disable();
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue())) {
            return;
        }
        this.timer.reset();
        int sumOfDamage = 0;
        final List<ItemStack> armour = AutoMend.mc.player.inventory.armorInventory;
        for (int i = 0; i < armour.size(); ++i) {
            final ItemStack itemStack = armour.get(i);
            if (!itemStack.isEmpty) {
                final float damageOnArmor = (float)(itemStack.getMaxDamage() - itemStack.getItemDamage());
                final float damagePercent = 100.0f - 100.0f * (1.0f - damageOnArmor / itemStack.getMaxDamage());
                if (damagePercent <= this.maxHeal.getValue()) {
                    if (damagePercent <= this.minDamage.getValue()) {
                        this.toMend |= (char)(1 << i);
                    }
                    if (this.predict.getValue()) {
                        sumOfDamage += (int)(itemStack.getMaxDamage() * this.maxHeal.getValue() / 100.0f - (itemStack.getMaxDamage() - itemStack.getItemDamage()));
                    }
                }
                else {
                    this.toMend &= (char)~(1 << i);
                }
            }
        }
        if (this.toMend > '\0') {
            if (this.predict.getValue()) {
                final int totalXp = AutoMend.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityXPOrb).filter(entity -> entity.getDistanceSq(AutoMend.mc.player) <= 1.0).mapToInt(entity -> ((EntityXPOrb)entity).xpValue).sum();
                if (totalXp * 2 < sumOfDamage) {
                    this.mendArmor();
                }
            }
            else {
                this.mendArmor();
            }
        }
    }
    
    private void mendArmor() {
        final int newSlot = this.findXPSlot();
        if (newSlot == -1) {
            return;
        }
        InventoryUtil.run(newSlot, this.packetSwitch.getValue(), () -> AutoMend.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
        if (this.takeOff.getValue()) {
            this.takeArmorOff();
        }
    }
    
    private void takeArmorOff() {
        for (int slot = 5; slot <= 8; ++slot) {
            final ItemStack item = this.getArmor(slot);
            final double max_dam = item.getMaxDamage();
            final double dam_left = item.getMaxDamage() - item.getItemDamage();
            final double percent = dam_left / max_dam * 100.0;
            if (percent >= this.maxHeal.getValue() && item.getItem() != Items.AIR) {
                if (!this.notInInv(Items.AIR)) {
                    return;
                }
                if (!this.takeOffTimer.passedMs(this.takeOffDelay.getValue())) {
                    return;
                }
                this.takeOffTimer.reset();
                boolean hasEmpty = false;
                for (int l_I = 0; l_I < 36; ++l_I) {
                    final ItemStack l_Stack = AutoMend.mc.player.inventory.getStackInSlot(l_I);
                    if (l_Stack.isEmpty) {
                        hasEmpty = true;
                        break;
                    }
                }
                if (hasEmpty) {
                    AutoMend.mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, AutoMend.mc.player);
                }
                else {
                    for (int l_l = 1; l_l < 5; ++l_l) {
                        if (AutoMend.mc.player.inventoryContainer.getSlot(l_l).getStack().isEmpty) {
                            AutoMend.mc.playerController.windowClick(AutoMend.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, AutoMend.mc.player);
                            AutoMend.mc.playerController.windowClick(AutoMend.mc.player.inventoryContainer.windowId, l_l, 0, ClickType.PICKUP, AutoMend.mc.player);
                        }
                    }
                }
            }
        }
    }
    
    private ItemStack getArmor(final int first) {
        return AutoMend.mc.player.inventoryContainer.getInventory().get(first);
    }
    
    public Boolean notInInv(final Item itemOfChoice) {
        int n = 0;
        if (itemOfChoice == AutoMend.mc.player.getHeldItemOffhand().getItem()) {
            return true;
        }
        for (int i = 35; i >= 0; --i) {
            final Item item = AutoMend.mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemOfChoice) {
                return true;
            }
            ++n;
        }
        return n <= 35;
    }
    
    private int findXPSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (AutoMend.mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    private boolean crystalDamage() {
        for (final Entity t : AutoMend.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && AutoMend.mc.player.getDistance(t) <= 12.0f && DamageUtil.calculateDamage(AutoMend.mc.player, AutoMend.mc.player.getPositionVector(), AutoMend.mc.player.getEntityBoundingBox(), (EntityEnderCrystal)t) * this.biasDamage.getValue() >= AutoMend.mc.player.getHealth()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkNearbyPlayers() {
        final AxisAlignedBB box = new AxisAlignedBB(AutoMend.mc.player.posX - 0.5, AutoMend.mc.player.posY - 0.5, AutoMend.mc.player.posZ - 0.5, AutoMend.mc.player.posX + 0.5, AutoMend.mc.player.posY + 2.5, AutoMend.mc.player.posZ + 0.5);
        for (final EntityPlayer entity : AutoMend.mc.world.playerEntities) {
            if (!EntityUtil.basicChecksEntity(entity) && AutoMend.mc.player.connection.getPlayerInfo(entity.getName()) != null) {
                if (LemonClient.speedUtil.getPlayerSpeed(entity) >= this.maxSpeed.getValue()) {
                    continue;
                }
                if (box.intersects(entity.getEntityBoundingBox())) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private boolean checkFinished() {
        int finished = 0;
        for (int slot = 5; slot <= 8; ++slot) {
            final ItemStack item = this.getArmor(slot);
            if (this.getItemDamage(slot) >= this.maxHeal.getValue() || item == ItemStack.EMPTY) {
                ++finished;
            }
        }
        return finished >= 4;
    }
    
    private int getItemDamage(final int slot) {
        final ItemStack itemStack = AutoMend.mc.player.inventoryContainer.getSlot(slot).getStack();
        final float green = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
        final float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }
}
