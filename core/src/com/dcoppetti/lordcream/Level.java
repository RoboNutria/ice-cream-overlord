package com.dcoppetti.lordcream;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame.EnemyTypes;
import com.dcoppetti.lordcream.IceCreamOverlordGame.Misc;
import com.dcoppetti.lordcream.IceCreamOverlordGame.PlayerObjects;
import com.dcoppetti.lordcream.entities.AlienSoldierEnemy;
import com.dcoppetti.lordcream.utils.Assets;
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
        loadLevelAssets();
    }

    // always load them, if they already exist they won't be reloaded so it's all good
	private void loadLevelAssets() {
		Assets.loadTexture("textures/dummy-8.png");
	}

	public void parseGameEntities(World world, TiledHandler tileHandler, String layerName, float ppm) {
        MapObjects objects = tileHandler.getMap().getLayers().get(layerName).getObjects();
        Array<RectangleMapObject> rectangleObjects = objects.getByType(RectangleMapObject.class);
        for(Iterator<RectangleMapObject> iterator = rectangleObjects.iterator(); iterator.hasNext(); ) {
            RectangleMapObject object = iterator.next();
            Rectangle rect = object.getRectangle();
            rect.x = rect.x / ppm;
            rect.y = rect.y / ppm;
            rect.width = rect.width / ppm;
            rect.height = rect.height / ppm;
            float x = rect.x;
            float y = rect.y;
            float offset = tileHandler.getTileSize()/2;
            y = y + offset;
            x = x + offset;
            if(object.getName().equals(PlayerObjects.player_ship.name())) {
                playerStartX = x;
                playerStartY = y;
            }
            if(object.getName().equals(Misc.chibi_ice_cream.name())) {
            	// TODO: Tafok do we get the region from?
            	// TIP: Just add Assets class from BaseGame to the utils and load the texture from there in PlayScreen
            	// Then do something like Assets.getTexture here and create a new region

            	//TextureRegion region = null;
            	//new ChibiIceCream(region, world, new Vector2(x, y));
            }
            if(object.getName().equals(Misc.death_zone.name())) {
            	BodyDef bdef = new BodyDef();
            	bdef.position.set(new Vector2(rect.x + rect.width/2f, rect.y + rect.height/2f));
            	bdef.fixedRotation = true;
            	PolygonShape shape = new PolygonShape();
            	shape.setAsBox(rect.width/2, rect.height/2);
            	FixtureDef fdef = new FixtureDef();
            	fdef.isSensor = true;
            	fdef.shape = shape;
            	Body b = world.createBody(bdef);
            	b.setUserData(Misc.death_zone.name());
            	b.createFixture(fdef);
            	shape.dispose();
            }
            if(object.getName().equals(EnemyTypes.enemy_flying_firing_fish.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_flying_fish.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_mutant_floor_snail.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_mutant_walking_rat.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_mutant_wall_snail.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_standing_alien.name())) {
            	TextureRegion region = new TextureRegion(Assets.getTexture("textures/dummy-8.png"));
            	new AlienSoldierEnemy(world, region, new Vector2(x, y));
            }
            if(object.getName().equals(EnemyTypes.enemy_tank_alien.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_turret_floor.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_turret_roof.name())) {
            }
            if(object.getName().equals(EnemyTypes.enemy_walking_alien.name())) {
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
