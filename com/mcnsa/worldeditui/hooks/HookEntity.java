package com.mcnsa.worldeditui.hooks;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class HookEntity extends Entity {	
	public HookEntity(World world) {
		super(world);
		
		this.ignoreFrustumCheck = true;
		this.noClip = true;
		this.setSize(0, 0);
		
		// alert!
		FMLLog.info("Hook entity created!");
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntityUpdate() {
		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		if(player == null) {
			return;
		}
		
		this.setPosition(player.posX, player.posY, player.posZ);
	}
	
	@Override
	public void setDead() {
		
	}
	
	@Override
	public String getEntityName() {
		return "WorldEditUIHook";
	}
	
	@Override
	public boolean isInRangeToRenderVec3D(Vec3 vec) {
		return true;
	}
	
	@Override
	public boolean isInRangeToRenderDist(double dist) {
		// yes, we want to *always* render
		return true;
	}
	
	@Override
	public int getBrightnessForRender(float f) {
		// always render at maximum brightness
		return 0xf000f0;
	}
	
	@Override
	public float getBrightness(float f) {
		// always render at maximum brightness
		return 1f;
	}
}
