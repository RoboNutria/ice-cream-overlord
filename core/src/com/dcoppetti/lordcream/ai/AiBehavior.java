package com.dcoppetti.lordcream.ai;

import com.dcoppetti.lordcream.entities.Enemy;

/**
 * @author Diego Coppetti
 *
 */
public interface AiBehavior {
	public void init(Enemy enemy);
	public void update(float delta, Enemy enemy);
}
