
public class GameBorder {
    public int xpos, ypos, xsize, ysize;
    public boolean active, internal;

    public int[] upLeftCorner, upRightCorner;
    public int[] downLeftCorner, downRightCorner;

    public GameBorder(int x, int y, int xs, int ys) {
        xpos = x; ypos = y;
        xsize = xs; ysize = ys;
        active = true; internal = false;

        upLeftCorner = new int[]{xpos, ypos};
        upRightCorner = new int[]{xpos + xsize, ypos};
        downLeftCorner = new int[]{xpos, ypos + ysize};
        downRightCorner = new int[]{xpos + xsize, ypos + ysize};
    }

    public void setActive(boolean v) { active = v; }
}
