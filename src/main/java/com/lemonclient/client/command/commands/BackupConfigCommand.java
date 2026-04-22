// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.command.commands;

import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.ZipUtils;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.lemonclient.client.command.Command;

@Command.Declaration(name = "BackupConfig", syntax = "backupconfig", alias = { "backupconfig" })
public class BackupConfigCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message, final boolean none) {
        final String filename = "lemonclient-cofig-backup-v0.0.9-" + new SimpleDateFormat("yyyyMMdd.HHmmss.SSS").format(new Date()) + ".zip";
        ZipUtils.zip(new File("LemonClient/"), new File(filename));
        MessageBus.sendCommandMessage("Config successfully saved in " + filename + "!", true);
    }
}
