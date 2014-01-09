package com.mcnsa.worldeditui.event;

import com.mcnsa.worldeditui.WorldEditUI;

import cpw.mods.fml.common.FMLLog;

public class PointEvent extends UIEvent {
	public static String identifier = "p";
	@Override
	public void handle(String[] args) {
		FMLLog.info("point event");
		
		// get parameters
		int id = getInt(args[0]);
		int x = getInt(args[1]);
		int y = getInt(args[2]);
		int z = getInt(args[3]);
		
		// and change the selection
		WorldEditUI.getSelection().setPoint(id, x, y, z);
	}
}
