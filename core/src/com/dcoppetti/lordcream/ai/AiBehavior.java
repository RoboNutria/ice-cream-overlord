package com.dcoppetti.lordcream.ai;

import com.dcoppetti.lordcream.entities.Enemy;

/**
 * @author Diego Coppetti
 *
 */
public abstract class AiBehavior {

	protected boolean active;
	protected Enemy enemy;

	public boolean isActive() {
		return active;
	}

	public AiBehavior(Enemy enemy) {
		this.active = true;
		this.enemy = enemy;
	}

	public void setActive(boolean b) {
		this.active = b;
	}

	public void update(float delta) {
		if(!isActive()) return;
		updateBehavior(delta);
	}

	public abstract void updateBehavior(float delta);

	public abstract void colliderNotify();

}
