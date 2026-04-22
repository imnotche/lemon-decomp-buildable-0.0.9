// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketPlayer;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.LemonClient;
import net.minecraft.item.ItemStack;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import java.util.List;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.client.module.ModuleManager;
import java.util.ArrayList;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.item.ItemBed;
import net.minecraft.init.Items;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BedAura", category = Category.Combat, priority = 999)
public class BedAura extends Module
{
    ModeSetting page;
    BooleanSetting predict;
    BooleanSetting selfPredict;
    DoubleSetting resetRotate;
    BooleanSetting detect;
    IntegerSetting startTick;
    IntegerSetting addTick;
    IntegerSetting tickPredict;
    BooleanSetting calculateYPredict;
    IntegerSetting startDecrease;
    IntegerSetting exponentStartDecrease;
    IntegerSetting decreaseY;
    IntegerSetting exponentDecreaseY;
    BooleanSetting splitXZ;
    BooleanSetting manualOutHole;
    BooleanSetting aboveHoleManual;
    BooleanSetting stairPredict;
    IntegerSetting nStair;
    DoubleSetting speedActivationStair;
    ModeSetting targetMode;
    DoubleSetting smartHealth;
    BooleanSetting monster;
    BooleanSetting neutral;
    BooleanSetting animal;
    ModeSetting mode;
    BooleanSetting packetPlace;
    BooleanSetting placeSwing;
    BooleanSetting breakSwing;
    BooleanSetting packetSwing;
    BooleanSetting checkBed;
    BooleanSetting highVersion;
    BooleanSetting placeInAir;
    BooleanSetting base;
    BooleanSetting allPossible;
    BooleanSetting detectBreak;
    BooleanSetting packetBase;
    BooleanSetting baseSwing;
    DoubleSetting toggleDmg;
    IntegerSetting baseDelay;
    DoubleSetting baseMinDmg;
    DoubleSetting maxY;
    DoubleSetting maxSpeed;
    IntegerSetting calcDelay;
    IntegerSetting updateDelay;
    IntegerSetting placeDelay;
    IntegerSetting breakDelay;
    IntegerSetting switchPlaceDelay;
    IntegerSetting switchBreakDelay;
    IntegerSetting stuckPlaceDelay;
    IntegerSetting stuckBreakDelay;
    DoubleSetting range;
    DoubleSetting yRange;
    IntegerSetting enemyRange;
    IntegerSetting maxEnemies;
    BooleanSetting autorotate;
    BooleanSetting pause;
    BooleanSetting pitch;
    DoubleSetting minDmg;
    BooleanSetting ignore;
    DoubleSetting maxSelfDmg;
    BooleanSetting suicide;
    DoubleSetting balance;
    IntegerSetting facePlaceValue;
    IntegerSetting armorCount;
    IntegerSetting armorRate;
    DoubleSetting fpMinDmg;
    BooleanSetting forcePlace;
    ModeSetting handMode;
    BooleanSetting autoSwitch;
    BooleanSetting silentSwitch;
    BooleanSetting packetSwitch;
    BooleanSetting refill;
    ModeSetting clickMode;
    ModeSetting refillMode;
    IntegerSetting slotS;
    BooleanSetting force;
    BooleanSetting slowFP;
    IntegerSetting slowPlaceDelay;
    IntegerSetting slowBreakDelay;
    DoubleSetting slowMinDmg;
    BooleanSetting showDamage;
    BooleanSetting showSelfDamage;
    ColorSetting color;
    ColorSetting color2;
    IntegerSetting alpha;
    IntegerSetting outAlpha;
    BooleanSetting gradient;
    BooleanSetting outGradient;
    IntegerSetting width;
    IntegerSetting movingTime;
    IntegerSetting lifeTime;
    BooleanSetting renderTest;
    ModeSetting hudDisplay;
    BooleanSetting hudSelfDamage;
    HashMap<EntityPlayer, MoveRotation> playerSpeed;
    EntityInfo target;
    BlockPos headPos;
    BlockPos basePos;
    BlockPos continuE;
    boolean canBasePlace;
    boolean burrow;
    float damage;
    float selfDamage;
    String face;
    Vec3d movingBaseNow;
    Vec3d movingHeadNow;
    BlockPos lastBestBase;
    BlockPos lastBestHead;
    Timing basetiming;
    Timing calctiming;
    Timing placetiming;
    Timing breaktiming;
    Timing updatetiming;
    EnumHand hand;
    int slot;
    int maxPredict;
    long updateTimeBase;
    long updateTimeHead;
    long startTime;
    Vec2f rotation;
    BlockPos[] sides;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    boolean switching;
    
