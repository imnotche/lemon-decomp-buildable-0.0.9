// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.item.EntityEnderPearl;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.player.social.SocialManager;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.lemonclient.client.manager.managers.TotemPopManager;
import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.ArrayList;
import com.lemonclient.api.util.misc.ColorUtil;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;
import net.minecraft.entity.Entity;
import java.util.List;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "PvPInfo", category = Category.Misc)
public class PvPInfo extends Module
{
    BooleanSetting visualRange;
    BooleanSetting coords;
    BooleanSetting pearlAlert;
    BooleanSetting strengthDetect;
    BooleanSetting weaknessDetect;
    BooleanSetting popCounter;
    BooleanSetting friend;
    BooleanSetting sharp32;
    ModeSetting type;
    ModeSetting type1;
    ModeSetting type2;
    ModeSetting type3;
    ModeSetting type4;
    ModeSetting type5;
    ModeSetting self;
    ModeSetting chatColor;
    ModeSetting nameColor;
    ModeSetting friColor;
    ModeSetting numberColor;
    List<Entity> knownPlayers;
    List<Entity> antiPearlList;
    List<Entity> players;
    List<Entity> pearls;
    private final Set<EntityPlayer> strengthPlayers;
    private final Set<EntityPlayer> weaknessPlayers;
    private final Set<EntityPlayer> sword;
    
