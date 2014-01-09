package com.mcnsa.chatbubbles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import com.mcnsa.chatbubbles.client.ClientTickHandler;

import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid="mod_ChatBubbles", name="Chat Bubbles", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class ChatBubbles {
	// properties that can be set in the configuration
	public static float NAME_TAG_RANGE = 128.0f;
	public static float TEXT_SCALE = 1.0f;
	public static int MAX_NUM_CHAT_LINES = 10;
	public static long CHAT_LIFETIME = 10;
	public static int MAX_WIDTH = 50;
	public static String CHAT_FORMAT = ".*<.+>(.*):\\s*(.*)";
	
	// states
	private static HashMap<String, String> playerPrefixes = new HashMap<String, String>();
	private static HashMap<String, LinkedList<ChatItem>> playerChatQueues = new HashMap<String, LinkedList<ChatItem>>();
	
	@Instance("mod_ChatBubbles")
	public static ChatBubbles instance;
    
    // where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.mcnsa.chatbubbles.client.ClientProxy", serverSide="com.mcnsa.chatbubbles.CommonProxy")
    public static CommonProxy proxy;
	
    // do configuration loading in here
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// load our configuration file
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        
        // load our values into memory
        config.load();
        
        // now get the actual parameters
        NAME_TAG_RANGE = (float)config.get(Configuration.CATEGORY_GENERAL, "name-tag-range", (double)NAME_TAG_RANGE, "How far away name plates and chat boxes will be rendered from (default = 128)").getDouble((double)NAME_TAG_RANGE);
        TEXT_SCALE = (float)config.get(Configuration.CATEGORY_GENERAL, "text-scale", (double)TEXT_SCALE, "Use this to make text smaller or bigger (default = 1)").getDouble((double)TEXT_SCALE);
        MAX_NUM_CHAT_LINES = config.get(Configuration.CATEGORY_GENERAL, "max-num-chat-lines", MAX_NUM_CHAT_LINES, "Maximum number of simultanenous lines of chat to view at once (default = 10)").getInt(MAX_NUM_CHAT_LINES);
        CHAT_LIFETIME = config.get(Configuration.CATEGORY_GENERAL, "chat-lifetime", CHAT_LIFETIME, "How long in seconds individual chat messages stay above someone's head (default = 10)").getInt((int)CHAT_LIFETIME);
        MAX_WIDTH = config.get(Configuration.CATEGORY_GENERAL, "max-width", MAX_WIDTH, "Maxmimum width, in characters of the chat boxes").getInt(MAX_WIDTH);
        //CHAT_FORMAT = config.get(Configuration.CATEGORY_GENERAL, "chat-format", CHAT_FORMAT).value;
        Property chatFormat = config.get(Configuration.CATEGORY_GENERAL, "chat-format", CHAT_FORMAT);
        chatFormat.comment = "A regular expression string capable of singling out usernames and chat strings in chat. Java group(1) should be the username, Java group(2) should be the chat line. Default = \"" + CHAT_FORMAT + "\"";
        CHAT_FORMAT = chatFormat.getString();
        
        // and save it!
        config.save();
	}
	
	// do initialization here
	@Init
	public void load(FMLInitializationEvent event) {
		// initialize our proxy
		proxy.initialize();
		
		// register our chat listener
		NetworkRegistry.instance().registerChatListener(proxy);
		
		// and set up a timer to purge old chats
		Timer purgeTimer = new Timer();
		purgeTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ChatBubbles.purgeOldChats();
			}
		}, 1000, 1000); // once every second, after a second
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public static void loadPlayerPrefixes() {
		try {
			// skip if we don't have a player yet
			if(FMLClientHandler.instance().getClient().thePlayer == null) {
				return;
			}
			
	        // get our player rank colour
	        NetClientHandler netClientHandler = FMLClientHandler.instance().getClient().thePlayer.sendQueue;
	        List playerInfoList = netClientHandler.playerInfoList;
	        
	        // clear our current prefixes
	        playerPrefixes.clear();
	        
	        // loop over all our players
	        for (int i = 0; i < playerInfoList.size(); i++) {
	            GuiPlayerInfo playerInfo = (GuiPlayerInfo)playerInfoList.get(i);
	            
	            // check to see if the player has a prefix
	            if(playerInfo.name.startsWith("\u00A7")) {
	            	// store the prefix
	            	playerPrefixes.put(playerInfo.name.substring(2), playerInfo.name.substring(0, 2));
	            }
	        }
		}
		catch(Exception e) {
			FMLLog.log(Level.WARNING, e, "[ChatBubbles] Failed to load player prefixes: %s", e.getMessage());
		}
	}
	
	public static void setPlayerPrefix(String playerName, String prefix) {
		playerPrefixes.put(playerName, prefix);
	}
	
	public static String getPlayerPrefix(String playerName) {
		if(!playerPrefixes.containsKey(playerName)) {
			// ok, we don't have the exact key
			// see if we have a partial key
			for(String key: playerPrefixes.keySet()) {
				if(playerName.startsWith(key)) {
					// we matched the start of the name with this key
					// it's most likely the case that the name simply got truncated
					// return this one!
					return playerPrefixes.get(key);
				}
			}
			// couldn't find it - no prefix for you!
			return "\u00A7f";
		}
		return playerPrefixes.get(playerName);
	}
	
	public static void enqueueChat(String username, String chat) {
		// get that player's queue
		LinkedList<ChatItem> queue = playerChatQueues.get(username);
		if(queue == null) {
			queue = new LinkedList();
		}
		
		// add the message to the queue!
		queue.add(new ChatItem(chat, CHAT_LIFETIME * 1000));
		
		// pop off anything off the end
		if(queue.size() > MAX_NUM_CHAT_LINES) {
			queue.remove();
		}
		
		// and set the player's queue!
		playerChatQueues.put(username, queue);
	}
	
	public static void purgeOldChats() {
		long currentTime = System.currentTimeMillis();
		
		for(String username: playerChatQueues.keySet()) {
			// get the queue
			LinkedList<ChatItem> queue = playerChatQueues.get(username);
			
			// continue to loop while we have old elements at the head
			while(queue.size() > 0 && currentTime >= queue.peek().timeoutTime) {
				queue.remove();
			}
		}
	}
	
	public static LinkedList<ChatItem> getChats(String username) {
		return playerChatQueues.get(username);
	}
}
