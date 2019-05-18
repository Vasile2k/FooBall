package net.vasile2k.fooball.game;

import net.vasile2k.fooball.game.scene.Scene;
import net.vasile2k.fooball.game.scene.SceneMenu;
import net.vasile2k.fooball.render.*;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by Vasile2k on 16.05.2019.
 *
 */

public class Game {

    private static Game instance;

    private SceneMenu menuScene;
    private Scene currentScene;

    private Window w;

    private String nextScene = "";

    static {
        Game.instance = new Game();
    }

    private Game(){
        this.menuScene = new SceneMenu();
        this.currentScene = this.menuScene;
    }

    public void start(){
        w = new Window();

        Renderer.getInstance().setupOpenGL(w);

        long currentTime = System.currentTimeMillis();

        this.currentScene.onLoad(w);

        while (!w.shouldClose()){

            Renderer.getInstance().clear();

            long now = System.currentTimeMillis();
            this.currentScene.onUpdate(now - currentTime);

            Renderer.getInstance().clear();
            this.currentScene.onRender();

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
                this.currentScene.onLoad(this.w);
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

            w.swapBuffers();
            w.pollEvents();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.currentScene.onUnload();
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
