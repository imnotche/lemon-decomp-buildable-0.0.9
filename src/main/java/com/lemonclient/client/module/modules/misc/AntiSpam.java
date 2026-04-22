// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.Setting;
import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.function.Predicate;
import com.lemonclient.api.util.misc.MultiThreading;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.player.social.SocialManager;
import net.minecraft.network.play.server.SPacketChat;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiSpam", category = Category.Misc)
public class AntiSpam extends Module
{
    BooleanSetting greenText;
    BooleanSetting discordLinks;
    BooleanSetting webLinks;
    BooleanSetting announcers;
    BooleanSetting spammers;
    BooleanSetting insulter;
    BooleanSetting greeters;
    BooleanSetting tradeChat;
    BooleanSetting ips;
    BooleanSetting ipsAgr;
    BooleanSetting numberSuffix;
    BooleanSetting duplicates;
    IntegerSetting duplicatesTimeout;
    BooleanSetting filterFriend;
    BooleanSetting showBlocked;
    BooleanSetting autoIgnore;
    IntegerSetting ignoreDuration;
    IntegerSetting violations;
    private final Pattern CHAT_PATTERN;
    private ConcurrentHashMap<String, Long> messageHistory;
    public List<String> ignoredBySpamCheck;
    public Map<String, Integer> violate;
    public List<String> ignoredList;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public AntiSpam() {
        this.greenText = this.registerBoolean("Green Text", true);
        this.discordLinks = this.registerBoolean("Discord Link", true);
        this.webLinks = this.registerBoolean("Web Link", true);
        this.announcers = this.registerBoolean("Announcer", true);
        this.spammers = this.registerBoolean("Spammer", true);
        this.insulter = this.registerBoolean("Insulter", true);
        this.greeters = this.registerBoolean("Greeter", true);
        this.tradeChat = this.registerBoolean("Trade Chat", true);
        this.ips = this.registerBoolean("Server Ip", true);
        this.ipsAgr = this.registerBoolean("Ip Aggressive", true);
        this.numberSuffix = this.registerBoolean("Number Suffix", true);
        this.duplicates = this.registerBoolean("Duplicates", true);
        this.duplicatesTimeout = this.registerInteger("Duplicates Timeout", 30, 1, 600, () -> this.duplicates.getValue());
        this.filterFriend = this.registerBoolean("Filter Friend", false);
        this.showBlocked = this.registerBoolean("Show Blocked", false);
        this.autoIgnore = this.registerBoolean("Auto Ignore", true);
        this.ignoreDuration = this.registerInteger("Ignore Duration", 120, 0, 43200, () -> this.autoIgnore.getValue());
        this.violations = this.registerInteger("Violations", 3, 1, 100, () -> this.autoIgnore.getValue());
        this.CHAT_PATTERN = Pattern.compile("<.*?> ");
        this.ignoredBySpamCheck = new ArrayList<String>();
        this.violate = new ConcurrentHashMap<String, Integer>();
        this.ignoredList = new ArrayList<String>();
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AntiSpam.mc.player != null && this.isEnabled()) {
                if (event.getPacket() instanceof SPacketChat) {
                    final String s = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText();
                    final Matcher matcher = this.CHAT_PATTERN.matcher(s);
                    String username = "null";
                    if (matcher.find()) {
                        final String username2 = matcher.group();
                        username = username2.substring(1, username2.length() - 2);
                    }
                    else if (s.contains(":")) {
                        final int spaceIndex = s.indexOf(" ");
                        if (spaceIndex != -1) {
                            username = s.substring(0, spaceIndex);
                        }
                    }
                    final String username3 = cleanColor(username);
                    if (!username3.equals("null") && AntiSpam.mc.player.connection.getPlayerInfo(username3) != null && !SocialManager.isIgnore(username3) && (!this.filterFriend.getValue() || !SocialManager.isOnFriendList(username3))) {
                        final SPacketChat sPacketChat = (SPacketChat)event.getPacket();
                        if (this.detectSpam(sPacketChat.getChatComponent().getUnformattedText()) && !username3.equalsIgnoreCase(AntiSpam.mc.player.getName())) {
                            if (this.autoIgnore.getValue()) {
                                if ((this.violate.get(username3) != null && this.violate.get(username3) >= this.violations.getValue()) || this.violations.getValue() == 0) {
                                    if (!SocialManager.isIgnore(username3)) {
                                        MessageBus.sendMessage(ChatFormatting.RED + username3 + " has exceeded the limitation of spam violation, ignoring.", Notification.Type.INFO, "AntiSpam", 13, false);
                                        final String finalUsername = username3;
                                        MultiThreading.runAsync(() -> this.startIgnore(finalUsername));
                                        this.violate.remove(username3);
                                    }
                                }
                                else if (this.violate.get(username3) == null) {
                                    this.violate.put(username3, 1);
                                }
                                else {
                                    this.violate.put(username3, this.violate.get(username3) + 1);
                                }
                            }
                            event.cancel();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public static String cleanColor(final String input) {
        return input.replaceAll("(?i)\\u00A7.", "");
    }
    
    public void startIgnore(final String finalUsername) {
        if (AntiSpam.mc.player.getName().equalsIgnoreCase(finalUsername)) {
            return;
        }
        if (SocialManager.isIgnore(finalUsername)) {
            return;
        }
        SocialManager.addIgnore(finalUsername);
        this.ignoredBySpamCheck.add(finalUsername);
        this.ignoredList.add(finalUsername);
        MessageBus.sendMessage(ChatFormatting.RED + finalUsername + " has been auto ignored by AntiSpam for " + this.ignoreDuration.getValue() + ((this.ignoreDuration.getValue() > 1) ? " seconds" : " second"), Notification.Type.INFO, "AntiSpam", 13, false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AntiSpam.this.ignoredList.remove(finalUsername);
                if (!SocialManager.isIgnore(finalUsername)) {
                    return;
                }
                SocialManager.delIgnore(finalUsername);
            }
        }, this.ignoreDuration.getValue() * 1000);
    }
    
    public void onEnable() {
        this.messageHistory = new ConcurrentHashMap<String, Long>();
    }
    
    public void onDisable() {
        this.messageHistory = null;
    }
    
    private boolean detectSpam(final String message) {
        if (this.greenText.getValue() && this.findPatterns(FilterPatterns.GREEN_TEXT, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Green Text: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.discordLinks.getValue() && this.findPatterns(FilterPatterns.DISCORD, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Discord Link: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.webLinks.getValue() && this.findPatterns(FilterPatterns.WEB_LINK, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Web Link: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.ips.getValue() && this.findPatterns(FilterPatterns.IP_ADDR, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] IP Address: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.ipsAgr.getValue() && this.findPatterns(FilterPatterns.IP_ADDR_AGR, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] IP Aggressive: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.tradeChat.getValue() && this.findPatterns(FilterPatterns.TRADE_CHAT, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Trade Chat: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.numberSuffix.getValue() && this.findPatterns(FilterPatterns.NUMBER_SUFFIX, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Number Suffix: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.announcers.getValue() && this.findPatterns(FilterPatterns.ANNOUNCER, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Announcer: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.spammers.getValue() && this.findPatterns(FilterPatterns.SPAMMER, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Spammers: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.insulter.getValue() && this.findPatterns(FilterPatterns.INSULTER, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Insulter: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.greeters.getValue() && this.findPatterns(FilterPatterns.GREETER, message)) {
            if (this.showBlocked.getValue()) {
                MessageBus.sendMessage("[AntiSpam] Greeter: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
            }
            return true;
        }
        if (this.duplicates.getValue()) {
            if (this.messageHistory == null) {
                this.messageHistory = new ConcurrentHashMap<String, Long>();
            }
            final boolean isDuplicate = this.messageHistory.containsKey(message) && (System.currentTimeMillis() - this.messageHistory.get(message)) / 1000L < this.duplicatesTimeout.getValue();
            this.messageHistory.put(message, System.currentTimeMillis());
            if (isDuplicate) {
                if (this.showBlocked.getValue()) {
                    MessageBus.sendMessage("[AntiSpam] Duplicate: " + message, Notification.Type.INFO, "AntiSpam", 13, false);
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean findPatterns(final String[] patterns, final String string) {
        for (final String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(string).find()) {
                return true;
            }
        }
        return false;
    }
    
    private static class FilterPatterns
    {
        private static final String[] ANNOUNCER;
        private static final String[] SPAMMER;
        private static final String[] INSULTER;
        private static final String[] GREETER;
        private static final String[] DISCORD;
        private static final String[] NUMBER_SUFFIX;
        private static final String[] GREEN_TEXT;
        private static final String[] TRADE_CHAT;
        private static final String[] WEB_LINK;
        private static final String[] IP_ADDR;
        private static final String[] IP_ADDR_AGR;
        
        static {
            ANNOUNCER = new String[] { "I just walked .+ feet!", "I just placed a .+!", "I just attacked .+ with a .+!", "I just dropped a .+!", "I just opened chat!", "I just opened my console!", "I just opened my GUI!", "I just went into full screen mode!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just took a screen shot!", "I just swaped hands!", "I just ducked!", "I just changed perspectives!", "I just jumped!", "I just ate a .+!", "I just crafted .+ .+!", "I just picked up a .+!", "I just smelted .+ .+!", "I just respawned!", "I just attacked .+ with my hands", "I just broke a .+!", "I recently walked .+ blocks", "I just droped a .+ called, .+!", "I just placed a block called, .+!", "Im currently breaking a block called, .+!", "I just broke a block called, .+!", "I just opened chat!", "I just opened chat and typed a slash!", "I just paused my game!", "I just opened my inventory!", "I just looked at the player list!", "I just changed perspectives, now im in .+!", "I just crouched!", "I just jumped!", "I just attacked a entity called, .+ with a .+", "Im currently eatting a peice of food called, .+!", "Im currently using a item called, .+!", "I just toggled full screen mode!", "I just took a screen shot!", "I just swaped hands and now theres a .+ in my main hand and a .+ in my off hand!", "I just used pick block on a block called, .+!", "Ra just completed his blazing ark", "Its a new day yes it is", "I just placed .+ thanks to (http:\\/\\/)?DotGod\\.CC!", "I just flew .+ meters like a butterfly thanks to (http:\\/\\/)?DotGod\\.CC!" };
            SPAMMER = new String[] { "WWE Client's spammer", "Lol get gud", "Future client is bad", "WWE > Future", "WWE > Impact", "Default Message", "IKnowImEZ is a god", "THEREALWWEFAN231 is a god", "WWE Client made by IKnowImEZ/THEREALWWEFAN231", "WWE Client was the first public client to have Path Finder/New Chunks", "WWE Client was the first public client to have color signs", "WWE Client was the first client to have Teleport Finder", "WWE Client was the first client to have Tunneller & Tunneller Back Fill" };
            INSULTER = new String[] { ".+ Download WWE utility mod, Its free!", ".+ 4b4t is da best mintscreft serber", ".+ dont abouse", ".+ you cuck", ".+ https://www.youtube.com/channel/UCJGCNPEjvsCn0FKw3zso0TA", ".+ is my step dad", ".+ again daddy!", "dont worry .+ it happens to every one", ".+ dont buy future it's crap, compared to WWE!", "What are you, fucking gay, .+?", "Did you know? .+ hates you, .+", "You are literally 10, .+", ".+ finally lost their virginity, sadly they lost it to .+... yeah, that's unfortunate.", ".+, don't be upset, it's not like anyone cares about you, fag.", ".+, see that rubbish bin over there? Get your ass in it, or I'll get .+ to whoop your ass.", ".+, may I borrow that dirt block? that guy named .+ needs it...", "Yo, .+, btfo you virgin", "Hey .+ want to play some High School RP with me and .+?", ".+ is an Archon player. Why is he on here? Fucking factions player.", "Did you know? .+ just joined The Vortex Coalition!", ".+ has successfully conducted the cactus dupe and duped a itemhand!", ".+, are you even human? You act like my dog, holy shit.", ".+, you were never loved by your family.", "Come on .+, you hurt .+'s feelings. You meany.", "Stop trying to meme .+, you can't do that. kek", ".+, .+ is gay. Don't go near him.", "Whoa .+ didn't mean to offend you, .+.", ".+ im not pvping .+, im WWE'ing .+.", "Did you know? .+ just joined The Vortex Coalition!", ".+, are you even human? You act like my dog, holy shit." };
            GREETER = new String[] { "Bye, Bye .+", "Farwell, .+" };
            DISCORD = new String[] { "discord.gg", "discordapp.com", "discord.io", "invite.gg" };
            NUMBER_SUFFIX = new String[] { ".+\\d{3,}$" };
            GREEN_TEXT = new String[] { "^<.+> >" };
            TRADE_CHAT = new String[] { "buy", "sell" };
            WEB_LINK = new String[] { "http:\\/\\/", "https:\\/\\/", "www." };
            IP_ADDR = new String[] { "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}\\b", "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "^(?:http(?:s)?:\\/\\/)?(?:[^\\.]+\\.)?.*\\..*\\..*$", ".*\\..*\\:\\d{1,5}$" };
            IP_ADDR_AGR = new String[] { ".*\\..*$" };
        }
    }
}
