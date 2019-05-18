package net.vasile2k.fooball.game.scene;

import net.vasile2k.fooball.game.Game;
import net.vasile2k.fooball.render.Renderer;
import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;

/**
 * Created by Vasile2k on 18.05.2019.
 *
 */

public class SceneGame implements Scene {

    private Window w;

    private EventHandler eventHandler;

    public SceneGame(){
        this.eventHandler = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                Game.getInstance().requestSceneChange("SceneMenu");
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {

            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {

            }

            @Override
            public void onScroll(double xPos, double yPos) {

            }
        };
    }

    @Override
    public void onLoad(Window window) {
        this.w = window;
        window.getEventListener().registerHandler(this.eventHandler);
    }

    @Override
    public void onUnload() {
        this.w.getEventListener().unregisterHandler(this.eventHandler);
    }

    @Override
    public void onUpdate(long deltaTime) {

    }

    @Override
    public void onRender() {
        Renderer.getInstance().clearColor(1.0F, 0.0F, 0.5F, 1.0F);
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
