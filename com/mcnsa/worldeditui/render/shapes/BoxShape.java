package com.mcnsa.worldeditui.render.shapes;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.mcnsa.worldeditui.hooks.IRenderOverlay;
import com.mcnsa.worldeditui.render.style.LineColour;
import com.mcnsa.worldeditui.render.style.LineInfo;

public class BoxShape implements IRenderOverlay {
	private double x1 = 0, y1 = 0, z1 = 0;
	private double x2 = 0, y2 = 0, z2 = 0;
	LineColour colour = LineColour.CUBOIDPOINT1;
	
	public BoxShape(LineColour colour, double x1, double y1, double z1, double x2, double y2, double z2) {
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
			
			tess.startDrawing(GL11.GL_LINES);
			colourInfo.prepareColor();

			double x, y, z;
			double offsetSize = 1.0;

			// Zmax XY plane, y axis
			z = z2;
			y = y1;
			int msize = 150;
			if ((y2 - y / offsetSize) < msize) {
				for (double yoff = 0; yoff + y <= y2; yoff += offsetSize) {
					tess.addVertex(x1, y + yoff, z);
					tess.addVertex(x2, y + yoff, z);
				}
			}

			// Zmin XY plane, y axis
			z = z1;
			if ((y2 - y / offsetSize) < msize) {
				for (double yoff = 0; yoff + y <= y2; yoff += offsetSize) {
					tess.addVertex(x1, y + yoff, z);
					tess.addVertex(x2, y + yoff, z);
				}
			}

			// Xmin YZ plane, y axis
			x = x1;
			if ((y2 - y / offsetSize) < msize) {
				for (double yoff = 0; yoff + y <= y2; yoff += offsetSize) {
					tess.addVertex(x, y + yoff, z1);
					tess.addVertex(x, y + yoff, z2);
				}
			}

			// Xmax YZ plane, y axis
			x = x2;
			if ((y2 - y / offsetSize) < msize) {
				for (double yoff = 0; yoff + y <= y2; yoff += offsetSize) {
					tess.addVertex(x, y + yoff, z1);
					tess.addVertex(x, y + yoff, z2);
				}
			}

			// Zmin XY plane, x axis
			x = x1;
			z = z1;
			if ((x2 - x / offsetSize) < msize) {
				for (double xoff = 0; xoff + x <= x2; xoff += offsetSize) {
					tess.addVertex(x + xoff, y1, z);
					tess.addVertex(x + xoff, y2, z);
				}
			}
			// Zmax XY plane, x axis
			z = z2;
			if ((x2 - x / offsetSize) < msize) {
				for (double xoff = 0; xoff + x <= x2; xoff += offsetSize) {
					tess.addVertex(x + xoff, y1, z);
					tess.addVertex(x + xoff, y2, z);
				}
			}
			// Ymin XZ plane, x axis
			y = y2;
			if ((x2 - x / offsetSize) < msize) {
				for (double xoff = 0; xoff + x <= x2; xoff += offsetSize) {
					tess.addVertex(x + xoff, y, z1);
					tess.addVertex(x + xoff, y, z2);
				}
			}
			// Ymax XZ plane, x axis
			y = y1;
			if ((x2 - x / offsetSize) < msize) {
				for (double xoff = 0; xoff + x <= x2; xoff += offsetSize) {
					tess.addVertex(x + xoff, y, z1);
					tess.addVertex(x + xoff, y, z2);
				}
			}

			// Ymin XZ plane, z axis
			z = z1;
			y = y1;
			if ((z2 - z / offsetSize) < msize) {
				for (double zoff = 0; zoff + z <= z2; zoff += offsetSize) {
					tess.addVertex(x1, y, z + zoff);
					tess.addVertex(x2, y, z + zoff);
				}
			}
			// Ymax XZ plane, z axis
			y = y2;
			if ((z2 - z / offsetSize) < msize) {
				for (double zoff = 0; zoff + z <= z2; zoff += offsetSize) {
					tess.addVertex(x1, y, z + zoff);
					tess.addVertex(x2, y, z + zoff);
				}
			}
			// Xmin YZ plane, z axis
			x = x2;
			if ((z2 - z / offsetSize) < msize) {
				for (double zoff = 0; zoff + z <= z2; zoff += offsetSize) {
					tess.addVertex(x, y1, z + zoff);
					tess.addVertex(x, y2, z + zoff);
				}
			}
			// Xmax YZ plane, z axis
			x = x1;
			if ((z2 - z / offsetSize) < msize) {
				for (double zoff = 0; zoff + z <= z2; zoff += offsetSize) {
					tess.addVertex(x, y1, z + zoff);
					tess.addVertex(x, y2, z + zoff);
				}
			}

			tess.draw();
		}
	}
}
