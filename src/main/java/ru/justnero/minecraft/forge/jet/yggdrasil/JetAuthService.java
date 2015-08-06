package ru.justnero.minecraft.forge.jet.yggdrasil;

import java.net.Proxy;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.minecraft.MinecraftSessionService;

public class JetAuthService extends YggdrasilAuthenticationService {

    public JetAuthService(Proxy proxy, String str) {
        super(proxy,str);
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new JetSessionService();
    }

}
