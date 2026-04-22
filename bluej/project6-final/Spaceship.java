
public class Spaceship {
    private int x;
    private final int y;

    private int score;
    private boolean crashed;
    private int speed;

    public Spaceship(int posx, int posy) {
        x = posx;
        y = posy;
        score = 0;
        crashed = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setSpeed(int s) { speed = s; }

    public int getScore() { return score; }
    public void addScore(int n) { score += n; }

    public boolean isCrashed() { return crashed; }
    public void crash() { crashed = true; }

    public void moveX(int dirx) { x += dirx * speed; }
    public void setX(int nx) { x = nx; }
}
