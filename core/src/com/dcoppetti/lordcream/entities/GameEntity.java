package com.dcoppetti.lordcream.entities;

/**
 * @author Diego Coppetti
 */
public interface GameEntity {

    public void update(float delta);
    public void dispose();
    public void collided(GameEntity b);
    public boolean isKill();
}
