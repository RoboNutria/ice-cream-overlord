package com.dcoppetti.lordcream;

import java.util.Iterator;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame.EnemyTypes;
import com.dcoppetti.lordcream.IceCreamOverlordGame.Items;
import com.dcoppetti.lordcream.IceCreamOverlordGame.PlayerObjects;
import com.dcoppetti.lordcream.utils.TiledHandler;

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
            if(object.getName().equals(PlayerObjects.player_ship.name())) {
                playerStartX = x;
                playerStartY = y;
            }
            if(object.getName().equals(Items.chibi_ice_cream.name())) {
            	// TODO: Tafok do we get the region from?
            	// TIP: Just add Assets class from BaseGame to the utils and load the texture from there in PlayScreen
            	// Then do something like Assets.getTexture here and create a new region

            	//TextureRegion region = null;
            	//new ChibiIceCream(region, world, new Vector2(x, y));
            }
            if(object.getName().equals(EnemyTypes.enemey_flying_firing_fish.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_flying_fish.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_mutant_floor_snail.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_mutant_walking_rat.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_mutant_wall_snail.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_standing_alien.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_tank_alien.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_turret_floor.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_turret_roof.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemey_walking_alien.name())) {
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
