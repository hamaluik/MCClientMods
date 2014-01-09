package com.mcnsa.worldeditui.event;

import com.mcnsa.worldeditui.WorldEditUI;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class CylinderEvent extends UIEvent {
	public static String identifier = "cyl";
	@Override
	public void handle(String[] args) {
		//FMLLog.info("cylinder event");
		FMLClientHandler.instance().getClient().thePlayer.addChatMessage("\u00a7cNOTE: WorldEditUI doesn't currently handle cylinder selections!");
		
		// clear their selection
		WorldEditUI.getSelection().clearPoints();
	}
}
