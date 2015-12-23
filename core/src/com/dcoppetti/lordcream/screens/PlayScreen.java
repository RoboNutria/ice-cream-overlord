package com.dcoppetti.lordcream.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.entities.Overlord;
import com.dcoppetti.lordcream.utils.TiledHandler;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.*;

/**
 * @author Diego Coppetti
 *
 */
public class PlayScreen implements Screen {
	
	private Game game;
	private Color backColor = Color.DARK_GRAY;
	
	private SpriteBatch batch;
	private Viewport viewport;
	private OrthographicCamera cam;
	
	private World world;
	private final int velIter = 6, posIter = 2;
	private Box2DDebugRenderer debugRenderer;
	private Vector2 gravity = new Vector2(0, -8.8f);
	
	private TiledHandler tiledHandler;
	private String tmxFile = "maps/debug-level.tmx";

	// player
	private Overlord overlord;
	private String overlordFile = "textures/test-neko.png";
	private Texture overlordTexture;

	public PlayScreen(Game game) {
		this.game = game;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		viewport = new FitViewport(V_WIDTH/PPM, V_HEIGHT/PPM, cam);
		cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
		
		world = new World(gravity, true);
		if(DEBUG_MODE) debugRenderer = new Box2DDebugRenderer();
		
		// load tiled map
		tiledHandler = new TiledHandler();
		tiledHandler.setPpm(PPM);
		// this already parses the box2d data
		tiledHandler.loadTmxMap(tmxFile, batch, world);

		// create player
		overlordTexture = new Texture(overlordFile);
		Vector2 position = new Vector2(cam.position.x, cam.position.y);
		overlord = new Overlord(world, new TextureRegion(overlordTexture), position);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update game entities
		overlord.update(delta);
		world.step(delta, velIter, posIter);

		tiledHandler.renderMap(cam);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		Box2DSprite.draw(batch, world);
		batch.end();
		
		if(DEBUG_MODE) debugRenderer.render(world, cam.combined);
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
		overlordTexture.dispose();
	}

}
