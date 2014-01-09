package com.mcnsa.utilitybelt;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class GraphicUtils {
	// get our screen size, makes for easier rendering
    public static int[] getScreenSize() {
    	ScaledResolution scSize = new ScaledResolution(FMLClientHandler.instance().getClient().gameSettings, FMLClientHandler.instance().getClient().displayWidth, FMLClientHandler.instance().getClient().displayHeight);
    	return new int[] { scSize.getScaledWidth(), scSize.getScaledHeight() };
    }
    
	// map image coordinates
	public static void mapImage(int[] points) {
		Tessellator.instance.addVertexWithUV(points[0], points[3], 1.0D, 0.0D, 1.0D);
		Tessellator.instance.addVertexWithUV(points[2], points[3], 1.0D, 1.0D, 1.0D);
		Tessellator.instance.addVertexWithUV(points[2], points[1], 1.0D, 1.0D, 0.0D);
		Tessellator.instance.addVertexWithUV(points[0], points[1], 1.0D, 0.0D, 0.0D);
	}

	// convenience function to render the image
	public static void renderImage(String image, int[] pos, int[] size, float rotation) {
		// set up colour
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		try {
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(image);
			GL11.glPushMatrix();
				GL11.glTranslatef(pos[0], pos[1], 0);
				GL11.glPushMatrix();
					GL11.glRotatef(rotation, 0, 0, 1);
					Tessellator.instance.startDrawingQuads();
					mapImage(new int[]{size[0] / -2, size[1] / -2, size[0] / 2, size[1] / 2});
					Tessellator.instance.draw();
				GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
		catch(Exception e) {
			FMLClientHandler.instance().haltGame("UtilityBelt Error: failed to render image (" + image + "): " + e.getMessage(), e);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}

	// strip colour tags from strings..
	public static String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9klmnor]))", "").replaceAll("(\u00A7([a-f0-9klmnor]))", "");
	}

	// strip colour tags from strings..
	public static String processColours(String str) {
		return str.replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2");
	}
}
