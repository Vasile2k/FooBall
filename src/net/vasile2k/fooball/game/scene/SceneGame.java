package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.game.entity.*;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Renderer;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.render.font.FontRenderer;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneGame implements Scene {

    private Window window;

    private EventHandler eventHandler;

    private Model arena;

    private Texture arenaTexture;
    private Shader shader;

    private Matrix4f camera;
    private Matrix4f arenaModelMatrix;

    private boolean initialized = false;

    private Player player;
    private ArrayList<Enemy> enemies;

    private ArrayList<Bullet> bullets;

    private boolean paused = false;

    private boolean resumeActive = false;
    private boolean saveActive = false;
    private boolean exitActive = false;

    public SceneGame(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                if(action == GLFW_PRESS && key == GLFW_KEY_ESCAPE){
                    paused = !paused;
                }
                if(!paused){
                    player.onKey(key, scancode, action, modifiers);
                }
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                if(!paused){
                    player.onMouseButton(button, action, modifiers);
                }else{
                    if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                        if(resumeActive){
                            paused = false;
                        }else if(saveActive){
                            //TODO
                        }else if(exitActive){
                            Game.getInstance().requestSceneChange("SceneMenu");
                        }
                    }
                }
            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                if(!paused){
                    player.onCursorPosition(xPos, yPos);
                }else{
                    int[] windowSize = window.getWidthAndHeight();
                    double xNorm = xPos/windowSize[0];
                    double yNorm = yPos/windowSize[1];

                    resumeActive = false;
                    saveActive = false;
                    exitActive = false;

                    if(xNorm > 0.02 && xNorm < 0.21){
                        if(yNorm > 0.7F){
                            if(yNorm < 0.76F){
                                resumeActive = true;
                            }else if(yNorm < 0.84F){
                                saveActive = true;
                            }else if(yNorm < 0.92F){
                                exitActive = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(double xPos, double yPos) {
                if(!paused){
                    player.onScroll(xPos, yPos);
                }
            }
        };
    }

    @Override
    public void onLoad(Window window) {
        this.window = window;
        window.getEventListener().registerHandler(this.eventHandler);

        if(!this.initialized){
            this.arena = Model.ModelFactory.createNewModelFromFile("res/model/arena.obj");

            this.arenaTexture = new Texture("res/texture/arena.png");

            this.shader = new Shader("res/shader/colored");

            this.player = new Player(this);
            this.enemies = new ArrayList<>();
            this.generateEnemies();
            this.bullets = new ArrayList<>();

            this.camera = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.1F, 100F)
                    .lookAt(0.0F, 10.0F, 9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
            this.arenaModelMatrix = new Matrix4f();
            this.initialized = true;
        }
        this.onResize();
    }

    @Override
    public void onUnload() {
        this.window.getEventListener().unregisterHandler(this.eventHandler);
    }

    @Override
    public void onUpdate(long deltaTime) {
        if(!paused){
            this.player.onUpdate(deltaTime);
            for(Enemy e: this.enemies){
                e.onUpdate(deltaTime);
            }
            for(Bullet b: this.bullets){
                b.onUpdate(deltaTime);
                if(b.isFiredByPlayer()){
                    for(Enemy e: this.enemies){
                        float distToBullet = CollisionHelper.dist2(b.getX(), b.getY(), e.getX(), e.getY());
                        if(distToBullet < 1.0F){
                            e.damage();
                            b.die();
                        }
                    }
                }else{
                    float distToPlayer = CollisionHelper.dist2(b.getX(), b.getY(), this.player.getX(), this.player.getY());
                    if(distToPlayer < 1.0F){
                        player.damage();
                        b.die();
                    }
                }
            }
            this.enemies.removeIf(Enemy::isDead);
            this.bullets.removeIf(Bullet::isDead);
        }
    }

    /**
     * Receives an array with coordinates and writes back to it coords after computing collisions
     * @param coordinates the array of coordinates
     */
    public void checkPlayerCoordinates(float[] coordinates){
        coordinates[0] = CollisionHelper.clampFloat(coordinates[0], -10.0F, 10.0F);
        coordinates[1] = CollisionHelper.clampFloat(coordinates[1], -6.0F, 6.0F);

        float newX1 = coordinates[0];
        float newX2 = coordinates[0];
        float newY1 = coordinates[1];
        float newY2 = coordinates[1];

        for(Enemy e: this.enemies){
            if(CollisionHelper.abs(player.getX() - e.getX()) < 2.0F){
                if(player.getY() > e.getY()){
                    if(CollisionHelper.abs(player.getY() - e.getY()) < 2.0F){
                        newY1 = e.getY() + 2.0F;
                    }
                }else{
                    if(CollisionHelper.abs(player.getY() - e.getY()) < 2.0F){
                        newY1 = e.getY() - 2.0F;
                    }
                }
            }
            if(CollisionHelper.abs(player.getY() - e.getY()) < 2.0F){
                if(player.getX() > e.getX()){
                    if(CollisionHelper.abs(player.getX() - e.getX()) < 2.0F){
                        newX2 = e.getX() + 2.0F;
                    }
                }else{
                    if(CollisionHelper.abs(player.getX() - e.getX()) < 2.0F){
                        newX2 = e.getX() - 2.0F;
                    }
                }
            }
            float dist1 = CollisionHelper.dist2(newX1, newY1, e.getX(), e.getY());
            float dist2 = CollisionHelper.dist2(newX2, newY2, e.getX(), e.getY());
            coordinates[0] = (dist1 < dist2) ? newX1 : newX2;
            coordinates[1] = (dist1 < dist2) ? newY1 : newY2;
        }
    }

    public void addBullet(Bullet b){
        this.bullets.add(b);
    }

    private void generateEnemies(){
        this.enemies.add(new Enemy(this, 10, -6, 20.0F));
//        this.enemies.add(new Enemy(this, (float)Math.random() * 10 - 5, (float)Math.random() * 10 - 5, 20.0F));
    }

    @Override
    public void onRender() {

        if(paused){
            Renderer.getInstance().clearColor(0.05F, 0.05F, 0.05F, 1.0F);
        }else{
            this.shader.bind();

            this.shader.setUniform3f("objectColor", 1.0F, 1.0F, 1.0F);
            this.shader.setUniformMat4f("viewProjMatrix", this.camera);

            this.shader.setUniformMat4f("modelMatrix", this.arenaModelMatrix);
            this.arenaTexture.bind(0);
            this.arena.render();

            this.player.render(this.shader);
            for(Enemy e: this.enemies){
                e.render(this.shader);
            }
            for(Bullet b: this.bullets){
                b.render(this.shader);
            }
        }
    }

    @Override
    public void onGuiRender(FontRenderer fontRenderer) {
        if(paused){
            fontRenderer.renderText("resume", 0.1F, 0.06F, -0.95F, -0.5F, resumeActive ? 0.8F : 0.2F, resumeActive ? 0.25F : 0.8F, 0.3F);
            fontRenderer.renderText("save", 0.1F, 0.06F, -0.95F, -0.65F, saveActive ? 0.8F : 0.2F, saveActive ? 0.25F : 0.8F, 0.3F);
            fontRenderer.renderText("exit", 0.1F, 0.06F, -0.95F, -0.8F, exitActive ? 0.8F : 0.2F, exitActive ? 0.25F : 0.8F, 0.3F);
        }
    }

    @Override
    public void onResize() {
        // Update projection matrix to new aspect ratio
        this.camera = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.1F, 100F)
                .lookAt(0.0F, 10.0F, 9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
