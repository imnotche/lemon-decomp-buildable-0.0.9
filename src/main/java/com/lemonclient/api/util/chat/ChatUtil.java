// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import net.minecraft.client.gui.GuiNewChat;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class ChatUtil extends SubscriberImpl
{
    public static Minecraft mc;
    private static final Map<Integer, Map<String, Integer>> message_ids;
    private static final SkippingCounter counter;
    
    public void clear() {
        if (ChatUtil.mc.ingameGUI != null) {
            ChatUtil.message_ids.values().forEach(m -> m.values().forEach(id -> ChatUtil.mc.ingameGUI.getChatGUI().deleteChatLine(id)));
        }
        ChatUtil.message_ids.clear();
        ChatUtil.counter.reset();
    }
    
    public static void sendMessage(final String message) {
        sendMessage(message, 0);
    }
    
    public static void sendClientMessage(final String append, final String modulename) {
        sendDeleteMessage(append, modulename, 1000);
    }
    
    public static void sendMessage(final String message, final int id) {
        sendComponent(new TextComponentString((message == null) ? "null" : message), id);
    }
    
    public static void sendComponent(final ITextComponent component) {
        sendComponent(component, 0);
    }
    
    public static void sendComponent(final ITextComponent c, final int id) {
        applyIfPresent(g -> g.printChatMessageWithOptionalDeletion(c, id));
    }
    
    public void sendDeleteMessageScheduled(final String message, final String uniqueWord, final int senderID) {
        final Integer id = ChatUtil.message_ids.computeIfAbsent(Integer.valueOf(senderID), v -> new ConcurrentHashMap<String, Integer>()).computeIfAbsent(uniqueWord, v -> ChatUtil.counter.next());
        ChatUtil.mc.addScheduledTask(() -> sendMessage(message, id));
    }
    
    public static void sendDeleteMessage(final String message, final String uniqueWord, final int senderID) {
        final Integer id = ChatUtil.message_ids.computeIfAbsent(Integer.valueOf(senderID), v -> new ConcurrentHashMap<String, Integer>()).computeIfAbsent(uniqueWord, v -> ChatUtil.counter.next());
        sendMessage(message, id);
    }
    
    public void deleteMessage(final String uniqueWord, final int senderID) {
        final Map<String, Integer> map = ChatUtil.message_ids.get(senderID);
        if (map != null) {
            final Integer id = map.remove(uniqueWord);
            if (id != null) {
                deleteMessage(id);
            }
        }
    }
    
    public static void deleteMessage(final int id) {
        applyIfPresent(g -> g.deleteChatLine(id));
    }
    
    public static void applyIfPresent(final Consumer<GuiNewChat> consumer) {
        final GuiNewChat chat = getChatGui();
        if (chat != null) {
            consumer.accept(chat);
        }
    }
    
    public static GuiNewChat getChatGui() {
        if (ChatUtil.mc.ingameGUI != null) {
            return ChatUtil.mc.ingameGUI.getChatGUI();
        }
        return null;
    }
    
    static {
        ChatUtil.mc = Minecraft.getMinecraft();
        message_ids = new ConcurrentHashMap<Integer, Map<String, Integer>>();
        counter = new SkippingCounter(1337, i -> i != -1);
    }
}
