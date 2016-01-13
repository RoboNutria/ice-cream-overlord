package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;

import com.dcoppetti.lordcream.handlers.CollisionHandler;

import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class SlugEnemy extends Enemy {

	private Animation idleAnim;
	private Animation slideAnim;
	private float idleFPS = 4;
	private float slideFPS = 8;
	private float idleAnimTimer = 0;
	private float slideAnimTimer = 0;

	public SlugEnemy(World world, TextureRegion region, Vector2 position) {
		super(world, region, position);
	}

	@Override
	public void collided(GameEntity b) {
		// TODO Auto-generated method stub

	}

    public void setAnimationRegions(Array<TextureRegion> idleRegions, Array<TextureRegion> slideRegions) {
    	idleAnim = new Animation(1f/idleFPS, idleRegions);
    	idleAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    	setRegion(idleAnim.getKeyFrame(0));
    	
    	slideAnim = new Animation(1f/slideFPS, slideRegions);
    	slideAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

	@Override
	protected void createBody(World world, Vector2 position) {
		float colliderWidth = 7/PPM;
		float colliderHeight = 4.5f/PPM;
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(colliderWidth/2, colliderHeight/2);
        FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_ENEMY;
		fdef.filter.maskBits = CollisionHandler.MASK_ENEMY;
		fdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
        fdef.shape = shape;
        body = world.createBody(bdef);
        Fixture f = body.createFixture(fdef);
        shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin((getWidth()/2)+0.03f, (getHeight()/2)+0.05f);
		setX(-getWidth()/2 + Box2DUtils.width(body) / 2);
		setY(-getHeight()/2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX()/1.2f/PPM, getScaleY()/1.2f/PPM);
		
		f.setUserData(this);
	}

	@Override
	protected void updateEnemy(float delta) {
		updateAnimations(delta);
	}

	private void updateAnimations(float delta) {
		TextureRegion region = null;
		if(idleAnim != null) {
			switch (state) {
			case Idle:
				slideAnimTimer = 0;
				idleAnimTimer += delta;
				region = idleAnim.getKeyFrame(idleAnimTimer);
				setRegion(region);
				break;
			case Sliding:
				idleAnimTimer = 0;
				slideAnimTimer += delta;
				region = slideAnim.getKeyFrame(slideAnimTimer);
				setRegion(region);
				break;
//			case OnAir:
//				idleAnimTimer = 0;
//				slideAnimTimer = 0;
//				wallAnimTimer = 0;
//				jumpAnimTimer += delta;
//				region = jumpAnim.getKeyFrame(jumpAnimTimer);
//				setRegion(region);
//				break;
//			case OnWall:
//				idleAnimTimer = 0;
//				slideAnimTimer = 0;
//				jumpAnimTimer = 0;
//				wallAnimTimer += delta;
//				region = wallAnim.getKeyFrame(wallAnimTimer);
//				setRegion(region);
//				break;
			default:
				break;
			}
		}
//		if(facingLeft) {
//			setFlip(true, false);
//		} else {
//			setFlip(false, false);
//		}
	}

}
