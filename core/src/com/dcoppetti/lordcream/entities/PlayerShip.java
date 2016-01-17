package com.dcoppetti.lordcream.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.screens.PlayScreen;

public class PlayerShip implements GameEntity {
	
	public PlayerShip(World world, Vector2 size, Vector2 position) {
		createBody(world, size, position);
	}

	private void createBody(World world, Vector2 size, Vector2 position) {
		float colliderWidth = size.x;
		float colliderHeight = size.y;
		BodyDef bdef = new BodyDef();
		bdef.position.set(new Vector2(position.x + colliderWidth/2, position.y + colliderHeight/2));
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.fixedRotation = true;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 2, colliderHeight / 2);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_COLLECTIBLE;
		fdef.filter.maskBits = CollisionHandler.MASK_COLLECTIBLE;
		Body body = world.createBody(bdef);
		Fixture f = body.createFixture(fdef);
		shape.dispose();

		f.setUserData(this);
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void collided(GameEntity b) {
		if(b instanceof Overlord) {
			if(PlayScreen.getBackToTheShip && !PlayScreen.levelSuccess) {
				PlayScreen.levelSuccess = true;
			}
		}
	}

	@Override
	public boolean isKill() {
		// TODO Auto-generated method stub
		return false;
	}

}
