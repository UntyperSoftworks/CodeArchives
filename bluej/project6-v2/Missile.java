
public class Missile {
    // player missiles don't move horizontally
    // so x-coordinate is constant (read-only)
    private final int x;
    private int y;
    // private boolean active;

    public Missile(int posx, int posy) {
        x = posx;
        y = posy;
        // active = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // public boolean isActive() { return active; }
    // public void setActive(boolean state) { active = state; }

    public void moveUpY(int diry) {
        y -= diry;
    }
    public void setY(int sy) {
        y = sy;
    }
}
