package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dcoppetti.lordcream.entities.Enemy;
import com.dcoppetti.lordcream.entities.GameEntity;
import com.dcoppetti.lordcream.entities.Overlord;

/**
 * @author Diego Coppetti
 *
 */
// TODO: Fucking collision filtering with groups man!
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
			return;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_FOOTER)) {
			player.playerFootContanct++;
			return;
		}
		
		// check if player can stick to a wall
		if(faData != null && faData.equals(Overlord.PLAYER_SIDE)) {
			// if it's an enemy don't do shit
			if(fb.getBody().getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}
		if(fbData != null && fbData.equals(Overlord.PLAYER_SIDE)) {
			// if it's an enemy don't do shit
			if(fa.getBody().getUserData() instanceof Enemy) {
				return;
			}
			player.playerSideContact = true;
			return;
		}

		// call their corresponding collision logic
		if(fa.getBody().getUserData() != null && fb.getBody().getUserData() != null) {
			checkCollision((GameEntity) fa.getBody().getUserData(), (GameEntity) fb.getBody().getUserData());
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
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		Body faBody = fa.getBody();
		Body fbBody = fb.getBody();
		if(faBody.getUserData() != null && fbBody.getUserData() != null) {
			GameEntity a = (GameEntity) faBody.getUserData();
			GameEntity b = (GameEntity) fbBody.getUserData();
			
			// Disable physics simulation between player and enemy contact
			if(a instanceof Overlord && b instanceof Enemy) {
				contact.setEnabled(false);
				return;
			}
			if(b instanceof Overlord && a instanceof Enemy) {
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
