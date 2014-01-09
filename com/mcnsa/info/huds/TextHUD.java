package com.mcnsa.info.huds;

import java.util.Collections;
import java.util.LinkedList;

import com.mcnsa.info.interfaces.HUD;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.client.FMLClientHandler;

public class TextHUD extends HUD {	
	Minecraft mc = null;
	private LinkedList<String> lines = new LinkedList<String>();
	
	public TextHUD(Minecraft mc) {
		this.mc = mc;
	}
	
	public void setContents(String contents) {
		String[] parts = contents.replaceAll("(&([a-f0-9k-or]))", "\u00a7$2").split("\n");
		lines.clear();
		Collections.addAll(lines, parts);
	}
	
	private int strippedStringWidth(String str) {
		return mc.fontRenderer.getStringWidth(str.replaceAll("(&([a-f0-9klmnor]))", "").replaceAll("(\u00A7([a-f0-9klmnor]))", ""));
	}
	
	private float screenWidth = 0;
	@Override
	protected void updateXPos(float screenWidth) {
		this.screenWidth = screenWidth;
	}
	
	protected int getXPos(String line) {
		switch(this.alignment) {
		case TOP_LEFT:
		case MID_LEFT:
		case BOT_LEFT:
			return 2;

		case TOP_CENTER:
		case MID_CENTER:
		case BOT_CENTER:
			return (int)((screenWidth - strippedStringWidth(line)) / 2.0f);
			
		case TOP_RIGHT:
		case MID_RIGHT:
		case BOT_RIGHT:
			return (int)(screenWidth - strippedStringWidth(line) - 2f);
		}
		
		return 0;
	}

	@Override
	public void render() {
		float lineY = yPos;
		for(int i = 0; i < lines.size(); i++) {
			String line = VariableManager.replaceVariables(lines.get(i).trim());
			mc.fontRenderer.drawStringWithShadow(line, getXPos(line), (int)lineY, 0xffffffff);
			lineY += 2 + mc.fontRenderer.FONT_HEIGHT;
		}
	}

	@Override
	protected float getWidth() {
		float maxWidth = 0;
		
		for(String line: lines) {
			float lineWidth = mc.fontRenderer.getStringWidth(line);
			if(lineWidth > maxWidth) {
				maxWidth = lineWidth;
			}
		}
		
		return maxWidth;
	}

	@Override
	protected float getHeight() {
		return mc.fontRenderer.FONT_HEIGHT * lines.size() + (2 * (lines.size() - 1));
	}
}
