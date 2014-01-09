package com.mcnsa.worldeditui.event;

import com.mcnsa.worldeditui.WorldEditUI;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class SelectionEvent extends UIEvent {
	public static String identifier = "s";
	@Override
	public void handle(String[] args) {
		//FMLLog.info("selection event");
		
		// clear their selection
		WorldEditUI.getSelection().clearPoints();
		if(!args[0].equals("cuboid")) {
			FMLClientHandler.instance().getClient().thePlayer.addChatMessage("\u00a7cNOTE: WorldEditUI currently ONLY handles cuboid selections!");
		}
	}
}
