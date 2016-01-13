package com.dcoppetti.lordcream.ai;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dcoppetti.lordcream.entities.Enemy;
import com.dcoppetti.lordcream.handlers.CollisionHandler;

public class WalkBehavior extends AiBehavior {
	
	private boolean stayOnPlatform = false;
	private float walkSpeed;
	private boolean changeDirection = false;
	private float yOffset = -6f;
	
	private Fixture leftSensor;
	private Fixture rightSensor;
	
	private Body body;
	
	public WalkBehavior(Enemy enemy, float walkSpeed, boolean stayOnPlatform) {
		super(enemy);
		this.walkSpeed = walkSpeed;
		this.stayOnPlatform = stayOnPlatform;
	}

	public void init(Enemy enemy) {
		this.body = enemy.getBody();
		if(stayOnPlatform) {
			if(enemy.isLookingLeft()) {
				createLeftSensor();
			} else {
				createRightSensor();
			}
		}
	}

	private void createLeftSensor() {
		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_ENEMY_SENSORS;
		fdef.filter.maskBits = CollisionHandler.MASK_SENSOR;
		fdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
		CircleShape shape = new CircleShape();
		shape.setRadius(1.5f/PPM);
		shape.setPosition(new Vector2(-6f/PPM, yOffset/PPM));
		fdef.isSensor = true;
		fdef.shape = shape;
		leftSensor = body.createFixture(fdef);
		leftSensor.setUserData(this);
		shape.dispose();
	}

	private void createRightSensor() {
		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionHandler.CATEGORY_ENEMY_SENSORS;
		fdef.filter.maskBits = CollisionHandler.MASK_SENSOR;
		fdef.filter.groupIndex = CollisionHandler.GROUP_SENSOR;
		CircleShape shape = new CircleShape();
		shape.setRadius(1.5f/PPM);
		shape.setPosition(new Vector2(6f/PPM, yOffset/PPM));
		fdef.isSensor = true;
		fdef.shape = shape;
		rightSensor = body.createFixture(fdef);
		rightSensor.setUserData(this);
		shape.dispose();
	}

	
	private void updateSensors() {
		if(!changeDirection) return;
		changeDirection = false;
		this.walkSpeed = walkSpeed * -1f;
		if(walkSpeed < 0) {
			body.destroyFixture(rightSensor);
			createLeftSensor();
		} else if(walkSpeed > 0) {
			body.destroyFixture(leftSensor);
			createRightSensor();
		}
	}

	public void changeDirection() {
		this.changeDirection = true;
	}
	
	public float getWalkSpeed() {
		return walkSpeed;
	}
	
	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}

	@Override
	public void updateBehavior(float delta) {
		updateSensors();
		Body b = enemy.getBody();
		b.setLinearVelocity(walkSpeed, b.getLinearVelocity().y);
	}


}
