package net.vasile2k.fooball.render;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class Model {

    private String objPath;
    private int vetexArrayId;
    private int vertexBufferId;
    private int indexBufferId;

    private float[] objCoords;
    private int[] objIndices;

    public static class ModelFactory{

        private ModelFactory(){

        }

        public static Model createNewModelFromFile(String filePath){
            // TODO: Maybe add more types of models and decide each one
            // but not now
            return new Model(filePath);
        }

    }

    private Model(String objPath){
        this.objPath = objPath;
        int vertexArrayId = glGenVertexArrays();
        glBindVertexArray(vertexArrayId);

        ArrayList<ObjLoader.Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> vertIndices = new ArrayList<>();

        try {
            ObjLoader.load(this.objPath, vertices, vertIndices);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load obj file: " + this.objPath);
        }

        this.objCoords = new float[vertices.size() * 8 /* floats per vertex */];
        this.objIndices = vertIndices.stream().mapToInt(i -> i).toArray();

        int _i = 0;
        for(ObjLoader.Vertex v: vertices){
            this.objCoords[_i++] = v.xPos;
            this.objCoords[_i++] = v.yPos;
            this.objCoords[_i++] = v.zPos;
            this.objCoords[_i++] = v.uTex;
            this.objCoords[_i++] = v.vTex;
            this.objCoords[_i++] = v.xNrm;
            this.objCoords[_i++] = v.yNrm;
            this.objCoords[_i++] = v.zNrm;
        }

        this. vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, this.objCoords, GL_STATIC_DRAW);

        FloatBuffer offsetBuffer = FloatBuffer.wrap(this.objCoords);
        offsetBuffer.position(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);
        offsetBuffer.position(3);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);
        offsetBuffer.position(5);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, offsetBuffer);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        this.indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.objIndices, GL_STATIC_DRAW);

    }

    public void render(){
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        glDrawElements(GL_TRIANGLES, this.objIndices.length, GL_UNSIGNED_INT, 0);
    }

}
