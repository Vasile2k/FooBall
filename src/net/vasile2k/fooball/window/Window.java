package net.vasile2k.fooball.window;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by Vasile2k on 16.05.2019.
 *
 */

public class Window {

    private long window;
    private EventListener eventListener;

    static {
        // Init GLFW once
        if(!glfwInit()){
            throw new RuntimeException("Failed to initialize GLFW");
        }
        // Setup an error callback to stderr
        GLFWErrorCallback.createPrint(System.err).set();

        // And make sure to clear at exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            glfwTerminate();
            GLFWErrorCallback callback = glfwSetErrorCallback(null);
            if(callback != null){
                callback.free();
            }
        }));
    }

    public Window(){
        this(640, 480, "FooBall", false);
    }

    public Window(int width, int height, String title, boolean fullscreen){

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

        if(window == NULL){
            throw new RuntimeException("Failed to create window!");
        }

        this.eventListener = new EventListener(this.window);
    }

    public void setResolution(int width, int height){
        glfwSetWindowSize(this.window, width, height);
    }

    public void setFullscreen(boolean fullscreen){
        // If we have to change state
        if(this.getFullscreen() != fullscreen){
            if(fullscreen){
                long monitor = glfwGetPrimaryMonitor();
                GLFWVidMode vidMode = glfwGetVideoMode(monitor);
                glfwSetWindowMonitor(this.window, monitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
            }else{
                int width[] = new int[1];
                int height[] = new int[1];
                glfwGetWindowSize(this.window, width, height);
                glfwSetWindowMonitor(this.window, NULL, 100, 100, width[0], height[0], GLFW_DONT_CARE);
            }
        }
    }

    public int getWidth(){
        int width[] = new int[1];
        glfwGetWindowSize(this.window, width, null);
        return width[0];
    }

    public int getHeight(){
        int height[] = new int[1];
        glfwGetWindowSize(this.window, null, height);
        return height[0];
    }

    public boolean getFullscreen(){
        return glfwGetWindowMonitor(this.window) != NULL;
    }

    public void pollEvents(){
        glfwPollEvents();
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(this.window);
    }

    public void swapBuffers(){
        glfwSwapBuffers(this.window);
    }

    public void makeContextCurrent(){
        glfwMakeContextCurrent(this.window);
        GL.createCapabilities();
    }

    public float getAspectRatio(){
        int width[] = new int[1];
        int height[] = new int[1];
        glfwGetWindowSize(this.window, width, height);
        return (float)width[0]/(float)height[0];
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void destroy(){
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);
    }
}
