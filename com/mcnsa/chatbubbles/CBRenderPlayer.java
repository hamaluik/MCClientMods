package com.mcnsa.chatbubbles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

@SideOnly(Side.CLIENT)
public class CBRenderPlayer extends RenderPlayer {
	public CBRenderPlayer() {
		super();
	}

	// renderName got removed in 1.5.1 (as opposed to 1.4.7)
	// this seems to have replaced it, which also renders a player's score information
	protected void func_96450_a(EntityPlayer entityPlayer, double x, double y, double z, String name, float scale, double distance) {
		// get the player's scoreboard if it exists
		Scoreboard scoreboard = entityPlayer.func_96123_co();
		ScoreObjective scoreobjective = scoreboard.func_96539_a(2);
		
		// if they have a scoreboard
		if (scoreobjective != null) {
			// get their score for the current objective
			Score score = scoreboard.func_96529_a(entityPlayer.getEntityName(), scoreobjective);

			// and render the label
			if (entityPlayer.isPlayerSleeping()) {
				// if they're sleeping, move it down so it's in-line with their head
				this.renderLivingLabel(entityPlayer, score.func_96652_c() + " " + scoreobjective.func_96678_d(), x, y - 1.5D, z, (int)ChatBubbles.NAME_TAG_RANGE);
			}
			else {
				this.renderLivingLabel(entityPlayer, score.func_96652_c() + " " + scoreobjective.func_96678_d(), x, y, z, (int)ChatBubbles.NAME_TAG_RANGE);
			}

			// make the name render higher up
			y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * scale);
		}
	
