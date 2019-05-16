package net.vasile2k.fooball.window;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

public interface EventHandler {

    void onKey(int key, int scancode, int action, int modifiers);
    void onMouseButton(int button, int action, int modifiers);
    void onCursorPosition(double xPos, double yPos);
    void onScroll(double xPos, double yPos);

}
