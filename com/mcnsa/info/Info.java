package com.mcnsa.info;

import java.util.LinkedList;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.Configuration;

import com.mcnsa.info.breadcrumbs.Breadcrumbs;
import com.mcnsa.info.hooks.HookEntity;
import com.mcnsa.info.huds.HUDs;
import com.mcnsa.info.huds.TextHUD;
import com.mcnsa.info.huds.VariableManager;
import com.mcnsa.info.interfaces.HUD;
import com.mcnsa.info.interfaces.HUD.Alignment;
import com.mcnsa.info.proxy.CommonProxy;
import com.mcnsa.info.spawncheck.SpawnChecker;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid="mod_Info", name="Info", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Info {
	@Instance("mod_Info")
	public static Info instance;
	
	// our breadcrumbs
	Breadcrumbs breadcrumbs = new Breadcrumbs();
	
	// and spawn checker
	//SpawnChecker spawnChecker = new SpawnChecker();
	
	// and HUDs
	//static HUDs huds = new HUDs();
    
    // where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.mcnsa.info.proxy.ClientProxy", serverSide="com.mcnsa.info.proxy.CommonProxy")
    public static CommonProxy proxy;
	
	public Info() {
		// store our instance
		instance = this;
	}
	
    // do configuration loading in here
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// load our configuration file
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        
        // load our values into memory
        config.load();
        
        // load properties
        breadcrumbs.loadConfiguration(config);
        //spawnChecker.loadConfiguration(config);
        //huds.load(FMLClientHandler.instance().getClient());
        
        // and save it!
        config.save();
	}
	
	// do initialization here
	@Init
	public void load(FMLInitializationEvent event) {
		// start our proxy
		proxy.initialize();
		
		// register our entity render hook
		EntityRegistry.registerModEntity(HookEntity.class, "infoHookEntity", 1, this, 9999999, 3, false);
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public static void onRenderTick() {
		//huds.onRenderTick();
	}
}
