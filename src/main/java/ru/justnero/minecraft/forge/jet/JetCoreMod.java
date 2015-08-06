package ru.justnero.minecraft.forge.jet;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import ru.justnero.minecraft.forge.jet.asm.*;

@Name("jetMinecraft Core")
@MCVersion("1.7.10")
@TransformerExclusions("ru.justnero.minecraft.forge.jet")
public class JetCoreMod implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        ClassTransformer.put("net.minecraft.client.Minecraft",new Minecraft());
        
        if(!Jet.isServer()) {
            ClassTransformer.put("net.minecraft.server.MinecraftServer",new MinecraftServer());
            ClassTransformer.put("net.minecraft.client.network.NetHandlerLoginClient",new NetHandlerLoginClient());
        }
        
        return new String[] {
            ClassTransformer.class.getName(),
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
