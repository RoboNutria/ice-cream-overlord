package com.dcoppetti.lordcream.handlers;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_WIDTH;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_HEIGHT;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
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
	private boolean useBounds = false;
	private float xBound;
	private float yBound;
	
	public CameraHandler(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	public void setTarget(Body target, boolean followTarget) {
		this.target = target;
		this.followTarget = followTarget;
	}
	
	public void update() {
		float x = 0;
		float y = 0;
		if(followTarget) {
			x = target.getPosition().x;
			y = target.getPosition().y;
		}
		if(followX) camera.position.x = camera.position.x + (x - camera.position.x) * lerp;
		if(followY) camera.position.y = camera.position.y + (y - camera.position.y) * lerp;
		if(useBounds) {
			camera.position.x = MathUtils.clamp(camera.position.x, V_WIDTH/2/PPM, xBound-(V_WIDTH/2/PPM));
			camera.position.y = MathUtils.clamp(camera.position.y, V_HEIGHT/2/PPM, yBound-(V_HEIGHT/2/PPM));
		}
		camera.update();
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

	public void setBoundY(float mapHeight) {
		useBounds = true;
		yBound = mapHeight;
	}

	public void setBoundX(float mapWidth) {
		useBounds = true;
		xBound = mapWidth;
	}

}
