package com.dcoppetti.lordcream;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dcoppetti.lordcream.entities.GameEntity;
import com.dcoppetti.lordcream.entities.Overlord;

/**
 * @author Diego Coppetti
 *
 */
public class CollisionHandler implements ContactListener {
	
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
		if(faData != null && faData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct++;
			// TODO: additional check if player stomps on something (?)
			return;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct++;
			// TODO: additional check if player stomps on something (?)
			return;
		}
		
		// check if player can stick to a wall
		if(faData != null && faData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = true;
			return;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = true;
			return;
		}

		// do their corresponding collision logic
		if(faData != null && fbData != null) {
			checkCollision((GameEntity) faData, (GameEntity) fbData);
		}

	}

	private void checkCollision(GameEntity a, GameEntity b) {
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Object faData = fa.getUserData();
		Object fbData = fb.getUserData();

		if(faData != null && faData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct--;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct--;
		}
		if(faData != null && faData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = false;
			return;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			player.playerSideContact = false;
			return;
		}

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
