// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import com.lemonclient.api.util.world.MotionUtil;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ClipFlight", category = Category.Exploits)
public class ClipFlight extends Module
{
    ModeSetting flight;
    IntegerSetting packets;
    IntegerSetting speed;
    IntegerSetting speedY;
    BooleanSetting bypass;
    IntegerSetting interval;
    BooleanSetting update;
    int num;
    double startFlat;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public ClipFlight() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   com/lemonclient/client/module/Module.<init>:()V
        //     4: aload_0         /* this */
        //     5: aload_0         /* this */
        //     6: ldc             "Mode"
        //     8: iconst_2       
        //     9: anewarray       Ljava/lang/String;
        //    12: dup            
        //    13: iconst_0       
        //    14: ldc             "Flight"
        //    16: aastore        
        //    17: dup            
        //    18: iconst_1       
        //    19: ldc             "Clip"
        //    21: aastore        
        //    22: invokestatic    java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
        //    25: ldc             "Clip"
        //    27: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerMode:(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;)Lcom/lemonclient/api/setting/values/ModeSetting;
        //    30: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.flight:Lcom/lemonclient/api/setting/values/ModeSetting;
        //    33: aload_0         /* this */
        //    34: aload_0         /* this */
        //    35: ldc             "Packets"
        //    37: bipush          80
        //    39: iconst_1       
        //    40: sipush          300
        //    43: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerInteger:(Ljava/lang/String;III)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    46: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.packets:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    49: aload_0         /* this */
        //    50: aload_0         /* this */
        //    51: ldc             "XZ Speed"
        //    53: bipush          7
        //    55: bipush          -99
        //    57: bipush          99
        //    59: aload_0         /* this */
        //    60: invokedynamic   BootstrapMethod #0, get:(Lcom/lemonclient/client/module/modules/movement/ClipFlight;)Ljava/util/function/Supplier;
        //    65: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    68: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.speed:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    71: aload_0         /* this */
        //    72: aload_0         /* this */
        //    73: ldc             "Y Speed"
        //    75: bipush          7
        //    77: bipush          -99
        //    79: bipush          99
        //    81: aload_0         /* this */
        //    82: invokedynamic   BootstrapMethod #1, get:(Lcom/lemonclient/client/module/modules/movement/ClipFlight;)Ljava/util/function/Supplier;
        //    87: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    90: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.speedY:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //    93: aload_0         /* this */
        //    94: aload_0         /* this */
        //    95: ldc             "Bypass"
        //    97: iconst_0       
        //    98: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerBoolean:(Ljava/lang/String;Z)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   101: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.bypass:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   104: aload_0         /* this */
        //   105: aload_0         /* this */
        //   106: ldc             "Interval"
        //   108: bipush          25
        //   110: iconst_1       
        //   111: bipush          100
        //   113: aload_0         /* this */
        //   114: invokedynamic   BootstrapMethod #2, get:(Lcom/lemonclient/client/module/modules/movement/ClipFlight;)Ljava/util/function/Supplier;
        //   119: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerInteger:(Ljava/lang/String;IIILjava/util/function/Supplier;)Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   122: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.interval:Lcom/lemonclient/api/setting/values/IntegerSetting;
        //   125: aload_0         /* this */
        //   126: aload_0         /* this */
        //   127: ldc             "Update Position Client Side"
        //   129: iconst_0       
        //   130: invokevirtual   com/lemonclient/client/module/modules/movement/ClipFlight.registerBoolean:(Ljava/lang/String;Z)Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   133: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.update:Lcom/lemonclient/api/setting/values/BooleanSetting;
        //   136: aload_0         /* this */
        //   137: iconst_0       
        //   138: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.num:I
        //   141: aload_0         /* this */
        //   142: dconst_0       
        //   143: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.startFlat:D
        //   146: aload_0         /* this */
        //   147: new             Lme/zero/alpine/listener/Listener;
        //   150: dup            
        //   151: aload_0         /* this */
        //   152: invokedynamic   BootstrapMethod #3, invoke:(Lcom/lemonclient/client/module/modules/movement/ClipFlight;)Lme/zero/alpine/listener/EventHook;
        //   157: iconst_0       
        //   158: anewarray       Ljava/util/function/Predicate;
        //   161: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //   164: putfield        com/lemonclient/client/module/modules/movement/ClipFlight.playerMoveEventListener:Lme/zero/alpine/listener/Listener;
        //   167: return         
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
    
    public void onEnable() {
        this.startFlat = ClipFlight.mc.player.posY;
        this.num = 0;
    }
}
