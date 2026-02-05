
/**
 * Pacman demo graphics internal
 *
 * @author Noah
 * @version 3.0
 */

import java.awt.*;
import javax.swing.*;

public class PacV3Graphics extends JPanel {
    // GRAPHICS VARIABLES
    Image pacman, cherry;
    Image[] ghosts;
    int px, py, dirx, diry;
    boolean paused, ended;
    boolean[] collected;
    int[][] pelletpos, ghostpos, predictpos;
    int score, pellets;
    boolean debugMode;
    boolean eatenGhost;

    // CONFIGURATION
    boolean debug_hitboxes = true;
    boolean debug_showHiddenHitboxes = true;
    Color debug_pacmanHitboxColor = Color.green;
    Color debug_pelletHitboxColor = Color.pink;
    Color debug_ghostHitboxColor = Color.red;
    Color debug_eatGhostHitboxColor = new Color(255, 0, 255);
    Color scoreColor = Color.green;
    int[] gameOverPos = {150, 300};

    // CLASS METHODS
    public void toggleDebug(boolean v) { debugMode = v; }
    public void setPelletPositions(int[][] p) { pelletpos = p; }
    public void setGhostPositions(int[][] p) { ghostpos = p; }
    public void collectPellet(int id) { collected[id] = true; }
    public void addScore(int v) { score = score + v; }
    public void setPause(boolean v) { paused = v; }
    public void setEnded() { if (ended == false) ended = true; }
    public void movePacman(int x, int y) { px = x; py = y; }
    public void setPellets(int v) { pellets = v; }
    public void eatGhost() { if (eatenGhost == false) eatenGhost = true; }
    
    private Image getImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(name);
    }

    public PacV3Graphics(int x, int y, int pellets) {
        setBackground(Color.black);
        pacman = getImage("pacman.png");
        cherry = getImage("cherry.png");

        ghosts = new Image[5];
        for (int i = 0; i < 4; i++) {
            String name = "ghost" + Integer.toString(i + 1) + ".png";
            ghosts[i] = getImage(name);
        }
        ghosts[4] = getImage("eatghost.png");

        paused = false; ended = false;
        score = 0;
        px = x; py = y;
        collected = new boolean[pellets];
        for (int i = 0; i < pellets; i++) collected[i] = false;

        debugMode = false;
    }

    public void paint(Graphics g) {
        super.paint(g);
        
        if (ended == false) {
            Font scorefont = new Font("Ariel", Font.BOLD, 20);
            g.setColor(scoreColor);
            g.setFont(scorefont);
            g.drawString("Score: " + score, 50, 50);

            g.drawImage(pacman, px, py, 40, 40, null);
            if (debugMode) {
                if (debug_hitboxes) {
                    g.setColor(debug_pacmanHitboxColor);
                    g.drawRect(px, py, 40, 40);
                }
            }

            // pellet render
            for (int id = 0; id < pellets; id++) {
                int size = 20;
                if (id == 1) size = 40;
                int[] pos = pelletpos[id];
                int x = pos[0], y = pos[1];

                boolean isCollected = collected[id];
                if (!isCollected) {
                    if (id == 1) g.setColor(new Color(255, 255, 100));
                    else g.setColor(Color.yellow);
                    g.fillOval(x, y, size, size);
                }
                if (debugMode && debug_hitboxes) {
                    if (debug_showHiddenHitboxes || (!debug_showHiddenHitboxes && !isCollected)) {
                        g.setColor(debug_pelletHitboxColor);
                        g.drawRect(x, y, size, size);
                    }
                }
            }

            // ghost render
            for (int id = 0; id < 5; id++) {
                int size = 40;
                int[] pos = ghostpos[id];
                int x = pos[0], y = pos[1];

                Image image = ghosts[id];
                if (id == 4) {
                    if (eatenGhost == false) g.drawImage(image, x, y, size, size, null);
                } else {
                    g.drawImage(image, x, y, size, size, null);
                }
                if (debugMode && debug_hitboxes) {
                    if (id == 4) {
                        g.setColor(debug_eatGhostHitboxColor);
                    } else {
                        g.setColor(debug_ghostHitboxColor);
                    }
                    g.drawRect(x, y, size, size);
                }
            }
        } else {
            Font gameoverfont = new Font("Ariel", Font.BOLD, 80);
            g.setColor(Color.red);
            g.setFont(gameoverfont);
            g.drawString("GAME OVER!", gameOverPos[0], gameOverPos[1]);
            
            Font finalscorefont = new Font("Ariel", Font.PLAIN, 32);
            g.setColor(scoreColor);
            g.setFont(finalscorefont);
            g.drawString("Final Score: " + score, gameOverPos[0] + 150, gameOverPos[1] + 50);
        }
    }
}
