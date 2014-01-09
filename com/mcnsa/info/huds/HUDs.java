package com.mcnsa.info.huds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONValue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

import com.mcnsa.info.interfaces.HUD;
import com.mcnsa.info.interfaces.HUD.Alignment;
import com.mcnsa.utilitybelt.UtilityBelt;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class HUDs {
	// our variable manager
	VariableManager variableManager = null;
	// keep track of all our HUDs
	private static LinkedList<HUD> huds = new LinkedList<HUD>();
	
	public void load(Minecraft mc) {
		// load up our variable manager
		variableManager = new VariableManager(FMLClientHandler.instance().getClient());
		
		try {
			File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
			File cfgfile = new File(cfgdir, "Info.huds.json");
			if(!cfgfile.exists()) {
				// try to extract it!
				// make the file
				if(!cfgfile.createNewFile()) {
					// uh-oh, we can't create a new file!
					FMLLog.warning("HUDs file doesn't exist and cannot be created!");
					return;
				}
				
				// write it out
				BufferedWriter out = new BufferedWriter(new FileWriter(cfgfile));
				out.write(defaultHUDsFile);
				
				// and close up
				out.close();
			}
			
			// load the file
			String lineSep = System.getProperty("line.separator");
			FileInputStream fin = new FileInputStream(cfgfile);
			BufferedReader input = new BufferedReader(new InputStreamReader(fin));
			String nextLine = "";
			StringBuffer sb = new StringBuffer();
			while((nextLine = input.readLine()) != null) {
				sb.append(nextLine);
				sb.append(lineSep);
			}
			
			// parse it all
			HashMap<String, Object> obj = (HashMap<String, Object>)JSONValue.parse(sb.toString());
			
			// grab the objects
			if(obj != null) {
				// loop through the huds
				for(String hudName: obj.keySet()) {
					// get the parts
					HashMap<String, Object> hudMap = (HashMap<String, Object>)obj.get(hudName);
					
					// make sure it has everything we need
					if(!hudMap.containsKey("type")) {
						FMLLog.warning("[Info] Failed to load hud %s: no type defined!", hudName);
						continue;
					}
					
					// ok, create a new HUD based on the type
					HUD hud = null;
					if(((String)hudMap.get("type")).equalsIgnoreCase("text")) {
						// create the hud
						hud = new TextHUD(mc);
						
						// load the text
						if(!hudMap.containsKey("contents")) {
							FMLLog.warning("[Info] Failed to load text hud %s: no text given!", hudName);
							continue;
						}
						((TextHUD)hud).setContents((String)hudMap.get("contents"));
					}
					else {
						// unknown type
						FMLLog.warning("[Info] Failed to load hud %s: unknown type '%s'!", hudName, hudMap.get("type"));
						continue;
					}
					
					// set our alignment
					// top-left by default
					hud.setAlignment(Alignment.TOP_LEFT);
					if(hudMap.containsKey("align")) {
						String align = (String)hudMap.get("align");
						if(align.equalsIgnoreCase("top-left")) {
							hud.setAlignment(Alignment.TOP_LEFT);
						}
						else if(align.equalsIgnoreCase("top-center")) {
							hud.setAlignment(Alignment.TOP_CENTER);
						}
						else if(align.equalsIgnoreCase("top-right")) {
							hud.setAlignment(Alignment.TOP_RIGHT);
						}
						else if(align.equalsIgnoreCase("mid-left")) {
							hud.setAlignment(Alignment.MID_LEFT);
						}
						else if(align.equalsIgnoreCase("mid-center")) {
							hud.setAlignment(Alignment.MID_CENTER);
						}
						else if(align.equalsIgnoreCase("mid-right")) {
							hud.setAlignment(Alignment.MID_RIGHT);
						}
						else if(align.equalsIgnoreCase("bot-left")) {
							hud.setAlignment(Alignment.BOT_LEFT);
						}
						else if(align.equalsIgnoreCase("bot-center")) {
							hud.setAlignment(Alignment.BOT_CENTER);
						}
						else if(align.equalsIgnoreCase("bot-right")) {
							hud.setAlignment(Alignment.BOT_RIGHT);
						}
						else {
							// unknown alignment
							FMLLog.warning("[Info] Invalid alignment for hud %s: %s is not understood. Defaulting to top-left!", hudName, align);
						}
					}
					
					// set our render options
					if(hudMap.containsKey("inGame")) {
						hud.inGame = (Boolean)hudMap.get("inGame");
					}
					if(hudMap.containsKey("inMenus")) {
						hud.inMenus = (Boolean)hudMap.get("inMenus");
					}
					
					// ok, now add our HUD
					huds.add(hud);
					FMLLog.info("[Info] Added hud: %s", hudName);
				}
			}
		}
		catch(Exception e) {
			FMLLog.warning("[Info] Failed to load HUDs: %s", e.getMessage());
		}
	}
	
	public void onRenderTick() {
		WorldClient world = FMLClientHandler.instance().getClient().theWorld;
		for(HUD hud: huds) {
			if((!hud.inMenus && world == null) || (!hud.inGame && world != null)) {
				continue;
			}
			hud.updatePosition();
			hud.render();
		}
	}
	
	private static String defaultHUDsFile =
			  "{ \"FPS\" : \n"
			+ "  {\n"
			+ "    \"type\": \"text\",\n"
			+ "    \"contents\": \"FPS: &3{fps}\",\n"
			+ "    \"align\": \"top-right\"\n"
			+ "  },\n"
			+ "  \"Location\":\n"
			+ "  {\n"
			+ "    \"type\": \"text\",\n"
			+ "    \"contents\": \"&8(&7{blockx}&8, &7{blocky}&8, &7{blockz}&8)\n&3{direction}\n&9{biome}\",\n"
			+ "    \"align\": \"top-center\"\n"
			+ "  },\n"
			+ "  \"Time\":\n"
			+ "  {\n"
			+ "    \"type\": \"text\",\n"
			+ "    \"contents\": \"&7[&c{mctime}&7]\"\n"
			+ "  },\n"
			+ "  \"Names\":\n"
			+ "  {\n"
			+ "    \"type\": \"text\",\n"
			+ "    \"contents\": \"&6{username}\n&eTime: &f{realtime}\n&eTexture pack: &7{texturepack}\",\n"
			+ "    \"align\": \"top-left\",\n"
			+ "    \"inGame\": false,\n"
			+ "    \"inMenus\": true\n"
			+ "  }\n"
			+ "}\n"
			+ "\n";
}
