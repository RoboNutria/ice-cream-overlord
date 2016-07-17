package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;

import com.badlogic.gdx.physics.box2d.*;
import com.dcoppetti.lordcream.handlers.InputHandler;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.handlers.BulletFactory;
import com.dcoppetti.lordcream.handlers.BulletFactory.BulletType;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
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
	public short playerSideContact = 0;

	private InputHandler input;

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
	private float deathDuration = 2f;
	private float deathTimer = 0f;
	public boolean willDie = false;
	private boolean invincible = false;
	private float invincibleTimer = 1f;
	private float invincibleTime = 0f;
	private short lives = 3;
	// Animations

	// for when player is hit by an enemy
	private boolean bounceHit = false;
	private float bounceHitTimer = 0;
	private float bounceHitDuration = 0.8f;

	// private float slideAccel = 6f;
	private float slideAccel = 7.5f;
	private float maxSlideSpeed = 10f;
	private float jumpPower = 62f;
	private float jumpLimit = 3.2f;
	private float stickForce = 35f;
	private float stickFallForce = -0.02f; // this force pulls you down when stick to walls,
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

	public boolean gameOver;

	public Overlord(World world, TextureRegion region, Vector2 position) {
		super(region);
		input = new InputHandler();
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
		fdef.filter.groupIndex = CollisionHandler.GROUP_PLAYER;
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
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_PLAYER;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 2.5f, colliderHeight / 8f, new Vector2(
				0, -colliderHeight / 2), 0);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		body.createFixture(sensorFdef).setUserData(PLAYER_FOOTER);
		shape.dispose();
	}

	private void createLeftSensor() {
		CircleShape shape = new CircleShape();
		shape.setPosition(new Vector2(-colliderWidth/2f, 0));
		shape.setRadius(colliderWidth/2.3f);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		sensorFdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER_SENSORS;
		sensorFdef.filter.maskBits = CollisionHandler.MASK_PLAYER_SENSOR;
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_PLAYER;
		leftSide = body.createFixture(sensorFdef);
		leftSide.setUserData(PLAYER_SIDE);
		shape.dispose();
	}

	private void createRightSensor() {
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(colliderWidth / 8f, colliderHeight / 2.5f, new Vector2(
		//		colliderWidth / 2f, 0), 0);
		CircleShape shape = new CircleShape();
		shape.setPosition(new Vector2(colliderWidth/2f, 0));
		shape.setRadius(colliderWidth/2.3f);
		sensorFdef.isSensor = true;
		sensorFdef.shape = shape;
		sensorFdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER_SENSORS;
		sensorFdef.filter.maskBits = CollisionHandler.MASK_PLAYER_SENSOR;
		sensorFdef.filter.groupIndex = CollisionHandler.GROUP_PLAYER;
		rightSide = body.createFixture(sensorFdef);
		rightSide.setUserData(PLAYER_SIDE);
		shape.dispose();

	}

	@Override
	public void update(float delta) {
		if(isKill()) return;
		if (willDie) {
			updateGameOver(delta);
			return;
		} else {
			updateState(delta);
			updateAnimations(delta);
			updateSideFixture();
			checkWasHit(delta);
			checkFire(delta);
			input.checkDpad();
			updateMovement();
		}
	}

	public void updateGameOver(float delta) {
		deathTimer += delta;
		body.setLinearVelocity(0, 0);
		if(deathTimer >= deathDuration) {
			gameOver = true;
			dispose();
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
		int side = facingLeft == true ? -1 : 1;
		float bulletVel = 5f;
		Vector2 position = new Vector2(body.getPosition().x, body.getPosition().y + colliderWidth/2);
		BulletFactory.createPlayerBullet(BulletType.Cone, body.getWorld(), position, new Vector2(bulletVel*side, 0));
		canFire = false;
		input.fire = false;
	}

	private void checkWasHit(float delta) {
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
			if(invincibleTime >= invincibleTimer) {
				setColor(Color.WHITE);
				invincible = false;
				invincibleTime = 0;
			}
		}
	}
	
	public short getLives() {
		return lives;
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
		if (playerSideContact > 0 && !isGrounded()) {
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
			if (input.x < 0) {
				moveLeft();
			}
			if (input.x > 0) {
				moveRight();
			}
			break;
		case Idle:
		case Sliding:
			if (input.jump) {
				jump();
			}
			if (input.x < 0) {
				moveLeft();
			}
			if (input.x > 0) {
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
			if (leftSide != null && (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
					|| input.x <= -1f) {
				System.out.println(input.x);
				newX = -stickForce;
			} else if (rightSide != null && (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
					|| input.x >= 1f) {
				System.out.println(input.x);
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
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)
				|| input.x < 0.2f) {
			if (velX - slideAccel >= -maxSlideSpeed) {
				newX = -slideAccel;
			}
		}
	}

	private void moveRight() {
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)
				|| input.x > 0.2f) {
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
		if(willDie) return;
		if(b instanceof Enemy) {
			if (!invincible) lives--;
			if(getLives() <= 0) {
				willDie = true;
				tweenDeathAnim();
				return;
			}
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
			float yVel = 170f;
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
		float flashDuration = invincibleTimer / flashAmount * 0.5f;
		TweenManager tm = IceCreamOverlordGame.TWEEN_MANAGER;
		Tween.to(this, SpriteAccessor.COLOR, 0).target(1, 0, 0).start(tm);
		Timeline.createSequence().beginSequence()
		.push(Tween.to(this, SpriteAccessor.ALPHA, flashDuration).target(0))
		.push(Tween.to(this, SpriteAccessor.ALPHA, flashDuration).target(1))
		.repeat(flashAmount, 0)
		.end().start(tm);
	}

	@Override
	public boolean isKill() {
		return gameOver;
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

	public boolean isInvincible() {
		return invincible;
	}

	public boolean isDead() {
		return this.dead;
	}

	public void contactDeathZone() {
		willDie = true;
		tweenDeathAnim();
	}

	public void tweenDeathAnim() {
		Timeline.createSequence().beginSequence()
		.push(Tween.to(this, SpriteAccessor.COLOR, deathDuration/2f).target(1, 0, 0))
		.push(Tween.to(this, SpriteAccessor.ALPHA, deathDuration/2f).target(0))
		.end().start(IceCreamOverlordGame.TWEEN_MANAGER);
		
	}

	public void tweenFadeOut() {
		Tween.to(this, SpriteAccessor.ALPHA, deathDuration).target(0).start(IceCreamOverlordGame.TWEEN_MANAGER);
		
	}

	public void setWillDie(boolean b) {
		this.willDie = b;
	}
}
