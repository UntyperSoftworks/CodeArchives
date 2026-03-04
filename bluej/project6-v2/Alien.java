
public class Alien {
    private final int x, y;
    private boolean destroyed;

    public Alien(int posx, int posy) {
        x = posx;
        y = posy;
        destroyed = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isAlive() { return (destroyed == false); }
    public void destroy() { destroyed = true; }
}
