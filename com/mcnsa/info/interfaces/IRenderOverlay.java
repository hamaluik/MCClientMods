package com.mcnsa.info.interfaces;

import net.minecraft.client.renderer.Tessellator;

public interface IRenderOverlay {
	public void render(Tessellator tess, float delta);
}
