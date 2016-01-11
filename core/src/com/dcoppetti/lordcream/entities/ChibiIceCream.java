package com.dcoppetti.lordcream.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class ChibiIceCream extends Box2DSprite implements GameEntity {
	
	private Body body;
	
	public ChibiIceCream(TextureRegion region, World world, Vector2 position) {
		super(region);
		createBody(world, position);
	}

	private void createBody(World world, Vector2 position) {
		// TODO: Create a body sensor PolygonShape as box
		// Check Overlord class
	}

	@Override
	public void update(float delta) {
		// TODO:
		// Probably we only want to update for animation and to self destroy when collected
	}

	@Override
	public void dispose() {
		// TODO: I'm not sure if we're gonna dispose anything here (probably not)
	}
	
	public Body getBody() {
		return body;
	}

	@Override
	public void collided(GameEntity b) {
		// TODO: This will be executed if any other GameEntity collides with it (player for example)
		// TODO: WE WILL NEED FILTERING, We don't want this to collide with an enemy or bullet (but ignore it for now)
		
		
		// So what you wanna do here is check if it collided with the Overlord.class (cast b to Overlord if instance of it) and set some boolean to true
		// to this instance (to mark it as shouldDestroy, then destroy on update)
		// NEVER destroy an entity in this method, only at the beginning of the update
		// We're not destroyng shit yet, so we probably will need to abstract a shouldDestroy boolean and iterate the world objects to check
		// what needs to be destroyed
	}

}
