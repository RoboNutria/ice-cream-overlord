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
		if(changeDirection) {
			walkSpeed = walkSpeed * -1f;
			changeDirection = false;
		}
		enemy.getBody().setLinearVelocity(walkSpeed, enemy.getBody().getLinearVelocity().y);
	}
	
	public void bump() {
		changeDirection = true;
	}
	

}
