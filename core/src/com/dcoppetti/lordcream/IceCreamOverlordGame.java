package com.dcoppetti.lordcream;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
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
	public static boolean DEBUG_MODE = true;

	public static final String SPRITES_PACK_FILE = "textures/sprites.pack";
	private static final String SAVE_DATA_FILE = "data.dat";

	public static CameraHandler CAMERA_HANDLER;
	public static TweenManager TWEEN_MANAGER;

	public static final String menuMusic = "music/what-a-nice-surprise.mp3";
	public static final String levelMusic = "music/the-sleeping-funk.mp3";
	public static final String levelMusic2 = "music/adventure.mp3";

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
		Assets.loadMusic(menuMusic);
		Assets.loadMusic(levelMusic);
		Assets.loadMusic(levelMusic2);
		setScreen(getInitialScreen());
	}
	
	@Override
	public void render() {
		super.render();
		TWEEN_MANAGER.update(Gdx.graphics.getDeltaTime());
	}

	private void setGameLevels() {
		levels = new Array<Level>();

		Level level1 = new Level("Alien ruins 1", "maps/stage-1-1.tmx", "textures/planet.png");
		Level level2 = new Level("Alien ruins 2", "maps/stage-1-2.tmx", "textures/planet-2.png");
		Level level3 = new Level("Slug Cave", "maps/stage-1-3.tmx", "textures/spacefield_a-000.png");
		Level level4 = new Level("Red Sluggers", "maps/stage-1-4.tmx", "textures/planet-4.png");
		Level level5 = new Level("Acid Falss", "maps/stage-1-5.tmx", "textures/planet-2.png");
		

		level1.setNext(level2);
		level2.setNext(level5);
		level5.setNext(level3);

		levels.add(level1);
		levels.add(level2);
		levels.add(level3);
		levels.add(level4);
		levels.add(level5);

		if(Gdx.files.local(SAVE_DATA_FILE).exists()) {
			load();
		} else {
			// Set here how you want it to be, the level unlocking and default par times
			level1.getLevelData().setParTime("01:10:00"); // verified
			level2.getLevelData().setParTime("01:30:00"); // verified
			level3.getLevelData().setParTime("01:25:00"); // verified
			level4.getLevelData().setParTime("01:15:00"); // not verified
			level5.getLevelData().setParTime("01:10:05"); // not verified
			
			level2.getLevelData().setUnlocked(false);
			level4.getLevelData().setUnlocked(false);
			
			level1.setNext(level2);
			level3.setNext(level4);
			
		}
	}

	private Screen getInitialScreen() {
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

	public void save() {
		Json json = new Json();
		Array<LevelData> levelData = new Array<LevelData>();
		for(Level l : levels) {
			LevelData ld = l.getLevelData();
			levelData.add(ld);
		}
		Gdx.files.local(SAVE_DATA_FILE).writeString(json.toJson(levelData, Array.class), false);
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		Json json = new Json();
		Array<LevelData> array = new Array<LevelData>();
		array = json.fromJson(Array.class, Gdx.files.local(SAVE_DATA_FILE));
		for(int i = 0 ; i < levels.size ; i++) {
			Level l = levels.get(i);
			LevelData ld = array.get(i);
			l.setLevelData(ld);
		}
	}
}
