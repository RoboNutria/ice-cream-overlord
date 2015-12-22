package com.dcoppetti.lordcream.utils;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Utility class to handle tmx map files.
 * It can also handle box2d data thanks to dermetfan's utils.
 * Don't remember to dispose after done with the object
 * 
 * @author Diego Coppetti
 */
public class TiledHandler {

	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Box2DMapObjectParser parser;
	private float ppm = 100f;

	public void setPpm(float ppm) {
		this.ppm = ppm;
	}

	// load the map without parsing box2d data
	public void loadTmxMap(String filepath, SpriteBatch batch) {
		map = new TmxMapLoader().load(filepath);
		if(mapRenderer == null) {
			mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ppm, batch);
		} else {
			mapRenderer.setMap(map);
		}
	}
	
	// load the map and parse box2d data
	public void loadTmxMap(String filepath, SpriteBatch batch, World world) {
		map = new TmxMapLoader().load(filepath);
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

	public void renderMap(OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
	}

	public void dispose() {
		mapRenderer.dispose();
		map.dispose();
	}

}
