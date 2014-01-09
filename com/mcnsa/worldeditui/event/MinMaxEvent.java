package com.mcnsa.worldeditui.event;

import com.mcnsa.worldeditui.WorldEditUI;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class MinMaxEvent extends UIEvent {
	public static String identifier = "mm";
	@Override
	public void handle(String[] args) {
		//FMLLog.info("minmax event");
		FMLClientHandler.instance().getClient().thePlayer.addChatMessage("\u00a7cNOTE: WorldEditUI doesn't currently handle polygon selections!");
		
		// clear their selection
		WorldEditUI.getSelection().clearPoints();
	}
}
