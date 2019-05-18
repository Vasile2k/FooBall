package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Renderer;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneMenu implements Scene {

    private Window window;

    private EventHandler eventHandler;

    private Model gameModel;
    private Model newGameModel; //button_new.obj
    private Model continueModel; // button_continue.obj
    private Model exitModel; // button_exit.obj
    private Shader textShader;
    private Texture gameTexture;
    private Texture menuButtonsTexture;// menu_buttons.png

    private boolean initialized = false;

    private Matrix4f gameViewProjectionMatrix;
    private Matrix4f gameModelMatrix;
    private Matrix4f newModelMatrix;
    private Matrix4f continueModelMatrix;
    private Matrix4f exitModelMatrix;

    private boolean newGameActive;
    private boolean continueActive;
    private boolean exitActive;

    public SceneMenu(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {

            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                // If click
                if(button == GLFW_MOUSE_BUTTON_1){
                    if(newGameActive){
                        Game.getInstance().requestSceneChange("SceneGame");
                    }else if(continueActive){

                    }else if(exitActive){
                        System.exit(0);
                    }
                }
            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                int[] windowSize = window.getWidthAndHeight();
                double xNorm = xPos/windowSize[0];
                double yNorm = yPos/windowSize[1];

                newGameActive = false;
                continueActive = false;
                exitActive = false;

                if(yNorm > 0.5){
                    if(yNorm < 0.61){
                        if(xNorm < 0.5){
                            newGameActive = true;
                        }
                    }else if(yNorm < 0.73){
                        if(xNorm < 0.45){
                            continueActive = true;
                        }
                    }else if(yNorm < 0.87){
                        if(xNorm < 0.3){
                            exitActive = true;
                        }
                    }
                }
            }

            @Override
            public void onScroll(double xPos, double yPos) {

            }
        };
        newModelMatrix = new Matrix4f().translate(-2.0F, -0.5F, -7.0F);
        continueModelMatrix = new Matrix4f().translate(-2.0F, -1.5F, -7.0F);
        exitModelMatrix = new Matrix4f().translate(-2.0F, -2.5F, -7.0F);
    }

    @Override
    public void onLoad(Window window) {
        this.window = window;
        this.window.getEventListener().registerHandler(this.eventHandler);

        if(!initialized){
            this.gameModel = Model.ModelFactory.createNewModelFromFile("res/model/fooball.obj");
            this.continueModel = Model.ModelFactory.createNewModelFromFile("res/model/button_continue.obj");
            this.newGameModel = Model.ModelFactory.createNewModelFromFile("res/model/button_new.obj");
            this.exitModel = Model.ModelFactory.createNewModelFromFile("res/model/button_exit.obj");
            this.textShader = new Shader("res/shader/textShader");
            this.gameTexture = new Texture("res/texture/fooball.png");
            this.menuButtonsTexture = new Texture("res/texture/menu_buttons.png");
            initialized = true;
        }

        this.textShader.bind();
        this.gameTexture.bind(0);

        this.textShader.setUniform1i("texture", 0);

        gameViewProjectionMatrix = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.01F, 100.0F);
        gameModelMatrix = new Matrix4f();
    }

    @Override
    public void onUnload() {
        this.window.getEventListener().unregisterHandler(this.eventHandler);

        this.textShader.unbind();
        this.gameTexture.unbind();
    }

    @Override
    public void onUpdate(long deltaTime) {
        double angle = (double)deltaTime/ 300;
        gameModelMatrix.identity().translate(0.0F, 0.7F, -3.0F).rotate(0.1F * (float) Math.sin(angle), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void onRender() {
        Renderer.getInstance().clearColor(0.1F, 0.1F, 0.1F, 1.0F);

        this.textShader.setUniformMat4f("viewProjMatrix", this.gameViewProjectionMatrix);
        this.textShader.setUniformMat4f("modelMatrix", this.gameModelMatrix);

        this.textShader.setUniform1f("active", 0.0F);

        this.gameTexture.bind(0);
        this.gameModel.render();

        Matrix4f scaledMatrix;

        this.menuButtonsTexture.bind(0);
        this.textShader.setUniform1f("active", newGameActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(newModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", newGameActive ? scaledMatrix : this.newModelMatrix);
        this.newGameModel.render();
        this.textShader.setUniform1f("active", continueActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(continueModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", continueActive ? scaledMatrix : this.continueModelMatrix);
        this.continueModel.render();
        this.textShader.setUniform1f("active", exitActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(exitModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", exitActive ? scaledMatrix : this.exitModelMatrix);
        this.exitModel.render();
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
