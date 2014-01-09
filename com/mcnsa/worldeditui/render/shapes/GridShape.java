package com.mcnsa.worldeditui.render.shapes;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.mcnsa.worldeditui.hooks.IRenderOverlay;
import com.mcnsa.worldeditui.render.style.LineColour;
import com.mcnsa.worldeditui.render.style.LineInfo;

public class GridShape implements IRenderOverlay {
	private double x1 = 0, y1 = 0, z1 = 0;
	private double x2 = 0, y2 = 0, z2 = 0;
	LineColour colour = LineColour.CUBOIDPOINT1;
	
	public GridShape(LineColour colour, double x1, double y1, double z1, double x2, double y2, double z2) {
		this.colour = colour;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
	}

	@Override
	public void render(Tessellator tess, float delta) {
		for(LineInfo colourInfo: colour.getColors()) {
			// prepare our rendering (for depth etc)
			colourInfo.prepareRender();
			
			// Draw bottom face
			tess.startDrawing(GL11.GL_LINE_LOOP);
			colourInfo.prepareColor();
			tess.addVertex(x1, y1, z1);
			tess.addVertex(x2, y1, z1);
			tess.addVertex(x2, y1, z2);
			tess.addVertex(x1, y1, z2);
			tess.draw();
			
			// Draw top face
			tess.startDrawing(GL11.GL_LINE_LOOP);
			colourInfo.prepareColor();
			tess.addVertex(x1, y2, z1);
			tess.addVertex(x2, y2, z1);
			tess.addVertex(x2, y2, z2);
			tess.addVertex(x1, y2, z2);
			tess.draw();
			
			// Draw join top and bottom faces
			tess.startDrawing(GL11.GL_LINES);
			colourInfo.prepareColor();
			
			tess.addVertex(x1, y1, z1);
			tess.addVertex(x1, y2, z1);
			
			tess.addVertex(x2, y1, z1);
			tess.addVertex(x2, y2, z1);
			
			tess.addVertex(x2, y1, z2);
			tess.addVertex(x2, y2, z2);
			
			tess.addVertex(x1, y1, z2);
			tess.addVertex(x1, y2, z2);

			tess.draw();
		}
	}
}
