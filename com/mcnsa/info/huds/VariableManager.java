package com.mcnsa.info.huds;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import cpw.mods.fml.client.FMLClientHandler;

public class VariableManager {
	private static Minecraft mc;
	public VariableManager(Minecraft mc) {
		this.mc = mc;
	}
	
	public static String replaceVariables(String text) {
		try { text = replaceVariable(text, "mctime", formatMinecraftTime(minecraftTime())); } catch(Exception e) { text = replaceVariable(text, "mctime", "null"); }
		try { text = replaceVariable(text, "realtime", formatRealTime(realTime())); } catch(Exception e) { text = replaceVariable(text, "realtime", "null"); }
		try { text = replaceVariable(text, "blockx", positionBlockX()); } catch(Exception e) { text = replaceVariable(text, "blockx", "null"); }
		try { text = replaceVariable(text, "blocky", positionBlockY()); } catch(Exception e) { text = replaceVariable(text, "blocky", "null"); }
		try { text = replaceVariable(text, "blockz", positionBlockZ()); } catch(Exception e) { text = replaceVariable(text, "blockz", "null"); }
		try { text = replaceVariable(text, "light", lightLevel()); } catch(Exception e) { text = replaceVariable(text, "light", "null"); }
		try { text = replaceVariable(text, "fps", fps()); } catch(Exception e) { text = replaceVariable(text, "fps", "null"); }
		try { text = replaceVariable(text, "direction", direction()); } catch(Exception e) { text = replaceVariable(text, "direction", "null"); }
		try { text = replaceVariable(text, "biome", biome()); } catch(Exception e) { text = replaceVariable(text, "biome", "null"); }
		try { text = replaceVariable(text, "xplevel", xpLevel()); } catch(Exception e) { text = replaceVariable(text, "xplevel", "null"); }
		try { text = replaceVariable(text, "xpthislevel", xpThisLevel()); } catch(Exception e) { text = replaceVariable(text, "xpthislevel", "null"); }
		try { text = replaceVariable(text, "xpuntilnext", xpUntilNext()); } catch(Exception e) { text = replaceVariable(text, "xpuntilnext", "null"); }
		try { text = replaceVariable(text, "xpcap", xpCap()); } catch(Exception e) { text = replaceVariable(text, "xpcap", "null"); }
		try { text = replaceVariable(text, "entitiesrendered", entitiesRendered()); } catch(Exception e) { text = replaceVariable(text, "entitiesrendered", "null"); }
		try { text = replaceVariable(text, "entitiestotal", entitiesTotal()); } catch(Exception e) { text = replaceVariable(text, "entitiesrendered", "null"); }
		try { text = replaceVariable(text, "username", username()); } catch(Exception e) { text = replaceVariable(text, "username", "null"); }
		try { text = replaceVariable(text, "texturepack", texturePack()); } catch(Exception e) { text = replaceVariable(text, "texturepack", "null"); }
		
		return text;
	}
	
	private static String replaceVariable(String text, String variable, Object value) {
		return replaceVariable(text, variable, String.valueOf(value));
	}
	
	private static String replaceVariable(String text, String variable, String value) {
		text = text.replaceAll("\\{" + variable + "\\}", value);
		return text;
	}
	
	private static String formatMinecraftTime(long time) {
		// get the actual hours and minutes
		int hours = (int) Math.floor(time / 1000);
		int minutes = (int) ((time % 1000) / 1000.0 * 60);

		// and format the string
		return String.format("%02d:%02d", hours, minutes);
	}
	
	private static long minecraftTime() {
		// grab the world time
		long time = mc.theWorld.getWorldTime();
		
		// adjust the time based on dawn offset
		time += 8000;

		// clamp it to within 24 hours
		while(time > 24000) {
			time -= 24000;
		}
		while(time < 0) {
			time += 24000;
		}
		
		return time;
	}
	
	private static String formatRealTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		return format.format(new Date(time));
	}
	
	private static long realTime() {
		return System.currentTimeMillis();
	}
	
	private static int positionBlockX() {
		return (int)Math.floor(mc.thePlayer.posX);
	}
	
	private static int positionBlockY() {
		return (int)Math.floor(mc.thePlayer.boundingBox.minY);
	}
	
	private static int positionBlockZ() {
		return (int)Math.floor(mc.thePlayer.posZ);
	}
	
	private static int lightLevel() {
		int posX = positionBlockX();
		int posY = positionBlockY();
		int posZ = positionBlockZ();
		return mc.theWorld.getChunkFromBlockCoords(posX, posZ)
			.getBlockLightValue(posX & 15, posY, posZ & 15, mc.theWorld.calculateSkylightSubtracted(1.0F));
	}
	
	private static int lightLevelNoSun() {
		int posX = positionBlockX();
		int posY = positionBlockY();
		int posZ = positionBlockZ();
		return mc.theWorld.getChunkFromBlockCoords(posX, posZ)
			.getSavedLightValue(EnumSkyBlock.Block, posX & 15, posY, posZ & 15);
	}
	
	private static String fps() {
		return mc.debug.substring(0, mc.debug.indexOf(" fps"));
	}

	private static String[] compass = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
	private static String direction() {
		int direction = MathHelper.floor_double((double)((mc.thePlayer.rotationYaw * 8F) / 360F) + 0.5D) & 7;
		return compass[direction];
	}
	
	private static String biome() {
		return mc.theWorld.getBiomeGenForCoords(positionBlockX(), positionBlockZ()).biomeName;
	}
	
	private static int xpLevel() {
		return mc.thePlayer.experienceLevel;
	}
	
	private static int xpThisLevel() {
		return (int)Math.ceil((double)(mc.thePlayer.experience * (float)mc.thePlayer.xpBarCap()));
	}
	
	private static int xpUntilNext() {
		return (int)Math.ceil((int)Math.floor((1.0 - (double) mc.thePlayer.experience) * (double)mc.thePlayer.xpBarCap()));
	}
	
	private static int xpCap() {
		return mc.thePlayer.xpBarCap();
	}
	
	private static String entitiesRendered() {
		String debugStr = mc.getEntityDebug();
		return debugStr.substring(debugStr.indexOf(32) + 1, debugStr.indexOf(47));
	}
	
	private static String entitiesTotal() {
		String debugStr = mc.getEntityDebug();
		return debugStr.substring(debugStr.indexOf(47) + 1, debugStr.indexOf(46));
	}
	
	private static String username() {
		return mc.session.username;
	}
	
	private static String texturePack() {
		return mc.texturePackList.getSelectedTexturePack().getTexturePackFileName();
	}
}
