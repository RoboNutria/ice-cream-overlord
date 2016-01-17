package com.dcoppetti.lordcream.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.dcoppetti.lordcream.handlers.CollisionHandler;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

/**
 * Utility class to handle tmx map files.
 * -Handles box2d data thanks to dermetfan's utils.
 * -Parses layers to create collisions
 * -Renders the map (anything in the world like user data mas be handled by you)
 *
 * Remember to dispose after done with the object
 * 
 * @author Diego Coppetti
 */
public class TiledHandler {

	private TiledMap map;
	public static float MAP_WIDTH;
	public static float MAP_HEIGHT;
	private float tileSize;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Box2DMapObjectParser parser;
	private float ppm = 100f;

	public void setPpm(float ppm) {
		this.ppm = ppm;
	}

	// load the map without parsing box2d data
	public void loadTmxMap(String filepath, SpriteBatch batch) {
		map = new TmxMapLoader().load(filepath);
		loadProps();
		if(mapRenderer == null) {
			mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ppm, batch);
		} else {
			mapRenderer.setMap(map);
		}
	}
	
	private void loadProps() {
		MapProperties prop = map.getProperties();
		int width = prop.get("width", Integer.class);
		int height = prop.get("height", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);
		MAP_WIDTH = (width * tilePixelWidth) / ppm;
		MAP_HEIGHT = (height * tilePixelHeight) / ppm;
		tileSize = tilePixelWidth / ppm;
	}

	// load the map and parse box2d data
	public void loadTmxMap(String filepath, SpriteBatch batch, World world) {
		map = new TmxMapLoader().load(filepath);
		loadProps();
		if(mapRenderer == null) {
			mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ppm, batch);
		} else {
			mapRenderer.setMap(map);
		}
		parseBox2dMapObjects(world);
	}

	public void parseBox2dMapObjects(World world) {
		if(parser == null) {
			parser = new Box2DMapObjectParser(1/ppm);
		}
		parser.load(world, map);
		Body body = parser.getBodies().get("boundary");
		body.getFixtureList().first().setUserData(CollisionHandler.LEVEL_BOUNDARY);
		ObjectMap<String, Fixture> omFixtures = parser.getFixtures();
		Filter f = new Filter();
		f.categoryBits = CollisionHandler.CATEGORY_SCENARY;
		f.maskBits = CollisionHandler.MASK_SCENARY;
		for (Fixture fx: omFixtures.values()) {
			fx.setFilterData(f);
		}
	}

	public void parseBox2dMapObjects(World world, String layerName) {
		if(parser == null) {
			parser = new Box2DMapObjectParser(1/ppm);
		}
		MapLayer layer = map.getLayers().get(layerName);
		parser.load(world, layer);
		Body body = parser.getBodies().get("boundary");
		body.getFixtureList().first().setUserData(CollisionHandler.LEVEL_BOUNDARY);
		ObjectMap<String, Fixture> omFixtures = parser.getFixtures();
		Filter f = new Filter();
		f.categoryBits = CollisionHandler.CATEGORY_SCENARY;
		f.maskBits = CollisionHandler.MASK_SCENARY;
		for (Fixture fx: omFixtures.values()) {
			fx.setFilterData(f);
		}
	}

	public void renderMap(OrthographicCamera camera, int[] layers) {
		mapRenderer.setView(camera);
		mapRenderer.render(layers);
	}

	public void renderMap(int[] layers) {
		mapRenderer.render(layers);
	}

	public void renderMap(OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
	}

	public void dispose() {
		mapRenderer.dispose();
		map.dispose();
	}

	public Box2DMapObjectParser getBox2DMapParser() {
		return this.parser;
	}

	public TiledMap getMap() {
		return map;
	}

	public float getMapHeight() {
		return MAP_HEIGHT;
	}

	public float getMapWidth() {
		return MAP_WIDTH;
	}
	
	public float getTileSize() {
		return tileSize;
	}

	public Rectangle getBounds() {
		return null;
	}
	
}
