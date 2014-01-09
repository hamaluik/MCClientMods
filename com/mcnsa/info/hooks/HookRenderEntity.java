package com.mcnsa.info.hooks;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.mcnsa.info.interfaces.IRenderOverlay;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class HookRenderEntity extends Render {
	private static double size = 1;
	
	public static LinkedList<IRenderOverlay> renderOverlays = new LinkedList<IRenderOverlay>();

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
        boolean gltex2d = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
        boolean gldepth = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boolean glblend = GL11.glGetBoolean(GL11.GL_BLEND);
        boolean glfog   = GL11.glGetBoolean(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_FOG);
        
		/*GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);*/
		GL11.glPushMatrix();

		GL11.glTranslated(-x, -y, -z);
		for(IRenderOverlay overlay: renderOverlays) {
			try {
				overlay.render(tess, delta);
			}
			catch(Exception ignored) {}
		}

		/*GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);*/
		GL11.glPopMatrix();
        // cleaning
        if (glfog)   GL11.glEnable( GL11.GL_FOG);
        else         GL11.glDisable(GL11.GL_FOG);
        if (glblend) GL11.glEnable( GL11.GL_BLEND);
        else         GL11.glDisable(GL11.GL_BLEND);
        if (gldepth) GL11.glEnable( GL11.GL_DEPTH_TEST);
        else         GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (gltex2d) GL11.glEnable( GL11.GL_TEXTURE_2D);
        else         GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		// re-enable lighting
		RenderHelper.enableStandardItemLighting();
	}
}
