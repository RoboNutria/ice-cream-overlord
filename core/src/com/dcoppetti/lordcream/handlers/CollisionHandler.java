package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dcoppetti.lordcream.IceCreamOverlordGame.EnemyTriggers;
import com.dcoppetti.lordcream.IceCreamOverlordGame.Misc;
import com.dcoppetti.lordcream.ai.AiBehavior;
import com.dcoppetti.lordcream.ai.WalkBehavior;
import com.dcoppetti.lordcream.ai.WalkBumpBehavior;
import com.dcoppetti.lordcream.entities.*;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

/**
 * @author Diego Coppetti
 *
 */
public class CollisionHandler implements ContactListener {

	public static final short CATEGORY_PLAYER = 0x2;
	public static final short CATEGORY_ENEMY = 0x4;
	public static final short CATEGORY_COLLECTIBLE = 0x8;
	public static final short CATEGORY_PLAYER_SENSORS = 0x16;
	public static final short CATEGORY_SCENARY = 0x32;
	public static final short CATEGORY_ENEMY_SENSORS = 0x64;
	public static final short CATEGORY_TILED_SENSOR = 0x128;
	public static final short CATEGORY_CONE = 0x256;

	public static final short MASK_PLAYER = CATEGORY_ENEMY | CATEGORY_COLLECTIBLE | CATEGORY_SCENARY;
	public static final short MASK_CONE = ~CATEGORY_TILED_SENSOR | CATEGORY_ENEMY | CATEGORY_SCENARY;
	public static final short MASK_ENEMY = CATEGORY_TILED_SENSOR | CATEGORY_PLAYER | CATEGORY_SCENARY;
	public static final short MASK_SENSOR = CATEGORY_SCENARY & ~CATEGORY_ENEMY & ~CATEGORY_PLAYER_SENSORS & ~CATEGORY_PLAYER & ~CATEGORY_COLLECTIBLE;
	public static final short MASK_PLAYER_SENSOR = CATEGORY_SCENARY & ~CATEGORY_TILED_SENSOR;
	public static final short MASK_COLLECTIBLE = CATEGORY_PLAYER | CATEGORY_SCENARY;
	public static final short MASK_TILED_SENSOR = CATEGORY_ENEMY;
	public static final short MASK_SCENARY = -1;
	
	public static final short GROUP_ENEMY = -1;
	public static final short GROUP_PLAYER = -2;

	public static final String LEVEL_BOUNDARY = "levelBoundary";

	private Overlord player;

	public CollisionHandler(Overlord player) {
		this.player = player;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Object faData = fa.getUserData();
		Object fbData = fb.getUserData();

		// check if player footer on ground
		if (faData != null && faData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct++;
			return;
		}
		if (fbData != null && fbData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct++;
			return;
		}
		
		// check if player can stick to a wall
		if (faData != null && faData.equals(Overlord.PLAYER_SIDE)) {
			// level boundary, don't stick to wall
			if(fb.getUserData() != null && fb.getUserData().equals(CollisionHandler.LEVEL_BOUNDARY)) {
				System.out.println("aca");
				return;
			}
			// if it's an enemy don't do shit
			if (fb.getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}
		if (fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			// level boundary, don't stick to wall
			if(fa.getUserData() != null && fa.getUserData().equals(CollisionHandler.LEVEL_BOUNDARY)) {
				System.out.println("aca");
				return;
			}
			// if it's an enemy don't do shit
			if (fa.getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}

		// Bullet against a wall
		if (fbData != null && fbData instanceof Bullet && faData == null) {
			Bullet b = (Bullet) fbData;
			b.collided(null);
			return;
		}
		if (faData != null && faData instanceof Bullet && fbData == null) {
			Bullet b = (Bullet) faData;
			b.collided(null);
			return;
		}

		// call their corresponding collision logic
		if (faData != null && fbData != null) {
			// first check if it's the player colliding with a death zone
			if (faData.equals(Misc.death_zone.name()) && fbData instanceof Overlord) {
				Overlord overlord = (Overlord) fbData;
				overlord.contactDeathZone();
				return;
			}
			if (fbData.equals(Misc.death_zone.name()) && faData instanceof Overlord) {
				Overlord overlord = (Overlord) faData;
				overlord.contactDeathZone();
				return;
			}
			// Check enemy triggers on tiled
			if (faData.equals(EnemyTriggers.bumper.name()) && fbData instanceof SlugEnemy) {
				SlugEnemy enemy = (SlugEnemy) fbData;
				if(enemy.getAiBehavior().size == 0) return;
				if(enemy.wasHit) return;
				AiBehavior aiBehavior = (AiBehavior) enemy.getAiBehavior().first();
				aiBehavior.colliderNotify();
				return;
			}
			if (fbData.equals(EnemyTriggers.bumper.name()) && faData instanceof SlugEnemy) {
				SlugEnemy enemy = (SlugEnemy) faData;
				if(enemy.getAiBehavior().size == 0) return;
				if(enemy.wasHit) return;
				AiBehavior aiBehavior = (AiBehavior) enemy.getAiBehavior().first();
				aiBehavior.colliderNotify();
				return;
			}
			if (faData.equals(Misc.death_zone.name()) && fbData instanceof Enemy) {
				Enemy e = (Enemy) fbData;
				e.contactDeathZone();
				return;
			}
			if (fbData.equals(Misc.death_zone.name()) && faData instanceof Enemy) {
				Enemy e = (Enemy) faData;
				e.contactDeathZone();
				return;
			}
			// else it's a collision within entities
			if(!(faData instanceof GameEntity) || !(fbData instanceof GameEntity)) {
				return;
			}
			checkCollision((GameEntity) faData, (GameEntity) fbData);

		}

	}

	private void checkCollision(GameEntity a, GameEntity b) {
		a.collided(b);
		b.collided(a);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Object faData = fa.getUserData();
		Object fbData = fb.getUserData();

		if (faData != null && faData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct--;
		}
		if (fbData != null && fbData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct--;
		}
		if (faData != null && faData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = false;
			return;
		}
		if (fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = false;
			return;
		}

		// AI behaviour trigger checks
//		if (faData != null && faData instanceof WalkBehavior) {
//			WalkBehavior wb = (WalkBehavior) faData;
//			wb.changeDirection();
//			return;
//		}
//		if (fbData != null && fbData instanceof WalkBehavior) {
//			WalkBehavior wb = (WalkBehavior) fbData;
//			wb.changeDirection();
//			return;
		//}

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Object faData = fa.getUserData();
		Object fbData = fb.getUserData();
		if (faData != null && fbData != null) {

			if(faData instanceof String || fbData instanceof String) return;

			GameEntity a = (GameEntity) faData;
			GameEntity b = (GameEntity) fbData;

			// Disable between player and enemy contact
			if (a instanceof Overlord && b instanceof Enemy) {
				contact.setEnabled(false);
				return;
			}
			if (b instanceof Overlord && a instanceof Enemy) {
				contact.setEnabled(false);
				return;
			}
			// Disable between player and chibi
			if (a instanceof Overlord && b instanceof ChibiIceCream) {
				contact.setEnabled(false);
				return;
			}
			if (b instanceof Overlord && a instanceof ChibiIceCream) {
				contact.setEnabled(false);
				return;
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
