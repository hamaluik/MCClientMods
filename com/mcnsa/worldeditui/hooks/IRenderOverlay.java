package com.mcnsa.worldeditui.hooks;

import net.minecraft.client.renderer.Tessellator;

public interface IRenderOverlay {
	public void render(Tessellator tess, float delta);
}
