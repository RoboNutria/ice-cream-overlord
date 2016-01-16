package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.dcoppetti.lordcream.handlers.BulletFactory.BulletType;
import com.dcoppetti.lordcream.handlers.CollisionHandler;

public class Bullet extends Box2DSprite implements GameEntity {
	
	private Body body;
	private Vector2 velocity;
	private BulletType type;
	
	private boolean dead = false;
	
	public Bullet(BulletType type, TextureRegion region, World world, Vector2 velocity, Vector2 position) {
		super(region);
		createBody(world, position);
		this.velocity = velocity;
		this.type = type;
	}

	private void createBody(World world, Vector2 position) {
		float colliderWidth = getWidth() / PPM;
		float colliderHeight = getHeight() / PPM;
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.fixedRotation = true;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth / 2, colliderHeight / 2);
		FixtureDef fdef = new FixtureDef();
		fdef.isSensor = true;
		fdef.shape = shape;
		if(type == BulletType.PlayerBullet) {
			fdef.filter.categoryBits = CollisionHandler.CATEGORY_PLAYER;
			fdef.filter.maskBits = CollisionHandler.MASK_PLAYER;
		} else if(type == BulletType.EnemyBullet) {
			fdef.filter.categoryBits = CollisionHandler.CATEGORY_ENEMY;
			fdef.filter.maskBits = CollisionHandler.MASK_ENEMY;
		}
		body = world.createBody(bdef);
		Fixture f = body.createFixture(fdef);
		shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin(getWidth() / 2, getHeight() / 2);
		setX(-getWidth() / 2 + Box2DUtils.width(body) / 2);
		setY(-getHeight() / 2 + Box2DUtils.height(body) / 2);
		//setScale(getScaleX() / 1.5f / PPM, getScaleY() / 1.5f / PPM);
		setScale(getScaleX() / PPM, getScaleY() / PPM);

		f.setUserData(this);
	}

	@Override
	public void update(float delta) {
		if(dead) return;
		checkBounds();
		
		System.out.println("balin!");
		body.setLinearVelocity(velocity);
	}

	@Override
	public void dispose() {
		body.getWorld().destroyBody(body);
	}

	@Override
	public void collided(GameEntity b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isKill() {
		return dead;
	}

	public void checkBounds() {
		// TODO: Implement me
		// check bounds and set dead
	}

}
