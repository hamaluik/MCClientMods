package com.mcnsa.chatbubbles.client;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.mcnsa.chatbubbles.CommonProxy;
import com.mcnsa.chatbubbles.CBRenderPlayer;
import com.mcnsa.chatbubbles.ChatBubbles;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void initialize() {
		// create a player renderer and set it's render manager
		CBRenderPlayer renderPlayer = new CBRenderPlayer();
		renderPlayer.setRenderManager(RenderManager.instance);
		
		// re-map our player entity renderer
		RenderManager.instance.entityRenderMap.put(EntityPlayer.class, renderPlayer);
		
		// register our render tick handler
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		
		// create a timer that automatically updates the player colours every 10 seconds
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ChatBubbles.loadPlayerPrefixes();
			}
		}, 0, 10000);
		
		// and register our events
		MinecraftForge.EVENT_BUS.register(this);
	}
}
