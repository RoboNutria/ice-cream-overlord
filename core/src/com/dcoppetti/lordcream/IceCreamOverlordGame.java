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

	public static boolean DEBUG_MODE = true;

	public static HashMap<String, Level> levels;
	
	public static enum EnemyTypes {
		enemey_standing_alien,
		enemey_walking_alien,
		enemey_tank_alien,
		enemey_turret_floor,
		enemey_turret_roof,
		enemey_mutant_walking_rat,
		enemey_mutant_floor_snail,
		enemey_mutant_wall_snail,
		enemey_flying_fish,
		enemey_flying_firing_fish,
	};
	
	public static enum PlayerObjects {
		player_ship
	};
	
	public static enum Items {
		chibi_ice_cream
	};

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
