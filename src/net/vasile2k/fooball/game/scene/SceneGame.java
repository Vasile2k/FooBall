package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.database.DatabaseManager;
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

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneGame implements Scene, Serializable {

    /**
     * Required for serialization to match
     * A unique magic number
     */
    private static final long serialVersionUID = 0x69696969690A1ECAL;

    private transient Window window;

    private transient EventHandler eventHandler;

    private transient Model arena;

    private transient Texture arenaTexture;
    private transient Shader shader;

    private transient Matrix4f camera;
    private transient Matrix4f arenaModelMatrix;

    private transient boolean initialized = false;

    private Player player;
    private ArrayList<Enemy> enemies;

    private ArrayList<Bullet> bullets;

    private transient boolean paused = false;

    private transient boolean resumeActive = false;
    private transient boolean saveActive = false;
    private transient boolean exitActive = false;

    private int level = 1;

    private int score = 0;

    public SceneGame(){
        this.build();
    }

    /**
     * Basically the same shit as constructor, just that you can call it. <br/>
     * Needed when you deserialize the class
     */
    public void build(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                if(!player.isDead()){
                    if(action == GLFW_PRESS && key == GLFW_KEY_ESCAPE){
                        paused = !paused;
                    }
                    if(!paused){
                        player.onKey(key, scancode, action, modifiers);
                    }
                }else{
                    if(action == GLFW_PRESS && key == GLFW_KEY_ESCAPE){
                        DatabaseManager.saveScore(score);
                        Game.getInstance().requestSceneChange("SceneMenu");
                    }
                }
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                if(!player.isDead()){
                    if(!paused){
                        player.onMouseButton(button, action, modifiers);
                    }else{
                        if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                            if(resumeActive){
                                paused = false;
                            }else if(saveActive){
                                DatabaseManager.saveState(getThis());
                                Game.getInstance().requestSceneChange("SceneMenu");
                            }else if(exitActive){
                                Game.getInstance().requestSceneChange("SceneMenu");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                if(!player.isDead()){
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
            }

            @Override
            public void onScroll(double xPos, double yPos) {
                if(!player.isDead()){
                    if(!paused){
                        player.onScroll(xPos, yPos);
                    }
                }
            }
        };
        if(this.player != null){
            this.player.build();
        }
    }

    @Override
    public void onLoad(Window window) {
        this.window = window;
        window.getEventListener().registerHandler(this.eventHandler);

        if(!this.initialized){
            this.arena = Model.ModelFactory.createNewModelFromFile("res/model/arena.obj");

            this.arenaTexture = new Texture("res/texture/arena.png");

            this.shader = new Shader("res/shader/colored");

            // These can be loaded somewhere else when this class is deserialized
            // Check function saveState() for more details
            if(this.player == null){
                this.player = new Player(this, 20.0F);
            }
            if(this.enemies == null){
                this.enemies = new ArrayList<>();
                this.generateEnemies();
            }
            if(this.bullets == null){
                this.bullets = new ArrayList<>();
            }

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
        if(!player.isDead()){
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
                                ++score;
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
                if(this.enemies.size() == 0){
                    ++level;
                    this.generateEnemies();
                }
            }
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
        int enemiesToGenerate = level + 1;
        generate:
        while(enemiesToGenerate > 0){
            float x = (float)Math.random() * 20.0F - 10.0F;
            float y = (float)Math.random() * 12.0F - 6.0F;
            for(Enemy e: this.enemies){
                float dist2 = CollisionHelper.dist2(x, y, e.getX(), e.getY());
                if(dist2 < 3.0F){
                    continue generate;
                }
            }
            this.enemies.add(new Enemy(this, x, y, 8.0F + 2.0F * this.level, 1000 - 100 * this.level));
            --enemiesToGenerate;
        }
    }

    @Override
    public void onRender() {

        if(!player.isDead()){
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
        }else{
            Renderer.getInstance().clearColor(0.05F, 0.05F, 0.05F, 1.0F);
        }

    }

    @Override
    public void onGuiRender(FontRenderer fontRenderer) {
        if(paused){
            fontRenderer.renderText("resume", 0.1F, 0.06F, -0.95F, -0.5F, resumeActive ? 0.8F : 0.2F, resumeActive ? 0.25F : 0.8F, 0.3F);
            fontRenderer.renderText("save", 0.1F, 0.06F, -0.95F, -0.65F, saveActive ? 0.8F : 0.2F, saveActive ? 0.25F : 0.8F, 0.3F);
            fontRenderer.renderText("exit", 0.1F, 0.06F, -0.95F, -0.8F, exitActive ? 0.8F : 0.2F, exitActive ? 0.25F : 0.8F, 0.3F);
        }else{
            fontRenderer.renderText("Enemies left: " + this.enemies.size() + "     Level: " + this.level, 0.1F, 0.06F, -0.95F, -0.85F, 1.0F, 1.0F, 1.0F);
            fontRenderer.renderText("Score: " + this.score, 0.1F, 0.06F, -0.95F, 0.85F, 1.0F, 1.0F, 1.0F);
            this.player.renderHealth(fontRenderer);
            if(this.player.isDead()){
                fontRenderer.renderText("_ded", 0.3F, 0.2F, -0.4F, -0.15F, 1.0F, 0.0F, 0.0F);
                fontRenderer.renderText("Press ESCAPE to return to menu", 0.09F, 0.05F, -0.7F, -0.4F, 0.2F, 0.8F, 0.3F);
            }
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

    /**
     * Used to send this object to serializer
     * @return this object
     */
    private SceneGame getThis(){
        return this;
    }

}
