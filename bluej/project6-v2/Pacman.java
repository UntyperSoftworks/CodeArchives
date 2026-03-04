
public class Pacman {
    private int x, y;

    private int speed;
    private int score;

    public Pacman(int px, int py) {
        x = px;
        y = py;
        score = 0;
        speed = 1;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void moveDirection(int dx, int dy) {
        x += dx * speed;
        y += dy * speed;
    }
    public void forceSetPosition(int nx, int ny) {
        x = nx; y = ny;
    }
    public void setSpeed(int n) { speed = n; }

    public void addScore(int n) { score += n; }
    public int getScore() { return score; }
}
