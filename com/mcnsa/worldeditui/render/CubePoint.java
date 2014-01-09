package com.mcnsa.worldeditui.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

import com.mcnsa.worldeditui.hooks.IRenderOverlay;
import com.mcnsa.worldeditui.render.shapes.BoxShape;
import com.mcnsa.worldeditui.render.style.LineColour;
import com.mcnsa.worldeditui.render.style.LineInfo;

public class CubePoint implements IRenderOverlay {
	public double x, y, z;
	private BoxShape shape = null;
	
	public CubePoint(LineColour colour, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		shape = new BoxShape(colour, x + 0.03, y + 0.03, z + 0.03, x + 1.03, y + 1.03, z + 1.03);
	}

	@Override
	public void render(Tessellator tess, float delta) {
		shape.render(tess, delta);
	}
}
