// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.manager.managers;

import net.minecraft.entity.Entity;
import com.lemonclient.client.LemonClient;
import net.minecraft.world.World;
import net.minecraft.network.play.server.SPacketEntityStatus;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.player.social.SocialManager;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.event.events.TotemPopEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.HashMap;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import com.lemonclient.client.manager.Manager;

public enum TotemPopManager implements Manager
{
    INSTANCE;
    
    public Minecraft mc;
    public boolean sendMsgs;
    public ChatFormatting chatFormatting;
    public ChatFormatting nameFormatting;
    public ChatFormatting friFormatting;
    public ChatFormatting numberFormatting;
    public boolean friend;
    public String self;
    public String type4;
    private final HashMap<String, Integer> playerPopCount;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener;
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener;
    
    TotemPopManager() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1        
        //     2: iload_2        
        //     3: invokespecial   java/lang/Enum.<init>:(Ljava/lang/String;I)V
        //     6: aload_0         /* this */
        //     7: invokestatic    net/minecraft/client/Minecraft.getMinecraft:()Lnet/minecraft/client/Minecraft;
        //    10: putfield        com/lemonclient/client/manager/managers/TotemPopManager.mc:Lnet/minecraft/client/Minecraft;
        //    13: aload_0         /* this */
        //    14: iconst_0       
        //    15: putfield        com/lemonclient/client/manager/managers/TotemPopManager.sendMsgs:Z
        //    18: aload_0         /* this */
        //    19: getstatic       com/mojang/realmsclient/gui/ChatFormatting.WHITE:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    22: putfield        com/lemonclient/client/manager/managers/TotemPopManager.chatFormatting:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    25: aload_0         /* this */
        //    26: getstatic       com/mojang/realmsclient/gui/ChatFormatting.WHITE:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    29: putfield        com/lemonclient/client/manager/managers/TotemPopManager.nameFormatting:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    32: aload_0         /* this */
        //    33: getstatic       com/mojang/realmsclient/gui/ChatFormatting.WHITE:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    36: putfield        com/lemonclient/client/manager/managers/TotemPopManager.friFormatting:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    39: aload_0         /* this */
        //    40: getstatic       com/mojang/realmsclient/gui/ChatFormatting.WHITE:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    43: putfield        com/lemonclient/client/manager/managers/TotemPopManager.numberFormatting:Lcom/mojang/realmsclient/gui/ChatFormatting;
        //    46: aload_0         /* this */
        //    47: new             Ljava/util/HashMap;
        //    50: dup            
        //    51: invokespecial   java/util/HashMap.<init>:()V
        //    54: putfield        com/lemonclient/client/manager/managers/TotemPopManager.playerPopCount:Ljava/util/HashMap;
        //    57: aload_0         /* this */
        //    58: new             Lme/zero/alpine/listener/Listener;
        //    61: dup            
        //    62: aload_0         /* this */
        //    63: invokedynamic   BootstrapMethod #0, invoke:(Lcom/lemonclient/client/manager/managers/TotemPopManager;)Lme/zero/alpine/listener/EventHook;
        //    68: iconst_0       
        //    69: anewarray       Ljava/util/function/Predicate;
        //    72: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //    75: putfield        com/lemonclient/client/manager/managers/TotemPopManager.packetEventListener:Lme/zero/alpine/listener/Listener;
        //    78: aload_0         /* this */
        //    79: new             Lme/zero/alpine/listener/Listener;
        //    82: dup            
        //    83: aload_0         /* this */
        //    84: invokedynamic   BootstrapMethod #1, invoke:(Lcom/lemonclient/client/manager/managers/TotemPopManager;)Lme/zero/alpine/listener/EventHook;
        //    89: iconst_0       
        //    90: anewarray       Ljava/util/function/Predicate;
        //    93: invokespecial   me/zero/alpine/listener/Listener.<init>:(Lme/zero/alpine/listener/EventHook;[Ljava/util/function/Predicate;)V
        //    96: putfield        com/lemonclient/client/manager/managers/TotemPopManager.totemPopEventListener:Lme/zero/alpine/listener/Listener;
        //    99: return         
        //    Signature:
        //  ()V
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
    
    public void death(final EntityPlayer entityPlayer) {
        String name2 = entityPlayer.getName();
        if (this.mc.player.connection.getPlayerInfo(name2) == null) {
            return;
        }
        if (!this.playerPopCount.containsKey(name2)) {
            return;
        }
        final int pop = this.getPlayerPopCount(name2);
        this.playerPopCount.remove(entityPlayer.getName());
        if (this.sendMsgs) {
            if (this.mc.player.getName().equals(name2)) {
                final String self = this.self;
                switch (self) {
                    case "Disable": {
                        return;
                    }
                    case "I": {
                        name2 = "I";
                        break;
                    }
                }
            }
            if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type4.equals("Enemy"))) {
                if (this.friend) {
                    name2 = "My Friend " + name2;
                }
                MessageBus.sendClientPrefixMessage(this.friFormatting + name2 + this.chatFormatting + " died after popping " + this.numberFormatting + pop + this.chatFormatting + " totem" + ((pop > 1) ? "s." : "."), Notification.Type.INFO);
            }
            if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type4.equals("Friend")) {
                MessageBus.sendClientPrefixMessage(this.nameFormatting + name2 + this.chatFormatting + " died after popping " + this.numberFormatting + pop + this.chatFormatting + " totem" + ((pop > 1) ? "s." : "."), Notification.Type.INFO);
            }
        }
    }
    
    public int getPlayerPopCount(final String name) {
        if (this.playerPopCount.containsKey(name)) {
            return this.playerPopCount.get(name);
        }
        return 0;
    }
}
