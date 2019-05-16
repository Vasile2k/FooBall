package net.vasile2k.fooball.game;

import net.vasile2k.fooball.window.EventHandler;
import net.vasile2k.fooball.window.Window;

/**
 * Created by Vasile2k on 16.05.2019.
 *
 */

public class Game {

    public Game(){

    }

    public void run(){
        Window w = new Window();

        w.getEventListener().registerHandler(new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                System.out.println("On key " + key);
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                System.out.println("On mouse " + button);
            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                System.out.println("On cursor " + xPos + " " + yPos);
            }

            @Override
            public void onScroll(double xPos, double yPos) {
                System.out.println("On scroll " + xPos);
            }
        });

        final int[] i = {0};

        EventHandler h = new EventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                System.out.println("wOn key " + key);
            }

            @Override
            public void onMouseButton(int button, int action, int modifiers) {
                System.out.println("wOn mouse " + button);
                // TEST STUFF
                i[0] = 1;
            }

            @Override
            public void onCursorPosition(double xPos, double yPos) {
                System.out.println("wOn cursor " + xPos + " " + yPos);
            }

            @Override
            public void onScroll(double xPos, double yPos) {
                System.out.println("wOn scroll " + xPos);
                // TEST STUFF
                w.setFullscreen(!w.getFullscreen());
            }
        };

        w.getEventListener().registerHandler(h);

        while (!w.shouldClose()){
            w.pollEvents();

            // TEST STUFF
            if(i[0] == 1){
                w.getEventListener().unregisterHandler(h);
                i[0] = 0;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
