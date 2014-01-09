package com.mcnsa.capes.client;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

import com.mcnsa.capes.Capes;
import com.mcnsa.capes.CommonProxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	private static boolean alreadySwapped = false;
	
	@Override
	public void initialize() {
		// update the cloaks constantly
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ClientProxy.updateCloaks();
			}
		}, Capes.UPDATE_RATE, Capes.UPDATE_RATE);
	}
	
	public static void updateCloaks() {
		// get all of our players in the world
		Minecraft mc = FMLClientHandler.instance().getClient();
		// don't try to run us into the ground
		if(mc == null || mc.theWorld == null || mc.renderEngine == null) {
			return;
		}
		List<EntityPlayer> players = mc.theWorld.playerEntities;
		
		// make sure we have a valid players list
		if(players == null || players.size() == 0) {
			return;
		}
		
		// loop through them all
		for(EntityPlayer player: players) {
			// check to see if it's got a different URL than what we're using
			if(!player.cloakUrl.startsWith(Capes.CAPE_URL_PREFIX)) {
				// release their current cloak
				mc.renderEngine.releaseImageData(player.cloakUrl);
				
				// yup, it does!
				// change it!
				player.cloakUrl = Capes.CAPE_URL.replaceAll("\\{player\\}", StringUtils.stripControlCodes(player.username.toLowerCase()));
				
				// and update the cloak
				mc.renderEngine.releaseImageData(player.cloakUrl);
				mc.renderEngine.obtainImageData(player.cloakUrl, new ImageBufferDownload());
				
				FMLLog.info("[MCNSACapes] Changed %s's cloak to: %s", player.username, player.cloakUrl);
			}
		}
	}
}
