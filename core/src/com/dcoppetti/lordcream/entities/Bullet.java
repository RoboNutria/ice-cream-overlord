package com.dcoppetti.lordcream.entities;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.handlers.BulletFactory.BulletType;
import com.dcoppetti.lordcream.screens.PlayScreen;

public abstract class Bullet extends Box2DSprite implements GameEntity {
	
	protected Body body;
	protected Vector2 velocity;
	protected BulletType type;
	
	protected boolean dead = false;
	
	public Bullet(BulletType type, TextureRegion region, World world, Vector2 velocity, Vector2 position) {
		super(region);
		createBody(world, position);
		this.velocity = velocity;
		this.type = type;
	}

	protected abstract void createBody(World world, Vector2 position);

	@Override
	public void update(float delta) {
		if(dead) return;
		checkBounds();
		
		body.setLinearVelocity(velocity);
		updateAdditional(delta);
	}
	
	protected abstract void updateAdditional(float delta);

	@Override
	public void dispose() {
		body.getWorld().destroyBody(body);
	}

	public boolean isKill() {
		return dead;
	}

	public void checkBounds() {
		dead = IceCreamOverlordGame.CAMERA_HANDLER.isOffCamera(body.getPosition());
	}
	
	public Body getBody() {
		return body;
	}

}
