package com.lemonclient.mixin;

import com.lemonclient.client.LemonClient;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.Name(value="Lemon Client")
@IFMLLoadingPlugin.MCVersion(value="1.12.2")
public class LemonClientMixinLoader
implements IFMLLoadingPlugin {
    private static final String MIXIN_CONFIG = "mixins.gamesense.json";

    public LemonClientMixinLoader() {
        LemonClient.LOGGER.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration(MIXIN_CONFIG);
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

    public void injectData(Map<String, Object> data) {
        boolean isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
