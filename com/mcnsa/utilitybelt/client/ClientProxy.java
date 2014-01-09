package com.mcnsa.utilitybelt.client;

import com.mcnsa.utilitybelt.client.ClientTickHandler;
import com.mcnsa.utilitybelt.CommonProxy;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void initialize() {
		// register our render tick handler
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
	}
}
