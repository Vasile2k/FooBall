package net.vasile2k.fooball.render;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

public class ObjLoader {

    private static final String VERTEX = "v ";
    private static final String TEXTURE = "vt ";
    private static final String NORMAL = "vn ";
    private static final String FACE = "f ";

    private static final int VERTICES_PER_FACE = 3;

    private ObjLoader(){}

    public static class Vertex {
        public float xPos;
        public float yPos;
        public float zPos;
        public float uTex;
        public float vTex;
        public float xNrm;
        public float yNrm;
        public float zNrm;

        public Vertex(float xPos, float yPos, float zPos, float uTex, float vTex, float xNrm, float yNrm, float zNrm) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.zPos = zPos;
            this.uTex = uTex;
            this.vTex = vTex;
            this.xNrm = xNrm;
            this.yNrm = yNrm;
            this.zNrm = zNrm;
        }

        @Override
        public boolean equals(Object other){
            if(other != null && other instanceof Vertex){
                Vertex vertexOther = (Vertex)other;
                return vertexOther.xPos == this.xPos &&
                        vertexOther.yPos == this.yPos &&
                        vertexOther.zPos == this.zPos &&
                        vertexOther.uTex == this.uTex &&
                        vertexOther.vTex == this.vTex &&
                        vertexOther.xNrm == this.xNrm &&
                        vertexOther.yNrm == this.yNrm &&
                        vertexOther.zNrm == this.zNrm;
            }
            return false;
        }
    }

    public static void load(String path, ArrayList<Vertex> vertices, ArrayList<Integer> indices) throws IOException {

        ArrayList<Vector3f> vertexPositions = new ArrayList<>();
        ArrayList<Vector2f> textures = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line;

        while ((line = reader.readLine()) != null){
            if(line.startsWith(VERTEX)){
                String[] data = line.split("\\s+");
                vertexPositions.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            }else if(line.startsWith(FACE)){
                String[] data = line.split("\\s+");

                int[] vertIndex = new int[VERTICES_PER_FACE];
                int[] texCoordIndex = new int[VERTICES_PER_FACE];
                int[] normalIndex = new int[VERTICES_PER_FACE];

                for(int i = 0; i < VERTICES_PER_FACE; ++i){
                    String[] info = data[i+1].split("/");
                    vertIndex[i] = Integer.parseInt(info[0]);
                    texCoordIndex[i] = Integer.parseInt(info[1]);
                    normalIndex[i] = Integer.parseInt(info[2]);

                    Vector3f _v = vertexPositions.get(vertIndex[i] - 1); // -1 because OBJ starts counting from 1...
                    Vector2f _t = textures.get(texCoordIndex[i] - 1); // -1 because OBJ starts counting from 1...
                    Vector3f _n = normals.get(normalIndex[i] - 1); // -1 because OBJ starts counting from 1...
                    Vertex v = new Vertex(_v.x, _v.y, _v.z, _t.x, _t.y, _n.x, _n.y, _n.z);

                    int index = vertices.indexOf(v);
                    if(index == -1){
                        vertices.add(v);
                        indices.add(vertices.size() - 1);
                    }else{
                        indices.add(index);
                    }
                }

            }else if(line.startsWith(TEXTURE)){
                String[] data = line.split("\\s+");
                textures.add(new Vector2f(Float.parseFloat(data[1]), Float.parseFloat(data[2])));
            }else if(line.startsWith(NORMAL)){
                String[] data = line.split("\\s+");
                normals.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            }
        }

        reader.close();
    }

}
