package com.dcoppetti.lordcream;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.SPRITES_PACK_FILE;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame.EnemyTriggers;
import com.dcoppetti.lordcream.IceCreamOverlordGame.EnemyTypes;
import com.dcoppetti.lordcream.IceCreamOverlordGame.Misc;
import com.dcoppetti.lordcream.IceCreamOverlordGame.PlayerObjects;
import com.dcoppetti.lordcream.ai.WalkAccelBumpBehavior;
import com.dcoppetti.lordcream.ai.WalkBumpBehavior;
import com.dcoppetti.lordcream.entities.ChibiIceCream;
import com.dcoppetti.lordcream.entities.PlayerShip;
import com.dcoppetti.lordcream.entities.SlugEnemy;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.utils.Assets;
import com.dcoppetti.lordcream.utils.TiledHandler;

/**
 * @author Diego Coppetti
 */
public class Level {

	private String tmxFile;
	private String backgroundFile;
	private float playerStartX;
	private float playerStartY;
	private Level next;
	
	private LevelData levelData;

	public Level(String levelName, String tmxFile, String backgroundFile) {
		this.tmxFile = tmxFile;
		this.backgroundFile = backgroundFile;
		this.levelData = new LevelData(levelName);
	}
	
	public void setNext(Level next) {
		this.next = next;
	}
	
	public Level getNext() {
		return this.next;
	}

	public void parseGameEntities(World world, TiledHandler tileHandler, String layerName, float ppm) {
		MapObjects objects = tileHandler.getMap().getLayers().get(layerName).getObjects();
		Array<RectangleMapObject> rectangleObjects = objects.getByType(RectangleMapObject.class);

		// texture regions for the entities
		Array<TextureRegion> slugIdleRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "slug-idle", "-", 1);
		Array<TextureRegion> slugSlideRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "slug-slide", "-", 1);
		Array<TextureRegion> goombaIdleRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "chobi-idle", "-", 1);
		Array<TextureRegion> goombaSlideRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "chobi-walk", "-", 1);

		for (Iterator<RectangleMapObject> iterator = rectangleObjects.iterator(); iterator.hasNext();) {
			RectangleMapObject object = iterator.next();
			Rectangle rect = object.getRectangle();
			rect.x = rect.x / ppm;
			rect.y = rect.y / ppm;
			rect.width = rect.width / ppm;
			rect.height = rect.height / ppm;
			float x = rect.x;
			float y = rect.y;
			float offset = tileHandler.getTileSize() / 2;
			y = y + offset;
			x = x + offset;
			if (object.getName().equals(PlayerObjects.player_ship.name())) {
				playerStartX = x;
				playerStartY = y;
				// create ship collider (for ending the level)
				new PlayerShip(world, new Vector2(rect.width, rect.height), new Vector2(rect.x, rect.y));
			}
			else if (object.getName().equals(Misc.chibi_ice_cream.name())) {
				Array<TextureRegion> idleRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "chibi-idle", "-", 1);
				Array<TextureRegion> rescueRegions = Assets.getAtlasRegions(SPRITES_PACK_FILE, "chibi-rescue", "-", 1);
				ChibiIceCream chibiIcecream = new ChibiIceCream(world, idleRegions.first(), new Vector2(x, y));
				chibiIcecream.setAnimationRegions(idleRegions, rescueRegions);
			}
			else if (object.getName().equals(Misc.death_zone.name())) {
				BodyDef bdef = new BodyDef();
				bdef.position.set(new Vector2(rect.x + rect.width / 2f, rect.y + rect.height / 2f));
				bdef.fixedRotation = true;
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(rect.width / 2, rect.height / 2);
				FixtureDef fdef = new FixtureDef();
				fdef.isSensor = true;
				fdef.shape = shape;
				fdef.filter.categoryBits = CollisionHandler.CATEGORY_SCENARY;
				fdef.filter.maskBits = CollisionHandler.MASK_SCENARY;
				Body b = world.createBody(bdef);
				Fixture f = b.createFixture(fdef);
				f.setUserData(Misc.death_zone.name());
				shape.dispose();
			}
			else if (object.getName().equals(EnemyTypes.enemy_slug_floor.name())) {
				SlugEnemy slug = new SlugEnemy(world, slugIdleRegions.first(), new Vector2(x, y), rect.width);
				slug.setAnimationRegions(slugIdleRegions, slugSlideRegions);
				
				WalkBumpBehavior aiWalkBump = new WalkBumpBehavior(slug, -0.5f);
				slug.addAiBehavior(aiWalkBump);
			}
			else if (object.getName().equals(EnemyTypes.enemy_slug_goomba.name())) {
				SlugEnemy slug = new SlugEnemy(world, goombaIdleRegions.first(), new Vector2(x, y), rect.width);
				slug.setAnimationRegions(goombaIdleRegions, goombaSlideRegions);
				WalkAccelBumpBehavior aiWalkAccelBump = new WalkAccelBumpBehavior(slug, -2f, 3);
				slug.addAiBehavior(aiWalkAccelBump);
			}
			else if (object.getName().equals(EnemyTriggers.bumper.name())) {
				BodyDef bdef = new BodyDef();
				bdef.position.set(new Vector2(rect.x + rect.width / 2f, rect.y + rect.height / 2f));
				bdef.fixedRotation = true;
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(rect.width / 2, rect.height / 2);
				FixtureDef fdef = new FixtureDef();
				fdef.isSensor = true;
				fdef.shape = shape;
				fdef.filter.categoryBits = CollisionHandler.CATEGORY_TILED_SENSOR;
				fdef.filter.maskBits = CollisionHandler.MASK_TILED_SENSOR;
				Body b = world.createBody(bdef);
				Fixture f = b.createFixture(fdef);
				f.setUserData(EnemyTriggers.bumper.name());
				shape.dispose();
			}
		}
	}

    public String getTmxFile() {
        return tmxFile;
    }

    public String getBackgroundFile() {
        return backgroundFile;
    }
    
    public LevelData getLevelData() {
    	return this.levelData;
    }

    public float getPlayerStartX() {
        return this.playerStartX;
    }

    public float getPlayerStartY() {
        return this.playerStartY;
    }
}
