// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import java.awt.datatransfer.Clipboard;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Copy IP", category = Category.Misc)
public class CopyIp extends Module
{
    String server;
    
    public void onEnable() {
        try {
            this.server = CopyIp.mc.getCurrentServerData().serverIP;
        }
        catch (final Exception e) {
            this.server = "Singleplayer";
        }
        final String myString = this.server;
        final StringSelection stringSelection = new StringSelection(myString);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        MessageBus.sendClientPrefixMessage("Copied '" + this.server + "' to clipboard.", Notification.Type.INFO);
        this.disable();
    }
}
