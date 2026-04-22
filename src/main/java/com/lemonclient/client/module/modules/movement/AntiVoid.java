// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.util.player.PlacementUtil;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiVoid", category = Category.Movement)
public class AntiVoid extends Module
{
    ModeSetting mode;
    DoubleSetting height;
    BooleanSetting chorus;
    BooleanSetting packetFly;
    boolean chorused;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public AntiVoid() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   com/lemonclient/client/module/Module.<init>:()V
        //     4: aload_0         /* this */
        //     5: aload_0         /* this */
        //     6: ldc             "Mode"
        //     8: iconst_3       
        //     9: anewarray       Ljava/lang/String;
        //    12: dup            
        //    13: iconst_0       
        //    14: ldc             "Freeze"
        //    16: aastore        
        //    17: dup            
        //    18: iconst_1       
        //    19: ldc             "Glitch"
        //    21: aastore        
        //    22: dup            
        //    23: iconst_2       
        //    24: ldc             "Catch"
        //    26: aastore        
        //    27: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //    30: ldc             "Freeze"
        //    32: invokevirtual   com/lemonclient/client/module/modules/movement/AntiVoid.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //    35: putfield        com/lemonclient/client/module/modules/movement/AntiVoid.mode:Lcom/lemonclient/api/setting/values/ModeSetting;
        //    38: aload_0         /* this */
        //    39: aload_0         /* this */
        //    40: ldc             "Height"
        //    42: ldc2_w          2.0
        //    45: dconst_0       
        //    46: ldc2_w          5.0
        //    49: invokevirtual   com/lemonclient/client/module/modules/movement/AntiVoid.registerDouble:(Ljava/lang/String;DDD)Lcom/lemonclient/api/setting/values/DoubleSetting;
        //    52: putfield        com/lemonclient/client/module/modules/movement/AntiVoid.height:Lcom/lemonclient/api/setting/values/DoubleSetting;
        //    55: aload_0         /* this */
        //    56: aload_0         /* this */
        //    57: ldc             "Chorus"
        //    59: iconst_0       
        //    60: aload_0         /* this */
        //    61: invokedynamic   BootstrapMethod #0, get:(Lcom/lemonclient/client/module/modules/movement/AntiVoid;)Ljava/util/function/Supplier;
        //    66: invokevirtual   com/lemonclient/client/module/modules/movement/AntiVoid.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    69: putfield        com/lemonclient/client/module/modules/movement/AntiVoid.chorus:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    72: aload_0         /* this */
        //    73: aload_0         /* this */
        //    74: ldc             "PacketFly"
        //    76: iconst_0       
        //    77: aload_0         /* this */
        //    78: invokedynamic   BootstrapMethod #1, get:(Lcom/lemonclient/client/module/modules/movement/AntiVoid;)Ljava/util/function/Supplier;
        //    83: invokevirtual   com/lemonclient/client/module/modules/movement/AntiVoid.registerBoolean:(Ljava/lang/String;ZLjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    86: putfield        com/lemonclient/client/module/modules/movement/AntiVoid.packetFly:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //    89: aload_0         /* this */
        //    90: new             Lme/zero/alpine/listener/Listener;
        //    93: dup            
        //    94: aload_0         /* this */
        //    95: invokedynamic   BootstrapMethod #2, invoke:(Lcom/lemonclient/client/module/modules/movement/AntiVoid;)Lme/zero/alpine/listener/EventHook;
        //   100: iconst_0       
        //   101: anewarray       Ljava/util/function/Predicate;
        //   104: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //   107: putfield        com/lemonclient/client/module/modules/movement/AntiVoid.playerMoveEventListener:Lme/zero/alpine/listener/Listener;
        //   110: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because the return value of "com.strobel.decompiler.ast.Variable.getType()" is null
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:252)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:185)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.nameVariables(AstMethodBodyBuilder.java:1482)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.populateVariables(AstMethodBodyBuilder.java:1411)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
}
