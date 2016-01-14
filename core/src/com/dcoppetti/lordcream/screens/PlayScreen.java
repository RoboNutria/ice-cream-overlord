package com.dcoppetti.lordcream.screens;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.DEBUG_MODE;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.SPRITES_PACK_FILE;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_HEIGHT;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_WIDTH;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.Hud;
import com.dcoppetti.lordcream.Level;
import com.dcoppetti.lordcream.entities.ChibiIceCream;
import com.dcoppetti.lordcream.entities.GameEntity;
import com.dcoppetti.lordcream.entities.Overlord;
import com.dcoppetti.lordcream.handlers.CameraHandler;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.utils.Assets;
import com.dcoppetti.lordcream.utils.TiledHandler;

/**
 * @author Diego Coppetti
 *
 */
public class PlayScreen implements Screen {

	private Game game;
	private Level level;
	private Color backColor = Color.DARK_GRAY;

	private SpriteBatch batch;
	private Viewport viewport;
	private OrthographicCamera cam;
	private CameraHandler camHandler;

	private World world;
	private final int velIter = 6, posIter = 2;
	private Box2DDebugRenderer debugRenderer;
	private Vector2 gravity = new Vector2(0, -6.8f);

	private TiledHandler tiledHandler;
	private Texture background;

	// player
	private Overlord overlord;

	// hud
	private Hud hud;

	public PlayScreen(Game game, Level level) {
		this.game = game;
		this.level = level;
	}

	@Override
	public void show() {
		// load player texture pack
		Assets.loadAtlas(SPRITES_PACK_FILE, true);

		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		viewport = new FitViewport(V_WIDTH / PPM, V_HEIGHT / PPM, cam);
		camHandler = new CameraHandler(cam);

		world = new World(gravity, true);
		if (DEBUG_MODE)
			debugRenderer = new Box2DDebugRenderer();

		// load tiled map
		tiledHandler = new TiledHandler();
		tiledHandler.setPpm(PPM);
		// this already parses the box2d data
		tiledHandler.loadTmxMap(level.getTmxFile(), batch);
		tiledHandler.parseBox2dMapObjects(world, "collisions");

		// load/parse entities from the map (including the player)
		level.parseGameEntities(world, tiledHandler, "entities", PPM);

		cam.position.set(level.getPlayerStartX(), tiledHandler.getMapHeight(),
				0);
		// create player

		Array<TextureRegion> idleRegions = Assets.getAtlasRegions(
				SPRITES_PACK_FILE, "idle", "-", 1);
		Array<TextureRegion> slideRegions = Assets.getAtlasRegions(
				SPRITES_PACK_FILE, "slide", "-", 1);
		Array<TextureRegion> jumpRegions = Assets.getAtlasRegions(
				SPRITES_PACK_FILE, "jump", "-", 1);
		Array<TextureRegion> wallRegions = Assets.getAtlasRegions(
				SPRITES_PACK_FILE, "wall", "-", 1);

		Vector2 position = new Vector2(level.getPlayerStartX(),
				level.getPlayerStartY());
		overlord = new Overlord(world, idleRegions.first(), position);
		overlord.setAnimationRegions(idleRegions, slideRegions, jumpRegions,
				wallRegions);

		camHandler.setTarget(overlord.getBody(), true);
		camHandler.setBoundX(tiledHandler.getMapWidth());
		camHandler.setBoundY(tiledHandler.getMapHeight());

		// the background
		background = new Texture(Gdx.files.internal(level.getBackgroundFile()));

		// create the hud
		hud = new Hud(batch);

		world.setContactListener(new CollisionHandler(overlord));

		Gdx.input.setInputProcessor(overlord.getInputHandler());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update the whole world
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for (Body b : bodies) {
			Object userData = b.getUserData();
			if (userData instanceof GameEntity) {
				if (userData instanceof ChibiIceCream
						&& ((ChibiIceCream) userData).shouldDestroy()
						&& ((ChibiIceCream) userData).rescueAnimationFinished()) {
					world.destroyBody(b);
					b.setUserData(null);
					b = null;
				} else {
					((GameEntity) userData).update(delta);
				}
			}
		}
		camHandler.update();
		hud.update(overlord);
		world.step(1f / 60f, velIter, posIter);
		// render all
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(background, cam.position.x - viewport.getWorldWidth() / 2,
				cam.position.y - viewport.getWorldHeight() / 2,
				viewport.getWorldWidth(), viewport.getWorldHeight());
		batch.end();
		tiledHandler.renderMap(cam);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		Box2DSprite.draw(batch, world, true);
		batch.end();

		hud.render();

		if (DEBUG_MODE)
			debugRenderer.render(world, cam.combined);
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
		if (DEBUG_MODE)
			debugRenderer.dispose();
		world.dispose();
		hud.dispose();
		background.dispose();
		Assets.dispose();
	}

}
