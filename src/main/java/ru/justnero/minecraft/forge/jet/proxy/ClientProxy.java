package ru.justnero.minecraft.forge.jet.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import ru.justnero.minecraft.forge.jet.client.GuiManipulator;
import ru.justnero.minecraft.forge.jet.client.gui.IIngameTrigger;
import ru.justnero.minecraft.forge.jet.client.gui.JetIngame;
import ru.justnero.minecraft.forge.jet.protect.ProtectManager;
import ru.justnero.minecraft.forge.jet.client.IngameRenderer;
import ru.justnero.minecraft.forge.jet.client.ProtectEntity;
import ru.justnero.minecraft.forge.jet.client.ProtectRender;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        initEvents();
        initRender();
        initTick();
        initKey();
        initJet();
    }

    private void initEvents() {
    	MinecraftForge.EVENT_BUS.register(new GuiManipulator());
    }

    private void initRender() {
        RenderingRegistry.registerEntityRenderingHandler(ProtectEntity.class,new ProtectRender(FMLClientHandler.instance().getClient()));
    }

    private void initTick() {
        FMLCommonHandler.instance().bus().register(ProtectManager.instance());
    }

    private void initKey() {
        keyDispay = new KeyBinding("key.jet.display",Keyboard.KEY_F12,"key.categories.jet");
        ClientRegistry.registerKeyBinding(keyDispay);
    }

    private void initJet() {
    	initDebugInfo();
    	initIngameRender();
    }

    private void initDebugInfo() {
    	JetIngame.debugInfo.add("protect.debug.protects");
    	JetIngame.debugInfo.add("protect.debug.set");
    	JetIngame.debugInfo.add("protect.debug.add");
    	JetIngame.debugInfo.add("protect.debug.delete");
    	JetIngame.debugInfo.add("protect.debug.info");
    	JetIngame.debugInfo.add("protect.debug.remove");
    	JetIngame.debugInfo.add("protect.debug.toggle");
    	List<IIngameTrigger> tmp = new ArrayList<IIngameTrigger>();
    	tmp.add( new IIngameTrigger() {
			@Override
			public String execute() {
				return GameSettings.getKeyDisplayString(keyDispay.getKeyCode());
			}
    	});
    	JetIngame.debugTriggers.put("protect.debug.toggle",tmp);
    }

    private void initIngameRender() {
    	GuiManipulator.addIngameRender(new IngameRenderer());
    }

}
