package ru.justnero.minecraft.forge.jet.yggdrasil;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.EnumMap;
import ru.justnero.minecraft.forge.jet.Jet;
import ru.justnero.minecraft.forge.jet.client.JetServerData;

public class JetSessionService implements MinecraftSessionService {

    @Override
    public void joinServer(GameProfile gp,String sessionID,String server) throws AuthenticationException {
        String result = "Ошибка сервера авторизации";
        try {
            Minecraft mc = Minecraft.getMinecraft();
            result = Jet.joinServer(mc.getSession().getUsername(),mc.getSession().getToken(),server,((JetServerData) mc.func_147104_D()).serverID());
        } catch(IOException ex) {
            Jet.log.error("Error joining server");
            Jet.log.catching(ex);
            throw new AuthenticationUnavailableException();
        }
        if(!result.equals("OK")) {
            throw new AuthenticationException(result);
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gp,String server) throws AuthenticationUnavailableException {
    	try {
            if(Jet.checkServer(gp.getName(), server)) {
                gp = new GameProfile(UUID.nameUUIDFromBytes(gp.getName().getBytes()),gp.getName());
                return gp;
            }
    	} catch(IOException ex) {
            Jet.log.error("Error checking server");
            throw new AuthenticationUnavailableException();
        }
        return null;
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
    	Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> result = new EnumMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>(MinecraftProfileTexture.Type.class);
    	result.put(MinecraftProfileTexture.Type.SKIN,new MinecraftProfileTexture(String.format(Jet.skins(),profile.getName())));
    	//result.put(MinecraftProfileTexture.Type.CAPE,new MinecraftProfileTexture(String.format(Jet.cloaks(),profile.getName())));
    	return result;
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
    	GameProfile result = new GameProfile(UUID.nameUUIDFromBytes(profile.getName().getBytes()),profile.getName());
    	return result;
    }

}
