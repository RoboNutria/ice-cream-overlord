package com.dcoppetti.lordcream.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.PPM;

/**
 * @author Diego Coppetti
 */
public class Overlord extends Box2DSprite implements GameEntity {


    public enum PlayerState {
        Idle,
        Walking,
        Jumping
    };

    private Body body;
    private PlayerState state = PlayerState.Idle;
    private boolean canMove = true;

    private float slideSpeed = 15f;
    private float maxSlideSpeed = 2f;
    private float jumpPower = 10f;


    public Overlord(World world, TextureRegion region, Vector2 position) {
        super(region);
        createBody(world, position);
    }

    private void createBody(World world, Vector2 position) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32/PPM, 32/PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.friction = 0.2f;
        fdef.shape = shape;
        body = world.createBody(bdef);
        body.createFixture(fdef);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        inputMovement();
    }

    private void inputMovement() {
        if(!canMove) return;
        float x = 0;
        // left
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            x = -slideSpeed;
        }
        // right
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            x = slideSpeed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
        }
        // jump
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
        }
        // crouch
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
        }

        // This would be the traditional way of moving at a fixed pace
        //body.setLinearVelocity(x, y);

        // This way im applying a force making it feel like it's "sliding"
        float velX = body.getLinearVelocity().x;
        if(velX < 0) {
            this.setFlip(true, false);
        } else if(velX > 0){
            this.setFlip(false, false);
        }
        if(velX < maxSlideSpeed && velX > -maxSlideSpeed) {
            body.applyForceToCenter(x, 0, true);
        }
    }

    @Override
    public void dispose() {

    }
}
