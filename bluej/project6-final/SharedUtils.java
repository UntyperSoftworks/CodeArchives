
/**
 * Utility functions used for multiple classes
 */

public class SharedUtils {
    public static int randomNumber(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        double x3 = Math.pow((double)(x2 - x1), 2);
        double y3 = Math.pow((double)(y2 - y1), 2);
        double a = x3 + y3;
        double b = Math.sqrt(a);
        return (int)b;
    }

    public static int convertToSteps(int seconds, int stepMs) {
        return (int)((seconds * 1000) / stepMs);
    }

    public static int msToFrames(int ms, int step) {
        return (int)(ms / step);
    }
}