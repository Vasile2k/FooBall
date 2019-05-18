package net.vasile2k.fooball;

import net.vasile2k.fooball.game.Game;

/**
 * Created by Vasile2k on 16.05.2019.
 *
 */

public class Main {

    public static void main(String... args){
        Game game = Game.getInstance();
        game.start();
    }

}
