package net.vasile2k.fooball.game;

import net.vasile2k.fooball.render.ObjLoader;
import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;
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

    public Game(){

    }

    public void run(){
        Window w = new Window();

        final float[] uniform = {0.0f, 0.0f, 0.0F, -3.0F};

        w.getEventListener().registerHandler(new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                switch (key){
                    case GLFW_KEY_W:
                        uniform[2] += 0.1F;
                        break;
                    case GLFW_KEY_S:
                        uniform[2] -= 0.1F;
                        break;
                    case GLFW_KEY_A:
                        uniform[3] += 0.1F;
                        break;
                    case GLFW_KEY_D:
                        uniform[3] -= 0.1F;
                        break;
                }
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
                -0.5f, -0.5f, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F,
                -0.5f,  0.5f, 0.0F, -1.0F,  1.0F, 0.0F, 0.0F, 0.0F,
                 0.5f,  0.5f, 0.0F,  1.0F,  1.0F, 0.0F, 0.0F, 0.0F,
                 0.5f, -0.5f, 0.0F,  1.0F, -1.0F, 0.0F, 0.0F, 0.0F,
        };

        int[] indices = {
                0, 1, 2,
                0, 2, 3,
        };

        ArrayList<ObjLoader.Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> vertIndices = new ArrayList<>();

        try {
            ObjLoader.load("res/model/suzanne.obj", vertices, vertIndices);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] objIndices = vertIndices.stream().mapToInt(i -> i).toArray();
        float[] objCoords = new float[vertices.size() * 8 /* floats per vertex */];

        int _i = 0;
        for(ObjLoader.Vertex v: vertices){
            objCoords[_i++] = v.xPos;
            objCoords[_i++] = v.yPos;
            objCoords[_i++] = v.zPos;
            objCoords[_i++] = v.uTex;
            objCoords[_i++] = v.vTex;
            objCoords[_i++] = v.xNrm;
            objCoords[_i++] = v.yNrm;
            objCoords[_i++] = v.zNrm;
        }

        int vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, objCoords, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        FloatBuffer offsetBuffer = FloatBuffer.wrap(objCoords);
        offsetBuffer.position(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);
        offsetBuffer.position(3);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);
        offsetBuffer.position(5);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);

        int indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, objIndices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);

        Shader s = new Shader("res/shader/red");
        s.bind();

        Texture t = new Texture("res/texture/UV_grid2.png");
        Texture monkeyTexture = new Texture("res/texture/monkey_baked.png");
//        t.bind(0);
        monkeyTexture.bind(0);

        s.setUniform1i("texture", 0);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Matrix4f proj = new Matrix4f().setPerspective(1.0F, 1.0F, 0.01F, 100.0F);
        Matrix4f view = new Matrix4f().setLookAt(0.0F, 0.0F, 0.0F, uniform[0], uniform[1], 1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f viewProjection = new Matrix4f();
        proj.mulPerspectiveAffine(view, viewProjection); // <- multiplication into 'viewProjection'

        Matrix4f modelMatrix = new Matrix4f();

        while (!w.shouldClose()){

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glClearColor(0.0F, 0.5F, 0.75F, 1.0F);

            view = new Matrix4f();
            view.rotate(uniform[0], 0.0F, 1.0F, 0.0F);
            view.rotate(uniform[1], 1.0F, 0.0F, 0.0F);
            proj.mulPerspectiveAffine(view, viewProjection);

            modelMatrix.identity().translate(0.0F, uniform[2], uniform[3]);

//            s.setUniform2f("pos", uniform[0], uniform[1]);
            s.setUniformMat4f("viewProj", viewProjection);
            s.setUniformMat4f("model", modelMatrix);

            glDrawElements(GL_TRIANGLES, objIndices.length, GL_UNSIGNED_INT, 0);

            w.swapBuffers();
            w.pollEvents();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        t.unbind();
        s.unbind();

        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
    }

}
