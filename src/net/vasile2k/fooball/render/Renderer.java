package net.vasile2k.fooball.render;

import net.vasile2k.fooball.window.Window;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class Renderer {
    private static Renderer instance;

    static {
        Renderer.instance = new Renderer();
    }

    private Renderer() {

    }

    public static Renderer getInstance() {
        return instance;
    }

    public void setupOpenGL(Window window){

        window.makeContextCurrent();

        System.out.println("OpenGl Version: " + glGetString(GL_VERSION));
        System.out.println("OpenGl Vendor: " + glGetString(GL_VENDOR));

        GLUtil.setupDebugMessageCallback();

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void finalizeOpenGl(){

    }

    public void clear(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void clearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }
}
