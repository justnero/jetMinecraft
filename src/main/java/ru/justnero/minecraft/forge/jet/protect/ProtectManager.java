package ru.justnero.minecraft.forge.jet.protect;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import ru.justnero.minecraft.forge.jet.Jet;
import ru.justnero.minecraft.forge.jet.JetMinecraft;
import ru.justnero.minecraft.forge.jet.client.ProtectEntity;

public class ProtectManager {

    private static final ProtectManager _instance = new ProtectManager();

    private final Minecraft           _mc      = FMLClientHandler.instance().getClient();
    private final Map<String,Protect> _regions = new HashMap<String,Protect>();
    private final Protect             _lock    = new Protect(
        "Locked",
        new Cuboid(
            Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,
            Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE
        ),
        -99,
        false,
        false,
        false,
        false
    );

    private long   _freezed   = 0;
    private String _lastWorld = "";

    public boolean drawFrameActivated = false;
    public long    avaitingProtects   = 0;
    public long    freezeTime         = 0;

    public Map<String, Protect> all() {
        return _regions;
    }

    public Protect get(String name) {
        if(_regions.containsKey(name)) {
          return _regions.get(name);
        }
        return null;
    }

    public void put(String name, Protect rg, boolean isUpdate) {
        _regions.put(name,rg);
        if(!isUpdate && avaitingProtects > 0) {
            avaitingProtects--;
        }
    }

    public void put(Map<String,Protect> regions, boolean isUpdate) {
        _regions.putAll(regions);
        if(!isUpdate && avaitingProtects > 0) {
            avaitingProtects -= regions.size();
        }
    }

    public void remove(String name) {
        _regions.remove(name);
    }

    public void remove(ArrayList<String> names) {
        for(String name : names) {
            _regions.remove(name);
        }
    }

    public void clear() {
        _regions.clear();
        avaitingProtects = 0;
    }

    public Protect getCollision(double x, double y, double z) {
        if (_regions.isEmpty()) {
            return null;
        }
        int priority = Integer.MIN_VALUE;
        Protect collision = null;
        for(Protect rg : _regions.values()) {
            if(rg.contains(new Point3D(x,y,z))) {
                if(rg.priority() >= priority) {
                    priority = rg.priority();
                    collision = rg;
                }
            }
        }
        return collision;
    }

    public Protect getPlayerPositionCollision() {
        if(_mc.thePlayer != null) {
            return this.getCollision(
                    _mc.thePlayer.posX,
                    _mc.thePlayer.posY-1.0D,
                    _mc.thePlayer.posZ
            );
        }
        return null;
    }

    public Protect getPlayerLookCollision(boolean message) {
        if(message && avaitingProtects > 0) {
            _mc.thePlayer.addChatMessage(new ChatComponentText(I18n.format("protect.locked")));
        }
        if(avaitingProtects > 0) {
            return _lock;
        }
        if(_mc.objectMouseOver != null && _mc.objectMouseOver.entityHit == null) {
            return this.getCollision(
                    _mc.objectMouseOver.blockX,
                    _mc.objectMouseOver.blockY,
                    _mc.objectMouseOver.blockZ
            );
        }
        return null;
    }

    public boolean playerCanBuildOnPosition() {
        Protect rg = getPlayerPositionCollision();
        return rg != null ? rg.build() : true;
    }

    public boolean playerCanBuildOnView() {
        Protect rg = this.getPlayerLookCollision(false);
        return rg != null ? rg.build() : true;
    }

    public boolean playerCanUseOnView() {
        Protect rg = this.getPlayerLookCollision(false);
        return rg != null ? rg.use() : true;
    }

    public void freeze(long ms) {
        _freezed = System.currentTimeMillis() + ms;
    }

    public boolean isFreezed() {
        return System.currentTimeMillis() < _freezed;
    }

    public static ProtectManager instance() {
        return _instance;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public boolean onClientTick(ClientTickEvent event) {
        if(_mc.isSingleplayer() && !_regions.isEmpty()) {
            clear();
        } else if(_mc.theWorld != null && !_mc.theWorld.getWorldInfo().getWorldName().equals(_lastWorld)) {
            _lastWorld = _mc.theWorld.getWorldInfo().getWorldName();
            _mc.theWorld.spawnEntityInWorld(new ProtectEntity(_mc.theWorld));
        } else if(_mc.theWorld == null) {
            _lastWorld = "";
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if(JetMinecraft.proxy.keyDispay.isPressed()) {
            drawFrameActivated = !drawFrameActivated;
        }
    }

    public static boolean checkPermission(boolean leftClick) {
        ProtectManager pm = ProtectManager.instance();
        if(pm.isFreezed()) {
            return false;
        }
        MovingObjectPosition objectMouseOver = FMLClientHandler.instance().getClient().objectMouseOver;
        if(pm.getPlayerLookCollision(true) != null &&
           !(objectMouseOver  == null ||
             objectMouseOver.typeOfHit == null ||
             objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY ||
             objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS ||
            (objectMouseOver.blockX == 0 && objectMouseOver.blockY == 0 && objectMouseOver.blockZ == 0))) {
            if(leftClick) {
                if(!pm.playerCanBuildOnView()) {
                	FMLClientHandler.instance().getClient().playerController.resetBlockRemoving();

                	try {
                            Minecraft.class.getDeclaredField("field_71427_U").set(FMLClientHandler.instance(),0);
                	} catch(Exception ex) {
                            Jet.log.catching(ex);
                	}
                	return false;
                }
            } else {
                return pm.playerCanUseOnView();
            }
        }
        return true;
    }

}
