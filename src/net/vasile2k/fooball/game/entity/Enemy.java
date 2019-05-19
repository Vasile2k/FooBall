package net.vasile2k.fooball.game.entity;

import net.vasile2k.fooball.game.scene.SceneGame;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class Enemy extends Entity {

    private static Model model;
    private static Model shadow;
    private static Texture modelTexture;
    private static Texture shadowTexture;

    private float x;
    private float y;

    private float angle;

    private float maxHealth;
    private float health;

    private long firingInterval;

    private SceneGame sceneGame;

    private long timeSinceLastBullet = 0;

    static {
        model = Model.ModelFactory.createNewModelFromFile("res/model/enemy.obj");
        shadow = Model.ModelFactory.createNewModelFromFile("res/model/shadow.obj");
        modelTexture = new Texture("res/texture/enemy.png");
        shadowTexture = new Texture("res/texture/shadow.png");
    }

    public Enemy(SceneGame sceneGame, float x, float y, float health, long firingInterval){
        this.x = x;
        this.y = y;
        this.angle = 0.0F;
        this.sceneGame = sceneGame;
        this.maxHealth = health;
        this.health = health;
        this.firingInterval = firingInterval;
    }

    @Override
    public void onUpdate(long deltaTime) {
        timeSinceLastBullet += deltaTime;
        if(timeSinceLastBullet > firingInterval){
            this.fire();
            timeSinceLastBullet -= firingInterval;
        }
        this.angle += 0.01F;
    }

    @Override
    public void render(Shader shader) {
        shader.setUniform3f("objectColor", 1.0F, this.health/this.maxHealth, this.health/this.maxHealth);
        shader.setUniformMat4f("modelMatrix", new Matrix4f().translate(this.x, 0, this.y).rotate(this.angle, 0.0F, 1.0F, 0.0F));
        modelTexture.bind(0);
        model.render();

        shadowTexture.bind(0);
        shadow.render();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void fire(){
        this.sceneGame.addBullet(new Bullet(this.sceneGame, this.x, this.y, new Vector3f(0.0F, 0.0F, 1.0F).rotateY(this.angle), false));
        this.sceneGame.addBullet(new Bullet(this.sceneGame, this.x, this.y, new Vector3f(0.0F, 0.0F, 1.0F).rotateY(this.angle + (float)Math.PI/2), false));
        this.sceneGame.addBullet(new Bullet(this.sceneGame, this.x, this.y, new Vector3f(0.0F, 0.0F, 1.0F).rotateY(this.angle + (float)Math.PI), false));
        this.sceneGame.addBullet(new Bullet(this.sceneGame, this.x, this.y, new Vector3f(0.0F, 0.0F, 1.0F).rotateY(this.angle + (float)Math.PI*3/2), false));
    }

    public void damage(){
        this.health -= 1.0F;
    }

    @Override
    public boolean isDead() {
        return this.health <= 0.0F;
    }
}
