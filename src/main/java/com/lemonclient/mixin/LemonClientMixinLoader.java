// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin;

import java.util.Map;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.launch.MixinBootstrap;
import com.lemonclient.client.LemonClient;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Lemon Client")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class LemonClientMixinLoader implements IFMLLoadingPlugin
{
    public LemonClientMixinLoader() {
        LemonClient.LOGGER.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.lemonclient.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        LemonClient.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    @Nullable
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        final boolean isObfuscatedEnvironment = (boolean) data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
