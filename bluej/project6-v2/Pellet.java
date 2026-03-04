
public class Pellet {
    private final int x, y;
    private boolean eaten;

    private final int normalScore = 10;
    private final int bigScore = 50;

    boolean powerPellet;

    public Pellet(int posx, int posy) {
        x = posx;
        y = posy;
        eaten = false;
        powerPellet = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isEaten() { return eaten; }
    public void consume() { eaten = true; }

    public int getScore() {
        int _score = normalScore;
        if (powerPellet) _score = bigScore;
        return _score;
    }
}
