package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.render.Model;
import net.vasile2k.fooball.render.Renderer;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.joml.Matrix4f;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneMenu implements Scene {

    private Window window;

    private EventHandler eventHandler;

    private Model gameModel;
    private Shader textShader;
    private Texture gameTexture;

    private Matrix4f viewProjectionMatrix;
    private Matrix4f modelMatrix;

    public SceneMenu(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                System.out.println("cal");
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
//                Game.getInstance().requestSceneChange("SceneGame");
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
        this.window.getEventListener().registerHandler(this.eventHandler);

        if(this.gameModel == null){
            this.gameModel = Model.ModelFactory.createNewModelFromFile("res/model/fooball.obj");
        }
        if(this.textShader == null){
            this.textShader = new Shader("res/shader/textShader");
        }
        if(this.gameTexture == null){
            this.gameTexture = new Texture("res/texture/fooball.png");
        }

        this.textShader.bind();
        this.gameTexture.bind(0);

        this.textShader.setUniform1i("texture", 0);

        viewProjectionMatrix = new Matrix4f().setPerspective(1.0F, this.window.getAspectRatio(), 0.01F, 100.0F);
        modelMatrix = new Matrix4f();
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
        modelMatrix.identity().translate(0.0F, 0.7F, -3.0F).rotate(0.1F * (float) Math.sin(angle), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void onRender() {
        Renderer.getInstance().clearColor(0.1F, 0.1F, 0.1F, 1.0F);

        this.textShader.setUniformMat4f("viewProjMatrix", this.viewProjectionMatrix);
        this.textShader.setUniformMat4f("modelMatrix", this.modelMatrix);

        this.gameModel.render();
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
