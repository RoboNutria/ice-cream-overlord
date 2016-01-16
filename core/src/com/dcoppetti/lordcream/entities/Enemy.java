package com.dcoppetti.lordcream.entities;

import java.util.Iterator;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.ai.AiBehavior;
import com.dcoppetti.lordcream.utils.SpriteAccessor;

public abstract class Enemy extends Box2DSprite implements GameEntity {
	
	public boolean wasHit = false;
	protected float recoveryTimer = 0;
	protected float recoverySpeed = 1f;
	protected float deathTimer = 0f;
	protected float deathSpeed = 2f;
	protected Color hitColor = Color.YELLOW;
	protected Color deathColor = Color.RED;
	protected boolean dead = false;
	private boolean skipNextFlip = false; // oh ffs it came to this, but it werks :V

	public enum EnemyStates {
		Idle, Sliding, OnAir, OnWall
	}

	protected EnemyStates state;

	protected Body body;
	public boolean lookingLeft = true;

	private Array<AiBehavior> behavior;

	public Enemy(World world, TextureRegion region, Vector2 position, float size) {
		super(region);
		behavior = new Array<AiBehavior>();
		createBody(world, position, size);
		state = EnemyStates.Idle;
	}

	protected abstract void createBody(World world, Vector2 position, float size);

	@Override
	public void update(float delta) {
		if(isKill()) {
			dispose();
			return;
		}
		updateEnemy(delta);
		setFlip(!lookingLeft, false);
		if(wasHit) {
			skipNextFlip = true;
			recoveryTimer += delta;
			if(recoveryTimer >= recoverySpeed) {
				recoveryTimer = 0;
				wasHit = false;
			}
		} else {
			float x = body.getLinearVelocity().x;
			if(!skipNextFlip) {
				if (x < 0f) {
					lookingLeft = true;
				} else if (x > 0f) {
					lookingLeft = false;
				}
			}
			skipNextFlip = false;
			
			updateBehavior(delta);
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
		body.getWorld().destroyBody(body);
	}

	public Body getBody() {
		return body;
	}

	public boolean isLookingLeft() {
		return lookingLeft;
	}

	public void tweenHitAnim() {
		TweenManager tm = IceCreamOverlordGame.TWEEN_MANAGER;
		Timeline.createSequence().beginSequence()
		.push(Tween.to(this, SpriteAccessor.COLOR, 0).target(hitColor.r, hitColor.g, hitColor.b))
		.push(Tween.to(this, SpriteAccessor.COLOR, recoverySpeed).target(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b))
		.end().start(tm);
	}

	public void tweenDeathAnim() {
		TweenManager tm = IceCreamOverlordGame.TWEEN_MANAGER;
		Timeline.createSequence().beginSequence()
		.push(Tween.to(this, SpriteAccessor.COLOR, deathSpeed/2f).target(deathColor.r, deathColor.g, deathColor.b))
		.push(Tween.to(this, SpriteAccessor.ALPHA, deathSpeed/2f).target(0))
		.end().start(tm);
	}

	public void contactDeathZone() {
		dead = true;
	}

	public boolean isKill() {
		return dead;
	}

}
