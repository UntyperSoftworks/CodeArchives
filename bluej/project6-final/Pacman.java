
import java.util.ArrayList;

public class Pacman {
    private double x, y;
    private int xdir, ydir;

    private int score;
    private double speed;

    public final int sizeConst = 40;

    private int queueX = 0, queueY = 0;
    private int lives;

    private boolean gridded = false;
    private int[][] griddedPos;

    // pacman will have built-in border checks
    private ArrayList<GameBorder> borders;

    public Pacman(int px, int py) {
        x = px; y = py;
        lives = 1;
        score = 0;
        speed = 1;
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }

    public void setGrided(boolean v) { gridded = v; }
    public void setGridPos(int[][] gp) { griddedPos = gp; }

    public void setBorderTable(ArrayList<GameBorder> bt) { borders = bt; }

    public void forceSetPosition(int nx, int ny) {
        x = (double)nx;
        y = (double)ny;
    }

    public void queueDirection(int dx, int dy) {
        if (gridded) {
            queueX = dx;
            queueY = dy;
        } else {
            xdir = dx;
            ydir = dy;
        }
    }
    public void resetDirection() {
        if (gridded) {
            queueX = 0;
            queueY = 0;
        }
        xdir = 0;
        ydir = 0;
    }

    // Finally fixed pacman not moving in
    // certain directions within gridded positions
    public boolean checkDirection() {
        boolean collision = false;

        int halfSize = sizeConst / 2;
        int centerX = getX() + halfSize, centerY = getY() + halfSize;

        int queuePredictX = centerX + ((halfSize * queueX) + ((int)speed * queueX));
        int queuePredictY = centerY + ((halfSize * queueY) + ((int)speed * queueY));

        boolean unsafe = false;

        // Checks if queued direction is valid to move freely
        // If not, queue direction will validate if on gridded position

        for (GameBorder border : borders) {
            if (border.active) {
                if (border.internal) {
                    if ((queuePredictX > border.xpos) && (queuePredictX < border.xpos + border.xsize)) {
                        if ((getY() < border.ypos + border.ysize) && (getY() + sizeConst > border.ypos)) {
                            unsafe = true;
                        }
                    }
                    if ((queuePredictY > border.ypos) && (queuePredictY < border.ypos + border.ysize)) {
                        if ((getX() < border.xpos + border.xsize) && (getX() + sizeConst > border.xpos)) {
                            unsafe = true;
                        }
                    }
                } else {
                   if (queuePredictX < border.xpos || queuePredictX > border.xpos + border.xsize) unsafe = true;
                   if (queuePredictY < border.ypos || queuePredictY > border.ypos + border.ysize) unsafe = true;
                }
            }
        }

        // System.out.println("Unsafe direction: " + unsafe);

        if (unsafe) {
            for (int[] gp : griddedPos) {
                if ((x > gp[0] - (int)speed) && (x < gp[0] + (int)speed)) {
                    if ((y > gp[1] - (int)speed) && (y < gp[1] + (int)speed)) {
                        xdir = queueX;
                        ydir = queueY;
                        forceSetPosition(gp[0], gp[1]);
                        break;
                    }
                }
            }
        } else {
            xdir = queueX;
            ydir = queueY;
        }

        int predictX = centerX + ((halfSize * xdir) + ((int)speed * xdir));
        int predictY = centerY + ((halfSize * ydir) + ((int)speed * ydir));

        for (GameBorder border : borders) {
            if (border.active && border.internal) {
                if ((predictX > border.xpos) && (predictX < border.xpos + border.xsize)) {
                    if ((getY() < border.ypos + border.ysize) && (getY() + sizeConst > border.ypos)) {
                        resetDirection();
                        collision = true;
                    }
                }
                if ((predictY > border.ypos) && (predictY < border.ypos + border.ysize)) {
                    if ((getX() < border.xpos + border.xsize) && (getX() + sizeConst > border.xpos)) {
                        resetDirection();
                        collision = true;
                    }
                }
            }
        }

        return collision;
    }

    /**
     * Moves pacman while checking if the curret direction is valid (not touching barriers)
     */
    public void move() {
        boolean collision = checkDirection();

        if (collision == false) {
            x += xdir * speed;
            y += ydir * speed;
        }

        // external border check
        for (GameBorder border : borders) {
            if (border.active) {
                if (!border.internal) {
                    if (getX() < border.xpos) forceSetPosition(border.xpos, getY());
                    if (getX() + sizeConst > border.xpos + border.xsize) forceSetPosition(border.xpos + border.xsize - sizeConst, getY());

                    if (getY() < border.ypos) forceSetPosition(getX(), border.ypos);
                    if (getY() + sizeConst > border.ypos + border.ysize) forceSetPosition(getX(), border.ypos + border.ysize - sizeConst);
                }
            }
        }
    }

    public void setSpeed(double n) { speed = n; }
    public double getSpeed() { return speed; }

    public void addScore(int n) { score += n; }
    public int getScore() { return score; }

    public void setLives(int n) { lives = n; }
    public void removeLive() { lives -= 1; }
    public int getLives() { return lives; }

    public int[] getDirection() {
        int[] dir = new int[2];
        dir[0] = xdir; dir[1] = ydir;
        return dir;
    }
    public int[] getQueueDirection() {
        int[] qdir = new int[2];
        qdir[0] = queueX; qdir[1] = queueY;
        return qdir;
    }
}
