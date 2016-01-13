package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dcoppetti.lordcream.IceCreamOverlordGame.Misc;
import com.dcoppetti.lordcream.entities.Enemy;
import com.dcoppetti.lordcream.entities.GameEntity;
import com.dcoppetti.lordcream.entities.Overlord;

/**
 * @author Diego Coppetti
 *
 */
// TODO: Fucking collision filtering with masks and category bits!
public class CollisionHandler implements ContactListener {

	public static final short CATEGORY_PLAYER = 0x2;
	public static final short CATEGORY_ENEMY = 0x4;
	public static final short CATEGORY_COLLECTIBLE = 0x8;
	public static final short CATEGORY_PLAYER_SENSORS = 0x16;
	public static final short CATEGORY_SCENARY = 0x32;
	public static final short CATEGORY_ENEMY_SENSORS = 0x64;

	public static final short MASK_PLAYER = CATEGORY_ENEMY
			| CATEGORY_COLLECTIBLE | CATEGORY_SCENARY;
	public static final short MASK_ENEMY = CATEGORY_PLAYER | CATEGORY_SCENARY;
	public static final short MASK_PLAYER_SENSORS = CATEGORY_SCENARY;
	public static final short MASK_COLLECTIBLE = CATEGORY_PLAYER
			| CATEGORY_SCENARY;
	public static final short MASK_SCENARY = -1;

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
			// if it's an enemy don't do shit
			if (fb.getBody().getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}
		if (fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			// if it's an enemy don't do shit
			if (fa.getBody().getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}

		// call their corresponding collision logic
		if (fa.getBody().getUserData() != null
				&& fb.getBody().getUserData() != null) {
			// first check if it's the player colliding with a death zone
			if (fa.getBody().getUserData().equals(Misc.death_zone.name())
					&& fb.getBody().getUserData() instanceof Overlord) {
				Overlord overlord = (Overlord) fb.getBody().getUserData();
				overlord.setIsDead(true);
				return;
			}
			if (fb.getBody().getUserData().equals(Misc.death_zone.name())
					&& fa.getBody().getUserData() instanceof Overlord) {
				Overlord overlord = (Overlord) fa.getBody().getUserData();
				overlord.setIsDead(true);
				return;
			}
			// else it's a collision within entities
			checkCollision((GameEntity) fa.getBody().getUserData(),
					(GameEntity) fb.getBody().getUserData());

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

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Body faBody = fa.getBody();
		Body fbBody = fb.getBody();
		if (faBody.getUserData() != null && fbBody.getUserData() != null) {
			GameEntity a = (GameEntity) faBody.getUserData();
			GameEntity b = (GameEntity) fbBody.getUserData();

			// Disable physics simulation between player and enemy contact
			if (a instanceof Overlord && b instanceof Enemy) {
				contact.setEnabled(false);
				return;
			}
			if (b instanceof Overlord && a instanceof Enemy) {
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
