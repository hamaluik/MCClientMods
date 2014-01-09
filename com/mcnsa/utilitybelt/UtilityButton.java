package com.mcnsa.utilitybelt;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.client.FMLClientHandler;

public class UtilityButton {
	public String display = "herp";
	public String macro = "/home";
	public Boolean interpretDisplayAsImagePath = false;
	
	public double theta = 0f;
	
	public UtilityButton(String display, String macro) {
		this.display = display.endsWith(".png") ? display : GraphicUtils.processColours(display);
		this.macro = macro;
		this.interpretDisplayAsImagePath = display.endsWith(".png");
	}
	
	void render(double radius, Boolean highlighted) {
		// figure out where to go
		int[] scSize = GraphicUtils.getScreenSize();
		int[] pos = {(scSize[0] / 2) + (int)(radius * (Math.cos(theta))),
					 (scSize[1] / 2) + (int)(radius * (Math.sin(theta)))};
		
		// now draw our image
		// or possibly just a text string
		if(interpretDisplayAsImagePath) {
			
		}
		else {
			// center our text string
			pos[0] -= (FMLClientHandler.instance().getClient().fontRenderer.getStringWidth(display) / 2);
			pos[1] -= (FMLClientHandler.instance().getClient().fontRenderer.FONT_HEIGHT / 2);
			
			// and draw the string
			FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow(display, pos[0], pos[1], highlighted? 0xffffffff : 0x7f7f7f7f);
		}
	}
}
