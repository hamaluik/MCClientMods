package com.mcnsa.utilitybelt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONValue;

import net.minecraft.client.Minecraft;

public class MenuLoader {
	public static HashMap<String, UtilityMenu> loadMenus() {
		HashMap<String, UtilityMenu> menus = new HashMap<String, UtilityMenu>();
		UtilityBelt.menuEnabled = false;
		menus.put("root", new UtilityMenu("root"));
		try {
			File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
			File cfgfile = new File(cfgdir, "UtilityBelt.menus.json");
			if(!cfgfile.exists()) {
				// try to extract it!
				// make the file
				if(!cfgfile.createNewFile()) {
					// uh-oh, we can't create a new file!
					UtilityBelt.displayErrorString("menus file does not exist and can't create a new file!");
					return menus;
				}
				
				// write it out
				BufferedWriter out = new BufferedWriter(new FileWriter(cfgfile));
				out.write(defaultMenuFile);
				
				// and close up
				out.close();
			}

			// load the file
			try {
				String lineSep = System.getProperty("line.separator");
				FileInputStream fin = new FileInputStream(cfgfile);
				BufferedReader input = new BufferedReader(new InputStreamReader(fin));
				String nextLine = "";
				StringBuffer sb = new StringBuffer();
				while((nextLine = input.readLine()) != null) {
					sb.append(nextLine);
					sb.append(lineSep);
				}
				
				// start parsing
				HashMap<String, Object> obj = (HashMap<String, Object>)JSONValue.parse(sb.toString());
				
				// if we made it here, we can remove our dummy root menu
				menus.remove("root");
				
				// grab the objects
				if(obj != null) {
					// loop through the menus
					//Boolean first = true;
					for(String menuName: obj.keySet()) {
						UtilityMenu menu = new UtilityMenu(menuName);
						
						// now get a list of all it's parts
						ArrayList<ArrayList<String>> items = (ArrayList<ArrayList<String>>)obj.get(menuName);
						// and loop over the items
						for(int i = 0; i < items.size(); i++) {
							// make sure it's valid sized!
							if(items.get(i).size() != 2) {
								UtilityBelt.displayErrorString("bad menu item: " + menuName + "." + (i+1));
								continue;
							}
							
							// parse the texture / display string
							String texture = items.get(i).get(0);
							
							UtilityButton button = new UtilityButton(texture, items.get(i).get(1));
							menu.addButton(button);
						}
						
						// now add the menu
						menus.put(menuName, menu);
						
						// set the root and current menus
						/*if(first) {
							first = false;
							Config.rootMenu = menuName;
							UtilityBelt.currentMenu = menuName;
						}*/
					}
					
					// enable the menu
					UtilityBelt.menuEnabled = true;
				}
				else {
					// error!
					UtilityBelt.displayErrorString("unable to parse menus!");
				}

				// and close up!
				input.close();
			}
			catch(Exception e) {
				UtilityBelt.displayErrorString("error parsing menu: " + e.getMessage());
			}
		}
		catch(Exception e) {
			UtilityBelt.displayErrorString(e.getMessage());
		}
		
		// and we're done
		return menus;
	}
	
	private static String defaultMenuFile =
			  "{ \"root\" : [\n"
			+ "      [ \"&5Spawn\",\n"
			+ "        \"/spawn\"\n"
			+ "      ],\n"
			+ "      [ \"&6Home\",\n"
			+ "        \"/home\"\n"
			+ "      ],\n"
			+ "      [ \"&3Toggle TP\",\n"
			+ "        \"/tptoggle\"\n"
			+ "      ],\n"
			+ "      [ \"&b&nPo8\",\n"
			+ "        \"##po8\"\n"
			+ "      ]\n"
			+ "    ],\n"
			+ "  \"po8\" : [\n"
			+ "      [ \"View Balance\",\n"
			+ "        \"/po8 balance\"\n"
			+ "      ],\n"
			+ "      [ \"Held Item Info\",\n"
			+ "        \"/po8 info held 1\"\n"
			+ "      ],\n"
			+ "      [ \"Order\",\n"
			+ "        \"##po8Orders\"\n"
			+ "      ],\n"
			+ "      [ \"Sell\",\n"
			+ "        \"##po8Sell\"\n"
			+ "      ]\n"
			+ "    ],\n"
			+ "  \"po8Orders\" : [\n"
			+ "      [ \"New Order\",\n"
			+ "        \"/po8 order new\"\n"
			+ "      ],\n"
			+ "      [ \"Check Price\",\n"
			+ "        \"/po8 order price\"\n"
			+ "      ],\n"
			+ "      [ \"List Items\",\n"
			+ "        \"/po8 order list\"\n"
			+ "      ],\n"
			+ "      [ \"Submit Order\",\n"
			+ "        \"/po8 order submit\"\n"
			+ "      ]\n"
			+ "    ],\n"
			+ "  \"po8Sell\" : [\n"
			+ "      [ \"Sell Items\",\n"
			+ "        \"/po8 sell\"\n"
			+ "      ],\n"
			+ "      [ \"Sell Value\",\n"
			+ "        \"/po8 value\"\n"
			+ "      ]\n"
			+ "    ]\n"
			+ "}\n";
}
