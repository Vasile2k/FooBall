package net.vasile2k.fooball.game.entity;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.game.scene.SceneGame;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.render.font.FontRenderer;
import net.vasile2k.fooball.window.EventHandler;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class Player extends Entity implements EventHandler {

    private Model model;
    private Model shadow;
    private Texture playerTexture;
    private Texture shadowTexture;

    private Set<Integer> keysPressed;

    private float x;
    private float y;

    private float angle;

    private SceneGame scene;
    protected float maxHealth;
    protected float health;

    public Player(SceneGame sceneGame, float health){
        this.model = Model.ModelFactory.createNewModelFromFile("res/model/suzanne2.obj");
        this.shadow = Model.ModelFactory.createNewModelFromFile("res/model/shadow.obj");
        this.playerTexture = new Texture("res/texture/monkey_baked.png");
        this.shadowTexture = new Texture("res/texture/shadow.png");
        this.keysPressed = new HashSet<>();
        this.x = 0.0F;
        this.y = 0.0F;
        this.angle = 0.0F;
        this.scene = sceneGame;
        this.maxHealth = health;
        this.health = health;
    }

    @Override
    public void onUpdate(long deltaTime) {
        float speed = 2.0F/deltaTime;

        if(this.keysPressed.contains(GLFW_KEY_W)) {
            Vector3f dir = new Vector3f(0.0F, 0.0F, speed);
            dir.rotateY(this.angle);
            this.x += dir.x;
            this.y += dir.z;
        }
        if(this.keysPressed.contains(GLFW_KEY_S)) {
            Vector3f dir = new Vector3f(0.0F, 0.0F, -speed);
            dir.rotateY(this.angle);
            this.x += dir.x;
            this.y += dir.z;
        }
        if(this.keysPressed.contains(GLFW_KEY_A)) {
            this.angle += speed;
        }
        if(this.keysPressed.contains(GLFW_KEY_D)) {
            this.angle -= speed;
        }
        float[] coords = {this.x, this.y};
        this.scene.checkPlayerCoordinates(coords);
        this.x = coords[0];
        this.y = coords[1];
    }

    @Override
    public void render(Shader shader) {

        shader.setUniform3f("objectColor", this.health/this.maxHealth, this.health/this.maxHealth, this.health/this.maxHealth);

        shader.setUniformMat4f("modelMatrix", new Matrix4f().translate(this.x, 0, this.y).rotate(this.angle, 0.0F, 1.0F, 0.0F));
        this.playerTexture.bind(0);
        this.model.render();

        this.shadowTexture.bind(0);
        this.shadow.render();
    }

    public void renderHealth(FontRenderer fontRenderer){
        fontRenderer.renderText("Health: " + (int)this.health, 0.1F, 0.06F, 0.1F, 0.85F, 1.0F, 0.0F, 0.0F);
    }

    @Override
    public void onKey(int key, int scancode, int action, int modifiers) {
        if(action == GLFW_PRESS){
            switch (key){
                case GLFW_KEY_W:
                case GLFW_KEY_S:
                case GLFW_KEY_A:
                case GLFW_KEY_D:
                    keysPressed.add(key);
                    break;
                case GLFW_KEY_SPACE:
                    this.fire();
                    break;
            }
        }else if(action == GLFW_RELEASE){
            switch (key){
                case GLFW_KEY_W:
                case GLFW_KEY_S:
                case GLFW_KEY_A:
                case GLFW_KEY_D:
                    keysPressed.remove(key);
                    break;
            }
        }
    }

    @Override
    public void onMouseButton(int button, int action, int modifiers) {

    }

    @Override
    public void onCursorPosition(double xPos, double yPos) {

    }

    @Override
    public void onScroll(double xPos, double yPos) {

    }

    public void damage(){
        this.health -= 1.0F;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean isDead() {
        return this.health <= 0.0F;
    }

    private void fire(){
        this.scene.addBullet(new Bullet(this.scene, this.x, this.y, new Vector3f(0.0F, 0.0F, 1.0F).rotateY(this.angle), true));
    }
}
