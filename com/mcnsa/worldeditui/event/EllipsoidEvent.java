package com.mcnsa.worldeditui.event;

import com.mcnsa.worldeditui.WorldEditUI;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class EllipsoidEvent extends UIEvent {
	public static String identifier = "e";
	@Override
	public void handle(String[] args) {
		//FMLLog.info("ellipsoid event");
		FMLClientHandler.instance().getClient().thePlayer.addChatMessage("\u00a7cNOTE: WorldEditUI doesn't currently handle ellipsoid selections!");
		
		// clear their selection
		WorldEditUI.getSelection().clearPoints();
	}
}
