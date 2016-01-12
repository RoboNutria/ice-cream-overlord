package com.dcoppetti.lordcream.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

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
	private float mapWidth;
	private float mapHeight;
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
		mapWidth = (width * tilePixelWidth) / ppm;
		mapHeight = (height * tilePixelHeight) / ppm;
		tileSize = tilePixelWidth / ppm;
		System.out.println(width);
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
	}

	public void parseBox2dMapObjects(World world, String layerName) {
		if(parser == null) {
			parser = new Box2DMapObjectParser(1/ppm);
		}
		MapLayer layer = map.getLayers().get(layerName);
		parser.load(world, layer);
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

	public TiledMap getMap() {
		return map;
	}

	public float getMapHeight() {
		return mapHeight;
	}

	public float getMapWidth() {
		return mapWidth;
	}
	
	public float getTileSize() {
		return tileSize;
	}

	public Rectangle getBounds() {
		return null;
	}
	
}
