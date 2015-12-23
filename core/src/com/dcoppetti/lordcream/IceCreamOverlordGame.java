package com.dcoppetti.lordcream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.dcoppetti.lordcream.screens.PlayScreen;

/**
 * @author Diego Coppetti
 *
 */
public class IceCreamOverlordGame extends Game {
	
	public static final int W_WIDTH = 640;
	public static final int W_HEIGHT = 480;

	public static final int V_WIDTH = 640;
	public static final int V_HEIGHT = 480;

	public static final float PPM = 100;
	public static final int FPS = 60;

	public static boolean DEBUG_MODE = true;

	@Override
	public void create() {
		setScreen(getInitialScreen());
	}

	private Screen getInitialScreen() {
		return new PlayScreen(this);
	}
	
}