		// now render their nametag
		renderName(entityPlayer, x, y, z);
	}

	/**
	 * Used to render a player's name above their head
	 */
	protected void renderName(EntityPlayer entityPlayer, double x, double y, double z) {
		// for now, only render names when the gui is enabled and players don't have potions on them
		if (Minecraft.isGuiEnabled() && entityPlayer != this.renderManager.livingPlayer && !entityPlayer.getHasActivePotion()) {
			double sqDistance = entityPlayer.getDistanceSqToEntity(this.renderManager.livingPlayer);

			if (sqDistance < (double)(ChatBubbles.NAME_TAG_RANGE * ChatBubbles.NAME_TAG_RANGE)) {
				// add the distance to the player as the username
				double distance = Math.sqrt(sqDistance);
				String nameString = ChatBubbles.getPlayerPrefix(entityPlayer.username) + entityPlayer.username + " \u00A7f- " + ((float)((int)(distance * 10f)) / 10f) + " [m]";
				
				// render the label!
				// don't handle sneaking or sleeping any differently
				this.renderLivingLabel(entityPlayer, nameString, x, y, z, distance, (int)ChatBubbles.NAME_TAG_RANGE);
				
				// get the player's chat
				LinkedList<ChatItem> chatLines = ChatBubbles.getChats(entityPlayer.username);
				// and render them!
				if(chatLines != null && chatLines.size() > 0) {
					renderChatBubble(entityPlayer, chatLines, x, y, z, distance);
				}
			}
		}
	}

	/**
	 * Draws the debug or playername text above a living
	 */
	protected void renderLivingLabel(EntityLiving entity, String labelString, double x, double y, double z, double distance, int maxDistance) {
	//protected void renderLivingLabel(EntityLiving par1EntityLiving, String par2Str, double par3, double par5, double par7, int par9)
		FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
		// set the scale
		//float scale = 0.016666668f * 1.6f;
		float scale = (float)(((double)distance * 0.00263D + 0.042369999999999998D) * (double)((ChatBubbles.TEXT_SCALE) / 2.0F));
		
		GL11.glPushMatrix();
			// make the string appear ABOVE the head
			GL11.glTranslatef((float)x + 0.0F, (float)y + 2.3F, (float)z);
			
			// rotate it to look at the player
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			
			// scale it appropriately
			GL11.glScalef(-scale, -scale, scale);
			
			// set the various render flags
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tesselator = Tessellator.instance;
	
			// adjust for deadmau5's ears
			byte earOffset = 0;
			if (labelString.startsWith("deadmau5"))
			{
				earOffset = -10;
			}
	
			// draw the background to the name
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tesselator.startDrawingQuads();
			int stringWidth = fontRenderer.getStringWidth(labelString) / 2;
			tesselator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(-1 + earOffset), 0.0D);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(8 + earOffset), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(8 + earOffset), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(-1 + earOffset), 0.0D);
			tesselator.draw();
			
			// now draw the actual name
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			// just always draw it as white, never fade it out
			tesselator.setColorRGBA_F(1f, 1f, 1f, 1f);
			fontRenderer.drawString(labelString, -fontRenderer.getStringWidth(labelString) / 2, earOffset, 0xffffffff);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			fontRenderer.drawString(labelString, -fontRenderer.getStringWidth(labelString) / 2, earOffset, 0xffffffff);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	/**
	 * Draws the chat lines above a living
	 */
	protected void renderChatBubble(EntityLiving entity, LinkedList<ChatItem> chatLines, double x, double y, double z, double distance) {
		FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
		float scale = (float)(((double)distance * 0.00263D + 0.042369999999999998D) * (double)((ChatBubbles.TEXT_SCALE) / 2.0F));
		
		GL11.glPushMatrix();
			// make the string appear ABOVE the head
			GL11.glTranslatef((float)x + 0.0F, (float)y + 2.3F, (float)z);
			
			// rotate it to look at the player
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			
			// scale it appropriately
			GL11.glScalef(-scale, -scale, scale);
			
			// set the various render flags
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tesselator = Tessellator.instance;
	
			// move us up based on the number of lines we're rendering
			int lineOffset = -10 * chatLines.size() - 10;
			int addedChatHeight = 10 * (chatLines.size() - 1);
			
			// calculate our maximum string width
			int stringWidth = 0;
			for(Iterator<ChatItem> it = chatLines.iterator(); it.hasNext();) {
				ChatItem ci = it.next();
				int sw = fontRenderer.getStringWidth(ci.chat) / 2;
				if(sw > stringWidth) {
					stringWidth = sw;
				}
			}
	
			// draw the background to the name
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			int padding = 3;
			/*tesselator.startDrawingQuads();
			tesselator.setColorRGBA_F(1f, 1f, 1f, 1f);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(-1 + lineOffset), 0.0D);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(8 + lineOffset + addedChatHeight), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(8 + lineOffset + addedChatHeight), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(-1 + lineOffset), 0.0D);
			tesselator.draw();*/
			// draw the locator pip
			tesselator.startDrawing(GL11.GL_TRIANGLES);
			tesselator.setColorRGBA_F(1f, 1f, 1f, 1f);
			tesselator.addVertex((double)(-padding), (double)(8 + lineOffset + addedChatHeight + padding), 0.0D);
			tesselator.addVertex((double)(0), (double)(8 + lineOffset + addedChatHeight + padding + padding), 0.0D);
			tesselator.addVertex((double)(padding), (double)(8 + lineOffset + addedChatHeight + padding), 0.0D);
			tesselator.draw();
			// draw the body of the chat bubble
			tesselator.startDrawing(GL11.GL_TRIANGLE_STRIP);
			tesselator.setColorRGBA_F(1f, 1f, 1f, 1f);
			tesselator.addVertex((double)(-stringWidth - 1 - padding), (double)(-1 + lineOffset), 0.0D);
			tesselator.addVertex((double)(-stringWidth - 1 - padding), (double)(8 + lineOffset + addedChatHeight), 0.0D);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(-1 + lineOffset - padding), 0.0D);
			tesselator.addVertex((double)(-stringWidth - 1), (double)(8 + lineOffset + addedChatHeight + padding), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(-1 + lineOffset - padding), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1), (double)(8 + lineOffset + addedChatHeight + padding), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1 + padding), (double)(-1 + lineOffset), 0.0D);
			tesselator.addVertex((double)(stringWidth + 1 + padding), (double)(8 + lineOffset + addedChatHeight), 0.0D);
			tesselator.draw();
			
			// now draw the actual name
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// now draw the lines
			int yOffset = 0;
			for(Iterator<ChatItem> it = chatLines.iterator(); it.hasNext(); yOffset += 10) {
				ChatItem ci = it.next();
				fontRenderer.drawString(ci.chat, -fontRenderer.getStringWidth(ci.chat) / 2, lineOffset + yOffset, 0xff000000);
			}
			
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			
			// redraw draw the lines
			yOffset = 0;
			for(Iterator<ChatItem> it = chatLines.iterator(); it.hasNext(); yOffset += 10) {
				ChatItem ci = it.next();
				fontRenderer.drawString(ci.chat, -fontRenderer.getStringWidth(ci.chat) / 2, lineOffset + yOffset, 0xff000000);
			}
			
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
