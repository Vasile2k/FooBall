package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.database.DatabaseManager;
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
import static org.lwjgl.opengl.GL44.*;

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

    private boolean toggleFullscreenActive;

    public SceneMenu(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {

            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                // If click
                if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                    if(newGameActive){
                        Game.getInstance().requestSceneChange("SceneGame");
                    }else if(continueActive){
                        SceneGame sceneGame = DatabaseManager.loadState();
                        if(sceneGame != null){
                            sceneGame.build();
                            Game.getInstance().requestSceneChange(sceneGame);
                        }
                    }else if(exitActive){
                        System.exit(0);
                    }else if(toggleFullscreenActive){
                        Game.getInstance().toggleFullscreen();
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
                toggleFullscreenActive = false;

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

                if(xNorm > 0.6F && xNorm < 0.86F && yNorm > 0.86F && yNorm < 0.9F){
                    toggleFullscreenActive = true;
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
        this.onResize();
    }

    @Override
    public void onUnload() {
        this.window.getEventListener().unregisterHandler(this.eventHandler);

        this.textShader.unbind();
        this.gameTexture.unbind();
    }

    private double angle = 0.0F;

    @Override
    public void onUpdate(long deltaTime) {
        angle += (double)deltaTime/ 500;
        gameModelMatrix.identity().translate(0.0F, 0.7F, -3.0F).rotate(0.2F * (float) Math.sin(angle), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void onRender() {
        Renderer.getInstance().clearColor(0.1F, 0.1F, 0.1F, 1.0F);

        this.textShader.bind();

        this.textShader.setUniformMat4f("viewProjMatrix", this.gameViewProjectionMatrix);
        this.textShader.setUniformMat4f("modelMatrix", this.gameModelMatrix);

        this.textShader.setUniform1f("isActive", 0.0F);

        this.gameTexture.bind(0);
        this.gameModel.render();

        Matrix4f scaledMatrix;

        this.menuButtonsTexture.bind(0);

        this.textShader.setUniform1f("isActive", newGameActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(newModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", newGameActive ? scaledMatrix : this.newModelMatrix);
        this.newGameModel.render();

        this.textShader.setUniform1f("isActive", continueActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(continueModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", continueActive ? scaledMatrix : this.continueModelMatrix);
        this.continueModel.render();

        this.textShader.setUniform1f("isActive", exitActive ? 1.0F : 0.0F);
        scaledMatrix = new Matrix4f(exitModelMatrix).scale(1.1F, 1.1F, 1.1F);
        this.textShader.setUniformMat4f("modelMatrix", exitActive ? scaledMatrix : this.exitModelMatrix);
        this.exitModel.render();

    }

    @Override
    public void onGuiRender(FontRenderer fontRenderer) {
        fontRenderer.renderText("Made by Vasile2k.", 0.1f, 0.05F, 0.0F, -0.95F, 0.25F, 0.63F, 0.95F);

        fontRenderer.renderText("Toggle fullscreen", 0.07F, 0.03F, 0.2F, -0.8F, toggleFullscreenActive ? 0.8F : 0.2F, toggleFullscreenActive ? 0.25F : 0.8F, 0.3F);

        int[] highScores = DatabaseManager.getHighscores();

        fontRenderer.renderText("Highscores:", 0.08F, 0.04F, 0.2F, 0.0F, 1.0F, 1.0F, 1.0F);
        if(highScores.length == 0){
            fontRenderer.renderText("-", 0.08F, 0.04F, 0.2F, -0.1F, 1.0F, 1.0F, 1.0F);
        }else{
            float yCoordRow = -0.1F;
            for (int highScore : highScores) {
                fontRenderer.renderText("" + highScore /* ez toString() */, 0.08F, 0.04F, 0.3F, yCoordRow, 1.0F, 1.0F, 1.0F);
                yCoordRow -= 0.1F;
            }
        }

    }

    @Override
    public void onResize() {
        // Update projection matrix to new aspect ratio
        gameViewProjectionMatrix = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.01F, 100.0F);
    }

    @Override
    public boolean isDone() {
        return false;
    }

}
