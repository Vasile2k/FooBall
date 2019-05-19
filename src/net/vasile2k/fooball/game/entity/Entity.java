package net.vasile2k.fooball.game.entity;

import net.vasile2k.fooball.render.Shader;

import java.io.Serializable;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public abstract class Entity implements Serializable {

    public abstract void onUpdate(long deltaTime);
    public abstract void render(Shader shader);
    public abstract float getX();
    public abstract float getY();
    public abstract boolean isDead();
}
