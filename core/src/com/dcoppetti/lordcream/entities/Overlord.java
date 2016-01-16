package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.handlers.PlayerInputHandler;
import com.dcoppetti.lordcream.screens.PlayScreen;
import com.dcoppetti.lordcream.utils.SpriteAccessor;

/**
 * @author Diego Coppetti
 */
public class Overlord extends Box2DSprite implements GameEntity {


	public enum PlayerState {
		Idle, Sliding, OnAir, OnWall
	}

	public static final String PLAYER_FOOTER = "PLAYER_FOOTER";
	public static final String PLAYER_SIDE = "PLAYER_SIDE";
	public int playerFootContanct;
	public boolean playerSideContact = false;

	private PlayerInputHandler input;
	TweenManager tm = new TweenManager();

	// experimental mechanic changes
	boolean poopMode = false;

	// box2d stuff
	private Body body;
	private FixtureDef sensorFdef;
	private Fixture leftSide;
	private Fixture rightSide;
	private float colliderWidth;
	private float colliderHeight;
	
	// bullet stuff
	private float bulletTimer;
	private float bulletTime = 0.4f;
	private boolean canFire = true;

	// state stuff
	private PlayerState state = PlayerState.Idle;
	private boolean canMove = true;
	private boolean facingLeft = false;
	private boolean dead = false;
	private float respawnTime = 2f;
	private float respawnTimer = 0f;
	private float respawnX;
	private float respawnY;
	private boolean invincible = false;
	private float invincibleTiemer = 1f;
	private float invincibleTime = 0f;
	// Animations

	// for when player is hitted by an enemy
	private boolean bounceHit = false;
	private float bounceHitTimer = 0;
	private float bounceHitDuration = 0.8f;

	// private float slideAccel = 6f;
	private float slideAccel = 7.5f;
	private float maxSlideSpeed = 10f;
	private float jumpPower = 60f;
	private float jumpLimit = 3f;
	private float stickForce = 80f;
	private float stickFallForce = -0.1f; // this force pulls you down when stick to walls,
											// this could be changed in certain levels
	// Animations
	private Animation idleAnim;
	private Animation slideAnim;
	private Animation jumpAnim;
	private Animation wallAnim;
	private float idleFPS = 4f;
	private float slideFPS = 10f;
	private float jumpFPS = 5f;
	private float wallFPS = 2f;
	private float idleAnimTimer = 0;
	private float slideAnimTimer = 0;
	private float jumpAnimTimer = 0;
	private float wallAnimTimer = 0;

	// movement variables changed in the update movement method (don't touch
	// here)
	float velX;
	float newX;
	float newY;

	public Overlord(World world, TextureRegion region, Vector2 position) {
		super(region);
		input = new PlayerInputHandler();
		respawnX = position.x;
		respawnY = position.y;
		setZIndex(3);
		createBody(world, position);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
	}

	public void setAnimationRegions(Array<TextureRegion> idleRegions,
			Array<TextureRegion> slideRegions,
			Array<TextureRegion> jumpRegions, Array<TextureRegion> wallRegions) {
		idleAnim = new Animation(1f / idleFPS, idleRegions);
		idleAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		setRegion(idleAnim.getKeyFrame(0));

		slideAnim = new Animation(1f / slideFPS, slideRegions);
		slideAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		jumpAnim = new Animation(1f / jumpFPS, jumpRegions);
		jumpAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		wallAnim = new Animation(1f / wallFPS, wallRegions);
		wallAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	}

	private void createBody(World world, Vector2 position) {
		colliderWidth = getWidth() / 3.6f / PPM;
		colliderHeight = getHeight() / 4 / PPM;
		colliderWidth = 4 / PPM;
		colliderHeight = 4 / PPM;
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.fixedRotation = true;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 2, colliderHeight / 2);
		FixtureDef fdef = new FixtureDef();
		fdef.friction = 0.2f;
		fdef.shape = shape;
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER;
		fdef.filter.maskBits = CollisionHandler.MASK_PLAYER;
		body = world.createBody(bdef);
		Fixture f = body.createFixture(fdef);
		shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin(getWidth() / 2, (getHeight() / 2) + 0.03f);
		setX(-getWidth() / 2 + Box2DUtils.width(body) / 2);
		setY(-getHeight() / 2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX() / 1.5f / PPM, getScaleY() / 1.5f / PPM);

		f.setUserData(this);

