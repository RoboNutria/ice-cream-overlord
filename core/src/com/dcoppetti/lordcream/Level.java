package com.dcoppetti.lordcream;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.utils.TiledHandler;

import java.util.Iterator;

/**
 * @author Diego Coppetti
 */
public class Level {

    private String levelName;
    private String tmxFile;
    private String backgroundFile;
    private float playerStartX;
    private float playerStartY;
    private int world;

    public Level(int world, String levelName, String tmxFile, String backgroundFile) {
        this.world = world;
        this.levelName = levelName;
        this.tmxFile = tmxFile;
        this.backgroundFile = backgroundFile;
    }

    public void parseGameEntities(World world, TiledHandler tileHandler, String layerName, float ppm) {
        MapObjects objects = tileHandler.getMap().getLayers().get(layerName).getObjects();
        Array<RectangleMapObject> rectangleObjects = objects.getByType(RectangleMapObject.class);
        for(Iterator<RectangleMapObject> iterator = rectangleObjects.iterator(); iterator.hasNext(); ) {
            RectangleMapObject object = iterator.next();
            Rectangle rect = object.getRectangle();
            float x = rect.x / ppm;
            float y = rect.y / ppm;
            if(object.getName().equals("player pod")) {
                playerStartX = x;
                playerStartY = y;
            }
            if(object.getName().equals("rescue")) {
            }
            if(object.getName().equals("enemy 1")) {
            }
            if(object.getName().equals("turret 1")) {
            }
        }
    }

    public String getTmxFile() {
        return tmxFile;
    }

    public String getBackgroundFile() {
        return backgroundFile;
    }

    public String getLevelName() {
        return levelName;
    }

    public float getPlayerStartX() {
        return this.playerStartX;
    }

    public float getPlayerStartY() {
        return this.playerStartY;
    }
}
