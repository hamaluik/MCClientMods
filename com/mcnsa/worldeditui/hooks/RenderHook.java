package com.mcnsa.worldeditui.hooks;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class RenderHook extends Render {
	private static double size = 1;
	
	private static LinkedList<IRenderOverlay> renderOverlays = new LinkedList<IRenderOverlay>();
	
	public static void registerOverlay(IRenderOverlay overlay) {
		if(!renderOverlays.contains(overlay)) {
			renderOverlays.add(overlay);
		}
	}
	
	public static void releaseOverlay(IRenderOverlay overlay) {
		renderOverlays.remove(overlay);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float delta) {
		// only render if we have any overlays
		if(renderOverlays.size() < 1) {
			return;
		}
		
		// get the location of the player
		double posX = FMLClientHandler.instance().getClient().thePlayer.posX;
		double posY = FMLClientHandler.instance().getClient().thePlayer.posY;
		double posZ = FMLClientHandler.instance().getClient().thePlayer.posZ;
		
		// get their last location
		double lastX = FMLClientHandler.instance().getClient().thePlayer.lastTickPosX;
		double lastY = FMLClientHandler.instance().getClient().thePlayer.lastTickPosY;
		double lastZ = FMLClientHandler.instance().getClient().thePlayer.lastTickPosZ;
		
		// now calculate our actual render base
		x = lastX + (posX - lastX) * delta;
		y = lastY + (posY - lastY) * delta;
		z = lastZ + (posZ - lastZ) * delta;
		
		// grab our tessellator
		Tessellator tess = Tessellator.instance;
		
		// disable lighting
		RenderHelper.disableStandardItemLighting();
		
		// start drawing!        
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();

		GL11.glTranslated(-x, -y, -z);
		for(IRenderOverlay overlay: renderOverlays) {
			try {
				overlay.render(tess, delta);
			}
			catch(Exception ignored) {}
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		// re-enable lighting
		RenderHelper.enableStandardItemLighting();
	}
}
