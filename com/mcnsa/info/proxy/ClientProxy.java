package com.mcnsa.info.proxy;

import com.mcnsa.info.hooks.HookEntity;
import com.mcnsa.info.hooks.HookRenderEntity;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void initialize() {
		// register our render tick handler
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		
		// register our render hook
		RenderingRegistry.registerEntityRenderingHandler(HookEntity.class, new HookRenderEntity());
	}
}
