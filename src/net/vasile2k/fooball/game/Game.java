package net.vasile2k.fooball.game;

import net.vasile2k.fooball.database.DatabaseManager;
import net.vasile2k.fooball.game.scene.Scene;
import net.vasile2k.fooball.game.scene.SceneMenu;
import net.vasile2k.fooball.render.*;
import net.vasile2k.fooball.render.font.FontRenderer;
import net.vasile2k.fooball.window.Window;

/**
 * Created by Vasile2k on 16.05.2019.
 *
 */

public class Game {

    private static Game instance;

    private SceneMenu menuScene;
    private Scene currentScene;

    private Window window;

    private String nextScene = "";

    static {
        Game.instance = new Game();
    }

    private Game(){
        this.menuScene = new SceneMenu();
        this.currentScene = this.menuScene;
    }

    public void start(){
        window = new Window();

        Renderer.getInstance().setupOpenGL(window);

        long currentTime = System.currentTimeMillis();

        this.window.setFullscreen(DatabaseManager.getSettingsFullscreen());

        this.currentScene.onLoad(window);

        FontRenderer fontRenderer = FontRenderer.FontRendererBuilder.buildFontRenderer();

        while (!window.shouldClose()){

            Renderer.getInstance().clear();

            long now = System.currentTimeMillis();
            this.currentScene.onUpdate(now - currentTime);
            currentTime = now;

            Renderer.getInstance().clear();
            this.currentScene.onRender();

            this.currentScene.onGuiRender(fontRenderer);

            if(!this.nextScene.equals("")){
                this.currentScene.onUnload();

                if(this.nextScene.equals("SceneMenu")){
                    this.currentScene = this.menuScene;
                }else{
                    try {
                        Class sceneClass = Class.forName("net.vasile2k.fooball.game.scene." + this.nextScene);
                        this.currentScene = (Scene) sceneClass.newInstance();
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
                this.currentScene.onLoad(this.window);
                this.nextScene = "";
            }

            if(this.currentScene != this.menuScene){
                if(this.currentScene.isDone()){
                    this.currentScene.onUnload();
                    this.currentScene = this.menuScene;
                }else{
                    if(this.currentScene.isDone()){
                        break;
                    }
                }
            }

            window.swapBuffers();
            window.pollEvents();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.currentScene.onUnload();
    }

    public void toggleFullscreen(){
        boolean fullscreen = this.window.getFullscreen();
        this.window.setFullscreen(!fullscreen);
        if(fullscreen){
            this.window.setResolution(640, 480);
        }
        this.currentScene.onResize();
        DatabaseManager.setSettingsFullscreen(!fullscreen);
    }

    public void requestSceneChange(String className){
        this.nextScene = className;
    }

    public static Game getInstance(){
        return instance;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        Renderer.getInstance().finalizeOpenGl();
    }

}
