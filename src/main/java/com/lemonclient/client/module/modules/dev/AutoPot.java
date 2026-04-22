// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import java.util.Objects;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import java.util.List;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.world.World;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec2f;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.Arrays;
import com.lemonclient.api.event.events.EntityRemovedEvent;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import java.util.HashMap;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoPot", category = Category.Dev, priority = 1001)
public class AutoPot extends Module
{
    ModeSetting page;
    BooleanSetting hp;
    IntegerSetting health;
    BooleanSetting equal;
    BooleanSetting predict;
    DoubleSetting times;
    IntegerSetting predictHpDelay;
    IntegerSetting healthSlot;
    IntegerSetting hpDelay;
    BooleanSetting speed;
    IntegerSetting time;
    IntegerSetting swiftnessSlot;
    IntegerSetting speedDelay;
    BooleanSetting only;
    BooleanSetting silentSwitch;
    IntegerSetting delay;
    DoubleSetting factor;
    DoubleSetting range;
    IntegerSetting badSlot;
    BooleanSetting weak;
    BooleanSetting jump;
    BooleanSetting poison;
    BooleanSetting slow;
    BooleanSetting debug;
    HashMap<Integer, Long> weaknessTime;
    HashMap<Integer, Long> jumpBoostTime;
    HashMap<Integer, Long> poisonTime;
    HashMap<Integer, Long> slownessTime;
    Timing hpTimer;
    Timing hpPredictTimer;
    Timing speedTimer;
    Timing badPotTimer;
    int potionSlot;
    int potSlot;
    double lastHealth;
    boolean working;
    boolean preHp;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<PacketEvent.PostReceive> receiveListener;
    @EventHandler
    private final Listener<EntityRemovedEvent> entityRemovedEventListener;
    
