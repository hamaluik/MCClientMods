package com.mcnsa.chatbubbles.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
	public static ArrayList<String> messages = new ArrayList<String>();
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// determine what kind of tick we're dealing with
		if (type.equals(EnumSet.of(TickType.RENDER))) {
			onRenderTick();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return null;
	}
	
	void onRenderTick() {
		for(int i = 0; i < messages.size(); i++) {
			FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow(messages.get(i), 2, 2 + (10 * i), 0xffffffff);
		}
	}
}
