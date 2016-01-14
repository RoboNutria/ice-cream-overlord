package com.dcoppetti.lordcream.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dcoppetti.lordcream.entities.GameEntity;

import java.util.Iterator;

/**
 * @author Diego Coppetti
 */
public class EntityHandler {

    public Array<GameEntity> inactiveAntities;
    public Array<GameEntity> activeEntities;
    public Array<Body> bodies;

    public EntityHandler() {
        inactiveAntities = new Array<GameEntity>();
        activeEntities = new Array<GameEntity>();
    }

    public EntityHandler(boolean useBodies) {
        if(useBodies) {
            bodies = new Array<Body>();
        } else {
            activeEntities = new Array<GameEntity>();
        }
        inactiveAntities = new Array<GameEntity>();
    }

    public void updateFromWorld(World world, float delta) {
        disposeInactive();
        world.getBodies(bodies);
        Iterator<Body> it = bodies.iterator();
        while(it.hasNext()) {
            Body b = it.next();
            Object userData = b.getFixtureList().first().getUserData();
            if(userData instanceof GameEntity) {
                GameEntity entity = (GameEntity) userData;
                if(entity.isKill()) {
                    inactiveAntities.add(entity);
                } else {
                    entity.update(delta);
                }
            }
        }

    }

    public void disposeInactive() {
        for(GameEntity dead : inactiveAntities) {
            dead.dispose();
        }
        inactiveAntities.clear();
    }
}
