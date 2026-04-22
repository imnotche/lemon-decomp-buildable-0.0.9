// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.List;

public class CapeUtil
{
    private static final List<UUID> uuids;
    
    public static void init() {
        try {
            final URL capesList = new URL("https://raw.githubusercontent.com/OaDwH/CapeUUID/main/list.txt");
            final BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                CapeUtil.uuids.add(UUID.fromString(inputLine));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean hasCape(final UUID id) {
        return CapeUtil.uuids.contains(id);
    }
    
    static {
        uuids = new ArrayList<UUID>();
    }
}
