package net.vasile2k.fooball.game.entity;

import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import org.joml.Matrix4f;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class Enemy extends Entity {

    private Model model;
    private Model shadow;
    private Texture modelTexture;
    private Texture shadowTexture;

    private float x;
    private float y;

    private float angle;

    public Enemy(){
        this.model = Model.ModelFactory.createNewModelFromFile("res/model/enemy.obj");
        this.shadow = Model.ModelFactory.createNewModelFromFile("res/model/shadow.obj");
        this.modelTexture = new Texture("res/texture/enemy.png");
        this.shadowTexture = new Texture("res/texture/shadow.png");
        this.x = 0.0F;
        this.y = 0.0F;
        this.angle = 0.0F;
    }

    @Override
    public void onUpdate(long deltaTime) {
        this.angle += 0.01F;
    }

    @Override
    public void render(Shader shader) {
        shader.setUniformMat4f("modelMatrix", new Matrix4f().translate(this.x, 0, this.y).rotate(this.angle, 0.0F, 1.0F, 0.0F));
        this.modelTexture.bind(0);
        this.model.render();

        this.shadowTexture.bind(0);
        this.shadow.render();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
