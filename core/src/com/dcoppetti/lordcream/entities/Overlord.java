package com.dcoppetti.lordcream.entities;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author Diego Coppetti
 */
public class Overlord extends Box2DSprite implements GameEntity {


    public enum PlayerState {
        Idle,
        Sliding,
        OnAir
    }

	public static final String PLAYER_FOOTER = "PLAYER_FOOTER";
	public static int playerFootContanct;

    private Body body;
    private float colliderWidth;
    private float colliderHeight;
    private PlayerState state = PlayerState.Idle;
    private boolean canMove = true;

    private float slideAccel = 6f;
    private float maxSlideSpeed = 10f;
    private float jumpPower = 80f;


    public Overlord(World world, TextureRegion region, Vector2 position) {
        super(region);
        createBody(world, position);
    }

    private void createBody(World world, Vector2 position) {
		colliderWidth = getWidth()/3.6f/PPM;
		colliderHeight = getHeight()/4/PPM;
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(colliderWidth/2, colliderHeight/2);
        FixtureDef fdef = new FixtureDef();
        fdef.friction = 0.2f;
        fdef.shape = shape;
        body = world.createBody(bdef);
        body.createFixture(fdef);
        shape.dispose();

		setUseOrigin(true);
		setAdjustSize(false);
		setOrigin(getWidth()/2, (getHeight()/2)+0.08f);
		setX(-getWidth()/2 + Box2DUtils.width(body) / 2);
		setY(-getHeight()/2 + Box2DUtils.height(body) / 2);
		setScale(getScaleX()/2/PPM, getScaleY()/2/PPM);

        body.setUserData(this);
        
        createSensorFooter();
    }

    // for jumping and other detections :F
    private void createSensorFooter() {
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colliderWidth/2.5f, colliderHeight/8f,
				new Vector2(0, -colliderHeight/2), 0);
		fdef.isSensor = true;
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(PLAYER_FOOTER);
		body.createFixture(fdef);
		shape.dispose();
	}

	@Override
    public void update(float delta) {
    	updateState();
        inputMovement();
    }

    private void updateState() {
    	if((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Keys.D)) 
    			&& body.getLinearVelocity().x != 0 && isGrounded()) {
    		state = PlayerState.Sliding;
    	} else {
    		if(body.getLinearVelocity().y < -0.1f || body.getLinearVelocity().y > 0.1f) {
    			state = PlayerState.OnAir;
    		} else {
    			state = PlayerState.Idle;
    		}
    	}
	}

	private void inputMovement() {
        if(!canMove) return;
        float x = 0;
        float velX = body.getLinearVelocity().x;
        // left
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	if(velX - slideAccel >= -maxSlideSpeed) {
        		x = -slideAccel;
        	}
        }
        // right
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
        	if(velX + slideAccel <= maxSlideSpeed) {
        		x = slideAccel;
        	}
        }
        // jump
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			if(isGrounded()) {
				// Limit it or not?
				// Now limited to 20% higher
				if(body.getLinearVelocity().y + jumpPower < jumpPower*1.2f) {
					body.applyForceToCenter(0, jumpPower, true);
				}
			}
        }
        // crouch
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
        }

        // This would be the traditional way of moving at a fixed pace
        //body.setLinearVelocity(x, y);

        // This way im applying a force making it feel like it's "sliding"
        if(velX < -0.1f) {
            this.setFlip(true, false);
        } else if(velX > 0.1f){
            this.setFlip(false, false);
        }
        body.applyForceToCenter(x, 0, true);
    }
	
	public boolean isGrounded() {
		return Overlord.playerFootContanct > 0;
	}

    @Override
    public void dispose() {

    }

	@Override
	public void collided(GameEntity b) {
		// TODO Auto-generated method stub
		
	}

	public PlayerState getState() {
		return state;
	}
	
	public Body getBody() {
		return body;
	}
}
