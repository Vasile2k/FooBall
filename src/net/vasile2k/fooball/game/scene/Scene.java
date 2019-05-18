package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.render.font.FontRenderer;
import net.vasile2k.fooball.window.Window;

/**
 * Created by Vasile2k on 18.05.2019.<br/>
 * Register a listener to the window provided on onLoad() for these:
 * <ul>
 *     <li>
 *         void onKey(int key, int scancode, int action, int modifiers);
 *     </li>
 *     <li>
 *         void onMouseButton(int button, int action, int modifiers);
 *     </li>
 *     <li>
 *         void onCursorPosition(double xPos, double yPos);
 *     </li>
 *     <li>
 *         void onScroll(double xPos, double yPos);
 *     </li>
 * </ul>
 * Don't forget to unregister
 */

public interface Scene {

    void onLoad(Window window);
    void onUnload();
    void onUpdate(long deltaTime); // In millis
    void onRender();
    void onGuiRender(FontRenderer fontRenderer);
    void onResize();
    boolean isDone();

}
