package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * @author Diego Coppetti
 *
 */
public class CameraHandler {
	
	private OrthographicCamera camera;
	private Body target;
	private float lerp = 0.08f;
	private boolean followTarget = false;
	private boolean followX = true;
	private boolean followY = true;
	
	public CameraHandler(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	public void setTarget(Body target, boolean followTarget) {
		this.target = target;
		this.followTarget = followTarget;
	}
	
	public void update() {
		if(followTarget) {
			float x = target.getPosition().x;
			float y = target.getPosition().y;
			if(followX) camera.position.x = camera.position.x + (x - camera.position.x) * lerp;
			if(followY) camera.position.y = camera.position.y + (y - camera.position.y) * lerp;
			camera.update();
		}
	}
	
	public void setLerp(float lerp) {
		this.lerp = lerp;
	}
	
	public void setFollowX(boolean follow) {
		this.followX = follow;
	}

	public void setFollowY(boolean follow) {
		this.followY = follow;
	}

}
