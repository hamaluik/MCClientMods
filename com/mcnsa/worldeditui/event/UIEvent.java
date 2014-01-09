package com.mcnsa.worldeditui.event;

public abstract class UIEvent {
	public static String identifier = null;
	public abstract void handle(String[] args);
	
	protected int getInt(String arg) {
		try {
			return Integer.parseInt(arg);
		}
		catch(NumberFormatException e) {
			return (int)getFloat(arg);
		}
	}
	
	protected float getFloat(String arg) {
		try {
			return (int)Float.parseFloat(arg);
		}
		catch(NumberFormatException e2) {
			return 0;
		}
	}
}