		createFootSensor();
		createRightSensor();
	}

	private void createFootSensor() {
		sensorFdef = new FixtureDef();
		sensorFdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER_SENSORS;
		sensorFdef.filter.maskBits = CollisionHandler.MASK_PLAYER_SENSOR;
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 2.5f, colliderHeight / 8f, new Vector2(
				0, -colliderHeight / 2), 0);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		body.createFixture(sensorFdef).setUserData(PLAYER_FOOTER);
		shape.dispose();
	}

	private void createLeftSensor() {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 8f, colliderHeight / 2.5f, new Vector2(
				-colliderWidth / 2f, 0), 0);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		sensorFdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER_SENSORS;
		sensorFdef.filter.maskBits = CollisionHandler.MASK_PLAYER_SENSOR;
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
		leftSide = body.createFixture(sensorFdef);
		leftSide.setUserData(PLAYER_SIDE);
		shape.dispose();
	}

	private void createRightSensor() {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 8f, colliderHeight / 2.5f, new Vector2(
				colliderWidth / 2f, 0), 0);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		sensorFdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER_SENSORS;
		sensorFdef.filter.maskBits = CollisionHandler.MASK_PLAYER_SENSOR;
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
		rightSide = body.createFixture(sensorFdef);
		rightSide.setUserData(PLAYER_SIDE);
		shape.dispose();

	}

	@Override
	public void update(float delta) {
		tm.update(delta);
		if (isDead()) {
			checkRespawn(delta);
		} else {
			updateState(delta);
			updateAnimations(delta);
			updateSideFixture();
			checkWasHitted(delta);
			checkFire(delta);
			updateMovement();
		}
	}

	private void checkFire(float delta) {
		if(bounceHit) return;
		if(state == PlayerState.Idle || state == PlayerState.Sliding || state == PlayerState.OnAir) {
			bulletTimer += delta;
			if(bulletTimer >= bulletTime) {
				canFire = true;
				bulletTimer = 0;
			}
			if(input.fire) {
				fireBullet();
			}
		}
	}
	
	private void fireBullet() {
		if(!canFire) return;
		System.out.println("fire bullet");
		canFire = false;
	}

	private void checkWasHitted(float delta) {
		if(bounceHit) {
			bounceHitTimer += delta;
			invincible = true;
			if(bounceHitTimer >= bounceHitDuration) {
				bounceHit = false;
				canMove = true;
				bounceHitTimer = 0;
			}
		}
		if(invincible) {
			//setColor(1,0,0,1);
			invincibleTime += delta;
			if(invincibleTime >= invincibleTiemer) {
				setColor(Color.WHITE);
				invincible = false;
				invincibleTime = 0;
			}
		}
	}

	private void checkRespawn(float delta) {
		if (!isDead()) return;
		//setColor(1,0,0,1);
		body.setLinearVelocity(0, 0);
		respawnTimer += delta;
		if (respawnTimer >= respawnTime) {
			// now we respawn it
			//setColor(Color.WHITE);
			body.setTransform(respawnX, respawnY, body.getAngle());
			setIsDead(false);
			respawnTimer = 0;
		}
	}

	private void updateAnimations(float delta) {
		TextureRegion region = null;
		if (idleAnim != null) {
			switch (state) {
			case Idle:
				slideAnimTimer = 0;
				jumpAnimTimer = 0;
				wallAnimTimer = 0;
				idleAnimTimer += delta;
				region = idleAnim.getKeyFrame(idleAnimTimer);
				setRegion(region);
				break;
			case Sliding:
				idleAnimTimer = 0;
				jumpAnimTimer = 0;
				wallAnimTimer = 0;
				slideAnimTimer += delta;
				region = slideAnim.getKeyFrame(slideAnimTimer);
				setRegion(region);
				break;
			case OnAir:
				idleAnimTimer = 0;
				slideAnimTimer = 0;
				wallAnimTimer = 0;
				jumpAnimTimer += delta;
				region = jumpAnim.getKeyFrame(jumpAnimTimer);
				setRegion(region);
				break;
			case OnWall:
				idleAnimTimer = 0;
				slideAnimTimer = 0;
				jumpAnimTimer = 0;
				wallAnimTimer += delta;
				region = wallAnim.getKeyFrame(wallAnimTimer);
				setRegion(region);
				break;
			default:
				break;
			}
		}
		if (facingLeft) {
			setFlip(true, false);
		} else {
			setFlip(false, false);
		}
	}

	private void updateSideFixture() {
		if (isFlipX() && rightSide != null) {
			body.destroyFixture(rightSide);
			rightSide = null;
			createLeftSensor();
		}
		if (!isFlipX() && leftSide != null) {
			body.destroyFixture(leftSide);
			leftSide = null;
			createRightSensor();
		}
	}

	private void updateState(float delta) {
		if (playerSideContact && !isGrounded()) {
			if (leftSide != null || rightSide != null) {
				state = PlayerState.OnWall;
				return;
			}
		}
		if (isGrounded() && MathUtils.floor(body.getLinearVelocity().x) != 0) {
			state = PlayerState.Sliding;
			return;
		}
		if (!isGrounded()) {
			state = PlayerState.OnAir;
			return;
		}
		state = PlayerState.Idle;
	}

	private void updateMovement() {
		if (!canMove)
			return;
		newX = 0;
		newY = 0;
		velX = body.getLinearVelocity().x;

		switch (state) {
		case OnWall:
			// Stick to walls
			if (!input.jump) {
				stickToWall();
			} else {
				WallJump();
				input.jump = false;
			}
			break;
		case OnAir:
			if (input.left) {
				moveLeft();
			}
			if (input.right) {
				moveRight();
			}
			break;
		case Idle:
		case Sliding:
			if (input.jump) {
				jump();
			}
			if (input.left) {
				moveLeft();
			}
			if (input.right) {
				moveRight();
			}
			break;
		default:
			break;
		}

		if (velX < -0.1f) {
			facingLeft = true;
		} else if (velX > 0.1f) {
			facingLeft = false;
		}

		newX = MathUtils.floor(newX);
		newY = MathUtils.floor(newY);
		body.applyForceToCenter(newX, newY, true);
	}

	private void WallJump() {
		if (leftSide != null) {
			if (body.getLinearVelocity().y <= 0.0f) {
				body.applyForceToCenter(138, jumpPower * 3, true);
			}
		} else if (rightSide != null) {
			if (body.getLinearVelocity().y <= 0.0f) {
				body.applyForceToCenter(-138, jumpPower * 3, true);
			}
		}

	}

	private void stickToWall() {
		if (poopMode) {
			if (leftSide != null) {
				newX = -stickForce;
			} else if (rightSide != null) {
				newX = stickForce;
			}
		} else {
			if (leftSide != null && Gdx.input.isKeyPressed(Keys.A)) {
				newX = -stickForce;
			} else if (rightSide != null && Gdx.input.isKeyPressed(Keys.D)) {
				newX = stickForce;
			}
		}
		newY = stickFallForce;
	}

	private void jump() {
		// Limit it or not?
		// Now limited to 20% higher
		if (body.getLinearVelocity().y < jumpLimit) {
			body.applyForceToCenter(0, jumpPower, true);
		}
	}

	private void moveLeft() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			if (velX - slideAccel >= -maxSlideSpeed) {
				newX = -slideAccel;
			}
		}
	}

	private void moveRight() {
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (velX + slideAccel <= maxSlideSpeed) {
				newX = slideAccel;
			}
		}
	}

	public boolean isGrounded() {
		return this.playerFootContanct > 0;
	}

	@Override
	public void dispose() {
		leftSide = null;
		rightSide = null;
		body.getWorld().destroyBody(body);
	}

	@Override
	public void collided(GameEntity b) {
		if(b instanceof Enemy) {
			bounceOffEnemy((Enemy) b);
		}
	}

	private void bounceOffEnemy(Enemy b) {
		if(invincible) return;
		if(!bounceHit) {
			bounceHit = true;
			Body eBody = b.getBody();
			Vector2 eVelocity = eBody.getLinearVelocity();
			Vector2 velocity = body.getLinearVelocity();
			float xMul = 30;
			float yVel = 150f;
			float side = facingLeft == true ? -1f : 1f;
			if(velocity.x <= 2f && velocity.x >= -2f) {
				velocity.x = eVelocity.x;
				xMul = 150f;
			}
			float x = (velocity.x * xMul) * side;
			body.setLinearVelocity(0, 0);
			body.applyForceToCenter(x, yVel, true);
			canMove = false;
			tweenHitAnim();
		}
	}

	private void tweenHitAnim() {
		int flashAmount = 12;
		float flashDuration = invincibleTiemer / flashAmount * 0.5f;
		Tween.to(this, SpriteAccessor.COLOR, 0).target(1, 0, 0).start(tm);
		Timeline.createSequence().beginSequence()
		.push(Tween.to(this, SpriteAccessor.ALPHA, flashDuration).target(0))
		.push(Tween.to(this, SpriteAccessor.ALPHA, flashDuration).target(1))
		.repeat(flashAmount, 0)
		.end().start(tm);
	}

	@Override
	public boolean isKill() {
		return false;
	}

	public PlayerState getState() {
		return state;
	}

	public Body getBody() {
		return body;
	}

	public InputAdapter getInputHandler() {
		return input;
	}

	public void setIsDead(boolean dead) {
		this.dead = dead;
	}
	
	public boolean isInvincible() {
		return invincible;
	}

	public boolean isDead() {
		return this.dead;
	}
}
