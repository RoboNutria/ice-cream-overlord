package com.dcoppetti.lordcream.ai;

import com.dcoppetti.lordcream.entities.Enemy;


public class WalkBumpBehavior extends AiBehavior {
	
	private float walkSpeed;
	private boolean changeDirection;
	
	public WalkBumpBehavior(Enemy enemy, float walkSpeed) {
		super(enemy);
		this.walkSpeed = walkSpeed;
	}

	@Override
	public void updateBehavior(float delta) {
		changeDir();
		enemy.getBody().setLinearVelocity(walkSpeed, enemy.getBody().getLinearVelocity().y);
	}
	
	private void changeDir() {
		if(changeDirection) {
			walkSpeed = walkSpeed * -1f;
			enemy.getBody().setLinearVelocity(walkSpeed, enemy.getBody().getLinearVelocity().y);
			changeDirection = false;
		}
	}
	
	@Override
	public void colliderNotify() {
		changeDirection = true;
	}
	

}
