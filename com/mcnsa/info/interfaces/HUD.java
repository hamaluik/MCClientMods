package com.mcnsa.info.interfaces;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class HUD {
	public static enum Alignment {
		TOP_LEFT, TOP_CENTER, TOP_RIGHT,
		MID_LEFT, MID_CENTER, MID_RIGHT,
		BOT_LEFT, BOT_CENTER, BOT_RIGHT
	};

	public boolean inGame = true;
	public boolean inMenus = false;
	protected Alignment alignment = Alignment.TOP_LEFT;
	
	public Alignment setAlignment(Alignment alignment) {
		return (this.alignment = alignment);
	}
	
	public abstract void render();
	
	protected abstract float getWidth();
	protected abstract float getHeight();
	
	protected float xPos = 0, yPos = 0;
	protected void updateXPos(float screenWidth) {
		switch(this.alignment) {
		case TOP_LEFT:
		case MID_LEFT:
		case BOT_LEFT:
			xPos = 2;
			break;

		case TOP_CENTER:
		case MID_CENTER:
		case BOT_CENTER:
			xPos = ((float)screenWidth - getWidth()) / 2.0f;
			break;
			
		case TOP_RIGHT:
		case MID_RIGHT:
		case BOT_RIGHT:
			xPos = (float)screenWidth - getWidth() - 2f;
			break;
		}
	}
	protected void updateYPos(float screenHeight) {
		switch(this.alignment) {
		case TOP_LEFT:
		case TOP_CENTER:
		case TOP_RIGHT:
			yPos = 2;
			break;
			
		case MID_LEFT:
		case MID_CENTER:
		case MID_RIGHT:
			yPos = (screenHeight - getHeight()) / 2.0f;
			break;
			
		case BOT_LEFT:
		case BOT_CENTER:
		case BOT_RIGHT:
			yPos = screenHeight - getHeight() - 2f;
			break;
		}
	}
	
	public void updatePosition() {
		// get the screen size
		Minecraft mc = FMLClientHandler.instance().getClient();
		ScaledResolution screenSize = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int screenWidth = screenSize.getScaledWidth();
		int screenHeight = screenSize.getScaledHeight();

		updateXPos(screenWidth);
		updateYPos(screenHeight);
	}
}
