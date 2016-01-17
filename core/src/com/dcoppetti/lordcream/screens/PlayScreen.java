package com.dcoppetti.lordcream.screens;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.CAMERA_HANDLER;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.DEBUG_MODE;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.SPRITES_PACK_FILE;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_HEIGHT;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_WIDTH;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.Hud;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.Level;
import com.dcoppetti.lordcream.entities.Overlord;
import com.dcoppetti.lordcream.handlers.CameraHandler;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.handlers.EntityHandler;
import com.dcoppetti.lordcream.utils.Assets;
import com.dcoppetti.lordcream.utils.TiledHandler;

/**
 * @author Diego Coppetti
 *
 */
public class PlayScreen implements Screen {

	private IceCreamOverlordGame game;
	private Level level;
	private Color backColor = Color.BLACK;

	
	// I'm using 2 batches because when I changed tint of a sprite the other textures also got affected
	// I'd have to check b2dsprite class to see, but no time :V
	private SpriteBatch batch;
	private Viewport viewport;
	private OrthographicCamera cam;
	private EntityHandler entityHandler;
	
	private World world;
	private final int velIter = 6, posIter = 2;
	private Box2DDebugRenderer debugRenderer;
	private Vector2 gravity = new Vector2(0, -6.8f);
	
	private TiledHandler tiledHandler;
	private Texture background;

	// screen transition
	private boolean transitionDone = false;
	private String transitionAction = null;
	private boolean endLevel = false;
	
	// player
	private Overlord overlord;
	
	// hud
	private Hud hud;
	public static short chibiAmount;
	public static short rescueAmount;
	// level progress flags
	public static boolean getBackToTheShip = false;
	public static boolean levelSuccess = false; // NOTE: The entity PlayerShip sets this to true

	public PlayScreen(IceCreamOverlordGame game, Level level) {
		this.game = game;
		this.level = level;
		entityHandler = new EntityHandler(true);
	}
	

	@Override
	public void show() {
		getBackToTheShip = false;
		levelSuccess = false;
		batch = new SpriteBatch();
		batch.setColor(0, 0, 0, 0);

		chibiAmount = 0;
		rescueAmount = 0;

		cam = new OrthographicCamera();
		viewport = new FitViewport(V_WIDTH/PPM, V_HEIGHT/PPM, cam);
		CAMERA_HANDLER = new CameraHandler(cam);
		
		world = new World(gravity, true);
		if(DEBUG_MODE) debugRenderer = new Box2DDebugRenderer();
		
		// load tiled map
		tiledHandler = new TiledHandler();
		tiledHandler.setPpm(PPM);
		// this already parses the box2d data
		tiledHandler.loadTmxMap(level.getTmxFile(), batch);
		tiledHandler.parseBox2dMapObjects(world, "collisions");

		// load/parse entities from the map (including the player)
		level.parseGameEntities(world, tiledHandler, "entities", PPM);

		cam.position.set(level.getPlayerStartX(), tiledHandler.getMapHeight(), 0);
		// create player
		
		Array<TextureRegion> idleRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "idle", "-", 1);
		Array<TextureRegion> slideRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "slide", "-", 1);
		Array<TextureRegion> jumpRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "jump", "-", 1);
		Array<TextureRegion> wallRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "wall", "-", 1);
		
		Vector2 position = new Vector2(level.getPlayerStartX(), level.getPlayerStartY());
		overlord = new Overlord(world, idleRegions.first(), position);
		overlord.setAnimationRegions(idleRegions, slideRegions, jumpRegions, wallRegions);
		
		CAMERA_HANDLER.setTarget(overlord.getBody(), true);
		CAMERA_HANDLER.setBoundX(tiledHandler.getMapWidth());
		CAMERA_HANDLER.setBoundY(tiledHandler.getMapHeight());
		
		// the background
		if(level.getBackgroundFile() != null) {
			background = Assets.loadTexture(level.getBackgroundFile());
		}
		

		world.setContactListener(new CollisionHandler(overlord));

		// create the hud
		hud = new Hud(batch, level);
		hud.getStage().getRoot().getColor().a = 0;
		hud.getStage().getRoot().addAction(Actions.fadeIn(0.25f));
		
		Gdx.input.setInputProcessor(new InputMultiplexer(overlord.getInputHandler(), new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.ESCAPE && transitionAction == null) {
					changeScreen("menu", 0);
				}
				return false;
			}
		}));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(transitionDone) {
			if(transitionAction.equals("restart")) {
				game.setPlayScreen(level);
			} else if(transitionAction.equals("menu")) {
				game.setScreen(new LevelSelectScreen(game));
			}
       		return;
		}
		
		
		CAMERA_HANDLER.update();
		
		checkLevelProgress();

		entityHandler.updateFromWorld(world, delta);

		hud.update(overlord, delta);
		world.step(1f/60f, velIter, posIter);

		// render all
		batch.setProjectionMatrix(cam.combined);
		if(background != null) {
			batch.begin();
			batch.draw(background, cam.position.x-viewport.getWorldWidth()/2, cam.position.y-viewport.getWorldHeight()/2, viewport.getWorldWidth(), viewport.getWorldHeight());
			batch.end();
		}
		tiledHandler.renderMap(cam);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		Box2DSprite.draw(batch, world, true);
		batch.end();
		hud.render();
		
		if(getBackToTheShip) {
			batch.begin();
			batch.end();
		}
		
		if(DEBUG_MODE) debugRenderer.render(world, cam.combined);
		
		if(overlord.gameOver && transitionAction == null) {
			changeScreen("restart", 0);
			return;
		}
		
		if(endLevel) {
			endLevel = false;
			displaySuccessMessages();
			updateLevels();
			hud.stopTimer();
			changeScreen("menu", 3);
		}
	}

	private void updateLevels() {
		level.getLevelData().setBestTime(hud.getTime());
		Level next = level.getNext();
		if(next != null) {
			next.getLevelData().setUnlocked(true);
		}
		game.save();
	}


	private void displaySuccessMessages() {
		Vector2 pos = new Vector2(V_WIDTH-30, V_HEIGHT+30);
		hud.displayMessage("SUCCESS!", pos);
	}


	private void checkLevelProgress() {
		if(!getBackToTheShip && PlayScreen.rescueAmount == PlayScreen.chibiAmount) {
			hud.displayMessage("Get back to the ship!");
			getBackToTheShip = true;
		}
		else if(levelSuccess) {
			levelSuccess = false;
			transitionAction = "levelSuccess";
			overlord.setWillDie(true);
			overlord.tweenFadeOut();
			hud.clearMessages();
			endLevel = true;
		}
	}

	private void changeScreen(final String transitionAction, float delay) {
		this.transitionAction = transitionAction;
		Stage stage = hud.getStage();
		stage.getRoot().getColor().a = 1;
	    SequenceAction sequenceAction = new SequenceAction();
	    sequenceAction.addAction(Actions.delay(delay));
	    sequenceAction.addAction(Actions.fadeOut(0.25f));
	    sequenceAction.addAction(Actions.run(new Runnable() {
	        @Override
	        public void run() {
	        	transitionDone = true;
	        }
	    }));
	    stage.getRoot().addAction(sequenceAction);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
		tiledHandler.dispose();
		if(DEBUG_MODE) debugRenderer.dispose();
		world.dispose();
		hud.dispose();
		entityHandler.disposeInactive();
		CAMERA_HANDLER = null;
		Gdx.input.setInputProcessor(null);
	}

}
