package com.mcnsa.info.spawncheck;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraftforge.common.Configuration;

import com.mcnsa.info.breadcrumbs.Breadcrumb;
import com.mcnsa.info.hooks.HookRenderEntity;
import com.mcnsa.info.interfaces.IRenderOverlay;

import cpw.mods.fml.client.FMLClientHandler;

public class SpawnChecker implements IRenderOverlay {
	public static int MAX_CHECKS = 2048;
	public static int CHECK_RADIUS = 8;
	public static boolean SHOW = false;
	public static int KEY_TOGGLE_SHOW = Keyboard.KEY_L;
	
	private class SpawnCheck {
		public int x, y, z;
		SpawnWhen when = SpawnWhen.NOT_POSSIBLE;
	}
	
	public static enum SpawnWhen {
		NOT_POSSIBLE, NIGHT, ALWAYS
	}

	LinkedList<SpawnCheck> spawnCheckLocations = new LinkedList<SpawnCheck>();
	public SpawnChecker() {
		// register ourself as an overlay
		HookRenderEntity.renderOverlays.add(this);
		
		// fill our out default spawnCheckLocations
		for(int i = 0; i < MAX_CHECKS; i++) {
			spawnCheckLocations.add(new SpawnCheck());
		}
	}
	
	public void loadConfiguration(Configuration config) {
		MAX_CHECKS = config.get("SpawnChecker", "max-checks", MAX_CHECKS, "Maximum number of blocks to check (default = " + MAX_CHECKS + ")").getInt(MAX_CHECKS);
		CHECK_RADIUS = config.get("SpawnChecker", "check-radius", CHECK_RADIUS, "Radius of blocks to check around you (default = " + CHECK_RADIUS + ")").getInt(CHECK_RADIUS);
		KEY_TOGGLE_SHOW = config.get("SpawnChecker", "key-toggle-show", KEY_TOGGLE_SHOW, "Key that toggles showing / hiding (default = " + KEY_TOGGLE_SHOW + ")").getInt(KEY_TOGGLE_SHOW);
	}

	private int checkTimer = 32;
	private boolean showKeyDown = false;
	@Override
	public void render(Tessellator tess, float delta) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		boolean chatShowing = (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign));
		
		// get key state information
		boolean showKeyState = Keyboard.isKeyDown(KEY_TOGGLE_SHOW);
		// see if it's a press
		if(!showKeyState && showKeyDown) {
			// they just released the button!
			if(!chatShowing) SHOW = !SHOW;
		}
		showKeyDown = showKeyState;
		
		// make sure we're actually showing anything
		if(!SHOW) {
			return;
		}
		
		// grab our player's location
		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		if(player == null) {
			return;
		}
		double posX = FMLClientHandler.instance().getClient().thePlayer.posX;
		double posY = FMLClientHandler.instance().getClient().thePlayer.boundingBox.minY + 0.1;
		double posZ = FMLClientHandler.instance().getClient().thePlayer.posZ;
		
		// update our spawn locations
		if(--checkTimer < 0) {
			recheckSpawns((int)posX, (int)posY, (int)posZ);
			checkTimer = 32;
		}

		// loop over all our spawn check locations
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		for(SpawnCheck location: spawnCheckLocations) {
			
			// skip not possibles
			if(location.when == SpawnWhen.NOT_POSSIBLE) {
				continue;
			}
			
			// change our colour based on what level of safe it is
			switch(location.when) {
			case NIGHT:
				GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
				break;
			case ALWAYS:
				GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
				break;
			}
			
			// draw it
			tess.startDrawing(GL11.GL_QUADS);
			tess.addVertex(location.x + 0.05, location.y + 0.1f, location.z + 0.05);
			tess.addVertex(location.x + 0.05, location.y + 0.1f, location.z + 0.95);
			tess.addVertex(location.x + 0.95, location.y + 0.1f, location.z + 0.95);
			tess.addVertex(location.x + 0.95, location.y + 0.1f, location.z + 0.05);
			tess.draw();
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private boolean emptySpace(WorldClient world, int pX, int pY, int pZ) {
		// stolen from Zombe
        double x = pX + 0.5, y = pY, z = pZ + 0.5;
        double r = 0.3, h = 0.5;
        AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x - r, y, z - r, x + r, y + h, z + r);
        return world.getAllCollidingBoundingBoxes(aabb).isEmpty() && !world.isAnyLiquid(aabb);
	}
	
	private SpawnWhen checkSpawn(int x, int y, int z) {
		WorldClient world = FMLClientHandler.instance().getClient().theWorld;
		if(world == null) {
			return SpawnWhen.NOT_POSSIBLE;
		}
		if(!emptySpace(world, x, y, z)) {
			return SpawnWhen.NOT_POSSIBLE;
		}
		
		int lightLevel = world.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15);
		
		// check!
		if(lightLevel < 8 && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, world, x, y, z)) {
			int skyLightLevel = world.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15);
			if(skyLightLevel > 7) {
				return SpawnWhen.NIGHT;
			}
			else {
				return SpawnWhen.ALWAYS;
			}
		}
		
		return SpawnWhen.NOT_POSSIBLE;
	}
	
	private void recheckSpawns(int posX, int posY, int posZ) {
		int i = 0;
		for(int x = (posX - CHECK_RADIUS); x <= (posX + CHECK_RADIUS); ++x) {
			for(int y = (posY - CHECK_RADIUS); y <= (posY + CHECK_RADIUS); ++y) {
				for(int z = (posZ - CHECK_RADIUS); z <= (posZ + CHECK_RADIUS); ++z) {
					SpawnWhen when = checkSpawn(x, y, z);
					if(when != SpawnWhen.NOT_POSSIBLE) {
						spawnCheckLocations.get(i).x = x;
						spawnCheckLocations.get(i).y = y;
						spawnCheckLocations.get(i).z = z;
						spawnCheckLocations.get(i).when = when;
						
						++i;
						if(i >= MAX_CHECKS) {
							return;
						}
					}
				}
			}
		}
		
		for(int n = i; n < MAX_CHECKS; n++) {
			spawnCheckLocations.get(i).when = SpawnWhen.NOT_POSSIBLE;
		}
	}
}
