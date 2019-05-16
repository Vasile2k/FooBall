package net.vasile2k.fooball.window;

import java.util.ArrayList;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

public class EventListener {

    private long window;

    private ArrayList<EventHandler> handlers;

    public EventListener(long window){
        this.window = window;
        this.handlers = new ArrayList<>();
    }

    public void onKey(long window, int key, int scancode, int action, int modifiers){
        for(EventHandler handler: this.handlers){
            handler.onKey(key, scancode, action, modifiers);
        }
    }

    public void onMouseButton(long window, int button, int action, int modifiers){
        for(EventHandler handler: this.handlers){
            handler.onMouseButton(button, action, modifiers);
        }
    }

    public void onCursorPosition(long window, double xPos, double yPos){
        for(EventHandler handler: this.handlers){
            handler.onCursorPosition(xPos, yPos);
        }
    }

    public void onScroll(long window, double xPos, double yPos){
        for(EventHandler handler: this.handlers){
            handler.onScroll(xPos, yPos);
        }
    }

    public void registerHandler(EventHandler handler){
        this.handlers.add(handler);
        GlobalGlfwEventHandler.getInstance().registerWindowListener(this.window, handler);
    }

    public void unregisterHandler(EventHandler handler){
        this.handlers.remove(handler);
        GlobalGlfwEventHandler.getInstance().unregisterWindowListener(this.window, handler);
    }

}
