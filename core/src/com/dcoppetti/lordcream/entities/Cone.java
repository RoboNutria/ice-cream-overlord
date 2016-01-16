package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.handlers.BulletFactory.BulletType;
import com.dcoppetti.lordcream.handlers.CollisionHandler;

public class Cone extends Bullet {
	
	private Animation anim;
	private float animTimer = 0;
	private float animFPS = 4f;

	public Cone(BulletType type, TextureRegion region, World world,
			Vector2 velocity, Vector2 position) {
		super(type, region, world, velocity, position);
		setZIndex(3);
	}

	public void setAnimationRegions(Array<TextureRegion> regions) {
		anim = new Animation(1f / animFPS, regions);
		anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		setRegion(anim.getKeyFrame(0));
	}

	@Override
	public void collided(GameEntity b) {
		if(b == null) {
			dead = true;
		} else {
			if(b instanceof Enemy) {
				dead = true;
			}
		}
	}

	@Override
	protected void createBody(World world, Vector2 position) {
		float colliderWidth = getWidth() / 8 / PPM;
		colliderWidth = 4 / PPM;
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.gravityScale = 0;
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.fixedRotation = true;
		CircleShape shape = new CircleShape();
		shape.setRadius(colliderWidth/2f);
		FixtureDef fdef = new FixtureDef();
		fdef.isSensor = true;
		fdef.shape = shape;
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_CONE;
		fdef.filter.maskBits = CollisionHandler.MASK_CONE;
		fdef.filter.groupIndex = CollisionHandler.GROUP_PLAYER;
		body = world.createBody(bdef);
		Fixture f = body.createFixture(fdef);
		shape.dispose();
		body.setAngularVelocity(5f);

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin(getWidth() / 2, getHeight() / 2);
		setX(-getWidth() / 2 + Box2DUtils.width(body) / 2);
		setY(-getHeight() / 2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX() / 2f / PPM, getScaleY() / 2f / PPM);

		f.setUserData(this);
	}

	@Override
	protected void updateAdditional(float delta) {
		animTimer += delta;
		setRegion(anim.getKeyFrame(animTimer));
		if(animTimer >= anim.getAnimationDuration()) {
			animTimer = 0;
		}
	}

}
