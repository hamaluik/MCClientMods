package com.mcnsa.utilitybelt;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiEditSign;
import cpw.mods.fml.client.FMLClientHandler;

public class UtilityMenu {
	ArrayList<UtilityButton> menuButtons = new ArrayList<UtilityButton>();
	
	public String name = "root";
	public double radius = 128;
	public double minSelectionRadius = 64;
	
	int currentSelection = -1;
	
	public UtilityMenu(String name) {
		this.name = name;
	}
	
	// re-arrange the buttons so they're spaced evenly around the circle
	void refactorButtons() {
		double angle = 0f;
		double deltaAngle = 2.0 * Math.PI / (double)menuButtons.size();
		
		for(int i = 0; i < menuButtons.size(); i++) {
			menuButtons.get(i).theta = angle;
			angle += deltaAngle;
		}
		
		// now calculate our radius
		// TODO: check that its a good formula.. (48 <= radius <= 96)
		radius = Math.max(Math.min(menuButtons.size() * 16, 96), 48);
		minSelectionRadius = radius / 2;
	}
	
	// add a button to this menu's list
	void addButton(UtilityButton button) {
		menuButtons.add(button);
		refactorButtons();
	}
	
	void render() {
		// get the mouse position
        int[] scSize = GraphicUtils.getScreenSize();
        int mx = Mouse.getX() * (scSize[0] + 5) / FMLClientHandler.instance().getClient().displayWidth;
        int my = (scSize[1] + 5) - Mouse.getY() * (scSize[1] + 5) / FMLClientHandler.instance().getClient().displayHeight - 1;
        
		// calculate the angle of the arrow
		double angle = Math.toDegrees(Math.atan2(my - (scSize[1] / 2), mx - (scSize[0] / 2))) + 90;
		
		// figure out which one is selected
		currentSelection = -1;
		if(Math.sqrt(Math.pow((double)(mx - (scSize[0]/2)), 2) + Math.pow((double)(my - (scSize[1]/2)), 2)) >= minSelectionRadius) {
			// clamp our angle
			while(angle < 0) {
				angle += 360;
			}
			while(angle > 360) {
				angle -= 360;
			}
			
			currentSelection = menuButtons.size() - 1;
			int span = 360 / menuButtons.size();
			int offset1 = 90 - span / 2;
			int offset2 = 90 + span / 2;
			for(int i = 0; i < menuButtons.size() - 1; i++) {
				if(angle >= (i * span + offset1) && angle < (i * span + offset2)) {
					currentSelection = i;
				}
			}
		}
		
		// go through and render all of our buttons
		for(int i = 0; i < menuButtons.size(); i++) {
			menuButtons.get(i).render(radius, currentSelection == i);
		}
	}
	
	String getSelectedMacro() {
		if(currentSelection < 0) {
			return "";
		}
		
		return menuButtons.get(currentSelection).macro;
	}
}
