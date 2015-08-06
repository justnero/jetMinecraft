package ru.justnero.minecraft.forge.jet.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import ru.justnero.minecraft.forge.jet.client.gui.IIngameRender;
import ru.justnero.minecraft.forge.jet.protect.Protect;
import ru.justnero.minecraft.forge.jet.protect.ProtectManager;

public class IngameRenderer implements IIngameRender {

	private static final String colorChar = String.valueOf((char) 167);

	@Override
	public void draw(Gui gui, FontRenderer fontrenderer, int width, int height) {
            ProtectManager pm = ProtectManager.instance();
            Protect currRegion = pm.getPlayerPositionCollision();
            if(currRegion == null)
                currRegion = pm.getPlayerLookCollision(false);
            if(currRegion != null) {
                String color = colorChar+(pm.playerCanBuildOnView() ? "2" : "4");
                String currRegName = new StringBuilder().append(colorChar).append("f[ ").append(color).append(currRegion.name()).append(" ").append(colorChar).append("f]").toString();
                gui.drawString(fontrenderer,currRegName,width/2-fontrenderer.getStringWidth(currRegName)/2,2,0);
            }
	}

}
