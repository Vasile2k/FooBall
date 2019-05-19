package net.vasile2k.fooball.game.entity;

import net.vasile2k.fooball.game.scene.SceneGame;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class Bullet extends Entity {

    /**
     * Required for serialization to match
     * A unique magic number
     */
    private static final long serialVersionUID = 0xCAL;

    private static Model model;
    private static Texture modelTexture;

    private float x;
    private float y;

    private Vector3f forwardVector;

    private SceneGame sceneGame;

    private boolean firedByPlayer;

    private boolean dead = false;

    static {
        model = Model.ModelFactory.createNewModelFromFile("res/model/bullet.obj");
        modelTexture = new Texture("res/texture/enemy.png");
    }

    public Bullet(SceneGame sceneGame, float x, float y, Vector3f forwardVector, boolean firedByPlayer){
        this.x = x;
        this.y = y;
        this.forwardVector = forwardVector;
        this.sceneGame = sceneGame;
        this.firedByPlayer = firedByPlayer;
    }

    @Override
    public void onUpdate(long deltaTime) {
        float speed = 3.5F/deltaTime;
        this.x += speed * forwardVector.x;
        this.y += speed * forwardVector.z;
    }

    @Override
    public void render(Shader shader) {
        shader.setUniform3f("objectColor", this.firedByPlayer ? 1.0F : 0.0F, 0.0F, 0.0F);
        shader.setUniformMat4f("modelMatrix", new Matrix4f().translate(this.x, 0, this.y));
        modelTexture.bind(0);
        model.render();
    }

    public boolean isFiredByPlayer(){
        return this.firedByPlayer;
    }

    public void die(){
        this.dead = true;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public boolean isDead() {
        return this.dead || CollisionHelper.abs(this.x) > 20.0F || CollisionHelper.abs(this.y) > 20.0F;
    }
}
