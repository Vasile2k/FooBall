package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Renderer;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.render.font.FontRenderer;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneGame implements Scene {

    private Window window;

    private EventHandler eventHandler;

    private Model arena;
    private Model player;
    private Model shadow;

    private Texture arenaTexture;
    private Texture playerTexture;
    private Texture shadowTexture;
    private Shader shader;

    private Matrix4f camera;
    private Matrix4f arenaModelMatrix;
    private Matrix4f playerModelMatrix;

    private boolean initialized = false;

    public SceneGame(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
//                if(action == GLFW_PRESS){
                    switch (key){
                        case GLFW_KEY_W:
                            playerModelMatrix.translate(0.0F, 0.0F, 0.1F);
                            break;
                        case GLFW_KEY_S:
                            playerModelMatrix.translate(0.0F, 0.0F, -0.1F);
                            break;
                        case GLFW_KEY_A:
//                            playerModelMatrix.translate(0.1F, 0.0F, 0.0F);
                            playerModelMatrix.rotate(0.1F, 0.0F, 1.0F, 0.0F);
                            break;
                        case GLFW_KEY_D:
//                            playerModelMatrix.translate(0.1F, 0.0F, 0.0F);
                            playerModelMatrix.rotate(-0.1F, 0.0F, 1.0F, 0.0F);
                            break;
                        case GLFW_KEY_ESCAPE:
                            Game.getInstance().requestSceneChange("SceneMenu");
                            break;
                    }
//                }
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
        };
    }

    @Override
    public void onLoad(Window window) {
        this.window = window;
        window.getEventListener().registerHandler(this.eventHandler);

        if(!this.initialized){
            this.arena = Model.ModelFactory.createNewModelFromFile("res/model/arena.obj");
            this.player = Model.ModelFactory.createNewModelFromFile("res/model/suzanne2.obj");
            this.shadow = Model.ModelFactory.createNewModelFromFile("res/model/shadow.obj");

            this.arenaTexture = new Texture("res/texture/arena.png");
            this.playerTexture = new Texture("res/texture/monkey_baked.png");
            this.shadowTexture = new Texture("res/texture/shadow.png");

            this.shader = new Shader("res/shader/red");

            this.camera = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.1F, 100F)
                    .lookAt(0.0F, 10.0F, 9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
            this.arenaModelMatrix = new Matrix4f();
            this.playerModelMatrix = new Matrix4f().translate(0.0F, 0.7F, 0.0F);
            this.initialized = true;
            System.out.println("cal");
        }
        this.onResize();
    }

    @Override
    public void onUnload() {
        this.window.getEventListener().unregisterHandler(this.eventHandler);
    }

    @Override
    public void onUpdate(long deltaTime) {

    }

    @Override
    public void onRender() {
        this.shader.bind();

        this.shader.setUniformMat4f("viewProjMatrix", this.camera);

        this.shader.setUniformMat4f("modelMatrix", this.arenaModelMatrix);
        this.arenaTexture.bind(0);
        this.arena.render();

        this.shader.setUniformMat4f("modelMatrix", this.playerModelMatrix);
        this.playerTexture.bind(0);
        this.player.render();

        this.shadowTexture.bind(0);
        this.shadow.render();
    }

    @Override
    public void onGuiRender(FontRenderer fontRenderer) {

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
