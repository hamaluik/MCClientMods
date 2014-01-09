package com.mcnsa.capes;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
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

@Mod(modid="mod_MCNSACapes", name="MCNSA Capes", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Capes {
	@Instance("mod_MCNSACapes")
	public static Capes instance;
	
	// options
	public static long UPDATE_RATE = 1000l;
	public static String CAPE_URL = "http://www.mcnsa.com/capes/{player}.png";
	public static String CAPE_URL_PREFIX = "http://www.mcnsa.com/capes/";
    
    // where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.mcnsa.capes.client.ClientProxy", serverSide="com.mcnsa.capes.CommonProxy")
    public static CommonProxy proxy;
	
	public Capes() {
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
        
        // load our actual settings
        UPDATE_RATE = config.get(Configuration.CATEGORY_GENERAL, "update-rate", UPDATE_RATE, "Rate at which capes will get updated (in ms) (default: 1000)").getInt((int)UPDATE_RATE);
        Property urlFormat = config.get(Configuration.CATEGORY_GENERAL, "chat-format", CAPE_URL);
        urlFormat.comment = "Where the cape server is located. Default = \"" + CAPE_URL + "\"";
        CAPE_URL = urlFormat.getString();
        
        // parse our prefix
        String[] parts = CAPE_URL.split("\\{player\\}", 2);
        if(parts.length < 2) {
        	FMLLog.warning("[MCNSACapes] Failed to parse cape url appropriately!");
        }
        else if(parts.length >= 1) {
        	CAPE_URL_PREFIX = parts[0];
        }
        
        // and save it!
        config.save();
	}
	
	// do initialization here
	@Init
	public void load(FMLInitializationEvent event) {
		proxy.initialize();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
}
