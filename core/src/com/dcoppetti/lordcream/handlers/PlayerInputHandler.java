package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.Input.Keys;
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
	public boolean fire = false;
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
		case Keys.A:
			left = true;
			break;
		case Keys.RIGHT:
		case Keys.D:
			right = true;
			break;
		case Keys.W:
			jump = true;
			break;
		case Keys.UP:
			jump = true;
			break;
		case Keys.SPACE:
			fire = true;
			break;
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.UP:
			jump = false;
			break;
		case Keys.LEFT:
		case Keys.A:
			left = false;
			break;
		case Keys.RIGHT:
		case Keys.D:
			right = false;
			break;
		case Keys.W:
			jump = false;
			break;
		case Keys.SPACE:
			fire = false;
			break;
		default:
			break;
		}
		return false;
	}

}
