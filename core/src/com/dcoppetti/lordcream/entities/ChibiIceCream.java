package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.handlers.CollisionHandler;
import com.dcoppetti.lordcream.screens.PlayScreen;

/**
 * @author Marccio Silva, Diego Coppetti
 *
 */
public class ChibiIceCream extends Box2DSprite implements GameEntity {

	private Animation rescueAnim;
	private float rescueFPS = 2;
	private float rescueAnimTimer = 0;
	private Animation idleAnim;
	private float idleAnimTimer = 0;
	private Body body;
	private float idleFPS = 2;
	private float colliderWidth;
	private float colliderHeight;
	private boolean inactive = false;
//	private short collisionsOverlord = 0;

	public enum ChibiStates {
		Idle, Rescued
	}

	private ChibiStates state;

	public ChibiIceCream(World world, TextureRegion region, Vector2 position) {
		super(region);
		createBody(world, position);
		state = ChibiStates.Idle;
		PlayScreen.chibiAmount++;
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
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_COLLECTIBLE;
		fdef.filter.maskBits = CollisionHandler.MASK_COLLECTIBLE;
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
	}

	@Override
	public void update(float delta) {
		TextureRegion region = null;
		if (idleAnim != null || rescueAnim != null) {
			switch (state) {
			case Idle:
				idleAnimTimer += delta;
				region = idleAnim.getKeyFrame(idleAnimTimer);
				setRegion(region);
				break;
			case Rescued:
				rescueAnimTimer += delta;
				region = rescueAnim.getKeyFrame(rescueAnimTimer);
				setRegion(region);
				if (rescueAnim.isAnimationFinished(rescueAnimTimer)) {
					inactive = true;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void dispose() {
		body.getWorld().destroyBody(body);
	}

	public Body getBody() {
		return body;
	}

	@Override
	public void collided(GameEntity b) {
		if (b instanceof Overlord) {
			if (state == ChibiStates.Idle) {
				PlayScreen.rescueAmount++;
				state = ChibiStates.Rescued;
			}
		}
	}

	@Override
	public boolean isKill() {
		return inactive;
	}

	public boolean isRescued() {
		return state == ChibiStates.Rescued;
	}
	
//	public short getCollisionsOverlord() {
//		return collisionsOverlord;
//	}
	
	public void setAnimationRegions(Array<TextureRegion> idleRegions, Array<TextureRegion> rescueRegions) {
		idleAnim = new Animation(1f / idleFPS, idleRegions);
		idleAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		setRegion(idleAnim.getKeyFrame(0));
		rescueAnim = new Animation(1f / rescueFPS, rescueRegions);
		rescueAnim.setPlayMode(Animation.PlayMode.NORMAL);
		setRegion(rescueAnim.getKeyFrame(0));
	}

}
