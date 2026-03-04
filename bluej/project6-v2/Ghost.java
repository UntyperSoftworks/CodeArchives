/**
 * @version v0.1-beta
 */

import java.lang.reflect.Array;

public class Ghost {
    private int x, y;
    private boolean eaten;
    private int xdirection, ydirection;
    private int currentSteps, currentSpeed, currentDirection;

    public boolean isEatable;

    private final int ghostSizeConst = 40;

    // "enums"
    public static int leftDir = 1, rightDir = 2, upDir = 3, downDir = 4;
    
    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
    private int randomNumberArray(int[] t) {
        int len = t.length;
        int index = math_random(0, len - 1);
        return t[index];
    }

    public Ghost(int posx, int posy) {
        x = posx; y = posy;
        xdirection = 0; ydirection = 0;

        eaten = false;
        isEatable = false;

        currentSteps = 0;
        currentSpeed = 1;
        currentDirection = 1;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // movement methods

    // TODO: make this method public at some point
    private void changeDirection(int dirx, int diry) {
        // disallows diagonal directions (x overrides first)
        if (dirx != 0) diry = 0;
        else if (diry != 0) dirx = 0;

        xdirection = dirx;
        ydirection = diry;
    }
    public void changeDirectionByInt(int intDirection) {
        if (intDirection == leftDir) changeDirection(-1, 0);
        else if (intDirection == rightDir) changeDirection(1, 0);
        else if (intDirection == upDir) changeDirection(0, -1);
        else if (intDirection == downDir) changeDirection(0, 1);
        currentDirection = intDirection;
    }
    public void setSpeed(int speed) { currentSpeed = speed; }

    public void move() {
        if (isEaten() == false) {
            x += xdirection * currentSpeed;
            y += ydirection * currentSpeed;
            currentSteps += currentSpeed;
        }
    }

    public int getDirection() { return currentDirection; }
    public int getCurrentSteps() { return currentSteps; }
    public void resetSteps() { currentSteps = 0; }

    // preset directions

    public void toRandomDirection() {
        int _rand = math_random(1, 4);
        changeDirectionByInt(_rand);
    }
    public void toRandomDirectionV2() {
        int[] _newdir = new int[3];
        int _id = 0;
        for (int i = 0; i < 4; i++) {
            int _i = i + 1;
            if (_i != currentDirection) {
                Array.setInt(_newdir, _id, _i);
                _id++;
            }
        }
        changeDirectionByInt(randomNumberArray(_newdir));
    }
    public void toFleeingDirection(int px, int py) {
        int _rand = math_random(1, 2);
        if (_rand == 1) {
            if (getX() < px) changeDirectionByInt(leftDir);
            else changeDirectionByInt(rightDir);
        } else {
            if (getY() < py) changeDirectionByInt(upDir);
            else changeDirectionByInt(downDir);
        }
    }
    public void toChasingDirection(int px, int py) {
        int _rand = math_random(1, 2);
        if (_rand == 1) {
            if (getX() < px) changeDirectionByInt(rightDir);
            else changeDirectionByInt(leftDir);
        } else {
            if (getY() < py) changeDirectionByInt(downDir);
            else changeDirectionByInt(upDir);
        }
    }
    
    public boolean toAntiBoundsDirection(int boundx, int boundy) {
        boolean oobleft = (getX() < 0);
        boolean oobright = (getX() + ghostSizeConst > boundx);
        boolean oobup = (getY() < 0);
        boolean oobdown = (getY() + ghostSizeConst > boundy);

        if (oobleft) {
            int[] _newdir = {Ghost.rightDir,Ghost.upDir,Ghost.downDir};
            changeDirectionByInt(randomNumberArray(_newdir));
            resetSteps();
        } else if (oobright) {
            int[] _newdir = {Ghost.leftDir,Ghost.upDir,Ghost.downDir};
            changeDirectionByInt(randomNumberArray(_newdir));
            resetSteps();
        } else if (oobup) {
            int[] _newdir = {Ghost.leftDir,Ghost.rightDir,Ghost.downDir};
            changeDirectionByInt(randomNumberArray(_newdir));
            resetSteps();
        } else if (oobdown) {
            int[] _newdir = {Ghost.leftDir,Ghost.rightDir,Ghost.upDir};
            changeDirectionByInt(randomNumberArray(_newdir));
            resetSteps();
        }
        
        boolean outOfBounds = (oobleft || oobright || oobup || oobdown);
        return outOfBounds;
    }

    // eatable methods

    public boolean isEaten() { return eaten; }
    public void setEatable(boolean v) { isEatable = v; }

    public void consume() { if (isEatable && isEaten() == false) eaten = true; }
}
