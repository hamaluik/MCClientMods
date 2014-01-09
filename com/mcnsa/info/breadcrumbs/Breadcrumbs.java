package com.mcnsa.info.breadcrumbs;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.Configuration;

import com.mcnsa.info.hooks.HookRenderEntity;
import com.mcnsa.info.interfaces.IRenderOverlay;
import com.mcnsa.utilitybelt.Config;

import cpw.mods.fml.client.FMLClientHandler;

public class Breadcrumbs implements IRenderOverlay {
	// options
	public static double MIN_CRUMB_DISTANCE_SQ = 0.25f;
	public static int MAX_CRUMBS = 8192;
	public static float ANIMATION_TIME = 4f;
	public static boolean SHOW = false;
	public static int KEY_TOGGLE_SHOW = Keyboard.KEY_BACK;
	public static int KEY_RESET = Keyboard.KEY_DELETE;
	
	// keep track of all our breadcrumb points
	private LinkedList<Breadcrumb> breadcrumbs = new LinkedList<Breadcrumb>();
	
	public Breadcrumbs() {
		// register ourself as an overlay
		HookRenderEntity.renderOverlays.add(this);
	}
	
	public void loadConfiguration(Configuration config) {
		MIN_CRUMB_DISTANCE_SQ = (float)config.get("Breadcrumbs", "crumb-distance-sq", (double)MIN_CRUMB_DISTANCE_SQ, "Square of the distance between crumbs (default = 0.25)").getDouble((double)MIN_CRUMB_DISTANCE_SQ);
		MAX_CRUMBS = config.get("Breadcrumbs", "max-crumbs", MAX_CRUMBS, "Maximum number of crumbs to remember at once (default = 8192)").getInt(MAX_CRUMBS);
		ANIMATION_TIME = (float)config.get("Breadcrumbs", "animation-time", (double)ANIMATION_TIME, "How fast to run the crumb animation at (higher value = slower) (default = 4)").getDouble((double)ANIMATION_TIME);
		KEY_TOGGLE_SHOW = config.get("Breadcrumbs", "key-toggle-show", KEY_TOGGLE_SHOW, "Key used to toggle the breadcrumbs showing (default = " + KEY_TOGGLE_SHOW + ")").getInt(KEY_TOGGLE_SHOW);
		KEY_RESET = config.get("Breadcrumbs", "key-reset", KEY_RESET, "Key used to reset the list of breadcrumbs (default = " + KEY_RESET + ")").getInt(KEY_RESET);
	}
	
	public void reset() {
		breadcrumbs.clear();
	}

	private static float animTime = 0;
	private static int animOffset = 0;
	private boolean showKeyDown = false;
	private boolean deleteKeyDown = false;
	@Override
	public void render(Tessellator tess, float delta) {
		// get key state information
		Minecraft mc = FMLClientHandler.instance().getClient();
		boolean chatShowing = (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign));
		boolean showKeyState = Keyboard.isKeyDown(KEY_TOGGLE_SHOW);
		boolean deleteKeyState = Keyboard.isKeyDown(KEY_RESET);
		
		// see if it's a press
		if(!showKeyState && showKeyDown) {
			// they just released the button!
			if(!chatShowing) SHOW = !SHOW;
		}
		showKeyDown = showKeyState;
		if(!deleteKeyState && deleteKeyDown) {
			// they just released the button!
			if(!chatShowing) reset();
		}
		deleteKeyDown = deleteKeyState;
		
		// first, determine if we need to add a new breadcrumb
		// grab our player's location
		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		if(player == null) {
			return;
		}
		double posX = FMLClientHandler.instance().getClient().thePlayer.posX;
		double posY = FMLClientHandler.instance().getClient().thePlayer.posY - 1.25f;
		double posZ = FMLClientHandler.instance().getClient().thePlayer.posZ;
		
		// determine if we need a new breadcrumb or not
		boolean makeNew = false;
		if(breadcrumbs.size() > 0) {
			Breadcrumb last = breadcrumbs.getLast();
			double distanceSq = (posX - last.posX)*(posX - last.posX)
					+ (posY - last.posY)*(posY - last.posY)
					+ (posZ - last.posZ)*(posZ - last.posZ);
			if(distanceSq >= MIN_CRUMB_DISTANCE_SQ) {
				makeNew = true;
			}
		}
		else {
			// yup, we need a starting point!
			makeNew = true;
		}
		
		// make a new breadcrumb if we need to
		if(makeNew) {
			breadcrumbs.add(new Breadcrumb(posX, posY, posZ));
			
			// keep it to our appropriate size
			if(breadcrumbs.size() > MAX_CRUMBS) {
				breadcrumbs.removeFirst();
			}
		}
		
		// make sure we're actually showing anything
		if(!SHOW) {
			return;
		}
		
		// update our animations
		animTime += delta;
		if(animTime >= ANIMATION_TIME) {
			animTime = 0;
			animOffset++;
			if(animOffset >= 8) {
				animOffset = 0;
			}
		}
		
		// now go through and render our breadcrumbs!
		if(breadcrumbs.size() >= 1) {
			// draw the line
			GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			tess.startDrawing(GL11.GL_LINE_STRIP);
			for(Breadcrumb crumb: breadcrumbs) {
				tess.addVertex(crumb.posX, crumb.posY, crumb.posZ);
			}
			tess.draw();
			
			// draw the things that travel along the path
			GL11.glPointSize(5f);
			GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			tess.startDrawing(GL11.GL_POINTS);
			for(int i = animOffset; i < breadcrumbs.size(); i += 8) {
				tess.addVertex(breadcrumbs.get(i).posX, breadcrumbs.get(i).posY, breadcrumbs.get(i).posZ);
			}
			tess.draw();
		}
	}
}
