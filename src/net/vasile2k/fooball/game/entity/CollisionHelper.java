package net.vasile2k.fooball.game.entity;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class CollisionHelper {

    private CollisionHelper(){

    }

    public static float clampFloat(float val, float min, float max){
        return min(max(val, min), max);
    }

    public static float min(float a, float b){
        return a < b ? a : b;
    }

    public static float max(float a, float b){
        return a > b ? a : b;
    }

    public static float abs(float num){
        return num < 0 ? -num : num;
    }

    /**
     * Calculate distance sqared
     * @param x1 x coord of point 1
     * @param y1 y coord of point 1
     * @param x2 x coord of point 2
     * @param y2 y coord of point 2
     * @return distance sqared between (x1, y1) and (x2, y2)
     */
    public static float dist2(float x1, float y1, float x2, float y2){
        return (x1 - x2)*(x1 - x2) + (y2 - y1)*(y2 - y1);
    }

}
