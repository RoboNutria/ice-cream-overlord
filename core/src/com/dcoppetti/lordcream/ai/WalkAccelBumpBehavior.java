package com.dcoppetti.lordcream.ai;

import com.dcoppetti.lordcream.entities.Enemy;


public class WalkAccelBumpBehavior extends AiBehavior {
	
	private float accel;
	private float limit;
	private boolean changeDirection;
	
	public WalkAccelBumpBehavior(Enemy enemy, float accel, float limit) {
		super(enemy);
		this.accel = accel;
		this.limit = limit;
	}

	@Override
	public void updateBehavior(float delta) {
		changeDir();
		enemy.getBody().applyForceToCenter(accel, 0, true);
		if(enemy.getBody().getLinearVelocity().x >= limit) {
			enemy.getBody().setLinearVelocity(limit, enemy.getBody().getLinearVelocity().y);
		}
		if(enemy.getBody().getLinearVelocity().x <= -limit) {
			enemy.getBody().setLinearVelocity(-limit, enemy.getBody().getLinearVelocity().y);
		}
	}
	
	private void changeDir() {
		if(changeDirection) {
			accel = accel * -1f;
			enemy.getBody().setLinearVelocity(accel, enemy.getBody().getLinearVelocity().y);
			changeDirection = false;
		}
	}

	@Override
	public void colliderNotify() {
		changeDirection = true;
	}
	

}
