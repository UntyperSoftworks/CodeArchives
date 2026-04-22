
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ghost {
    private double x, y;
    private boolean eaten;
    private int xdirection, ydirection, currentDirection;
    private double currentSpeed;

    public boolean isEatable;

    public final int ghostSizeConst = 40;

    public boolean _internalActive = false;

    private ArrayList<GameBorder> borders;

    private int[][] griddedPos;

    public static int leftDir = 1, rightDir = 2, upDir = 3, downDir = 4;
    
    private int math_random(int min, int max) {
        return SharedUtils.randomNumber(min, max);
    }

    public Ghost(int posx, int posy) {
        x = posx; y = posy;
        xdirection = 0; ydirection = 0;

        eaten = false;
        isEatable = false;

        currentSpeed = 1;
        currentDirection = 1;
    }

    public void setBorderTable(ArrayList<GameBorder> bt) { borders = bt; }
    public void setGridPos(int[][] gp) { griddedPos = gp; }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }

    // movement methods

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
    public void setSpeed(double speed) { currentSpeed = speed; }
    public double getSpeed() { return currentSpeed; }

    /**
     * Just use this function. It'll automatically change directions.
     */
    public void move() {
        if (eaten == false) {
            x += xdirection * currentSpeed;
            y += ydirection * currentSpeed;

            // border check
            boolean hitExternal = false;
            boolean hitGrid = false;

            // external border check
            for (GameBorder border : borders) {
                if (border.active && !border.internal) {
                    if (getX() < border.xpos) {
                        forceSetPosition(border.xpos, getY());
                        hitExternal = true;
                    }
                    if (getX() + ghostSizeConst > border.xpos + border.xsize) {
                        forceSetPosition(border.xpos + border.xsize - ghostSizeConst, getY());
                        hitExternal = true;
                    }

                    if (getY() < border.ypos) {
                        forceSetPosition(getX(), border.ypos);
                        hitExternal = true;
                    }
                    if (getY() + ghostSizeConst > border.ypos + border.ysize) {
                        forceSetPosition(getX(), border.ypos + border.ysize - ghostSizeConst);
                        hitExternal = true;
                    }
                    if (hitExternal) break;
                }
            }

            // gridded position check
            for (int[] gp : griddedPos) {
                if ((x > gp[0] - currentSpeed) && (x < gp[0] + currentSpeed)) {
                    if ((y > gp[1] - currentSpeed) && (y < gp[1] + currentSpeed)) {
                        forceSetPosition(gp[0], gp[1]);
                        hitGrid = true;
                        break;
                    }
                }
            }

            if (hitExternal || hitGrid) {
                toRandomDirectionV3();
            }
        }
    }

    public void forceSetPosition(int nx, int ny) {
        x = nx;
        y = ny;
    }

    public int getDirection() { return currentDirection; }

    // preset directions

    /**
     * Randomly changes directions only based on if directions won't collide through barriers.
     */
    public void toRandomDirectionV3() {
        int[] queued = new int[4];
        int _id = 0;

        int halfSize = ghostSizeConst / 2;
        int centerX = getX() + halfSize, centerY = getY() + halfSize;

        for (int i = 0; i < 4; i++) {
            int cdir = i + 1;
            int xd = 0, yd = 0;

            if (cdir == leftDir) {
                xd = -1; yd = 0;
            } else if (cdir == rightDir) {
                xd = 1; yd = 0;
            } else if (cdir == upDir) {
                xd = 0; yd = -1;
            } else if (cdir == downDir) {
                xd = 0; yd = 1;
            }

            boolean invalid = false;

            int predictX = centerX + ((halfSize * xd) + ((int)currentSpeed * xd));
            int predictY = centerY + ((halfSize * yd) + ((int)currentSpeed * yd));
            for (GameBorder border : borders) {
                if (border.active) {
                    if (border.internal) {
                        if ((predictX > border.xpos) && (predictX < border.xpos + border.xsize)) {
                            if ((predictY > border.ypos) && (predictY < border.ypos + border.ysize)) {
                                invalid = true;
                            }
                        }
                    } else {
                        if ((predictX < border.xpos) || (predictX > border.xpos + border.xsize)) {
                            invalid = true;
                        }
                        if ((predictY < border.ypos) || (predictY > border.ypos + border.xsize)) {
                            invalid = true;
                        }
                    }
                }
            }

            if (invalid == false) {
                Array.setInt(queued, _id, cdir);
                // queued[_id] = cdir;
                _id++;
            }
        }

        // excludes unassigned values (zero)
        int randDir;
        while (true) {
            randDir = queued[math_random(0, queued.length - 1)];
            if (randDir != 0) break;
        }

        changeDirectionByInt(randDir);
    }

    // eatable methods

    public boolean isEaten() { return eaten; }
    public void setEatable(boolean v) { isEatable = v; }

    public void consume() { if (isEatable && isEaten() == false) eaten = true; }

    public void reset() {
        setEatable(false);
        eaten = false;
    }
}