    public BedAura() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   com/lemonclient/client/module/Module.<init>:()V
        //     4: aload_0         /* this */
        //     5: aload_0         /* this */
        //     6: ldc             "Page"
        //     8: bipush          8
        //    10: anewarray       Ljava/lang/String;
        //    13: dup            
        //    14: iconst_0       
        //    15: ldc             "Target"
        //    17: aastore        
        //    18: dup            
        //    19: iconst_1       
        //    20: ldc             "General"
        //    22: aastore        
        //    23: dup            
        //    24: iconst_2       
        //    25: ldc             "Delay"
        //    27: aastore        
        //    28: dup            
        //    29: iconst_3       
        //    30: ldc             "Base"
        //    32: aastore        
        //    33: dup            
        //    34: iconst_4       
        //    35: ldc             "Calc"
        //    37: aastore        
        //    38: dup            
        //    39: iconst_5       
        //    40: ldc             "SlowFacePlace"
        //    42: aastore        
        //    43: dup            
        //    44: bipush          6
        //    46: ldc             "Switch"
        //    48: aastore        
        //    49: dup            
        //    50: bipush          7
        //    52: ldc             "Render"
        //    54: aastore        
        //    55: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //    58: ldc             "General"
        //    60: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //    63: putfield        com/lemonclient/client/module/modules/combat/BedAura.page:Lcom/lemonclient/api/setting/values/ModeSetting;
        //    66: aload_0         /* this */
        //    67: aload_0         /* this */
        //    68: ldc             "Predict"
        //    70: iconst_1       
        //    71: aload_0         /* this */
        //    72: invokedynamic   BootstrapMethod #0, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //    77: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    80: putfield        com/lemonclient/client/module/modules/combat/BedAura.predict:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    83: aload_0         /* this */
        //    84: aload_0         /* this */
        //    85: ldc_w           "Predict Self"
        //    88: iconst_1       
        //    89: aload_0         /* this */
        //    90: invokedynamic   BootstrapMethod #1, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //    95: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    98: putfield        com/lemonclient/client/module/modules/combat/BedAura.selfPredict:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   101: aload_0         /* this */
        //   102: aload_0         /* this */
        //   103: ldc_w           "Reset Yaw Difference"
        //   106: ldc2_w          15.0
        //   109: dconst_0       
        //   110: ldc2_w          180.0
        //   113: aload_0         /* this */
        //   114: invokedynamic   BootstrapMethod #2, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   119: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   122: putfield        com/lemonclient/client/module/modules/combat/BedAura.resetRotate:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   125: aload_0         /* this */
        //   126: aload_0         /* this */
        //   127: ldc_w           "Detect Ping"
        //   130: iconst_0       
        //   131: aload_0         /* this */
        //   132: invokedynamic   BootstrapMethod #3, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   137: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   140: putfield        com/lemonclient/client/module/modules/combat/BedAura.detect:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   143: aload_0         /* this */
        //   144: aload_0         /* this */
        //   145: ldc_w           "Start Tick"
        //   148: iconst_2       
        //   149: iconst_0       
        //   150: bipush          30
        //   152: aload_0         /* this */
        //   153: invokedynamic   BootstrapMethod #4, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   158: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   161: putfield        com/lemonclient/client/module/modules/combat/BedAura.startTick:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   164: aload_0         /* this */
        //   165: aload_0         /* this */
        //   166: ldc_w           "Add Tick"
        //   169: iconst_4       
        //   170: iconst_0       
        //   171: bipush          10
        //   173: aload_0         /* this */
        //   174: invokedynamic   BootstrapMethod #5, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   179: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   182: putfield        com/lemonclient/client/module/modules/combat/BedAura.addTick:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   185: aload_0         /* this */
        //   186: aload_0         /* this */
        //   187: ldc_w           "Max Predict Ticks"
        //   190: bipush          10
        //   192: iconst_0       
        //   193: bipush          30
        //   195: aload_0         /* this */
        //   196: invokedynamic   BootstrapMethod #6, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   201: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   204: putfield        com/lemonclient/client/module/modules/combat/BedAura.tickPredict:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   207: aload_0         /* this */
        //   208: aload_0         /* this */
        //   209: ldc_w           "Calculate Y Predict"
        //   212: iconst_1       
        //   213: aload_0         /* this */
        //   214: invokedynamic   BootstrapMethod #7, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   219: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   222: putfield        com/lemonclient/client/module/modules/combat/BedAura.calculateYPredict:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   225: aload_0         /* this */
        //   226: aload_0         /* this */
        //   227: ldc_w           "Start Decrease"
        //   230: bipush          39
        //   232: iconst_0       
        //   233: sipush          200
        //   236: aload_0         /* this */
        //   237: invokedynamic   BootstrapMethod #8, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   242: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   245: putfield        com/lemonclient/client/module/modules/combat/BedAura.startDecrease:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   248: aload_0         /* this */
        //   249: aload_0         /* this */
        //   250: ldc_w           "Exponent Start"
        //   253: iconst_2       
        //   254: iconst_1       
        //   255: iconst_5       
        //   256: aload_0         /* this */
        //   257: invokedynamic   BootstrapMethod #9, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   262: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   265: putfield        com/lemonclient/client/module/modules/combat/BedAura.exponentStartDecrease:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   268: aload_0         /* this */
        //   269: aload_0         /* this */
        //   270: ldc_w           "Decrease Y"
        //   273: iconst_2       
        //   274: iconst_1       
        //   275: iconst_5       
        //   276: aload_0         /* this */
        //   277: invokedynamic   BootstrapMethod #10, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   282: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   285: putfield        com/lemonclient/client/module/modules/combat/BedAura.decreaseY:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   288: aload_0         /* this */
        //   289: aload_0         /* this */
        //   290: ldc_w           "Exponent Decrease Y"
        //   293: iconst_1       
        //   294: iconst_1       
        //   295: iconst_3       
        //   296: aload_0         /* this */
        //   297: invokedynamic   BootstrapMethod #11, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   302: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   305: putfield        com/lemonclient/client/module/modules/combat/BedAura.exponentDecreaseY:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   308: aload_0         /* this */
        //   309: aload_0         /* this */
        //   310: ldc_w           "Split XZ"
        //   313: iconst_1       
        //   314: aload_0         /* this */
        //   315: invokedynamic   BootstrapMethod #12, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   320: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   323: putfield        com/lemonclient/client/module/modules/combat/BedAura.splitXZ:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   326: aload_0         /* this */
        //   327: aload_0         /* this */
        //   328: ldc_w           "Manual Out Hole"
        //   331: iconst_0       
        //   332: aload_0         /* this */
        //   333: invokedynamic   BootstrapMethod #13, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   338: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   341: putfield        com/lemonclient/client/module/modules/combat/BedAura.manualOutHole:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   344: aload_0         /* this */
        //   345: aload_0         /* this */
        //   346: ldc_w           "Above Hole Manual"
        //   349: iconst_0       
        //   350: aload_0         /* this */
        //   351: invokedynamic   BootstrapMethod #14, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   356: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   359: putfield        com/lemonclient/client/module/modules/combat/BedAura.aboveHoleManual:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   362: aload_0         /* this */
        //   363: aload_0         /* this */
        //   364: ldc_w           "Stair Predict"
        //   367: iconst_0       
        //   368: aload_0         /* this */
        //   369: invokedynamic   BootstrapMethod #15, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   374: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   377: putfield        com/lemonclient/client/module/modules/combat/BedAura.stairPredict:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   380: aload_0         /* this */
        //   381: aload_0         /* this */
        //   382: ldc_w           "N Stair"
        //   385: iconst_2       
        //   386: iconst_1       
        //   387: iconst_4       
        //   388: aload_0         /* this */
        //   389: invokedynamic   BootstrapMethod #16, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   394: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   397: putfield        com/lemonclient/client/module/modules/combat/BedAura.nStair:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   400: aload_0         /* this */
        //   401: aload_0         /* this */
        //   402: ldc_w           "Speed Activation Stair"
        //   405: ldc2_w          0.3
        //   408: dconst_0       
        //   409: dconst_1       
        //   410: aload_0         /* this */
        //   411: invokedynamic   BootstrapMethod #17, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   416: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   419: putfield        com/lemonclient/client/module/modules/combat/BedAura.speedActivationStair:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   422: aload_0         /* this */
        //   423: aload_0         /* this */
        //   424: ldc             "Target"
        //   426: iconst_4       
        //   427: anewarray       Ljava/lang/String;
        //   430: dup            
        //   431: iconst_0       
        //   432: ldc_w           "Nearest"
        //   435: aastore        
        //   436: dup            
        //   437: iconst_1       
        //   438: ldc_w           "Damage"
        //   441: aastore        
        //   442: dup            
        //   443: iconst_2       
        //   444: ldc_w           "Health"
        //   447: aastore        
        //   448: dup            
        //   449: iconst_3       
        //   450: ldc_w           "Smart"
        //   453: aastore        
        //   454: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //   457: ldc_w           "Nearest"
        //   460: aload_0         /* this */
        //   461: invokedynamic   BootstrapMethod #18, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   466: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //   469: putfield        com/lemonclient/client/module/modules/combat/BedAura.targetMode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //   472: aload_0         /* this */
        //   473: aload_0         /* this */
        //   474: ldc_w           "Smart Health"
        //   477: ldc2_w          16.0
        //   480: dconst_0       
        //   481: ldc2_w          36.0
        //   484: aload_0         /* this */
        //   485: invokedynamic   BootstrapMethod #19, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   490: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   493: putfield        com/lemonclient/client/module/modules/combat/BedAura.smartHealth:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   496: aload_0         /* this */
        //   497: aload_0         /* this */
        //   498: ldc_w           "Monsters"
        //   501: iconst_1       
        //   502: aload_0         /* this */
        //   503: invokedynamic   BootstrapMethod #20, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   508: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   511: putfield        com/lemonclient/client/module/modules/combat/BedAura.monster:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   514: aload_0         /* this */
        //   515: aload_0         /* this */
        //   516: ldc_w           "Neutrals"
        //   519: iconst_1       
        //   520: aload_0         /* this */
        //   521: invokedynamic   BootstrapMethod #21, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   526: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   529: putfield        com/lemonclient/client/module/modules/combat/BedAura.neutral:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   532: aload_0         /* this */
        //   533: aload_0         /* this */
        //   534: ldc_w           "Animals"
        //   537: iconst_1       
        //   538: aload_0         /* this */
        //   539: invokedynamic   BootstrapMethod #22, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   544: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   547: putfield        com/lemonclient/client/module/modules/combat/BedAura.animal:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   550: aload_0         /* this */
        //   551: aload_0         /* this */
        //   552: ldc_w           "Mode"
        //   555: iconst_5       
        //   556: anewarray       Ljava/lang/String;
        //   559: dup            
        //   560: iconst_0       
        //   561: ldc_w           "PlaceBreak"
        //   564: aastore        
        //   565: dup            
        //   566: iconst_1       
        //   567: ldc_w           "BreakPlace"
        //   570: aastore        
        //   571: dup            
        //   572: iconst_2       
        //   573: ldc             "Switch"
        //   575: aastore        
        //   576: dup            
        //   577: iconst_3       
        //   578: ldc_w           "Stuck"
        //   581: aastore        
        //   582: dup            
        //   583: iconst_4       
        //   584: ldc_w           "Test"
        //   587: aastore        
        //   588: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //   591: ldc_w           "PlaceBreak"
        //   594: aload_0         /* this */
        //   595: invokedynamic   BootstrapMethod #23, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   600: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //   603: putfield        com/lemonclient/client/module/modules/combat/BedAura.mode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //   606: aload_0         /* this */
        //   607: aload_0         /* this */
        //   608: ldc_w           "Packet Place"
        //   611: iconst_1       
        //   612: aload_0         /* this */
        //   613: invokedynamic   BootstrapMethod #24, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   618: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   621: putfield        com/lemonclient/client/module/modules/combat/BedAura.packetPlace:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   624: aload_0         /* this */
        //   625: aload_0         /* this */
        //   626: ldc_w           "Place Swing"
        //   629: iconst_1       
        //   630: aload_0         /* this */
        //   631: invokedynamic   BootstrapMethod #25, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   636: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   639: putfield        com/lemonclient/client/module/modules/combat/BedAura.placeSwing:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   642: aload_0         /* this */
        //   643: aload_0         /* this */
        //   644: ldc_w           "Break Swing"
        //   647: iconst_1       
        //   648: aload_0         /* this */
        //   649: invokedynamic   BootstrapMethod #26, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   654: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   657: putfield        com/lemonclient/client/module/modules/combat/BedAura.breakSwing:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   660: aload_0         /* this */
        //   661: aload_0         /* this */
        //   662: ldc_w           "Packet Swing"
        //   665: iconst_1       
        //   666: aload_0         /* this */
        //   667: invokedynamic   BootstrapMethod #27, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   672: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   675: putfield        com/lemonclient/client/module/modules/combat/BedAura.packetSwing:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   678: aload_0         /* this */
        //   679: aload_0         /* this */
        //   680: ldc_w           "Placed Check"
        //   683: iconst_0       
        //   684: aload_0         /* this */
        //   685: invokedynamic   BootstrapMethod #28, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   690: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   693: putfield        com/lemonclient/client/module/modules/combat/BedAura.checkBed:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   696: aload_0         /* this */
        //   697: aload_0         /* this */
        //   698: ldc_w           "1.13"
        //   701: iconst_1       
        //   702: aload_0         /* this */
        //   703: invokedynamic   BootstrapMethod #29, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   708: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   711: putfield        com/lemonclient/client/module/modules/combat/BedAura.highVersion:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   714: aload_0         /* this */
        //   715: aload_0         /* this */
        //   716: ldc_w           "Place In Air"
        //   719: iconst_1       
        //   720: aload_0         /* this */
        //   721: invokedynamic   BootstrapMethod #30, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   726: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   729: putfield        com/lemonclient/client/module/modules/combat/BedAura.placeInAir:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   732: aload_0         /* this */
        //   733: aload_0         /* this */
        //   734: ldc_w           "Place Base"
        //   737: iconst_1       
        //   738: aload_0         /* this */
        //   739: invokedynamic   BootstrapMethod #31, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   744: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   747: putfield        com/lemonclient/client/module/modules/combat/BedAura.base:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   750: aload_0         /* this */
        //   751: aload_0         /* this */
        //   752: ldc_w           "Calc All Possible"
        //   755: iconst_1       
        //   756: aload_0         /* this */
        //   757: invokedynamic   BootstrapMethod #32, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   762: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   765: putfield        com/lemonclient/client/module/modules/combat/BedAura.allPossible:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   768: aload_0         /* this */
        //   769: aload_0         /* this */
        //   770: ldc_w           "Detect Break"
        //   773: iconst_1       
        //   774: aload_0         /* this */
        //   775: invokedynamic   BootstrapMethod #33, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   780: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   783: putfield        com/lemonclient/client/module/modules/combat/BedAura.detectBreak:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   786: aload_0         /* this */
        //   787: aload_0         /* this */
        //   788: ldc_w           "Packet Base Place"
        //   791: iconst_1       
        //   792: aload_0         /* this */
        //   793: invokedynamic   BootstrapMethod #34, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   798: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   801: putfield        com/lemonclient/client/module/modules/combat/BedAura.packetBase:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   804: aload_0         /* this */
        //   805: aload_0         /* this */
        //   806: ldc_w           "Base Swing"
        //   809: iconst_1       
        //   810: aload_0         /* this */
        //   811: invokedynamic   BootstrapMethod #35, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   816: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   819: putfield        com/lemonclient/client/module/modules/combat/BedAura.baseSwing:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   822: aload_0         /* this */
        //   823: aload_0         /* this */
        //   824: ldc_w           "Toggle Damage"
        //   827: ldc2_w          8.0
        //   830: dconst_0       
        //   831: ldc2_w          36.0
        //   834: aload_0         /* this */
        //   835: invokedynamic   BootstrapMethod #36, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   840: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   843: putfield        com/lemonclient/client/module/modules/combat/BedAura.toggleDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   846: aload_0         /* this */
        //   847: aload_0         /* this */
        //   848: ldc_w           "Base Delay"
        //   851: iconst_0       
        //   852: iconst_0       
        //   853: sipush          1000
        //   856: aload_0         /* this */
        //   857: invokedynamic   BootstrapMethod #37, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   862: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   865: putfield        com/lemonclient/client/module/modules/combat/BedAura.baseDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   868: aload_0         /* this */
        //   869: aload_0         /* this */
        //   870: ldc_w           "Base MinDmg"
        //   873: ldc2_w          8.0
        //   876: dconst_0       
        //   877: ldc2_w          36.0
        //   880: aload_0         /* this */
        //   881: invokedynamic   BootstrapMethod #38, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   886: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   889: putfield        com/lemonclient/client/module/modules/combat/BedAura.baseMinDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   892: aload_0         /* this */
        //   893: aload_0         /* this */
        //   894: ldc_w           "Max Y"
        //   897: dconst_1       
        //   898: dconst_0       
        //   899: ldc2_w          3.0
        //   902: aload_0         /* this */
        //   903: invokedynamic   BootstrapMethod #39, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   908: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   911: putfield        com/lemonclient/client/module/modules/combat/BedAura.maxY:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   914: aload_0         /* this */
        //   915: aload_0         /* this */
        //   916: ldc_w           "Max Target Speed"
        //   919: ldc2_w          10.0
        //   922: dconst_0       
        //   923: ldc2_w          50.0
        //   926: aload_0         /* this */
        //   927: invokedynamic   BootstrapMethod #40, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   932: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   935: putfield        com/lemonclient/client/module/modules/combat/BedAura.maxSpeed:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //   938: aload_0         /* this */
        //   939: aload_0         /* this */
        //   940: ldc_w           "Calc Delay"
        //   943: iconst_0       
        //   944: iconst_0       
        //   945: sipush          1000
        //   948: aload_0         /* this */
        //   949: invokedynamic   BootstrapMethod #41, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   954: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   957: putfield        com/lemonclient/client/module/modules/combat/BedAura.calcDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   960: aload_0         /* this */
        //   961: aload_0         /* this */
        //   962: ldc_w           "Update Delay"
        //   965: iconst_0       
        //   966: iconst_0       
        //   967: sipush          1000
        //   970: aload_0         /* this */
        //   971: invokedynamic   BootstrapMethod #42, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   976: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   979: putfield        com/lemonclient/client/module/modules/combat/BedAura.updateDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   982: aload_0         /* this */
        //   983: aload_0         /* this */
        //   984: ldc_w           "Place Delay"
        //   987: bipush          50
        //   989: iconst_0       
        //   990: sipush          1000
        //   993: aload_0         /* this */
        //   994: invokedynamic   BootstrapMethod #43, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //   999: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1002: putfield        com/lemonclient/client/module/modules/combat/BedAura.placeDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1005: aload_0         /* this */
        //  1006: aload_0         /* this */
        //  1007: ldc_w           "Break Delay"
        //  1010: bipush          50
        //  1012: iconst_0       
        //  1013: sipush          1000
        //  1016: aload_0         /* this */
        //  1017: invokedynamic   BootstrapMethod #44, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1022: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1025: putfield        com/lemonclient/client/module/modules/combat/BedAura.breakDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1028: aload_0         /* this */
        //  1029: aload_0         /* this */
        //  1030: ldc_w           "Switch Place Delay"
        //  1033: bipush          50
        //  1035: iconst_0       
        //  1036: sipush          1000
        //  1039: aload_0         /* this */
        //  1040: invokedynamic   BootstrapMethod #45, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1045: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1048: putfield        com/lemonclient/client/module/modules/combat/BedAura.switchPlaceDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1051: aload_0         /* this */
        //  1052: aload_0         /* this */
        //  1053: ldc_w           "Switch Break Delay"
        //  1056: bipush          50
        //  1058: iconst_0       
        //  1059: sipush          1000
        //  1062: aload_0         /* this */
        //  1063: invokedynamic   BootstrapMethod #46, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1068: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1071: putfield        com/lemonclient/client/module/modules/combat/BedAura.switchBreakDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1074: aload_0         /* this */
        //  1075: aload_0         /* this */
        //  1076: ldc_w           "Stuck Place Delay"
        //  1079: bipush          50
        //  1081: iconst_0       
        //  1082: sipush          1000
        //  1085: aload_0         /* this */
        //  1086: invokedynamic   BootstrapMethod #47, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1091: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1094: putfield        com/lemonclient/client/module/modules/combat/BedAura.stuckPlaceDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1097: aload_0         /* this */
        //  1098: aload_0         /* this */
        //  1099: ldc_w           "Stuck Break Delay"
        //  1102: bipush          50
        //  1104: iconst_0       
        //  1105: sipush          1000
        //  1108: aload_0         /* this */
        //  1109: invokedynamic   BootstrapMethod #48, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1114: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1117: putfield        com/lemonclient/client/module/modules/combat/BedAura.stuckBreakDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1120: aload_0         /* this */
        //  1121: aload_0         /* this */
        //  1122: ldc_w           "Place Range"
        //  1125: ldc2_w          5.0
        //  1128: dconst_0       
        //  1129: ldc2_w          10.0
        //  1132: aload_0         /* this */
        //  1133: invokedynamic   BootstrapMethod #49, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1138: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1141: putfield        com/lemonclient/client/module/modules/combat/BedAura.range:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1144: aload_0         /* this */
        //  1145: aload_0         /* this */
        //  1146: ldc_w           "Y Range"
        //  1149: ldc2_w          2.5
        //  1152: dconst_0       
        //  1153: ldc2_w          10.0
        //  1156: aload_0         /* this */
        //  1157: invokedynamic   BootstrapMethod #50, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1162: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1165: putfield        com/lemonclient/client/module/modules/combat/BedAura.yRange:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1168: aload_0         /* this */
        //  1169: aload_0         /* this */
        //  1170: ldc_w           "Enemy Range"
        //  1173: bipush          10
        //  1175: iconst_0       
        //  1176: bipush          16
        //  1178: aload_0         /* this */
        //  1179: invokedynamic   BootstrapMethod #51, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1184: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1187: putfield        com/lemonclient/client/module/modules/combat/BedAura.enemyRange:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1190: aload_0         /* this */
        //  1191: aload_0         /* this */
        //  1192: ldc_w           "Max Calc Enemies"
        //  1195: iconst_5       
        //  1196: iconst_0       
        //  1197: bipush          25
        //  1199: aload_0         /* this */
        //  1200: invokedynamic   BootstrapMethod #52, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1205: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1208: putfield        com/lemonclient/client/module/modules/combat/BedAura.maxEnemies:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1211: aload_0         /* this */
        //  1212: aload_0         /* this */
        //  1213: ldc_w           "Auto Rotate"
        //  1216: iconst_1       
        //  1217: aload_0         /* this */
        //  1218: invokedynamic   BootstrapMethod #53, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1223: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1226: putfield        com/lemonclient/client/module/modules/combat/BedAura.autorotate:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1229: aload_0         /* this */
        //  1230: aload_0         /* this */
        //  1231: ldc_w           "Pause While Burrow"
        //  1234: iconst_0       
        //  1235: aload_0         /* this */
        //  1236: invokedynamic   BootstrapMethod #54, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1241: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1244: putfield        com/lemonclient/client/module/modules/combat/BedAura.pause:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1247: aload_0         /* this */
        //  1248: aload_0         /* this */
        //  1249: ldc_w           "Pitch Down"
        //  1252: iconst_1       
        //  1253: aload_0         /* this */
        //  1254: invokedynamic   BootstrapMethod #55, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1259: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1262: putfield        com/lemonclient/client/module/modules/combat/BedAura.pitch:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1265: aload_0         /* this */
        //  1266: aload_0         /* this */
        //  1267: ldc_w           "Min Damage"
        //  1270: ldc2_w          8.0
        //  1273: dconst_0       
        //  1274: ldc2_w          36.0
        //  1277: aload_0         /* this */
        //  1278: invokedynamic   BootstrapMethod #56, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1283: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1286: putfield        com/lemonclient/client/module/modules/combat/BedAura.minDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1289: aload_0         /* this */
        //  1290: aload_0         /* this */
        //  1291: ldc_w           "Ignore Self Dmg"
        //  1294: iconst_0       
        //  1295: aload_0         /* this */
        //  1296: invokedynamic   BootstrapMethod #57, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1301: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1304: putfield        com/lemonclient/client/module/modules/combat/BedAura.ignore:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1307: aload_0         /* this */
        //  1308: aload_0         /* this */
        //  1309: ldc_w           "Max Self Dmg"
        //  1312: ldc2_w          10.0
        //  1315: dconst_1       
        //  1316: ldc2_w          36.0
        //  1319: aload_0         /* this */
        //  1320: invokedynamic   BootstrapMethod #58, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1325: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1328: putfield        com/lemonclient/client/module/modules/combat/BedAura.maxSelfDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1331: aload_0         /* this */
        //  1332: aload_0         /* this */
        //  1333: ldc_w           "Anti Suicide"
        //  1336: iconst_1       
        //  1337: aload_0         /* this */
        //  1338: invokedynamic   BootstrapMethod #59, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1343: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1346: putfield        com/lemonclient/client/module/modules/combat/BedAura.suicide:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1349: aload_0         /* this */
        //  1350: aload_0         /* this */
        //  1351: ldc_w           "Health Balance"
        //  1354: ldc2_w          2.5
        //  1357: dconst_0       
        //  1358: ldc2_w          10.0
        //  1361: aload_0         /* this */
        //  1362: invokedynamic   BootstrapMethod #60, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1367: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1370: putfield        com/lemonclient/client/module/modules/combat/BedAura.balance:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1373: aload_0         /* this */
        //  1374: aload_0         /* this */
        //  1375: ldc_w           "FacePlace HP"
        //  1378: bipush          8
        //  1380: iconst_0       
        //  1381: bipush          36
        //  1383: aload_0         /* this */
        //  1384: invokedynamic   BootstrapMethod #61, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1389: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1392: putfield        com/lemonclient/client/module/modules/combat/BedAura.facePlaceValue:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1395: aload_0         /* this */
        //  1396: aload_0         /* this */
        //  1397: ldc_w           "ArmorCount"
        //  1400: iconst_1       
        //  1401: iconst_0       
        //  1402: bipush          64
        //  1404: aload_0         /* this */
        //  1405: invokedynamic   BootstrapMethod #62, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1410: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1413: putfield        com/lemonclient/client/module/modules/combat/BedAura.armorCount:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1416: aload_0         /* this */
        //  1417: aload_0         /* this */
        //  1418: ldc_w           "ArmorDamage"
        //  1421: bipush          15
        //  1423: iconst_0       
        //  1424: bipush          100
        //  1426: aload_0         /* this */
        //  1427: invokedynamic   BootstrapMethod #63, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1432: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1435: putfield        com/lemonclient/client/module/modules/combat/BedAura.armorRate:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1438: aload_0         /* this */
        //  1439: aload_0         /* this */
        //  1440: ldc_w           "FP Min Damage"
        //  1443: dconst_1       
        //  1444: dconst_0       
        //  1445: ldc2_w          36.0
        //  1448: aload_0         /* this */
        //  1449: invokedynamic   BootstrapMethod #64, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1454: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1457: putfield        com/lemonclient/client/module/modules/combat/BedAura.fpMinDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1460: aload_0         /* this */
        //  1461: aload_0         /* this */
        //  1462: ldc_w           "Force Place"
        //  1465: iconst_0       
        //  1466: aload_0         /* this */
        //  1467: invokedynamic   BootstrapMethod #65, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1472: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1475: putfield        com/lemonclient/client/module/modules/combat/BedAura.forcePlace:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1478: aload_0         /* this */
        //  1479: aload_0         /* this */
        //  1480: ldc_w           "Hand"
        //  1483: iconst_3       
        //  1484: anewarray       Ljava/lang/String;
        //  1487: dup            
        //  1488: iconst_0       
        //  1489: ldc_w           "Main"
        //  1492: aastore        
        //  1493: dup            
        //  1494: iconst_1       
        //  1495: ldc_w           "Off"
        //  1498: aastore        
        //  1499: dup            
        //  1500: iconst_2       
        //  1501: ldc_w           "Auto"
        //  1504: aastore        
        //  1505: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //  1508: ldc_w           "Auto"
        //  1511: aload_0         /* this */
        //  1512: invokedynamic   BootstrapMethod #66, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1517: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1520: putfield        com/lemonclient/client/module/modules/combat/BedAura.handMode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1523: aload_0         /* this */
        //  1524: aload_0         /* this */
        //  1525: ldc_w           "Auto Switch"
        //  1528: iconst_1       
        //  1529: aload_0         /* this */
        //  1530: invokedynamic   BootstrapMethod #67, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1535: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1538: putfield        com/lemonclient/client/module/modules/combat/BedAura.autoSwitch:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1541: aload_0         /* this */
        //  1542: aload_0         /* this */
        //  1543: ldc_w           "Switch Back"
        //  1546: iconst_1       
        //  1547: aload_0         /* this */
        //  1548: invokedynamic   BootstrapMethod #68, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1553: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1556: putfield        com/lemonclient/client/module/modules/combat/BedAura.silentSwitch:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1559: aload_0         /* this */
        //  1560: aload_0         /* this */
        //  1561: ldc_w           "Packet Switch"
        //  1564: iconst_1       
        //  1565: aload_0         /* this */
        //  1566: invokedynamic   BootstrapMethod #69, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1571: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1574: putfield        com/lemonclient/client/module/modules/combat/BedAura.packetSwitch:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1577: aload_0         /* this */
        //  1578: aload_0         /* this */
        //  1579: ldc_w           "Refill Beds"
        //  1582: iconst_1       
        //  1583: aload_0         /* this */
        //  1584: invokedynamic   BootstrapMethod #70, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1589: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1592: putfield        com/lemonclient/client/module/modules/combat/BedAura.refill:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1595: aload_0         /* this */
        //  1596: aload_0         /* this */
        //  1597: ldc_w           "Click Mode"
        //  1600: iconst_3       
        //  1601: anewarray       Ljava/lang/String;
        //  1604: dup            
        //  1605: iconst_0       
        //  1606: ldc_w           "Quick"
        //  1609: aastore        
        //  1610: dup            
        //  1611: iconst_1       
        //  1612: ldc_w           "Swap"
        //  1615: aastore        
        //  1616: dup            
        //  1617: iconst_2       
        //  1618: ldc_w           "Pickup"
        //  1621: aastore        
        //  1622: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //  1625: ldc_w           "Quick"
        //  1628: aload_0         /* this */
        //  1629: invokedynamic   BootstrapMethod #71, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1634: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1637: putfield        com/lemonclient/client/module/modules/combat/BedAura.clickMode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1640: aload_0         /* this */
        //  1641: aload_0         /* this */
        //  1642: ldc_w           "Refill Mode"
        //  1645: iconst_2       
        //  1646: anewarray       Ljava/lang/String;
        //  1649: dup            
        //  1650: iconst_0       
        //  1651: ldc_w           "All"
        //  1654: aastore        
        //  1655: dup            
        //  1656: iconst_1       
        //  1657: ldc_w           "Only"
        //  1660: aastore        
        //  1661: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //  1664: ldc_w           "All"
        //  1667: aload_0         /* this */
        //  1668: invokedynamic   BootstrapMethod #72, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1673: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1676: putfield        com/lemonclient/client/module/modules/combat/BedAura.refillMode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //  1679: aload_0         /* this */
        //  1680: aload_0         /* this */
        //  1681: ldc_w           "Slot"
        //  1684: iconst_1       
        //  1685: iconst_1       
        //  1686: bipush          9
        //  1688: aload_0         /* this */
        //  1689: invokedynamic   BootstrapMethod #73, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1694: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1697: putfield        com/lemonclient/client/module/modules/combat/BedAura.slotS:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1700: aload_0         /* this */
        //  1701: aload_0         /* this */
        //  1702: ldc_w           "Force Refill"
        //  1705: iconst_0       
        //  1706: aload_0         /* this */
        //  1707: invokedynamic   BootstrapMethod #74, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1712: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1715: putfield        com/lemonclient/client/module/modules/combat/BedAura.force:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1718: aload_0         /* this */
        //  1719: aload_0         /* this */
        //  1720: ldc_w           "Slow Face Place"
        //  1723: iconst_1       
        //  1724: aload_0         /* this */
        //  1725: invokedynamic   BootstrapMethod #75, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1730: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1733: putfield        com/lemonclient/client/module/modules/combat/BedAura.slowFP:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1736: aload_0         /* this */
        //  1737: aload_0         /* this */
        //  1738: ldc_w           "SlowFP Place Delay"
        //  1741: sipush          500
        //  1744: iconst_0       
        //  1745: sipush          1000
        //  1748: aload_0         /* this */
        //  1749: invokedynamic   BootstrapMethod #76, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1754: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1757: putfield        com/lemonclient/client/module/modules/combat/BedAura.slowPlaceDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1760: aload_0         /* this */
        //  1761: aload_0         /* this */
        //  1762: ldc_w           "SlowFP Break Delay"
        //  1765: sipush          500
        //  1768: iconst_0       
        //  1769: sipush          1000
        //  1772: aload_0         /* this */
        //  1773: invokedynamic   BootstrapMethod #77, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1778: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1781: putfield        com/lemonclient/client/module/modules/combat/BedAura.slowBreakDelay:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1784: aload_0         /* this */
        //  1785: aload_0         /* this */
        //  1786: ldc_w           "SlowFP Min Dmg"
        //  1789: ldc2_w          0.05
        //  1792: dconst_0       
        //  1793: ldc2_w          36.0
        //  1796: aload_0         /* this */
        //  1797: invokedynamic   BootstrapMethod #78, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1802: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerDouble:(Ljava/lang/String;DDDLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1805: putfield        com/lemonclient/client/module/modules/combat/BedAura.slowMinDmg:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //  1808: aload_0         /* this */
        //  1809: aload_0         /* this */
        //  1810: ldc_w           "Render Dmg"
        //  1813: iconst_1       
        //  1814: aload_0         /* this */
        //  1815: invokedynamic   BootstrapMethod #79, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1820: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1823: putfield        com/lemonclient/client/module/modules/combat/BedAura.showDamage:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1826: aload_0         /* this */
        //  1827: aload_0         /* this */
        //  1828: ldc_w           "Self Dmg"
        //  1831: iconst_1       
        //  1832: aload_0         /* this */
        //  1833: invokedynamic   BootstrapMethod #80, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1838: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1841: putfield        com/lemonclient/client/module/modules/combat/BedAura.showSelfDamage:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1844: aload_0         /* this */
        //  1845: aload_0         /* this */
        //  1846: ldc_w           "Hand Color"
        //  1849: new             Lcom/lemonclient/api/util/render/GSColor;
        //  1852: dup            
        //  1853: sipush          255
        //  1856: iconst_0       
        //  1857: iconst_0       
        //  1858: bipush          50
        //  1860: invokespecial   com/lemonclient/api/util/render/GSColor.<init>:(IIII)V
        //  1863: aload_0         /* this */
        //  1864: invokedynamic   BootstrapMethod #81, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1869: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerColor:(Ljava/lang/String;Lcom/lemonclient/api/util/render/GSColor;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ColorSetting;
        //  1872: putfield        com/lemonclient/client/module/modules/combat/BedAura.color:Lcom/lemonclient/api/setting/values/ColorSetting;
        //  1875: aload_0         /* this */
        //  1876: aload_0         /* this */
        //  1877: ldc_w           "Base Color"
        //  1880: new             Lcom/lemonclient/api/util/render/GSColor;
        //  1883: dup            
        //  1884: iconst_0       
        //  1885: sipush          255
        //  1888: iconst_0       
        //  1889: bipush          50
        //  1891: invokespecial   com/lemonclient/api/util/render/GSColor.<init>:(IIII)V
        //  1894: aload_0         /* this */
        //  1895: invokedynamic   BootstrapMethod #82, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1900: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerColor:(Ljava/lang/String;Lcom/lemonclient/api/util/render/GSColor;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ColorSetting;
        //  1903: putfield        com/lemonclient/client/module/modules/combat/BedAura.color2:Lcom/lemonclient/api/setting/values/ColorSetting;
        //  1906: aload_0         /* this */
        //  1907: aload_0         /* this */
        //  1908: ldc_w           "Alpha"
        //  1911: bipush          60
        //  1913: iconst_0       
        //  1914: sipush          255
        //  1917: aload_0         /* this */
        //  1918: invokedynamic   BootstrapMethod #83, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1923: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1926: putfield        com/lemonclient/client/module/modules/combat/BedAura.alpha:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1929: aload_0         /* this */
        //  1930: aload_0         /* this */
        //  1931: ldc_w           "Outline Alpha"
        //  1934: bipush          120
        //  1936: iconst_0       
        //  1937: sipush          255
        //  1940: aload_0         /* this */
        //  1941: invokedynamic   BootstrapMethod #84, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1946: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1949: putfield        com/lemonclient/client/module/modules/combat/BedAura.outAlpha:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  1952: aload_0         /* this */
        //  1953: aload_0         /* this */
        //  1954: ldc_w           "Gradient"
        //  1957: iconst_1       
        //  1958: aload_0         /* this */
        //  1959: invokedynamic   BootstrapMethod #85, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1964: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1967: putfield        com/lemonclient/client/module/modules/combat/BedAura.gradient:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1970: aload_0         /* this */
        //  1971: aload_0         /* this */
        //  1972: ldc_w           "Outline Gradient"
        //  1975: iconst_1       
        //  1976: aload_0         /* this */
        //  1977: invokedynamic   BootstrapMethod #86, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  1982: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1985: putfield        com/lemonclient/client/module/modules/combat/BedAura.outGradient:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  1988: aload_0         /* this */
        //  1989: aload_0         /* this */
        //  1990: ldc_w           "Width"
        //  1993: iconst_1       
        //  1994: iconst_1       
        //  1995: bipush          10
        //  1997: aload_0         /* this */
        //  1998: invokedynamic   BootstrapMethod #87, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2003: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2006: putfield        com/lemonclient/client/module/modules/combat/BedAura.width:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2009: aload_0         /* this */
        //  2010: aload_0         /* this */
        //  2011: ldc_w           "MovingTime"
        //  2014: iconst_0       
        //  2015: iconst_0       
        //  2016: sipush          500
        //  2019: aload_0         /* this */
        //  2020: invokedynamic   BootstrapMethod #88, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2025: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2028: putfield        com/lemonclient/client/module/modules/combat/BedAura.movingTime:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2031: aload_0         /* this */
        //  2032: aload_0         /* this */
        //  2033: ldc_w           "FadeTime"
        //  2036: bipush          100
        //  2038: iconst_0       
        //  2039: sipush          500
        //  2042: aload_0         /* this */
        //  2043: invokedynamic   BootstrapMethod #89, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2048: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2051: putfield        com/lemonclient/client/module/modules/combat/BedAura.lifeTime:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //  2054: aload_0         /* this */
        //  2055: aload_0         /* this */
        //  2056: ldc_w           "Render Test"
        //  2059: iconst_0       
        //  2060: aload_0         /* this */
        //  2061: invokedynamic   BootstrapMethod #90, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2066: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  2069: putfield        com/lemonclient/client/module/modules/combat/BedAura.renderTest:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  2072: aload_0         /* this */
        //  2073: aload_0         /* this */
        //  2074: ldc_w           "HUD"
        //  2077: iconst_4       
        //  2078: anewarray       Ljava/lang/String;
        //  2081: dup            
        //  2082: iconst_0       
        //  2083: ldc             "Target"
        //  2085: aastore        
        //  2086: dup            
        //  2087: iconst_1       
        //  2088: ldc_w           "Damage"
        //  2091: aastore        
        //  2092: dup            
        //  2093: iconst_2       
        //  2094: ldc_w           "Both"
        //  2097: aastore        
        //  2098: dup            
        //  2099: iconst_3       
        //  2100: ldc_w           "None"
        //  2103: aastore        
        //  2104: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //  2107: ldc_w           "None"
        //  2110: aload_0         /* this */
        //  2111: invokedynamic   BootstrapMethod #91, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2116: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //  2119: putfield        com/lemonclient/client/module/modules/combat/BedAura.hudDisplay:Lcom/lemonclient/api/setting/values/ModeSetting;
        //  2122: aload_0         /* this */
        //  2123: aload_0         /* this */
        //  2124: ldc_w           "Show Self Damage"
        //  2127: iconst_0       
        //  2128: aload_0         /* this */
        //  2129: invokedynamic   BootstrapMethod #92, get:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Ljava/util/function/Supplier;
        //  2134: invokevirtual   com/lemonclient/client/module/modules/combat/BedAura.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  2137: putfield        com/lemonclient/client/module/modules/combat/BedAura.hudSelfDamage:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //  2140: aload_0         /* this */
        //  2141: new             Ljava/util/HashMap;
        //  2144: dup            
        //  2145: invokespecial   java/util/HashMap.<init>:()V
        //  2148: putfield        com/lemonclient/client/module/modules/combat/BedAura.playerSpeed:Ljava/util/HashMap;
        //  2151: aload_0         /* this */
        //  2152: aconst_null    
        //  2153: putfield        com/lemonclient/client/module/modules/combat/BedAura.target:Lcom/lemonclient/client/module/modules/combat/BedAura$EntityInfo;
        //  2156: aload_0         /* this */
        //  2157: new             Lnet/minecraft/util/math/Vec3d;
        //  2160: dup            
        //  2161: ldc2_w          -1.0
        //  2164: ldc2_w          -1.0
        //  2167: ldc2_w          -1.0
        //  2170: invokespecial   net/minecraft/util/math/Vec3d.<init>:(DDD)V
        //  2173: putfield        com/lemonclient/client/module/modules/combat/BedAura.movingBaseNow:Lnet/minecraft/util/math/Vec3d;
        //  2176: aload_0         /* this */
        //  2177: new             Lnet/minecraft/util/math/Vec3d;
        //  2180: dup            
        //  2181: ldc2_w          -1.0
        //  2184: ldc2_w          -1.0
        //  2187: ldc2_w          -1.0
        //  2190: invokespecial   net/minecraft/util/math/Vec3d.<init>:(DDD)V
        //  2193: putfield        com/lemonclient/client/module/modules/combat/BedAura.movingHeadNow:Lnet/minecraft/util/math/Vec3d;
        //  2196: aload_0         /* this */
        //  2197: aconst_null    
        //  2198: putfield        com/lemonclient/client/module/modules/combat/BedAura.lastBestBase:Lnet/minecraft/util/math/BlockPos;
        //  2201: aload_0         /* this */
        //  2202: aconst_null    
        //  2203: putfield        com/lemonclient/client/module/modules/combat/BedAura.lastBestHead:Lnet/minecraft/util/math/BlockPos;
        //  2206: aload_0         /* this */
        //  2207: new             Lcom/lemonclient/api/util/misc/Timing;
        //  2210: dup            
        //  2211: invokespecial   com/lemonclient/api/util/misc/Timing.<init>:()V
        //  2214: putfield        com/lemonclient/client/module/modules/combat/BedAura.basetiming:Lcom/lemonclient/api/util/misc/Timing;
        //  2217: aload_0         /* this */
        //  2218: new             Lcom/lemonclient/api/util/misc/Timing;
        //  2221: dup            
        //  2222: invokespecial   com/lemonclient/api/util/misc/Timing.<init>:()V
        //  2225: putfield        com/lemonclient/client/module/modules/combat/BedAura.calctiming:Lcom/lemonclient/api/util/misc/Timing;
        //  2228: aload_0         /* this */
        //  2229: new             Lcom/lemonclient/api/util/misc/Timing;
        //  2232: dup            
        //  2233: invokespecial   com/lemonclient/api/util/misc/Timing.<init>:()V
        //  2236: putfield        com/lemonclient/client/module/modules/combat/BedAura.placetiming:Lcom/lemonclient/api/util/misc/Timing;
        //  2239: aload_0         /* this */
        //  2240: new             Lcom/lemonclient/api/util/misc/Timing;
        //  2243: dup            
        //  2244: invokespecial   com/lemonclient/api/util/misc/Timing.<init>:()V
        //  2247: putfield        com/lemonclient/client/module/modules/combat/BedAura.breaktiming:Lcom/lemonclient/api/util/misc/Timing;
        //  2250: aload_0         /* this */
        //  2251: new             Lcom/lemonclient/api/util/misc/Timing;
        //  2254: dup            
        //  2255: invokespecial   com/lemonclient/api/util/misc/Timing.<init>:()V
        //  2258: putfield        com/lemonclient/client/module/modules/combat/BedAura.updatetiming:Lcom/lemonclient/api/util/misc/Timing;
        //  2261: aload_0         /* this */
        //  2262: iconst_4       
        //  2263: anewarray       Lnet/minecraft/util/math/BlockPos;
        //  2266: dup            
        //  2267: iconst_0       
        //  2268: new             Lnet/minecraft/util/math/BlockPos;
        //  2271: dup            
        //  2272: iconst_1       
        //  2273: iconst_0       
        //  2274: iconst_0       
        //  2275: invokespecial   net/minecraft/util/math/BlockPos.<init>:(III)V
        //  2278: aastore        
        //  2279: dup            
        //  2280: iconst_1       
        //  2281: new             Lnet/minecraft/util/math/BlockPos;
        //  2284: dup            
        //  2285: iconst_m1      
        //  2286: iconst_0       
        //  2287: iconst_0       
        //  2288: invokespecial   net/minecraft/util/math/BlockPos.<init>:(III)V
        //  2291: aastore        
        //  2292: dup            
        //  2293: iconst_2       
        //  2294: new             Lnet/minecraft/util/math/BlockPos;
        //  2297: dup            
        //  2298: iconst_0       
        //  2299: iconst_0       
        //  2300: iconst_m1      
        //  2301: invokespecial   net/minecraft/util/math/BlockPos.<init>:(III)V
        //  2304: aastore        
        //  2305: dup            
        //  2306: iconst_3       
        //  2307: new             Lnet/minecraft/util/math/BlockPos;
        //  2310: dup            
        //  2311: iconst_0       
        //  2312: iconst_0       
        //  2313: iconst_1       
        //  2314: invokespecial   net/minecraft/util/math/BlockPos.<init>:(III)V
        //  2317: aastore        
        //  2318: putfield        com/lemonclient/client/module/modules/combat/BedAura.sides:[Lnet/minecraft/util/math/BlockPos;
        //  2321: aload_0         /* this */
        //  2322: new             Lme/zero/alpine/listener/Listener;
        //  2325: dup            
        //  2326: aload_0         /* this */
        //  2327: invokedynamic   BootstrapMethod #93, invoke:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Lme/zero/alpine/listener/EventHook;
        //  2332: iconst_0       
        //  2333: anewarray       Ljava/util/function/Predicate;
        //  2336: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //  2339: putfield        com/lemonclient/client/module/modules/combat/BedAura.sendListener:Lme/zero/alpine/listener/Listener;
        //  2342: aload_0         /* this */
        //  2343: new             Lme/zero/alpine/listener/Listener;
        //  2346: dup            
        //  2347: aload_0         /* this */
        //  2348: invokedynamic   BootstrapMethod #94, invoke:(Lcom/lemonclient/client/module/modules/combat/BedAura;)Lme/zero/alpine/listener/EventHook;
        //  2353: iconst_0       
        //  2354: anewarray       Ljava/util/function/Predicate;
        //  2357: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //  2360: putfield        com/lemonclient/client/module/modules/combat/BedAura.onUpdateWalkingPlayerEventListener:Lme/zero/alpine/listener/Listener;
        //  2363: aload_0         /* this */
        //  2364: iconst_1       
        //  2365: putfield        com/lemonclient/client/module/modules/combat/BedAura.switching:Z
        //  2368: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:382)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void onUpdate() {
        if (BedAura.mc.player == null || BedAura.mc.world == null || EntityUtil.isDead(BedAura.mc.player) || this.inNether()) {
            this.target = null;
            final BlockPos blockPos = null;
            this.basePos = blockPos;
            this.headPos = blockPos;
            final float n = 0.0f;
            this.selfDamage = n;
            this.damage = n;
            this.rotation = null;
            return;
        }
        for (final EntityPlayer player : BedAura.mc.world.playerEntities) {
            if (BedAura.mc.player.getDistanceSq(player) > this.enemyRange.getValue() * this.enemyRange.getValue()) {
                continue;
            }
            double lastYaw = 512.0;
            int tick = this.startTick.getValue();
            if (this.playerSpeed.get(player) != null) {
                final MoveRotation info = this.playerSpeed.get(player);
                lastYaw = info.yaw;
                tick = info.tick + this.addTick.getValue();
            }
            if (tick > this.maxPredict) {
                tick = this.maxPredict;
            }
            this.playerSpeed.put(player, new MoveRotation(player, lastYaw, tick));
        }
        this.calc();
    }
    
    @Override
    public void fast() {
        if (BedAura.mc.player == null || BedAura.mc.world == null || EntityUtil.isDead(BedAura.mc.player) || this.inNether()) {
            return;
        }
        if (this.updatetiming.passedMs(this.updateDelay.getValue())) {
            this.updatetiming.reset();
            if (this.pause.getValue()) {
                final BlockPos pos = PlayerUtil.getPlayerPos();
                this.burrow = (this.isBurrow(pos) && !this.isBurrow(pos.up()));
            }
            else {
                this.burrow = false;
            }
            this.maxPredict = this.tickPredict.getValue();
            final NetHandlerPlayClient connection = BedAura.mc.getConnection();
            if (this.detect.getValue() && connection != null) {
                final NetworkPlayerInfo info = connection.getPlayerInfo(BedAura.mc.getConnection().getGameProfile().getId());
                if (info != null) {
                    this.maxPredict = info.getResponseTime() * 2 / 50;
                }
            }
            if (this.base.getValue() && this.basetiming.passedMs(this.baseDelay.getValue())) {
                this.canBasePlace = true;
                this.basetiming.reset();
            }
        }
        if (this.continuE != null && !isPos2(this.continuE, this.basePos) && this.isBed(this.continuE)) {
            this.switching = true;
        }
        this.bedaura();
    }
    
    private boolean isBurrow(final BlockPos pos) {
        final AxisAlignedBB box = BlockUtil.getBoundingBox(pos);
        if (box == null) {
            return false;
        }
        if (!BedAura.mc.player.boundingBox.intersects(box)) {
            return false;
        }
        final Block block = BlockUtil.getBlock(pos);
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.ENDER_CHEST;
    }
    
    private void bedaura() {
        if (this.renderTest.getValue() || this.headPos == null || this.basePos == null) {
            return;
        }
        if (this.target.defaultPlayer == null || ColorMain.INSTANCE.breakList.contains(this.basePos) || ColorMain.INSTANCE.breakList.contains(this.headPos)) {
            this.place(this.placeDelay.getValue());
            this.breakBed(this.breakDelay.getValue());
            return;
        }
        final String s = this.mode.getValue();
        switch (s) {
            case "PlaceBreak": {
                this.place(this.placeDelay.getValue());
                this.breakBed(this.breakDelay.getValue());
                break;
            }
            case "BreakPlace": {
                this.breakBed(this.breakDelay.getValue());
                this.place(this.placeDelay.getValue());
                break;
            }
            case "Switch":
                Label_0466: {
                    if (this.switching) {
                        if (this.place(this.placeDelay.getValue()) || this.breakBed(this.breakDelay.getValue())) {
                            this.switching = false;
                        }
                        break Label_0466;
                    }
                    else {
                        if (this.breakBed(this.switchBreakDelay.getValue()) || this.place(this.switchPlaceDelay.getValue())) {
                            this.switching = true;
                        }
                        break Label_0466;
                    }
                }
            case "Stuck": {
                if (this.stuck(this.target)) {
                    this.breakBed(this.stuckBreakDelay.getValue());
                    this.place(this.stuckPlaceDelay.getValue());
                    break;
                }
                this.place(this.placeDelay.getValue());
                this.breakBed(this.breakDelay.getValue());
                break;
            }
            case "Test": {
                if (this.stuck(this.target)) {
                    this.breakBed(this.stuckBreakDelay.getValue());
                    this.place(this.stuckPlaceDelay.getValue());
                    break;
                }
                if (this.switching) {
                    if (this.place(this.placeDelay.getValue()) || this.breakBed(this.breakDelay.getValue())) {
                        this.switching = false;
                        break;
                    }
                    break;
                }
                else {
                    if (this.breakBed(this.switchBreakDelay.getValue()) || this.place(this.switchPlaceDelay.getValue())) {
                        this.switching = true;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void calc() {
        if (this.calctiming.passedMs(this.calcDelay.getValue())) {
            this.calctiming.reset();
            this.target = null;
            final BlockPos blockPos = null;
            this.basePos = blockPos;
            this.headPos = blockPos;
            final float n = 0.0f;
            this.selfDamage = n;
            this.damage = n;
            this.rotation = null;
            final boolean offhand = !this.handMode.getValue().equals("Main") && BedAura.mc.player.getHeldItemOffhand().getItem() == Items.BED;
            if (!offhand && !this.handMode.getValue().equals("Off")) {
                if (this.refill.getValue()) {
                    this.refill_bed();
                }
                this.slot = BurrowUtil.findHotbarBlock(ItemBed.class);
                if (this.slot == -1) {
                    return;
                }
            }
            this.hand = (offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            final EntityInfo self = new EntityInfo(BedAura.mc.player, this.selfPredict.getValue());
            PlaceInfo placeInfo = this.getPlaceInfo(self, this.findBlocksExcluding(this.base.getValue() && this.canBasePlace));
            if (placeInfo == null) {
                final List<Entity> entityList = new ArrayList<Entity>();
                for (final Entity entity : BedAura.mc.world.loadedEntityList) {
                    if (BedAura.mc.player.getDistance(entity) <= this.enemyRange.getValue()) {
                        if (EntityUtil.isDead(entity)) {
                            continue;
                        }
                        if (this.monster.getValue() && EntityUtil.isMobAggressive(entity)) {
                            entityList.add(entity);
                        }
                        if (this.neutral.getValue() && EntityUtil.isNeutralMob(entity)) {
                            entityList.add(entity);
                        }
                        if (!this.animal.getValue() || !EntityUtil.isPassive(entity)) {
                            continue;
                        }
                        entityList.add(entity);
                    }
                }
                placeInfo = this.calculatePlacement(this.getNearestEntity(entityList), self, this.findBlocksExcluding(true));
                this.target = placeInfo.target;
            }
            else {
                this.target = placeInfo.target;
                if (ModuleManager.isModuleEnabled("AutoEz")) {
                    AutoEz.INSTANCE.addTargetedPlayer(this.target.defaultPlayer.getName());
                }
                if (this.base.getValue() && placeInfo.basePos != null) {
                    this.canBasePlace = false;
                    final int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                    final BlockPos pos = placeInfo.basePos;
                    InventoryUtil.run(obbySlot, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, this.packetBase.getValue(), false, this.baseSwing.getValue()));
                }
            }
            BlockPos bedPos = placeInfo.placePos;
            if (bedPos == null) {
                return;
            }
            this.damage = placeInfo.damage;
            this.selfDamage = placeInfo.selfDamage;
            this.headPos = bedPos;
            switch (RotationUtil.getFacing(PlayerPacketManager.INSTANCE.getServerSideRotation().x)) {
                case SOUTH: {
                    this.face = "SOUTH";
                    this.rotation = new Vec2f(0.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z - 1);
                    break;
                }
                case WEST: {
                    this.face = "WEST";
                    this.rotation = new Vec2f(90.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x + 1, this.headPos.y, this.headPos.z);
                    break;
                }
                case NORTH: {
                    this.face = "NORTH";
                    this.rotation = new Vec2f(180.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z + 1);
                    break;
                }
                case EAST: {
                    this.face = "EAST";
                    this.rotation = new Vec2f(-90.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x - 1, this.headPos.y, this.headPos.z);
                    break;
                }
            }
            if (!this.block(bedPos, true, true)) {
                if (!this.autorotate.getValue() || this.burrow) {
                    this.target = null;
                    final BlockPos blockPos2 = null;
                    this.basePos = blockPos2;
                    this.headPos = blockPos2;
                    final float n2 = 0.0f;
                    this.selfDamage = n2;
                    this.damage = n2;
                    this.rotation = null;
                    return;
                }
                if (this.block(this.headPos.east(), true, true)) {
                    this.face = "WEST";
                    this.rotation = new Vec2f(90.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x + 1, this.headPos.y, this.headPos.z);
                }
                else if (this.block(this.headPos.north(), true, true)) {
                    this.face = "SOUTH";
                    this.rotation = new Vec2f(0.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z - 1);
                }
                else if (this.block(this.headPos.west(), true, true)) {
                    this.face = "EAST";
                    this.rotation = new Vec2f(-90.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x - 1, this.headPos.y, this.headPos.z);
                }
                else {
                    if (!this.block(this.headPos.south(), true, true)) {
                        this.target = null;
                        final BlockPos blockPos3 = null;
                        this.basePos = blockPos3;
                        this.headPos = blockPos3;
                        final float n3 = 0.0f;
                        this.selfDamage = n3;
                        this.damage = n3;
                        this.rotation = null;
                        return;
                    }
                    this.face = "NORTH";
                    this.rotation = new Vec2f(180.0f, 90.0f);
                    bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z + 1);
                }
            }
            this.headPos = this.headPos.up();
            this.basePos = bedPos.up();
        }
    }
    
    private boolean place(final int delay) {
        if (this.checkBed.getValue() && (this.isBed(this.headPos) || this.isBed(this.basePos))) {
            return true;
        }
        if (this.placetiming.passedMs(this.getPlaceDelay(delay))) {
            if (this.continuE == null || this.continuE.distanceSq(this.basePos) > 14.0 || BlockUtil.getBlock(this.continuE) != Blocks.BED) {
                this.continuE = this.basePos;
            }
            final BlockPos neighbour = this.basePos.down();
            final EnumFacing opposite = EnumFacing.DOWN.getOpposite();
            final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            if (BlockUtil.blackList.contains(BedAura.mc.world.getBlockState(neighbour).getBlock()) && !ColorMain.INSTANCE.sneaking) {
                BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            this.run(() -> {
                if (this.packetPlace.getValue()) {
                    BedAura.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbour, EnumFacing.UP, this.hand, 0.5f, 1.0f, 0.5f));
                }
                else {
                    BedAura.mc.playerController.processRightClickBlock(BedAura.mc.player, BedAura.mc.world, neighbour, EnumFacing.UP, hitVec, this.hand);
                }
            }, this.slot);
            if (this.placeSwing.getValue()) {
                this.swing(this.hand);
            }
            this.placetiming.reset();
            return true;
        }
        return false;
    }
    
    private void run(final Runnable runnable, final int slot) {
        if (this.hand == EnumHand.OFF_HAND) {
            runnable.run();
            return;
        }
        final int oldslot = BedAura.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
        }
        else {
            if (this.packetSwitch.getValue()) {
                InventoryUtil.packetSwitch(slot);
            }
            else {
                InventoryUtil.switchSlot(slot);
            }
            runnable.run();
            if (this.silentSwitch.getValue()) {
                if (this.packetSwitch.getValue()) {
                    InventoryUtil.packetSwitch(oldslot);
                }
                else {
                    InventoryUtil.switchSlot(oldslot);
                }
            }
            BedAura.mc.player.openContainer.detectAndSendChanges();
        }
    }
    
    private boolean breakBed(final int delay) {
        if (this.breaktiming.passedMs(this.getBreakDelay(delay))) {
            final EnumFacing side = EnumFacing.UP;
            final Vec3d facing = this.getHitVecOffset(side);
            if (ModuleManager.getModule(ColorMain.class).sneaking) {
                BedAura.mc.player.connection.sendPacket(new CPacketEntityAction(BedAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            BedAura.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.basePos, side, this.hand, (float)facing.x, (float)facing.y, (float)facing.z));
            if (this.isBed(this.headPos) && !this.isBed(this.basePos)) {
                BedAura.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.headPos, side, this.hand, (float)facing.x, (float)facing.y, (float)facing.z));
            }
            if (this.breakSwing.getValue()) {
                this.swing(this.hand);
            }
            this.breaktiming.reset();
            return true;
        }
        return false;
    }
    
    private PlaceInfo getPlaceInfo(final EntityInfo self, final List<BlockPos> posList) {
        PlaceInfo placeInfo = null;
        final List<EntityPlayer> playerList = PlayerUtil.getNearPlayers(this.enemyRange.getValue(), this.maxEnemies.getValue());
        final String s = this.targetMode.getValue();
        switch (s) {
            case "Nearest": {
                final EntityPlayer entityPlayer = playerList.stream().min(Comparator.comparing(p -> BedAura.mc.player.getDistance(p))).orElse(null);
                if (entityPlayer != null) {
                    final EntityInfo player = new EntityInfo(entityPlayer, this.predict.getValue());
                    placeInfo = this.calculateBestPlacement(player, self, posList);
                    break;
                }
                break;
            }
            case "Damage": {
                PlaceInfo best = null;
                for (final EntityPlayer entityPlayer2 : playerList) {
                    if (entityPlayer2 != null) {
                        final EntityInfo player2 = new EntityInfo(entityPlayer2, this.predict.getValue());
                        final PlaceInfo info = this.calculateBestPlacement(player2, self, posList);
                        if (best != null && info.damage <= best.damage) {
                            continue;
                        }
                        best = info;
                    }
                }
                placeInfo = best;
                break;
            }
            case "Health": {
                double health = 37.0;
                EntityPlayer player3 = null;
                for (final EntityPlayer entityPlayer3 : playerList) {
                    if (player3 == null || health > entityPlayer3.getHealth() + entityPlayer3.getAbsorptionAmount()) {
                        player3 = entityPlayer3;
                        health = entityPlayer3.getHealth() + entityPlayer3.getAbsorptionAmount();
                    }
                }
                if (player3 != null) {
                    placeInfo = this.calculateBestPlacement(new EntityInfo(player3, this.predict.getValue()), self, posList);
                    break;
                }
                break;
            }
            case "Smart": {
                final List<EntityPlayer> players = new ArrayList<EntityPlayer>();
                for (final EntityPlayer entityPlayer2 : playerList) {
                    if (this.smartHealth.getValue() >= entityPlayer2.getHealth() + entityPlayer2.getAbsorptionAmount()) {
                        players.add(entityPlayer2);
                    }
                }
                final EntityPlayer target = players.stream().min(Comparator.comparing(p -> p.getHealth() + p.getAbsorptionAmount())).orElse(null);
                PlaceInfo best2 = null;
                if (target != null) {
                    final EntityInfo player2 = new EntityInfo(target, this.predict.getValue());
                    best2 = this.calculateBestPlacement(player2, self, posList);
                }
                if (best2 == null) {
                    for (final EntityPlayer entityPlayer3 : playerList) {
                        if (entityPlayer3 != null) {
                            final EntityInfo player4 = new EntityInfo(entityPlayer3, this.predict.getValue());
                            final PlaceInfo info2 = this.calculateBestPlacement(player4, self, posList);
                            if (best2 != null && info2.damage <= best2.damage) {
                                continue;
                            }
                            best2 = info2;
                        }
                    }
                }
                placeInfo = best2;
                break;
            }
        }
        return placeInfo;
    }
    
    private List<BlockPos> findBlocksExcluding(final boolean calcWithOutBase) {
        return EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue() + 1.0, this.yRange.getValue(), false, false, 0).stream().filter(pos -> this.canPlaceBed(pos, !calcWithOutBase)).collect(Collectors.toList());
    }
    
    private boolean canFacePlace(final EntityInfo target) {
        if (target.hp <= this.facePlaceValue.getValue()) {
            return true;
        }
        for (final ItemStack itemStack : target.defaultPlayer.getArmorInventoryList()) {
            if (itemStack.isEmpty()) {
                continue;
            }
            if (itemStack.getCount() > this.armorRate.getValue()) {
                continue;
            }
            final float dmg = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
            if (dmg < this.armorRate.getValue() / 100.0f) {
                return true;
            }
        }
        return false;
    }
    
    private PlaceInfo calculateBestPlacement(final EntityInfo target, final EntityInfo self, final List<BlockPos> blocks) {
        PlaceInfo best = new PlaceInfo(target, null, (float)Math.min(Math.min(this.minDmg.getValue(), this.slowMinDmg.getValue()), this.fpMinDmg.getMin()), -1.0f, null);
        if (target == null || self == null) {
            return best;
        }
        final boolean facePlace = this.canFacePlace(target);
        for (final BlockPos pos : blocks) {
            BlockPos basePos = null;
            final boolean air = BlockUtil.canReplace(pos);
            final boolean canPlace = this.highVersion.getValue() || (!air && !this.needBase(pos));
            if (!canPlace) {
                if (!this.base.getValue()) {
                    continue;
                }
                if (best.damage >= this.toggleDmg.getValue() && best.basePos == null) {
                    continue;
                }
                if (pos.getY() + 1 > target.player.posY + this.maxY.getValue()) {
                    continue;
                }
                if (BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
                    continue;
                }
                if (LemonClient.speedUtil.getPlayerSpeed(target.defaultPlayer) > this.maxSpeed.getValue()) {
                    continue;
                }
                basePos = this.getBestBasePos(pos);
                if (basePos == null) {
                    continue;
                }
            }
            final double x = pos.getX() + 0.5;
            final double y = pos.getY() + 1.5625;
            final double z = pos.getZ() + 0.5;
            final float targetDamage = DamageUtil.calculateDamage(target.defaultPlayer, target.position, target.boundingBox, x, y, z, 5.0f, "Bed");
            if (!canPlace) {
                if (targetDamage < this.baseMinDmg.getValue()) {
                    continue;
                }
                if (targetDamage == best.damage) {
                    continue;
                }
            }
            if (targetDamage < best.damage) {
                continue;
            }
            if (facePlace) {
                if (targetDamage < this.fpMinDmg.getValue()) {
                    continue;
                }
            }
            else if (targetDamage < this.minDmg.getValue()) {
                if (targetDamage < this.slowMinDmg.getValue()) {
                    continue;
                }
                if (!this.slowFP.getValue()) {
                    continue;
                }
            }
            float selfDamage = 0.0f;
            if (!self.player.isCreative()) {
                selfDamage = DamageUtil.calculateDamage(self.defaultPlayer, self.position, self.boundingBox, x, y, z, 5.0f, "Bed");
                if (selfDamage + this.balance.getValue() > this.maxSelfDmg.getValue()) {
                    if (targetDamage >= target.hp) {
                        if (!this.forcePlace.getValue()) {
                            continue;
                        }
                    }
                    else if (!this.ignore.getValue()) {
                        continue;
                    }
                }
                if (this.suicide.getValue() && selfDamage + this.balance.getValue() >= self.hp) {
                    continue;
                }
            }
            best = new PlaceInfo(target, pos, targetDamage, selfDamage, basePos);
        }
        return best;
    }
    
    private PlaceInfo calculatePlacement(final EntityLivingBase target, final EntityInfo self, final List<BlockPos> poslist) {
        PlaceInfo best = new PlaceInfo(new EntityInfo(target), null, (float)Math.min(Math.min(this.minDmg.getValue(), this.slowMinDmg.getValue()), this.fpMinDmg.getMin()), -1.0f, null);
        if (target == null || self == null) {
            return best;
        }
        for (final BlockPos pos : poslist) {
            final double x = pos.getX() + 0.5;
            final double y = pos.getY() + 1.5625;
            final double z = pos.getZ() + 0.5;
            final float targetDamage = DamageUtil.calculateDamage(target, target.getPositionVector(), target.boundingBox, x, y, z, 5.0f, "Bed");
            final float selfDamage = DamageUtil.calculateDamage(self.defaultPlayer, self.position, self.boundingBox, x, y, z, 5.0f, "Bed");
            if (targetDamage < this.minDmg.getValue() && (targetDamage < this.slowMinDmg.getValue() || !this.slowFP.getValue()) && targetDamage < this.fpMinDmg.getValue()) {
                continue;
            }
            if (!self.player.isCreative()) {
                if (selfDamage + this.balance.getValue() > this.maxSelfDmg.getValue() && !this.ignore.getValue()) {
                    continue;
                }
                if (this.suicide.getValue() && selfDamage + this.balance.getValue() >= self.hp) {
                    continue;
                }
            }
            if (targetDamage <= best.damage) {
                continue;
            }
            best = new PlaceInfo(new EntityInfo(target), pos, targetDamage, selfDamage, null);
        }
        return best;
    }
    
    private boolean near(final EntityInfo player) {
        final AxisAlignedBB box = player.defaultPlayer.boundingBox;
        if (box.intersects(this.bedBoundingBox(this.basePos)) && box.intersects(this.bedBoundingBox(this.basePos))) {
            return false;
        }
        final boolean near = (int)(player.defaultPlayer.posY + 0.5) + 2 >= this.headPos.y && (player.defaultPlayer.getDistance(this.headPos.getX() + 0.5, this.headPos.getY() + 0.25, this.headPos.getZ() + 0.5) < 2.5 || player.defaultPlayer.getDistance(this.basePos.getX() + 0.5, player.defaultPlayer.posY, this.basePos.getZ() + 0.5) < 2.5) && player.defaultPlayer.getDistance(BedAura.mc.player) <= 6.0f;
        final boolean predictNear = player.player.posY > this.headPos.y && (player.player.getDistance(this.headPos.getX() + 0.5, this.headPos.getY() + 0.25, this.headPos.getZ() + 0.5) < 2.5 || player.player.getDistance(this.basePos.getX() + 0.5, player.player.posY, this.basePos.getZ() + 0.5) < 1.5) && player.player.getDistance(BedAura.mc.player) <= 6.0f;
        return near || predictNear;
    }
    
    private boolean stuck(final EntityPlayer player) {
        return player.posY - (int)player.posY > 0.3;
    }
    
    private boolean stuck(final EntityInfo target) {
        final EntityPlayer player = target.defaultPlayer;
        final EntityPlayer predict = target.player;
        boolean inAir = true;
        for (final Vec3d vec3d : new Vec3d[] { new Vec3d(0.25, 0.0, 0.25), new Vec3d(0.25, 0.0, -0.25), new Vec3d(-0.25, 0.0, 0.25), new Vec3d(-0.25, 0.0, -0.25) }) {
            BlockPos pos = new BlockPos(player.posX + vec3d.x, player.posY + 0.7, player.posZ + vec3d.z);
            pos = pos.down();
            if (!BlockUtil.canReplace(pos) && BlockUtil.getBlock(pos) != Blocks.BED) {
                inAir = false;
                break;
            }
        }
        final double y = predict.posY - player.posY;
        return this.near(target) && (this.stuck(player) || this.stuck(predict) || inAir || y > 0.5 || y < -0.5);
    }
    
    private AxisAlignedBB bedBoundingBox(final BlockPos pos) {
        return new AxisAlignedBB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 0.4, pos.z + 1);
    }
    
    private int getPlaceDelay(final int value) {
        if (this.damage < this.minDmg.getValue()) {
            return this.slowPlaceDelay.getValue();
        }
        return value;
    }
    
    private int getBreakDelay(final int value) {
        if (this.damage < this.minDmg.getValue()) {
            return this.slowBreakDelay.getValue();
        }
        return value;
    }
    
    private EntityLivingBase getNearestEntity(final List<Entity> list) {
        return list.stream().filter(EntityLivingBase.class::isInstance).map(EntityLivingBase.class::cast).min(Comparator.comparingDouble(BedAura.mc.player::getDistance)).orElse(null);
    }
    
    private boolean canPlaceBed(final BlockPos blockPos, final boolean baseCheck) {
        if (!this.block(blockPos, !this.highVersion.getValue() || !this.allPossible.getValue() || baseCheck, false)) {
            return false;
        }
        if (this.autorotate.getValue() && !this.burrow) {
            for (final EnumFacing facing : EnumFacing.VALUES) {
                if (facing != EnumFacing.UP) {
                    if (facing != EnumFacing.DOWN) {
                        final BlockPos pos = blockPos.offset(facing);
                        if (this.block(pos, (this.highVersion.getValue() && !this.placeInAir.getValue()) || baseCheck, true)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        final BlockPos pos2 = blockPos.offset(RotationUtil.getFacing(PlayerPacketManager.INSTANCE.getServerSideRotation().x), -1);
        return this.block(pos2, (this.highVersion.getValue() && !this.placeInAir.getValue()) || baseCheck, true) && this.inRange(pos2.up());
    }
    
    private boolean canPlaceBase(final BlockPos pos) {
        return (!this.detectBreak.getValue() || !ColorMain.INSTANCE.breakList.contains(pos)) && this.inRange(pos) && BurrowUtil.getBedFacing(pos) != null && this.space(pos.up()) && !this.intersectsWithEntity(pos);
    }
    
    private boolean needBase(final BlockPos pos) {
        for (final BlockPos side : this.sides) {
            final BlockPos blockPos = pos.add(side);
            if (this.space(blockPos.up())) {
                if (this.inRange(blockPos.up())) {
                    if (!BlockUtil.canReplace(blockPos) && this.solid(pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private BlockPos getBestBasePos(final BlockPos pos) {
        BlockPos bestPos = null;
        double bestRange = 1000.0;
        if (this.autorotate.getValue() && !this.burrow) {
            for (final BlockPos side : this.sides) {
                final BlockPos base = pos.add(side);
                if (this.canPlaceBase(base)) {
                    if (!this.intersectsWithEntity(pos)) {
                        if (bestPos == null || bestRange > BedAura.mc.player.getDistanceSq(base)) {
                            bestRange = BedAura.mc.player.getDistanceSq(base);
                            bestPos = base;
                        }
                    }
                }
            }
            return bestPos;
        }
        final BlockPos base2 = pos.offset(RotationUtil.getFacing(PlayerPacketManager.INSTANCE.getServerSideRotation().x), -1);
        if (this.canPlaceBase(base2)) {
            return base2;
        }
        return null;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : BedAura.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean block(final BlockPos pos, final boolean baseCheck, final boolean rangeCheck) {
        if (!this.space(pos.up())) {
            return false;
        }
        if (BlockUtil.canReplace(pos)) {
            if (baseCheck || !this.canPlaceBase(pos)) {
                return false;
            }
        }
        else if (!this.highVersion.getValue() && !this.solid(pos)) {
            return false;
        }
        return !rangeCheck || this.inRange(pos.up());
    }
    
    private boolean isBed(final BlockPos pos) {
        final Block block = BedAura.mc.world.getBlockState(pos).getBlock();
        return block == Blocks.BED || block instanceof BlockBed;
    }
    
    private boolean space(final BlockPos pos) {
        return BedAura.mc.world.isAirBlock(pos) || BedAura.mc.world.getBlockState(pos).getBlock() == Blocks.BED;
    }
    
    private boolean solid(final BlockPos pos) {
        return !BlockUtil.isBlockUnSolid(pos) && !(BedAura.mc.world.getBlockState(pos).getBlock() instanceof BlockBed) && BedAura.mc.world.getBlockState(pos).isSideSolid(BedAura.mc.world, pos, EnumFacing.UP) && BlockUtil.getBlock(pos).fullBlock;
    }
    
    private boolean inRange(final BlockPos pos) {
        final double x = pos.x - BedAura.mc.player.posX;
        final double z = pos.z - BedAura.mc.player.posZ;
        final double y = pos.y - PlayerUtil.getEyesPos().y;
        final double add = Math.sqrt(y * y) / 2.0;
        return x * x + z * z <= (this.range.getValue() - add) * (this.range.getValue() - add) && y * y <= this.yRange.getValue() * this.yRange.getValue();
    }
    
    private static boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    public void refill_bed() {
        if (!(BedAura.mc.currentScreen instanceof GuiContainer) || BedAura.mc.currentScreen instanceof GuiInventory) {
            final int airSlot = this.isSpace();
            if (airSlot != -1) {
                int i = 9;
                while (i < 36) {
                    if (BedAura.mc.player.inventory.getStackInSlot(i).getItem() == Items.BED) {
                        if (this.clickMode.getValue().equalsIgnoreCase("Quick")) {
                            if (BedAura.mc.player.inventory.getStackInSlot(airSlot).getItem() != Items.AIR) {
                                BedAura.mc.playerController.windowClick(BedAura.mc.player.inventoryContainer.windowId, airSlot + 36, 0, ClickType.QUICK_MOVE, BedAura.mc.player);
                            }
                            BedAura.mc.playerController.windowClick(BedAura.mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, BedAura.mc.player);
                            break;
                        }
                        if (this.clickMode.getValue().equalsIgnoreCase("Swap")) {
                            BedAura.mc.playerController.windowClick(0, i, airSlot, ClickType.SWAP, BedAura.mc.player);
                            break;
                        }
                        BedAura.mc.playerController.windowClick(BedAura.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, BedAura.mc.player);
                        BedAura.mc.playerController.windowClick(BedAura.mc.player.inventoryContainer.windowId, airSlot + 36, 0, ClickType.PICKUP, BedAura.mc.player);
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
    }
    
    private int isSpace() {
        int slot = -1;
        if (this.force.getValue()) {
            if (this.refillMode.getValue().equals("Only")) {
                final int slot2 = this.slotS.getValue() - 1;
                if (BedAura.mc.player.inventory.getStackInSlot(slot2).getItem() != Items.BED) {
                    slot = slot2;
                }
            }
            else {
                for (int i = 0; i < 9; ++i) {
                    if (BedAura.mc.player.inventory.getStackInSlot(i).getItem() != Items.BED) {
                        slot = i;
                    }
                }
            }
        }
        else if (this.refillMode.getValue().equals("Only")) {
            final int slot2 = this.slotS.getValue() - 1;
            if (BedAura.mc.player.inventory.getStackInSlot(slot2).getItem() == Items.AIR) {
                slot = slot2;
            }
        }
        else {
            for (int i = 0; i < 9; ++i) {
                if (BedAura.mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR) {
                    slot = i;
                }
            }
        }
        return slot;
    }
    
    private Vec3d getHitVecOffset(final EnumFacing face) {
        final Vec3i vec = face.getDirectionVec();
        return new Vec3d(vec.x * 0.5f + 0.5f, vec.y * 0.5f + 0.5f, vec.z * 0.5f + 0.5f);
    }
    
    private void swing(final EnumHand hand) {
        if (this.packetSwing.getValue()) {
            BedAura.mc.player.connection.sendPacket(new CPacketAnimation(hand));
        }
        else {
            BedAura.mc.player.swingArm(hand);
        }
    }
    
    private boolean inNether() {
        return BedAura.mc.player.dimension == 0;
    }
    
    public void onEnable() {
        this.calctiming.reset();
        this.basetiming.reset();
        this.placetiming.reset();
        this.breaktiming.reset();
        this.updatetiming.reset();
        this.continuE = null;
        this.switching = true;
        this.updateTimeBase = System.currentTimeMillis();
        this.updateTimeHead = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.lastBestBase = null;
        this.lastBestHead = null;
        this.movingBaseNow = new Vec3d(-1.0, -1.0, -1.0);
        this.movingHeadNow = new Vec3d(-1.0, -1.0, -1.0);
    }
    
    public void onDisable() {
        this.headPos = null;
        this.basePos = null;
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (BedAura.mc.world == null || BedAura.mc.player == null) {
            return;
        }
        final BlockPos nowBase = this.basePos;
        final BlockPos nowHead = this.headPos;
        if (nowBase != this.lastBestBase) {
            if (this.basePos != null && this.lastBestBase == null) {
                this.movingBaseNow = new Vec3d(this.basePos.getX(), this.basePos.getY(), this.basePos.getZ());
            }
            this.updateTimeBase = System.currentTimeMillis();
            if (this.basePos == null) {
                this.startTime = System.currentTimeMillis();
            }
            else if (this.lastBestBase == null) {
                this.startTime = System.currentTimeMillis();
            }
            this.lastBestBase = this.basePos;
        }
        if (nowHead != this.lastBestHead) {
            if (this.headPos != null && this.lastBestHead == null) {
                this.movingHeadNow = new Vec3d(this.headPos.getX(), this.headPos.getY(), this.headPos.getZ());
            }
            this.updateTimeHead = System.currentTimeMillis();
            this.lastBestHead = this.headPos;
        }
        if (this.lastBestBase != null) {
            if (this.movingBaseNow.x == -1.0 && this.movingBaseNow.y == -1.0 && this.movingBaseNow.z == -1.0) {
                this.movingBaseNow = new Vec3d((float)this.lastBestBase.getX(), (float)this.lastBestBase.getY(), (float)this.lastBestBase.getZ());
            }
            if (this.movingTime.getValue() == 0) {
                this.movingBaseNow = new Vec3d(this.lastBestBase);
            }
            else {
                this.movingBaseNow = new Vec3d(this.movingBaseNow.x + (this.lastBestBase.getX() - this.movingBaseNow.x) * this.toDelta(this.updateTimeBase, this.movingTime.getValue()), this.movingBaseNow.y + (this.lastBestBase.getY() - this.movingBaseNow.y) * this.toDelta(this.updateTimeBase, this.movingTime.getValue()), this.movingBaseNow.z + (this.lastBestBase.getZ() - this.movingBaseNow.z) * this.toDelta(this.updateTimeBase, this.movingTime.getValue()));
            }
            if (this.movingHeadNow.x == -1.0 && this.movingHeadNow.y == -1.0 && this.movingHeadNow.z == -1.0) {
                this.movingHeadNow = new Vec3d((float)this.lastBestHead.getX(), (float)this.lastBestHead.getY(), (float)this.lastBestHead.getZ());
            }
            if (this.movingTime.getValue() == 0) {
                this.movingHeadNow = new Vec3d(this.lastBestHead);
            }
            else {
                this.movingHeadNow = new Vec3d(this.movingHeadNow.x + (this.lastBestHead.getX() - this.movingHeadNow.x) * this.toDelta(this.updateTimeHead, this.movingTime.getValue()), this.movingHeadNow.y + (this.lastBestHead.getY() - this.movingHeadNow.y) * this.toDelta(this.updateTimeHead, this.movingTime.getValue()), this.movingHeadNow.z + (this.lastBestHead.getZ() - this.movingHeadNow.z) * this.toDelta(this.updateTimeHead, this.movingTime.getValue()));
            }
        }
        if (this.movingBaseNow.x != -1.0 || this.movingBaseNow.y != -1.0 || this.movingBaseNow.z != -1.0) {
            this.drawBoxMain(this.movingBaseNow.x, this.movingBaseNow.y, this.movingBaseNow.z, this.movingHeadNow.x, this.movingHeadNow.y, this.movingHeadNow.z);
        }
    }
    
    float toDelta(final long start, final float length) {
        float value = this.toDelta(start) / length;
        if (value > 1.0f) {
            value = 1.0f;
        }
        if (value < 0.0f) {
            value = 0.0f;
        }
        return value;
    }
    
    long toDelta(final long start) {
        return System.currentTimeMillis() - start;
    }
    
    private void drawAnimationRender(final AxisAlignedBB box1, final AxisAlignedBB box2) {
        float size;
        if (this.basePos == null) {
            size = 1.0f - this.toDelta(this.startTime, this.lifeTime.getValue());
        }
        else {
            size = this.toDelta(this.startTime, this.lifeTime.getValue());
        }
        final int alpha = (int)(this.alpha.getValue() * size);
        final int outAlpha = (int)(this.outAlpha.getValue() * size);
        final GSColor baseColor = new GSColor(this.color.getValue(), alpha);
        final GSColor baseOutColor = new GSColor(this.color.getValue(), outAlpha);
        final GSColor headColor = new GSColor(this.color2.getValue(), alpha);
        final GSColor headOutColor = new GSColor(this.color2.getValue(), outAlpha);
        final AxisAlignedBB box3 = new AxisAlignedBB(Math.min(box1.minX, box2.minX), box1.minY, Math.min(box1.minZ, box2.minZ), Math.max(box1.maxX, box2.maxX), box1.maxY, Math.max(box1.maxZ, box2.maxZ));
        if (baseColor.equals(headColor)) {
            RenderUtil.drawBox(box3, false, 0.5625, baseColor, 63);
            RenderUtil.drawBoundingBox(box3, this.width.getValue(), baseOutColor);
        }
        else {
            final String face = this.face;
            switch (face) {
                case "WEST": {
                    if (this.gradient.getValue()) {
                        RenderUtil.drawBoxDire(box3, 0.5625, baseColor, 0, 16);
                        RenderUtil.drawBoxDire(box3, 0.5625, headColor, 0, 32);
                    }
                    else {
                        RenderUtil.drawBox(box2, false, 0.5625, baseColor, 31);
                        RenderUtil.drawBox(box1, false, 0.5625, headColor, 47);
                    }
                    if (this.outGradient.getValue()) {
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), baseOutColor, 0, 16);
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), headOutColor, 0, 32);
                        break;
                    }
                    break;
                }
                case "EAST": {
                    if (this.gradient.getValue()) {
                        RenderUtil.drawBoxDire(box3, 0.5625, baseColor, 0, 32);
                        RenderUtil.drawBoxDire(box3, 0.5625, headColor, 0, 16);
                    }
                    else {
                        RenderUtil.drawBox(box2, false, 0.5625, baseColor, 47);
                        RenderUtil.drawBox(box1, false, 0.5625, headColor, 31);
                    }
                    if (this.outGradient.getValue()) {
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), baseOutColor, 0, 32);
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), headOutColor, 0, 16);
                        break;
                    }
                    break;
                }
                case "SOUTH": {
                    if (this.gradient.getValue()) {
                        RenderUtil.drawBoxDire(box3, 0.5625, baseColor, 0, 8);
                        RenderUtil.drawBoxDire(box3, 0.5625, headColor, 0, 4);
                    }
                    else {
                        RenderUtil.drawBox(box2, false, 0.5625, baseColor, 59);
                        RenderUtil.drawBox(box1, false, 0.5625, headColor, 55);
                    }
                    if (this.outGradient.getValue()) {
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), baseOutColor, 0, 8);
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), headOutColor, 0, 4);
                        break;
                    }
                    break;
                }
                case "NORTH": {
                    if (this.gradient.getValue()) {
                        RenderUtil.drawBoxDire(box3, 0.5625, baseColor, 0, 4);
                        RenderUtil.drawBoxDire(box3, 0.5625, headColor, 0, 8);
                    }
                    else {
                        RenderUtil.drawBox(box2, false, 0.5625, baseColor, 55);
                        RenderUtil.drawBox(box1, false, 0.5625, headColor, 59);
                    }
                    if (this.outGradient.getValue()) {
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), baseOutColor, 0, 4);
                        RenderUtil.drawBoundingBoxDire(box3, 0.5625, this.width.getValue(), headOutColor, 0, 8);
                        break;
                    }
                    break;
                }
            }
            if (!this.outGradient.getValue()) {
                RenderUtil.drawBoundingBox(box2, this.width.getValue(), baseOutColor);
                RenderUtil.drawBoundingBox(box1, this.width.getValue(), headOutColor);
            }
        }
        if (this.showDamage.getValue() && this.basePos != null) {
            String[] damageText = { String.format("%.1f", this.damage) };
            if (this.showSelfDamage.getValue()) {
                damageText = new String[] { String.format("%.1f", this.damage) + "/" + String.format("%.1f", this.selfDamage) };
            }
            RenderUtil.drawNametag(box2.minX + 0.5, box2.minY + 0.28125, box2.minZ + 0.5, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667, 0.0);
        }
    }
    
    void drawBoxMain(final double x, final double y, final double z, final double x2, final double y2, final double z2) {
        final AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1.0, y + 0.5625, z + 1.0);
        final AxisAlignedBB box2 = new AxisAlignedBB(x2, y2, z2, x2 + 1.0, y2 + 0.5625, z2 + 1.0);
        this.drawAnimationRender(box, box2);
    }
    
    @Override
    public String getHudInfo() {
        Entity currentTarget = null;
        if (this.target != null) {
            currentTarget = (this.target.defaultPlayer == null) ? this.target.entity : this.target.defaultPlayer;
        }
        final boolean isNull = currentTarget == null;
        final String s = this.hudDisplay.getValue();
        switch (s) {
            case "Target": {
                return isNull ? ("[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]") : ("[" + ChatFormatting.WHITE + currentTarget.getName() + ChatFormatting.GRAY + "]");
            }
            case "Damage": {
                return "[" + ChatFormatting.WHITE + String.format("%.1f", this.damage) + (this.hudSelfDamage.getValue() ? (" Self: " + String.format("%.1f", this.selfDamage)) : "") + ChatFormatting.GRAY + "]";
            }
            case "Both": {
                return "[" + ChatFormatting.WHITE + (isNull ? "None" : currentTarget.getName()) + " " + String.format("%.1f", this.damage) + (this.hudSelfDamage.getValue() ? (" Self: " + String.format("%.1f", this.selfDamage)) : "") + ChatFormatting.GRAY + "]";
            }
            default: {
                return "";
            }
        }
    }
    
    class PlaceInfo
    {
        EntityInfo target;
        BlockPos placePos;
        BlockPos basePos;
        float damage;
        float selfDamage;
        
        public PlaceInfo(final EntityInfo target, final BlockPos placePos, final float damage, final float selfDamage, final BlockPos basePos) {
            this.target = target;
            this.placePos = placePos;
            this.damage = damage;
            this.selfDamage = selfDamage;
            this.basePos = basePos;
        }
    }
    
    class EntityInfo
    {
        EntityPlayer player;
        EntityPlayer defaultPlayer;
        Vec3d position;
        AxisAlignedBB boundingBox;
        EntityLivingBase entity;
        double hp;
        
        public EntityInfo(final EntityPlayer player, final boolean predict) {
            this.player = null;
            this.defaultPlayer = null;
            this.entity = null;
            if (player == null) {
                return;
            }
            this.defaultPlayer = player;
            this.player = (predict ? PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings(BedAura.this.playerSpeed.get(player).tick, BedAura.this.calculateYPredict.getValue(), BedAura.this.startDecrease.getValue(), BedAura.this.exponentStartDecrease.getValue(), BedAura.this.decreaseY.getValue(), BedAura.this.exponentDecreaseY.getValue(), BedAura.this.splitXZ.getValue(), BedAura.this.manualOutHole.getValue(), BedAura.this.aboveHoleManual.getValue(), BedAura.this.stairPredict.getValue(), BedAura.this.nStair.getValue(), BedAura.this.speedActivationStair.getValue())) : player);
            this.position = this.player.getPositionVector();
            this.boundingBox = this.player.getEntityBoundingBox();
            this.hp = player.getHealth() + player.getAbsorptionAmount();
        }
        
        public EntityInfo(final EntityLivingBase entity) {
            this.player = null;
            this.defaultPlayer = null;
            this.entity = null;
            if (entity == null) {
                return;
            }
            this.entity = entity;
            this.hp = entity.getHealth() + entity.getAbsorptionAmount();
        }
    }
    
    class MoveRotation
    {
        double yaw;
        double lastYaw;
        int tick;
        
        public MoveRotation(final EntityPlayer player, final double lastYaw, final int tick) {
            this.yaw = RotationUtil.getRotationTo(player.getPositionVector(), new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)).x;
            this.lastYaw = lastYaw;
            final double difference = this.yaw - lastYaw;
            if ((lastYaw != 512.0 && (difference > BedAura.this.resetRotate.getValue() || difference < -BedAura.this.resetRotate.getValue())) || LemonClient.speedUtil.getPlayerSpeed(player) == 0.0) {
                this.tick = 0;
                return;
            }
            this.tick = tick;
        }
    }
}
