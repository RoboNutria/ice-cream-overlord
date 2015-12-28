package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

/**
 * This class indicates the input that is desired for the player to execute
 * 
 * @author Diego Coppetti
 *
 */
public class PlayerInputHandler extends InputAdapter {
	
	public boolean left = false;
	public boolean right = false;
	public boolean jump = false;
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.A:
			left = true;
			break;
		case Keys.D:
			right = true;
			break;
		case Keys.W:
			jump = true;
			break;
		default:
			break;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.A:
			left = false;
			break;
		case Keys.D:
			right = false;
			break;
		case Keys.W:
			jump = false;
			break;
		default:
			break;
		}
		return true;
	}

}
