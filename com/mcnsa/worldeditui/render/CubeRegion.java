package com.mcnsa.worldeditui.render;

import net.minecraft.client.renderer.Tessellator;

import com.mcnsa.worldeditui.hooks.IRenderOverlay;
import com.mcnsa.worldeditui.render.shapes.BoxShape;
import com.mcnsa.worldeditui.render.shapes.GridShape;
import com.mcnsa.worldeditui.render.style.LineColour;

import cpw.mods.fml.common.FMLLog;

public class CubeRegion implements IRenderOverlay {
	protected CubePoint firstPoint = null;
	protected CubePoint secondPoint = null;
	protected BoxShape outlineBox = null;
	protected GridShape gridBox = null;

	@Override
	public void render(Tessellator tess, float delta) {
		if(firstPoint != null && secondPoint != null) {
			// render our points
			firstPoint.render(tess, delta);
			secondPoint.render(tess, delta);
			
			// render a bounding box and grid for those points
			gridBox.render(tess, delta);
			outlineBox.render(tess, delta);
		}
		else if(firstPoint != null) {
			firstPoint.render(tess, delta);
		}
		else if(secondPoint != null) {
			secondPoint.render(tess, delta);
		}
	}
	
	public void setPoint(int id, int x, int y, int z) {
		if(id == 0) {
			firstPoint = new CubePoint(LineColour.CUBOIDPOINT1, x, y, z);
		}
		else if(id == 1) {
			secondPoint = new CubePoint(LineColour.CUBOIDPOINT2, x, y, z);
		}
		else {
			FMLLog.warning("[WorldEditUI] Unrecognized point id: %d", id);
		}
		
		if(firstPoint != null && secondPoint != null) {
			// get our bounds
			double[] minBounds = boundsMin();
			double[] maxBounds = boundsMax();
			
			// make our grid
			gridBox = new GridShape(LineColour.CUBOIDGRID,
					minBounds[0] + 0.01, minBounds[1] + 0.01, minBounds[2] + 0.01,
					maxBounds[0] + 1.01, maxBounds[1] + 1.01, maxBounds[2] + 1.01);
			
			// make our outline
			outlineBox = new BoxShape(LineColour.CUBOIDBOX,
					minBounds[0] + 0.02, minBounds[1] + 0.02, minBounds[2] + 0.02,
					maxBounds[0] + 1.02, maxBounds[1] + 1.02, maxBounds[2] + 1.02);
		}
	}
	
	public void clearPoints() {
		firstPoint = null;
		secondPoint = null;
		gridBox = null;
		outlineBox = null;
	}
	
	private double[] boundsMin() {
		double[] coords = new double[]{99999999, 99999999, 99999999};
		
		CubePoint[] points = new CubePoint[]{firstPoint, secondPoint};
		for(CubePoint point: points) {
			if(point == null) {
				continue;
			}

			if(point.x < coords[0]) {
				coords[0] = point.x;
			}
			if(point.y < coords[1]) {
				coords[1] = point.y;
			}
			if(point.z < coords[2]) {
				coords[2] = point.z;
			}
		}
		
		return coords;
	}
	
	private double[] boundsMax() {
		double[] coords = new double[]{-99999999, -99999999, -99999999};
		
		CubePoint[] points = new CubePoint[]{firstPoint, secondPoint};
		for(CubePoint point: points) {
			if(point == null) {
				continue;
			}

			if(point.x > coords[0]) {
				coords[0] = point.x;
			}
			if(point.y > coords[1]) {
				coords[1] = point.y;
			}
			if(point.z > coords[2]) {
				coords[2] = point.z;
			}
		}
		
		return coords;
	}
}
