package net.vasile2k.fooball.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL44.*;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

public class Shader {

    private ArrayList<String> files;
    private HashMap<String, Integer> uniformLocationCache;

    private int shaderId;

    /**
     * Builds a shader from all files matching a name
     * Searches all files with that name and with any extensions
     * These will get compiled into the shader
     * Valid extensions are:
     * <ul>
     *     <li>
     *         vert - Vertex shader - GL_VERTEX_SHADER
     *     </li>
     *     <li>
     *         frag - Fragment shader - GL_FRAGMENT_SHADER
     *     </li>
     *     <li>
     *         geom - Geometry shader - GL_GEOMETRY_SHADER
     *     </li>
     *     <li>
     *         comp - Compute shader - GL_COMPUTE_SHADER
     *     </li>
     * </ul>
     * Any file with other extension or without an extension is ignored
     * @param fileNames the path to the file base name(without extension)
     */
    public Shader(String fileNames){

        this.files = new ArrayList<>();
        this.uniformLocationCache = new HashMap<>();

        ArrayList<Integer> compiledShaders = new ArrayList<>();

        // Get the root directory of the file
        File baseFile =  new File(fileNames);
        String rootDirectory = baseFile.getParent();
        if(rootDirectory == null){
            rootDirectory = "./";
        }

        // Loop through all files in directory
        File f = new File(rootDirectory);
        File[] files = f.listFiles();
        if(files != null){
            for(File file: files) {
                // Get file extension
                String fileName = file.getName();
                // If the file matches the nave we gave
                if (fileName.startsWith(baseFile.getName())) {
                    int indexOfDot = fileName.lastIndexOf('.');
                    String extension = "";
                    if (indexOfDot > 0) {
                        extension = fileName.substring(indexOfDot + 1);
                    }

                    String fileContent;

                    String absoluteFilePath;

                    int shaderType = -1;

                    switch (extension) {
                        case "vert":
                            shaderType = GL_VERTEX_SHADER;
                            break;
                        case "frag":
                            shaderType = GL_FRAGMENT_SHADER;
                            break;
                        case "geom":
                            shaderType = GL_GEOMETRY_SHADER;
                            break;
                        case "comp":
                            shaderType = GL_COMPUTE_SHADER;
                            break;
                    }

                    if (shaderType != -1) {
                        absoluteFilePath = Paths.get(rootDirectory, fileName).toString();
                        fileContent = loadFile(absoluteFilePath);
                        int currentShader = compileShader(fileContent, shaderType);
                        compiledShaders.add(currentShader);
                        this.files.add(absoluteFilePath);
                    }
                }
            }
        }

        this.shaderId = createProgram(compiledShaders.stream().mapToInt(i -> i).toArray());

        if(this.shaderId == -1){
            throw new RuntimeException("Failed to compile shader: " + fileNames);
        }
    }

    @Override
    public void finalize() throws Throwable{
        super.finalize();
        glDeleteProgram(this.shaderId);
    }

    public void bind(){
        glUseProgram(this.shaderId);
    }

    public void unbind(){
        glUseProgram(0);
    }

    /**
     * Compiles a shader
     * @param shaderSource Source code of the shader
     * @param type Shader type. Can be: GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, GL_GEOMETRY_SHADER or GL_COMPUTE_SHADER
     * @return The shader
     */
    private static int compileShader(String shaderSource, int type){
        int shaderId = glCreateShader(type);

        glShaderSource(shaderId, shaderSource);

        glCompileShader(shaderId);

        int[] compilationResult = {0};
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compilationResult);
        if(compilationResult[0] != GL_TRUE){
            System.err.println("Failed to compile " + ((type == GL_VERTEX_SHADER) ? "vertex" : (type == GL_FRAGMENT_SHADER) ? "fragment" :
                    (type == GL_GEOMETRY_SHADER) ? "geometry" : (type == GL_COMPUTE_SHADER) ? "compute" : "unknown") + " shader.");
            String log = glGetShaderInfoLog(shaderId);
            System.err.println("Log: ");
            System.err.println(log);
            glDeleteProgram(shaderId);
        }

        return shaderId;
    }

    /**
     * Loads the file content into a String
     * @param path The path to the file
     * @return File content as a String
     */
    private static String loadFile(String path){
        try {

            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
                sb.append('\n');
            }

            reader.close();
            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a shader program from more shaders
     * @param shaderIds The ids of the shaders or -1 if null
     * @return The shader program
     */
    private static int createProgram(int[] shaderIds){
        int shaderProgramId = glCreateProgram();

        for(int shader: shaderIds){
            if(shader != -1){
                glAttachShader(shaderProgramId, shader);
            }
        }
        glLinkProgram(shaderProgramId);

        int[] linkResult = {0};
        glGetProgramiv(shaderProgramId, GL_LINK_STATUS, linkResult);
        if(linkResult[0] == GL_FALSE){
            System.err.println("Failed to link shader program.");
            String log = glGetProgramInfoLog(shaderProgramId);
            System.err.println("Log: ");
            System.err.println(log);
        }

        glValidateProgram(shaderProgramId);

        int[] validationResult = {0};
        glGetProgramiv(shaderProgramId, GL_VALIDATE_STATUS, validationResult);
        if(validationResult[0] == GL_FALSE){
            System.err.println("Failed to validate shader program.");
            String log = glGetProgramInfoLog(shaderProgramId);
            System.err.println("Log: ");
            System.err.println(log);
        }

        return shaderProgramId;
    }

    private int getUniformLocation(String name){
        Integer cacheLocation = this.uniformLocationCache.get(name);
        if(cacheLocation != null){
            return cacheLocation;
        }
        int location = glGetUniformLocation(this.shaderId, name);
        if(location == -1){
            System.out.println("Warning: Trying to access uniform " + name + " which does not exist");
        }
        this.uniformLocationCache.put(name, location);
        return location;
    }

    public void setUniform1f(String name, float f0){
        glUniform1f(this.getUniformLocation(name), f0);
    }

    public void setUniform2f(String name, float f0, float f1) {
        glUniform2f(this.getUniformLocation(name), f0, f1);
    }

    public void setUniform3f(String name, float f0, float f1, float f2) {
        glUniform3f(this.getUniformLocation(name), f0, f1, f2);
    }

    public void setUniform4f(String name, float f0, float f1, float f2, float f3) {
        glUniform4f(this.getUniformLocation(name), f0, f1, f2, f3);
    }

    public void setUniformMat4f(String name, Matrix4f mat) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mat.get(fb);
        glUniformMatrix4fv(this.getUniformLocation(name), false, fb);
    }

    public void setUniform1i(String name, int val) {
        glUniform1i(this.getUniformLocation(name), val);
    }

}
