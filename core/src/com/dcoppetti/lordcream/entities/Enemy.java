package com.dcoppetti.lordcream.entities;

import java.util.Iterator;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.ai.AiBehavior;

public abstract class Enemy extends Box2DSprite implements GameEntity {

	public enum EnemyStates {
		Idle, Sliding, OnAir, OnWall
	}

	protected EnemyStates state;

	protected Body body;
	private boolean lookingLeft = true;

	private Array<AiBehavior> behavior;

	public Enemy(World world, TextureRegion region, Vector2 position) {
		super(region);
		behavior = new Array<AiBehavior>();
		createBody(world, position);
		state = EnemyStates.Idle;
	}

	protected abstract void createBody(World world, Vector2 position);

	@Override
	public void update(float delta) {
		updateBehavior(delta);
		updateEnemy(delta);
		float x = body.getLinearVelocity().x;
		if (x < -0.1f) {
			lookingLeft = true;
			setFlip(false, false);
		} else if (x > 0.1f) {
			lookingLeft = false;
			setFlip(true, false);
		}
	}

	protected abstract void updateEnemy(float delta);

	private void updateBehavior(float delta) {
		Iterator<AiBehavior> it = behavior.iterator();
		while (it.hasNext()) {
			AiBehavior ab = it.next();
			ab.update(delta);
		}
	}

	public void addAiBehavior(AiBehavior aib) {
		behavior.add(aib);
	}

	public void removeAiBehavior(AiBehavior aib) {
		behavior.removeValue(aib, false);
	}
	
	public Array<AiBehavior> getAiBehavior() {
		return this.behavior;
	}

	@Override
	public void dispose() {
		behavior.clear();
	}

	public Body getBody() {
		return body;
	}

	public boolean isLookingLeft() {
		return lookingLeft;
	}

	public void setLookingLeft(boolean b) {
		this.lookingLeft = b;
	}

}
