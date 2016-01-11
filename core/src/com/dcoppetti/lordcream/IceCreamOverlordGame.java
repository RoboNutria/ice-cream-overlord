package com.dcoppetti.lordcream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.dcoppetti.lordcream.screens.PlayScreen;

import java.util.HashMap;

/**
 * @author Diego Coppetti
 *
 */
public class IceCreamOverlordGame extends Game {
	
	public static final int W_WIDTH = 800;
	public static final int W_HEIGHT = 600;

	public static final int V_WIDTH = 160;
	public static final int V_HEIGHT = 128;

	public static final float PPM = 25;
	public static final int FPS = 60;

	public static boolean DEBUG_MODE = false;

	public static HashMap<String, Level> levels;

	@Override
	public void create() {
		setGameLevels();
		setScreen(getInitialScreen());
	}

	private void setGameLevels() {
		levels = new HashMap<String, Level>();
		Level level;

		level = new Level(1, "level 1-1", "maps/stage-1-1.tmx", "textures/planet.png");
		levels.put(level.getLevelName(), level);
		level = new Level(1, "level 1-2", "maps/stage-1-2.tmx", "textures/planet.png");
		levels.put(level.getLevelName(), level);
	}

	private Screen getInitialScreen() {
		return new PlayScreen(this, levels.get("level 1-1"));
	}

	public void setPlayScreen(Level level) {
		setScreen(new PlayScreen(this, level));
	}
	
}
