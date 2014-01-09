package com.mcnsa.utilitybelt;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mcnsa.utilitybelt.CommonProxy;

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

@Mod(modid="mod_UtilityBelt", name="Utility Belt", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class UtilityBelt {
	@Instance("mod_UtilityBelt")
	public static UtilityBelt instance;
	
	// keep a pointer to minecraft for convenience
	Minecraft mc;
	
	// our menus
	HashMap<String, UtilityMenu> menus = new HashMap<String, UtilityMenu>();
	
	// states
	public static Boolean menuEnabled = true;
	Boolean keyDown = false;
	Boolean mouseDown = false;
	GuiScreen tempScreen = null;
	public static String currentMenu = "root";
	
	static String errorString = "";
	static long errorTimeout = 0;
	static int errorAlpha = 255;
	
	// our constructor
	public UtilityBelt() {
		// store oure instance
		instance = this;
		
		// get a handle to our minecraft instance
		mc = FMLClientHandler.instance().getClient();
	}
    
    // where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.mcnsa.utilitybelt.client.ClientProxy", serverSide="com.mcnsa.utilitybelt.CommonProxy")
    public static CommonProxy proxy;
	
    // do configuration loading in here
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// load our configuration file
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        
        // load our values into memory
        config.load();
        
        // load the actual values
        Config.menuKey = config.get(Configuration.CATEGORY_GENERAL, "menu-key", Config.menuKey, "Key ID to open the menus (see http://www.lwjgl.org/javadoc/constant-values.html#org.lwjgl.input.Keyboard.KEY_0 for values)").getInt(Config.menuKey);
        Property rootMenu = config.get(Configuration.CATEGORY_GENERAL, "root-menu", Config.rootMenu);
        rootMenu.comment = "The name of the base menu that pressing the menu key will always display (default: root)";
        Config.rootMenu = rootMenu.getString();
        
        // and save it!
        config.save();
		
		// load our menus from file
		menus = MenuLoader.loadMenus();
	}
	
	// do initialization here
	@Init
	public void load(FMLInitializationEvent event) {
		proxy.initialize();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	// handle the rendering loop
	public void onRender() {
		// handle rendering error overlays 
		if(!errorString.equals("")) {
			FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow(errorString, 2, 2, 0xFF0000 | (errorAlpha << 24));
			
			if(System.currentTimeMillis() > (errorTimeout + 1000)) {
				// clear the error
				errorTimeout = 0;
				errorString = "";
			}
			else if(System.currentTimeMillis() > errorTimeout) {
				// handle error fading
				errorAlpha = (int)(255.0 - (255.0 * (System.currentTimeMillis() - errorTimeout) / 1000.0));
			}
		}
		
		// make sure our current menu is valid
		if(!menus.containsKey(currentMenu)) {
			displayErrorString("no such menu \"" + currentMenu + "\" defined!");
			return;
		}
		
		// only do things if the menu is actually enabled
		// or if we're in a menu
		if(!menuEnabled || (tempScreen == null && mc.currentScreen != null)) {
			return;
		}
		
		// get key state information
		boolean keyState = Keyboard.isKeyDown(Config.menuKey);
		boolean keyPressed = false;
		boolean keyReleased = false;
		
		// see if chat is showing
		if(keyState && !keyDown) {
			// they just pressed the button!
			keyPressed = true;
		}
		else if(!keyState && keyDown) {
			// they just released the button!
			keyReleased = true;
		}
		keyDown = keyState;
		
		// get mouse state information
		boolean mouseState = Mouse.isButtonDown(0);
		boolean mouseReleased = false;
		if(!mouseState && mouseDown) {
			mouseReleased = true;
		}
		mouseDown = mouseState;
		
		// whether or not a text input window is showing
		boolean chatShowing = (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign));
		
		// show we show the selection window?
		if(keyPressed && !chatShowing) {
			// enable the mouse
			tempScreen = new GuiScreen();
			mc.displayGuiScreen(tempScreen);
		}
		if(keyDown && !chatShowing) {
			// key is being held down
			menus.get(currentMenu).render();
		}
		if(keyReleased && !chatShowing) {
			// disable the mouse
			mc.displayGuiScreen((GuiScreen)null);
			mc.currentScreen = null;
			tempScreen = null;
			
			// handle the selected macro
			handleMultiMacro(menus.get(currentMenu).getSelectedMacro());
			
			// and reset the current menu
			currentMenu = Config.rootMenu;
		}
		
		// see if we clicked!
		if(mouseReleased && keyDown) {
			handleMultiMacro(menus.get(currentMenu).getSelectedMacro());
		}
	}
	
	public void handleMultiMacro(String macro) {
		if(macro.equals("")) {
			return;
		}
		
		if(macro.contains("|")) {
			// we have multiple actions!
			// parse it!
			String[] macros = macro.split("\\|");
			for(int i = 0; i < macros.length; i++) {
				handleMacro(macros[i]);
			}
		}
		else {
			// no multi macros, just pass it along
			handleMacro(macro);
		}
	}
	
	public void handleMacro(String macro) {
		if(macro.startsWith("##")) {
			// they want a different menu!
			macro = macro.substring(2).trim();
			if(menus.containsKey(macro)) {
				currentMenu = macro;
			}
			else {
				displayErrorString("no such menu \"" + macro + "\" defined!");
			}
		}
		else {
			// send the text to the server!
			FMLClientHandler.instance().getClient().thePlayer.sendChatMessage(macro);
		}
	}
	
	public static void displayErrorString(String error) {
		errorString = "UtilityBelt error: " + error;
		errorTimeout = System.currentTimeMillis() + 5000;
		errorAlpha = 255;
	}
}
