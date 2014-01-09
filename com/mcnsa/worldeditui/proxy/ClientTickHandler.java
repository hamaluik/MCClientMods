package com.mcnsa.worldeditui.proxy;

import java.util.EnumSet;

import com.mcnsa.worldeditui.hooks.HookEntity;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// determine what kind of tick we're dealing with
		if(type.equals(EnumSet.of(TickType.RENDER))) {
			onWorldTick();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "WorldEditUI";
	}
	
	private static World lastWorld = null;
	private static EntityClientPlayerMP lastPlayer = null;
	private void onWorldTick() {
		World world = FMLClientHandler.instance().getClient().theWorld;
		// check to see if anything changed
		if(world != null && FMLClientHandler.instance().getClient().thePlayer != null && (lastWorld != world || FMLClientHandler.instance().getClient().thePlayer != lastPlayer)) {
			// yup, we need to spawn our hook entity
			
			// store our last world and player
			lastPlayer = FMLClientHandler.instance().getClient().thePlayer;
			lastWorld = world;
			
			// spawn it at the player
			Entity entity = new HookEntity(world);
			entity.setPosition(lastPlayer.posX, lastPlayer.posY, lastPlayer.posZ);
			world.spawnEntityInWorld(entity);
			entity.setPosition(lastPlayer.posX, lastPlayer.posY, lastPlayer.posZ);
			FMLLog.info("Spawned hook entity in world");
		}
	}
}
