package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.handlers.CollisionHandler;

public class ChibiIceCream extends Box2DSprite implements GameEntity {

	private Animation idleAnim;
	private float idleAnimTimer = 0;
	private Body body;
	private float idleFPS = 2;
	private float colliderWidth;
	private float colliderHeight;
	private boolean shouldDestroy = false;

	public enum ChibiStates {
		Idle
	}

	private ChibiStates state;

	public ChibiIceCream(World world, TextureRegion region, Vector2 position) {
		super(region);
		createBody(world, position);
		state = ChibiStates.Idle;
	}

	private void createBody(World world, Vector2 position) {
		// TODO: Create a body sensor PolygonShape as box
		// Check Overlord class
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
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_COLLECTIBLE;
		fdef.filter.maskBits = CollisionHandler.MASK_COLLECTIBLE;
		body = world.createBody(bdef);
		body.createFixture(fdef);
		shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin(getWidth() / 2, (getHeight() / 2) + 0.03f);
		setX(-getWidth() / 2 + Box2DUtils.width(body) / 2);
		setY(-getHeight() / 2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX() / 1.5f / PPM, getScaleY() / 1.5f / PPM);

		body.setUserData(this);

	}

	@Override
	public void update(float delta) {
		// Probably we only want to update for animation and to self destroy
		// when collected
		TextureRegion region = null;
		if (idleAnim != null) {
			switch (state) {
			case Idle:
				idleAnimTimer += delta;
				region = idleAnim.getKeyFrame(idleAnimTimer);
				setRegion(region);
				break;
			// TODO: Handle rescue animation (not drawn yet)
			default:
				break;
			}
		}
	}

	@Override
	public void dispose() {
		// TODO: I'm not sure if we're gonna dispose anything here (probably
		// not)
	}

	public Body getBody() {
		return body;
	}

	public boolean shouldDestroy() {
		return shouldDestroy;
	}

	public boolean destroyAnimationFinished() {
		// TODO: set boolean to true when destroy animation finished
		return true;
	}

	@Override
	public void collided(GameEntity b) {
		// TODO: This will be executed if any other GameEntity collides with it
		// (player for example)
		// TODO: WE WILL NEED FILTERING, We don't want this to collide with an
		// enemy or bullet (but ignore it for now)

		// So what you wanna do here is check if it collided with the
		// Overlord.class (cast b to Overlord if instance of it) and set some
		// boolean to true
		// to this instance (to mark it as shouldDestroy, then destroy on
		// update)
		// NEVER destroy an entity in this method, only at the beginning of the
		// update
		// We're not destroyng shit yet, so we probably will need to abstract a
		// shouldDestroy boolean and iterate the world objects to check
		// what needs to be destroyed
		if (b instanceof Overlord) {
			shouldDestroy = true;
		}
	}

	public void setAnimationRegions(Array<TextureRegion> idleRegions) {
		idleAnim = new Animation(1f / idleFPS, idleRegions);
		idleAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		setRegion(idleAnim.getKeyFrame(0));
	}

}
