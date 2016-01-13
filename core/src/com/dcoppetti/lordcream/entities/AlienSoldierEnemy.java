package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class AlienSoldierEnemy extends Enemy {

	public AlienSoldierEnemy(World world, TextureRegion region, Vector2 position) {
		super(world, region, position);
	}

	@Override
	public void collided(GameEntity b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createBody(World world, Vector2 position) {
		float colliderWidth = 6f/PPM;
		float colliderHeight = 6f/PPM;
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(colliderWidth/2, colliderHeight/2);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body = world.createBody(bdef);
        Fixture f = body.createFixture(fdef);
        shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOriginCenter();
		setX(-getWidth()/2 + Box2DUtils.width(body) / 2);
		setY(-getHeight()/2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX()/PPM, getScaleY()/PPM);

        f.setUserData(this);
	}

	@Override
	protected void updateEnemy(float delta) {
		// TODO Auto-generated method stub

	}

}