    public PvPInfo() {
        this.visualRange = this.registerBoolean("Visual Range", false);
        this.coords = this.registerBoolean("Coords", true, () -> this.visualRange.getValue());
        this.pearlAlert = this.registerBoolean("Pearl Alert", false);
        this.strengthDetect = this.registerBoolean("Strength Detect", false);
        this.weaknessDetect = this.registerBoolean("Weakness Detect", false);
        this.popCounter = this.registerBoolean("Pop Counter", false);
        this.friend = this.registerBoolean("My Friend", false);
        this.sharp32 = this.registerBoolean("sharp32", true);
        this.type = this.registerMode("Visual Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.type1 = this.registerMode("Pearl Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.type2 = this.registerMode("Strength Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.type3 = this.registerMode("Weakness Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.type4 = this.registerMode("Pop Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.type5 = this.registerMode("32k Type", Arrays.asList("Friend", "Enemy", "All"), "All");
        this.self = this.registerMode("Self", Arrays.asList("I", "Name", "Disable"), "Name");
        this.chatColor = this.registerMode("Color", ColorUtil.colors, "Light Purple");
        this.nameColor = this.registerMode("Name Color", ColorUtil.colors, "Light Purple");
        this.friColor = this.registerMode("Friend Color", ColorUtil.colors, "Light Purple");
        this.numberColor = this.registerMode("Number Color", ColorUtil.colors, "Light Purple");
        this.knownPlayers = new ArrayList<Entity>();
        this.antiPearlList = new ArrayList<Entity>();
        this.strengthPlayers = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
        this.weaknessPlayers = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
        this.sword = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
    }
    
    @Override
    public void onUpdate() {
        if (PvPInfo.mc.player == null || PvPInfo.mc.world == null) {
            return;
        }
        TotemPopManager.INSTANCE.sendMsgs = this.popCounter.getValue();
        if (this.popCounter.getValue()) {
            TotemPopManager.INSTANCE.chatFormatting = ColorUtil.textToChatFormatting(this.chatColor);
            TotemPopManager.INSTANCE.nameFormatting = ColorUtil.textToChatFormatting(this.nameColor);
            TotemPopManager.INSTANCE.friFormatting = ColorUtil.textToChatFormatting(this.friColor);
            TotemPopManager.INSTANCE.numberFormatting = ColorUtil.textToChatFormatting(this.numberColor);
            TotemPopManager.INSTANCE.friend = this.friend.getValue();
            TotemPopManager.INSTANCE.self = this.self.getValue();
            TotemPopManager.INSTANCE.type4 = this.type4.getValue();
        }
        Entity e = null;
        if (this.visualRange.getValue()) {
            this.players = PvPInfo.mc.world.playerEntities.stream().filter(entity -> !entity.getName().equals(PvPInfo.mc.player.getName())).collect(Collectors.toList());
            try {
                final Iterator<Entity> iterator = this.players.iterator();
                while (iterator.hasNext()) {
                    e = iterator.next();
                    if (e.getName().equalsIgnoreCase("fakeplayer")) {
                        continue;
                    }
                    if (this.knownPlayers.contains(e)) {
                        continue;
                    }
                    this.knownPlayers.add(e);
                    final String xyz = this.coords.getValue() ? (" at x:" + (int)e.posX + " y:" + (int)e.posY + " z:" + (int)e.posZ) : "";
                    final String name = e.getName();
                    if (name.equals("") || name.equals(" ")) {
                        return;
                    }
                    if (name.equals("I") || (SocialManager.isFriend(name) && !this.type.getValue().equals("Enemy"))) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Found (" + ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + xyz, Notification.Type.INFO, "VisualRange" + name, 2000);
                    }
                    if (name.equals("I") || SocialManager.isFriend(name) || this.type.getValue().equals("Friend")) {
                        continue;
                    }
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Found (" + ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + xyz, Notification.Type.INFO, "VisualRange" + name, 2000);
                }
            }
            catch (final Exception ex) {}
            try {
                final Iterator<Entity> iterator2 = this.knownPlayers.iterator();
                while (iterator2.hasNext()) {
                    e = iterator2.next();
                    if (e.getName().equalsIgnoreCase("fakeplayer")) {
                        continue;
                    }
                    if (this.players.contains(e)) {
                        continue;
                    }
                    this.knownPlayers.remove(e);
                    final String xyz = this.coords.getValue() ? (" at x:" + (int)e.posX + " y:" + (int)e.posY + " z:" + (int)e.posZ) : "";
                    final String name = e.getName();
                    if (name.equals("") || name.equals(" ")) {
                        return;
                    }
                    if (name.equals("I") || (SocialManager.isFriend(name) && !this.type.getValue().equals("Enemy"))) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Gone (" + ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + xyz, Notification.Type.INFO, "VisualRange" + name, 2000);
                    }
                    if (name.equals("I") || SocialManager.isFriend(name) || this.type.getValue().equals("Friend")) {
                        continue;
                    }
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.chatColor) + "Gone (" + ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + ")" + xyz, Notification.Type.INFO, "VisualRange" + name, 2000);
                }
            }
            catch (final Exception ex2) {}
        }
        if (this.pearlAlert.getValue()) {
            this.pearls = PvPInfo.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderPearl).collect(Collectors.toList());
            try {
                final Iterator<Entity> iterator3 = this.pearls.iterator();
                while (iterator3.hasNext()) {
                    e = iterator3.next();
                    if (e instanceof EntityEnderPearl) {
                        if (e.getEntityWorld().getClosestPlayerToEntity(e, 3.0).getName().equalsIgnoreCase("fakeplayer")) {
                            continue;
                        }
                        if (this.antiPearlList.contains(e)) {
                            continue;
                        }
                        this.antiPearlList.add(e);
                        String faceing = e.getHorizontalFacing().toString();
                        if (faceing.equals("west")) {
                            faceing = "east";
                        }
                        else if (faceing.equals("east")) {
                            faceing = "west";
                        }
                        if (PvPInfo.mc.player.getName().equals(e.getEntityWorld().getClosestPlayerToEntity(e, 3.0).getName()) && this.self.getValue().equals("Disable")) {
                            return;
                        }
                        final String name = (e.getEntityWorld().getClosestPlayerToEntity(e, 3.0).getName().equals(PvPInfo.mc.player.getName()) && this.self.getValue().equals("I")) ? "I" : e.getEntityWorld().getClosestPlayerToEntity(e, 3.0).getName();
                        if (name.equals("") || name.equals(" ")) {
                            return;
                        }
                        if (name.equals("I") || (SocialManager.isFriend(name) && !this.type1.getValue().equals("Enemy"))) {
                            MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.friColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has just thrown a pearl! (" + faceing + ")", Notification.Type.INFO);
                        }
                        if (name.equals("I") || SocialManager.isFriend(name) || this.type1.getValue().equals("Friend")) {
                            continue;
                        }
                        MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.nameColor) + name + ColorUtil.textToChatFormatting(this.chatColor) + " has just thrown a pearl! (" + faceing + ")", Notification.Type.INFO);
                    }
                }
            }
            catch (final Exception ex3) {}
        }
        if (this.strengthDetect.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.world.playerEntities) {
                if (player.getName().equalsIgnoreCase("fakeplayer")) {
                    continue;
                }
                if (player.isPotionActive(MobEffects.STRENGTH) && !this.strengthPlayers.contains(player)) {
                    if (PvPInfo.mc.player.getName().equals(player.getName()) && this.self.getValue().equals("Disable")) {
                        return;
                    }
                    final String name2 = (player.getName().equals(PvPInfo.mc.player.getName()) && this.self.getValue().equals("I")) ? "I" : player.getName();
                    if (name2.equals("") || name2.equals(" ")) {
                        return;
                    }
                    if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type2.getValue().equals("Enemy"))) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name2 + ColorUtil.textToChatFormatting(this.chatColor) + " has drank strength", Notification.Type.INFO, "Strength" + name2, 2000);
                    }
                    if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type2.getValue().equals("Friend")) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + ChatFormatting.RED + " has drank strength", Notification.Type.INFO, "Strength" + name2, 2000);
                    }
                    this.strengthPlayers.add(player);
                }
                if (!this.strengthPlayers.contains(player)) {
                    continue;
                }
                if (player.isPotionActive(MobEffects.STRENGTH)) {
                    continue;
                }
                if (PvPInfo.mc.player.getName().equals(player.getName()) && this.self.getValue().equals("Disable")) {
                    return;
                }
                final String name2 = (player.getName().equals(PvPInfo.mc.player.getName()) && this.self.getValue().equals("I")) ? "I" : player.getName();
                if (name2.equals("") || name2.equals(" ")) {
                    return;
                }
                if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type2.getValue().equals("Enemy"))) {
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name2 + ColorUtil.textToChatFormatting(this.chatColor) + " no longer has strength", Notification.Type.INFO, "Strength" + name2, 2000);
                }
                if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type2.getValue().equals("Friend")) {
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + ChatFormatting.GREEN + " no longer has strength", Notification.Type.INFO, "Strength" + name2, 2000);
                }
                this.strengthPlayers.remove(player);
            }
        }
        if (this.weaknessDetect.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.world.playerEntities) {
                if (player.getName().equalsIgnoreCase("FakePlayer")) {
                    continue;
                }
                if (player.isPotionActive(MobEffects.WEAKNESS) && !this.weaknessPlayers.contains(player)) {
                    if (PvPInfo.mc.player.getName().equals(player.getName()) && this.self.getValue().equals("Disable")) {
                        return;
                    }
                    final String name2 = (player.getName().equals(PvPInfo.mc.player.getName()) && this.self.getValue().equals("I")) ? "I" : player.getName();
                    if (name2.isEmpty() || name2.equals(" ")) {
                        return;
                    }
                    if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type3.getValue().equals("Enemy"))) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name2 + ColorUtil.textToChatFormatting(this.chatColor) + " has drank weekness", Notification.Type.INFO, "Weakness" + name2, 2000);
                    }
                    if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type3.getValue().equals("Friend")) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + ChatFormatting.GREEN + " has drank weekness", Notification.Type.INFO, "Weakness" + name2, 2000);
                    }
                    this.weaknessPlayers.add(player);
                }
                if (!this.weaknessPlayers.contains(player)) {
                    continue;
                }
                if (player.isPotionActive(MobEffects.WEAKNESS)) {
                    continue;
                }
                if (PvPInfo.mc.player.getName().equals(player.getName()) && this.self.getValue().equals("Disable")) {
                    return;
                }
                final String name2 = (player.getName().equals(PvPInfo.mc.player.getName()) && this.self.getValue().equals("I")) ? "I" : player.getName();
                if (name2.equals("") || name2.equals(" ")) {
                    return;
                }
                if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type3.getValue().equals("Enemy"))) {
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name2 + ColorUtil.textToChatFormatting(this.chatColor) + " no longer has weekness", Notification.Type.INFO, "Weakness" + name2, 2000);
                }
                if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type3.getValue().equals("Friend")) {
                    MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + ChatFormatting.RED + " no longer has weekness", Notification.Type.INFO, "Weakness" + name2, 2000);
                }
                this.weaknessPlayers.remove(player);
            }
        }
        if (this.sharp32.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.world.playerEntities) {
                if (!player.getName().equalsIgnoreCase("fakeplayer")) {
                    if (player.getName().equals(PvPInfo.mc.player.getName())) {
                        continue;
                    }
                    if (this.is32k(player.itemStackMainHand) && !this.sword.contains(player)) {
                        final String name2 = player.getName();
                        if (name2.equals("") || name2.equals(" ")) {
                            return;
                        }
                        if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type5.getValue().equals("Enemy"))) {
                            MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + " is " + ColorUtil.textToChatFormatting(this.chatColor) + "holding a 32k", Notification.Type.INFO, "32k" + name2, 2000);
                        }
                        if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type5.getValue().equals("Friend")) {
                            MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + " is " + ChatFormatting.RED + "holding" + ColorUtil.textToChatFormatting(this.chatColor) + " a 32k", Notification.Type.INFO, "32k" + name2, 2000);
                        }
                        this.sword.add(player);
                    }
                    if (!this.sword.contains(player)) {
                        continue;
                    }
                    if (this.is32k(player.itemStackMainHand)) {
                        continue;
                    }
                    final String name2 = player.getName();
                    if (name2.equals("") || name2.equals(" ")) {
                        return;
                    }
                    if (name2.equals("I") || (SocialManager.isFriend(name2) && !this.type5.getValue().equals("Enemy"))) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.friColor) + name2 + " is " + ColorUtil.textToChatFormatting(this.chatColor) + "no longer holding a 32k", Notification.Type.INFO, "32k" + name2, 2000);
                    }
                    if (!name2.equals("I") && !SocialManager.isFriend(name2) && !this.type5.getValue().equals("Friend")) {
                        MessageBus.sendClientDeleteMessage(ColorUtil.textToChatFormatting(this.nameColor) + name2 + " is " + ChatFormatting.GREEN + "no longer holding" + ColorUtil.textToChatFormatting(this.chatColor) + " a 32k", Notification.Type.INFO, "32k" + name2, 2000);
                    }
                    this.sword.remove(player);
                }
            }
        }
    }
    
    private boolean is32k(final ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            final NBTTagList enchants = stack.getEnchantmentTagList();
            for (int i = 0; i < enchants.tagCount(); ++i) {
                if (enchants.getCompoundTagAt(i).getShort("lvl") >= 1000) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void onDisable() {
        this.knownPlayers.clear();
        TotemPopManager.INSTANCE.sendMsgs = false;
    }
}