    public AutoPot() {
        this.page = this.registerMode("Page", Arrays.asList("General", "BadPot"), "General");
        this.hp = this.registerBoolean("Health Potion", false, () -> this.page.getValue().equals("General"));
        this.health = this.registerInteger("Health", 16, 0, 20, () -> this.hp.getValue() && this.page.getValue().equals("General"));
        this.equal = this.registerBoolean("Equal", false, () -> this.hp.getValue() && this.page.getValue().equals("General"));
        this.predict = this.registerBoolean("Predict", false, () -> this.hp.getValue() && this.page.getValue().equals("General"));
        this.times = this.registerDouble("Time(s)", 1.0, 0.0, 5.0, () -> this.hp.getValue() && this.predict.getValue() && this.page.getValue().equals("General"));
        this.predictHpDelay = this.registerInteger("Predict Health Delay", 50, 0, 1000, () -> this.hp.getValue() && this.predict.getValue() && this.page.getValue().equals("General"));
        this.healthSlot = this.registerInteger("Health Slot", 1, 1, 9, () -> this.hp.getValue() && this.page.getValue().equals("General"));
        this.hpDelay = this.registerInteger("Health Delay", 50, 0, 1000, () -> this.hp.getValue() && this.page.getValue().equals("General"));
        this.speed = this.registerBoolean("Swiftness", false, () -> this.page.getValue().equals("General"));
        this.time = this.registerInteger("Time Left", 5, 0, 30, () -> this.speed.getValue() && this.page.getValue().equals("General"));
        this.swiftnessSlot = this.registerInteger("Swiftness Slot", 1, 1, 9, () -> this.speed.getValue() && this.page.getValue().equals("General"));
        this.speedDelay = this.registerInteger("Swiftness Delay", 50, 0, 1000, () -> this.speed.getValue() && this.page.getValue().equals("General"));
        this.only = this.registerBoolean("On GroundOnly", true, () -> this.page.getValue().equals("General"));
        this.silentSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("General"));
        this.delay = this.registerInteger("Delay", 10, 0, 30, () -> this.page.getValue().equals("BadPot"));
        this.factor = this.registerDouble("Factor", 0.75, 0.0, 1.5, () -> this.page.getValue().equals("BadPot"));
        this.range = this.registerDouble("Range", 4.0, 0.0, 10.0, () -> this.page.getValue().equals("BadPot"));
        this.badSlot = this.registerInteger("BadPot Slot", 1, 1, 9, () -> this.page.getValue().equals("BadPot"));
        this.weak = this.registerBoolean("Weakness", false, () -> this.page.getValue().equals("BadPot"));
        this.jump = this.registerBoolean("JumpBoost", false, () -> this.page.getValue().equals("BadPot"));
        this.poison = this.registerBoolean("Poison", false, () -> this.page.getValue().equals("BadPot"));
        this.slow = this.registerBoolean("Slowness", false, () -> this.page.getValue().equals("BadPot"));
        this.debug = this.registerBoolean("Debug", false, () -> this.page.getValue().equals("BadPot"));
        this.weaknessTime = new HashMap<Integer, Long>();
        this.jumpBoostTime = new HashMap<Integer, Long>();
        this.poisonTime = new HashMap<Integer, Long>();
        this.slownessTime = new HashMap<Integer, Long>();
        this.hpTimer = new Timing();
        this.hpPredictTimer = new Timing();
        this.speedTimer = new Timing();
        this.badPotTimer = new Timing();
        this.lastHealth = 36.0;
        this.working = false;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (AutoPot.mc.world == null || AutoPot.mc.player == null || AutoPot.mc.player.isDead) {
            }
            else {
                this.working = false;
                if (this.only.getValue() || AutoPot.mc.player.isInLava() || AutoPot.mc.player.isInWater()) {
                    final ArrayList<BlockPos> posList = new ArrayList<BlockPos>();
                    final Vec3d floorPos = new Vec3d(AutoPot.mc.player.posX, AutoPot.mc.player.posY - 2.0, AutoPot.mc.player.posZ);
                    final AxisAlignedBB potBox = new AxisAlignedBB(AutoPot.mc.player.posX - 0.125, AutoPot.mc.player.posY - 2.0, AutoPot.mc.player.posZ - 0.125, AutoPot.mc.player.posX + 0.125, AutoPot.mc.player.posY + AutoPot.mc.player.eyeHeight + 0.125, AutoPot.mc.player.posZ + 0.125);
                    for (int i = 0; i < AutoPot.mc.player.posY + AutoPot.mc.player.eyeHeight + 1.125 && (int)(floorPos.y + i) <= (int)(AutoPot.mc.player.posY + AutoPot.mc.player.eyeHeight + 0.125); ++i) {
                        final Vec3d[] array = { new Vec3d(0.125, 0.0, 0.125), new Vec3d(0.125, 0.0, -0.125), new Vec3d(-0.125, 0.0, 0.125), new Vec3d(-0.125, 0.0, -0.125) };
                        int j = 0;
                        for (int length = array.length; j < length; ++j) {
                            final Vec3d vec3d = array[j];
                            final BlockPos pos = new BlockPos(floorPos.x + vec3d.x, floorPos.y + i, floorPos.z + vec3d.z);
                            if (!BlockUtil.isAir(pos)) {
                                posList.add(pos);
                            }
                        }
                    }
                    boolean can = false;
                    for (final BlockPos pos2 : posList) {
                        final AxisAlignedBB box = BlockUtil.getBoundingBox(pos2);
                        if (box != null && MathUtil.isIntersect(potBox, box)) {
                            can = true;
                            break;
                        }
                    }
                    if (!can) {
                        return;
                    }
                }
                if (this.potionSlot == -1) {
                    this.potionSlot = this.getPotion();
                }
                if (this.potSlot == -1) {
                    this.potSlot = this.getBadPot();
                }
                if (this.potionSlot == -1 && this.potSlot == -1) {
                }
                else {
                    this.working = true;
                    if (this.potionSlot > 8) {
                        if (AutoPot.mc.currentScreen instanceof GuiContainer && !(AutoPot.mc.currentScreen instanceof GuiInventory)) {
                            return;
                        }
                        else {
                            final int finalSlot = (this.potionSlot == InventoryUtil.getPotion("swiftness")) ? this.swiftnessSlot.getValue() : ((int)this.healthSlot.getValue());
                            AutoPot.mc.playerController.windowClick(0, this.potionSlot, finalSlot - 1, ClickType.SWAP, AutoPot.mc.player);
                            AutoPot.mc.playerController.updateController();
                            this.potionSlot = finalSlot - 1;
                        }
                    }
                    if (this.potSlot > 8) {
                        if (AutoPot.mc.currentScreen instanceof GuiContainer && !(AutoPot.mc.currentScreen instanceof GuiInventory)) {
                            return;
                        }
                        else {
                            AutoPot.mc.playerController.windowClick(0, this.potSlot, this.badSlot.getValue() - 1, ClickType.SWAP, AutoPot.mc.player);
                            AutoPot.mc.playerController.updateController();
                            this.potSlot = this.badSlot.getValue() - 1;
                        }
                    }
                    if (event.getPhase() == Phase.PRE) {
                        new PlayerPacket(this, new Vec2f(PlayerPacketManager.INSTANCE.getServerSideRotation().x, 90.0f));
                        final PlayerPacket playerPacket = null;
                        final PlayerPacket packet = playerPacket;
                        PlayerPacketManager.INSTANCE.addPacket(packet);
                    }
                    if (event.getPhase() == Phase.POST && (PlayerPacketManager.INSTANCE.getPrevServerSideRotation().y > 85.0f || PlayerPacketManager.INSTANCE.getServerSideRotation().y > 85.0f)) {
                        final int slot = (this.potionSlot == -1) ? this.potSlot : this.potionSlot;
                        InventoryUtil.run(slot, this.silentSwitch.getValue(), () -> AutoPot.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
                        final int n = 0;
                        this.potSlot = n;
                        this.potionSlot = n;
                    }
                }
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.working) {
                if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                    ((CPacketPlayer.Rotation)event.getPacket()).pitch = 90.0f;
                }
                if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    ((CPacketPlayer.PositionRotation)event.getPacket()).pitch = 90.0f;
                }
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.PostReceive>(event -> {
            if (event.getPacket() instanceof SPacketDestroyEntities) {
                Arrays.stream(((SPacketDestroyEntities)event.getPacket()).getEntityIDs()).forEach(this.weaknessTime::remove);
                Arrays.stream(((SPacketDestroyEntities)event.getPacket()).getEntityIDs()).forEach(this.jumpBoostTime::remove);
                Arrays.stream(((SPacketDestroyEntities)event.getPacket()).getEntityIDs()).forEach(this.poisonTime::remove);
                Arrays.stream(((SPacketDestroyEntities)event.getPacket()).getEntityIDs()).forEach(this.slownessTime::remove);
            }
            if (event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus)event.getPacket()).getOpCode() == 35) {
                this.weaknessTime.remove(((SPacketEntityStatus)event.getPacket()).getEntity(AutoPot.mc.world).entityId);
                this.jumpBoostTime.remove(((SPacketEntityStatus)event.getPacket()).getEntity(AutoPot.mc.world).entityId);
                this.poisonTime.remove(((SPacketEntityStatus)event.getPacket()).getEntity(AutoPot.mc.world).entityId);
                this.slownessTime.remove(((SPacketEntityStatus)event.getPacket()).getEntity(AutoPot.mc.world).entityId);
            }
        }, new Predicate[0]);
        this.entityRemovedEventListener = new Listener<EntityRemovedEvent>(event -> {
            if (event.getEntity() instanceof EntityPotion) {
                final List effectList = PotionUtils.getEffectsFromStack(((EntityPotion)event.getEntity()).getPotion());
                PotionEffect weakness = null;
                PotionEffect jumpBoost = null;
                PotionEffect poison = null;
                PotionEffect slowness = null;
                for (final Object effectObject : effectList) {
                    final PotionEffect effect = (PotionEffect)effectObject;
                    if (effect.getPotion() == MobEffects.WEAKNESS) {
                        weakness = effect;
                    }
                    if (effect.getPotion() == MobEffects.JUMP_BOOST) {
                        jumpBoost = effect;
                    }
                    if (effect.getPotion() == MobEffects.POISON) {
                        poison = effect;
                    }
                    if (effect.getPotion() == MobEffects.SLOWNESS) {
                        slowness = effect;
                    }
                }
                final AxisAlignedBB box2 = event.getEntity().boundingBox.grow(4.0, 2.0, 4.0);
                final PotionEffect finalWeakness = weakness;
                final PotionEffect finalJumpBoost = jumpBoost;
                final PotionEffect finalPoison = poison;
                final PotionEffect finalSlowness = slowness;
                AutoPot.mc.world.playerEntities.stream().filter(p -> AutoPot.mc.player.connection.getPlayerInfo(p.getName()) != null).filter(EntityUtil::isAlive).filter(p -> box2.intersects(p.boundingBox)).forEach(p -> {
                    final double distanceSq = event.getEntity().getDistanceSq(p);
                    if (distanceSq < 16.0) {
                        final double factor = Math.sqrt(distanceSq) * this.factor.getValue();
                        if (finalWeakness != null) {
                            final double duration = factor * finalWeakness.getDuration();
                            this.weaknessTime.put(p.getEntityId(), (long)(System.currentTimeMillis() + duration * 50.0));
                        }
                        if (finalJumpBoost != null) {
                            final double duration2 = factor * finalJumpBoost.getDuration();
                            this.jumpBoostTime.put(p.getEntityId(), (long)(System.currentTimeMillis() + duration2 * 50.0));
                        }
                        if (finalPoison != null) {
                            final double duration3 = factor * finalPoison.getDuration();
                            this.poisonTime.put(p.getEntityId(), (long)(System.currentTimeMillis() + duration3 * 50.0));
                        }
                        if (finalSlowness != null) {
                            final double duration4 = factor * finalSlowness.getDuration();
                            this.slownessTime.put(p.getEntityId(), (long)(System.currentTimeMillis() + duration4 * 50.0));
                        }
                    }
                });
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        final HashMap<Integer, Long> hashMap = new HashMap<Integer, Long>();
        this.slownessTime = hashMap;
        this.poisonTime = hashMap;
        this.jumpBoostTime = hashMap;
        this.weaknessTime = hashMap;
    }
    
    @Override
    public void fast() {
        for (final EntityPlayer player : AutoPot.mc.world.playerEntities) {
            final int id = player.getEntityId();
            final long time = System.currentTimeMillis();
            if (this.weaknessTime.containsKey(id) && this.weaknessTime.get(id) <= time) {
                this.weaknessTime.remove(id);
            }
            if (this.jumpBoostTime.containsKey(id) && this.jumpBoostTime.get(id) <= time) {
                this.jumpBoostTime.remove(id);
            }
            if (this.poisonTime.containsKey(id) && this.poisonTime.get(id) <= time) {
                this.poisonTime.remove(id);
            }
            if (this.slownessTime.containsKey(id) && this.slownessTime.get(id) <= time) {
                this.slownessTime.remove(id);
            }
        }
        if (!this.debug.getValue()) {
            return;
        }
        final StringBuilder weak = new StringBuilder("Weakness");
        for (final EntityPlayer player2 : AutoPot.mc.world.playerEntities) {
            if (this.weaknessTime.containsKey(player2.getEntityId())) {
                weak.append(player2.getName()).append(" ").append(this.weaknessTime.get(player2.getEntityId()) - System.currentTimeMillis()).append(", ");
            }
        }
        if (!weak.toString().equals("Weakness")) {
            MessageBus.sendClientDeleteMessage(weak.toString(), Notification.Type.DISABLE, "Weakness", 0);
        }
    }
    
    private int getPotion() {
        if (this.hp.getValue()) {
            if (this.healthCheck(this.health.getValue()) && this.hpTimer.passedMs(this.hpDelay.getValue())) {
                this.preHp = false;
                this.hpTimer.reset();
                final int slot = InventoryUtil.getPotion("healing");
                if (slot != -1) {
                    return slot;
                }
            }
            if (this.predict.getValue()) {
                this.healthPredict();
            }
            if (this.preHp && this.hpPredictTimer.passedMs(this.predictHpDelay.getValue())) {
                this.preHp = false;
                this.hpPredictTimer.reset();
                final int slot = InventoryUtil.getPotion("healing");
                if (slot != -1) {
                    return slot;
                }
            }
        }
        if (this.speed.getValue() && (!AutoPot.mc.player.isPotionActive(MobEffects.SPEED) || Objects.requireNonNull(AutoPot.mc.player.getActivePotionEffect(MobEffects.SPEED)).getDuration() <= this.time.getValue() * 20) && this.speedTimer.passedMs(this.speedDelay.getValue())) {
            this.speedTimer.reset();
            return InventoryUtil.getPotion("swiftness");
        }
        return -1;
    }
    
    private int getBadPot() {
        if (this.badPotTimer.passedS(this.delay.getValue())) {
            this.badPotTimer.reset();
            for (final EntityPlayer player : AutoPot.mc.world.playerEntities) {
                if (AutoPot.mc.player.connection.getPlayerInfo(player.getName()) == null) {
                    continue;
                }
                if (EntityUtil.basicChecksEntity(player)) {
                    continue;
                }
                if (AutoPot.mc.player.getDistance(player) > this.range.getValue()) {
                    continue;
                }
                if (this.weak.getValue() && !this.weaknessTime.containsKey(player.getEntityId())) {
                    final int slot = InventoryUtil.getPotion("weakness");
                    if (slot != -1) {
                        return slot;
                    }
                }
                if (this.jump.getValue() && !this.jumpBoostTime.containsKey(player.getEntityId())) {
                    final int slot = InventoryUtil.getPotion("leaping");
                    if (slot != -1) {
                        return slot;
                    }
                }
                if (this.poison.getValue() && !this.poisonTime.containsKey(player.getEntityId())) {
                    final int slot = InventoryUtil.getPotion("poison");
                    if (slot != -1) {
                        return slot;
                    }
                }
                if (this.slow.getValue() && !this.slownessTime.containsKey(player.getEntityId())) {
                    return InventoryUtil.getPotion("slowness");
                }
            }
        }
        return -1;
    }
    
    private boolean healthCheck(final double value) {
        return AutoPot.mc.player.getHealth() < value || (this.equal.getValue() && AutoPot.mc.player.getHealth() == value);
    }
    
    private void healthPredict() {
        double health = AutoPot.mc.player.getHealth() + AutoPot.mc.player.getAbsorptionAmount();
        if (health == 36.0) {
            this.lastHealth = 36.0;
        }
        final double change = health - this.lastHealth;
        if (change >= 0.0) {
            return;
        }
        this.lastHealth = health;
        health += change * this.times.getValue();
        this.preHp = (health < this.health.getValue() || (this.equal.getValue() && health == this.health.getValue()));
    }
}
