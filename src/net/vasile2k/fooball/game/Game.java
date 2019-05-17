package net.vasile2k.fooball.game;

import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

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

    public Game(){

    }

    public void run(){
        Window w = new Window();

        final float[] uniform = {0.0f, 0.0f};

        w.getEventListener().registerHandler(new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {

            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {

            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                uniform[0] = (float)xPos/320.0f - 1.0f;
                uniform[1] = -(float)yPos/240.0f + 1.0f;
            }

            @Override
            public void onScroll(double xPos, double yPos) {

            }
        });

        w.makeContextCurrent();

        GLUtil.setupDebugMessageCallback();

        System.out.println("OpenGl Version: " + glGetString(GL_VERSION));
        System.out.println("OpenGl Vendor: " + glGetString(GL_VENDOR));

        int vertexArrayId = glGenVertexArrays();
        glBindVertexArray(vertexArrayId);

        float[] coords = {
                -0.5f, -0.5f, 0.0F,
                0.0f,  0.5f, 0.0F,
                0.5f, -0.5f, 0.0F,
        };

        int vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, coords, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);

        Shader s = new Shader("res/shader/red");
        s.bind();

        Texture t = new Texture("res/texture/UV_grid.png");
        t.bind(0);

        s.setUniform1i("texture", 0);

        glEnable(GL_DEPTH_TEST);

        while (!w.shouldClose()){

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glClearColor(0.0F, 0.5F, 0.75F, 1.0F);

            s.setUniform2f("pos", uniform[0], uniform[1]);

            glDrawArrays(GL_TRIANGLES, 0, 4);

            w.swapBuffers();
            w.pollEvents();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
