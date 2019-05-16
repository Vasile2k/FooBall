package net.vasile2k.fooball.window;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

import static org.lwjgl.glfw.GLFW.*;

public class GlobalGlfwEventHandler {

    private static GlobalGlfwEventHandler instance;

    private ArrayList<Pair<Long, EventHandler>> listeners;

    private GlobalGlfwEventHandler(){
        this.listeners = new ArrayList<>();
    }

    public static GlobalGlfwEventHandler getInstance(){
        if(instance == null){
            instance = new GlobalGlfwEventHandler();
        }
        return instance;
    }

    public void registerWindowListener(long window, EventHandler handler){
        this.listeners.add(new Pair<>(window, handler));
        // As lambda as IntelliJ can
        glfwSetKeyCallback(window, (window1, key, scancode, action, mods) ->
                GlobalGlfwEventHandler.instance.listeners.stream().filter(listener -> window == listener.getKey()).forEach(listener ->
                        listener.getValue().onKey(key, scancode, action, mods)));
        // Very lambda but not so lambda
        glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            GlobalGlfwEventHandler.instance.listeners.stream().filter(listener -> window == listener.getKey()).forEach(listener -> {
                listener.getValue().onMouseButton(button, action, mods);
            });
        });
        // Iterator
        glfwSetCursorPosCallback(window, (window1, xpos, ypos) -> {
            Iterator<Pair<Long, EventHandler>> it = GlobalGlfwEventHandler.instance.listeners.iterator();
            while(it.hasNext()){
                Pair<Long, EventHandler> listener = it.next();
                if(window == listener.getKey()){
                    listener.getValue().onCursorPosition(xpos, ypos);
                }
            }
        });
        // For each
        glfwSetScrollCallback(window, (window1, xoffset, yoffset) -> {
            for(Pair<Long, EventHandler> listener: GlobalGlfwEventHandler.instance.listeners){
                if(window == listener.getKey()){
                    listener.getValue().onScroll(xoffset, yoffset);
                }
            }
        });
    }

    public void unregisterWindowListener(long window, EventHandler handler){
        this.listeners.remove(new Pair<Long, EventHandler>(window, handler));
    }

}
