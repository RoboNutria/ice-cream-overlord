package com.dcoppetti.lordcream;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.handlers.CameraHandler;
import com.dcoppetti.lordcream.screens.PlayScreen;
import com.dcoppetti.lordcream.screens.TitleScreen;
import com.dcoppetti.lordcream.utils.Assets;

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

	public static final String SPRITES_PACK_FILE = "textures/sprites.pack";

	public static CameraHandler CAMERA_HANDLER;
	public static TweenManager TWEEN_MANAGER;

	public Array<Level> levels;
	
	public static enum EnemyTypes {
		enemy_standing_alien,
		enemy_walking_alien,
		enemy_tank_alien,
		enemy_turret_floor,
		enemy_turret_roof,
		enemy_mutant_walking_rat,
		enemy_slug_floor,
		enemy_slug_goomba,
		enemy_chobi,
		enemy_flying_fish,
		enemy_flying_firing_fish,
	};
	
	public static enum PlayerObjects {
		player_ship
	};
	
	public static enum Misc {
		chibi_ice_cream,
		death_zone,
	};
	
	public static enum EnemyTriggers {
		bumper,
	}

	@Override
	public void create() {
		setGameLevels();
		TWEEN_MANAGER = new TweenManager();
		// load player texture pack
		Assets.loadAtlas(SPRITES_PACK_FILE, true);
		setScreen(getInitialScreen());
	}
	
	@Override
	public void render() {
		super.render();
		TWEEN_MANAGER.update(Gdx.graphics.getDeltaTime());
	}

	private void setGameLevels() {
		levels = new Array<Level>();

		Level level1 = new Level("level 1-1", "maps/stage-1-2.tmx", "textures/planet.png");
		Level level2 = new Level("level 1-2", "maps/stage-1-2.tmx", "textures/planet.png");
		Level level3 = new Level("Dark Slug Cave 1", "maps/stage-1-2.tmx", "textures/spacefield_a-000.png");
		Level level4 = new Level("level 1-4", "maps/stage-1-2.tmx", "textures/planet-4.png");
		Level level5 = new Level("level 1-5", "maps/stage-1-2.tmx", "textures/planet-2.png");
		
		level2.getLevelData().setUnlocked(false);
		level3.getLevelData().setUnlocked(false);
		level5.getLevelData().setUnlocked(false);

		level1.setNext(level2);
		level2.setNext(level5);
		level5.setNext(level3);

		levels.add(level1);
		levels.add(level2);
		levels.add(level3);
		levels.add(level4);
		levels.add(level5);
	}

	private Screen getInitialScreen() {
//		return new PlayScreen(this, levels.get("Dark Slug Cave 1"));
		//return new PlayScreen(this, levels.get("level 1-4"));
		return new TitleScreen(this);
	}

	public void setPlayScreen(Level level) {
		setScreen(new PlayScreen(this, level));
	}

	@Override
	public void dispose() {
		CAMERA_HANDLER = null;
		TWEEN_MANAGER = null;
		super.dispose();
		Assets.dispose();
		Gdx.input.setInputProcessor(null);
	}
}
