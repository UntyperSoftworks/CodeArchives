
/* Pacman demo graphics internal */

import java.awt.*;
import javax.swing.*;

public class PacV3Graphics extends JPanel {
    // GRAPHICS VARIABLES
    Image pacmanImage;
    Image[] ghostIcons;
    Image[] eatGhostIcons;
    //int px, py;
    boolean paused, ended;
    boolean debugMode;

    int _maxsteps;

    Pellet[] pellets;
    Ghost[] ghosts;
    Pacman player;

    // CONFIGURATION
    boolean debug_hitboxes = true;
    boolean debug_showHiddenHitboxes = false;
    boolean debug_ghostDirection = true;

    Color debug_pacmanHitboxColor = Color.green;
    Color debug_pelletHitboxColor = new Color(255, 0, 255);
    Color debug_ghostHitboxColor = Color.red;
    Color debug_eatGhostHitboxColor = new Color(255, 0, 255);
    
    Color scoreColor = Color.green;
    int[] gameOverPos = {150, 300};

    final int numOfGhosts = 4;
    int eatTimer = 0;

    private int msToFrames(int ms, int step) {
        return (int)(ms / step);
    }

    // CLASS METHODS
    public void toggleDebug(boolean v) { debugMode = v; }
    public void setPause(boolean v) { paused = v; }
    public void setEnded() { if (ended == false) ended = true; }
    public void setPacman(Pacman plr) { player = plr; }
    
    public void setPellets(Pellet[] p) { pellets = p; }
    public void setGhosts(Ghost[] g) { ghosts = g; }

    public void setMaxGhostSteps(int x) { _maxsteps = x; }

    public void getEatTimer(int x) { eatTimer = x; }

    private Image getImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(name);
    }

    public PacV3Graphics() {
        setBackground(Color.black);
        pacmanImage = getImage("pacman.png");

        _maxsteps = 1000;

        ghostIcons = new Image[4];
        for (int i = 0; i < 4; i++) {
            String name = "ghost" + Integer.toString(i + 1) + ".png";
            ghostIcons[i] = getImage(name);
        }
        
        eatGhostIcons = new Image[2];
        eatGhostIcons[0] = getImage("eatghost.png");
        eatGhostIcons[1] = getImage("eatghost2.png");

        paused = false; ended = false;
        debugMode = false;
    }

    public void paint(Graphics g) {
        super.paint(g);
        
        if (ended == false) {
            int px = player.getX(), py = player.getY();
            int score = player.getScore();

            Font scorefont = new Font("Ariel", Font.BOLD, 20);
            g.setColor(scoreColor);
            g.setFont(scorefont);
            g.drawString("Score: " + score, 50, 50);

            g.drawImage(pacmanImage, px, py, 40, 40, null);
            if (debugMode) {
                if (debug_hitboxes) {
                    g.setColor(debug_pacmanHitboxColor);
                    g.drawRect(px, py, 40, 40);
                }
            }

            // pellet render
            for (Pellet pill : pellets) {
                int size = 20;
                if (pill.powerPellet) size = 40;
                int x = pill.getX(), y = pill.getY();
                boolean isCollected = pill.isEaten();
                if (!isCollected) {
                    if (pill.powerPellet) g.setColor(new Color(255, 255, 100));
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
            for (int id = 0; id < numOfGhosts; id++) {
                Ghost ghoul = ghosts[id];
                int size = 40;
                int x = ghoul.getX(), y = ghoul.getY();

                int _imgid = Math.floorMod(id, 4);
                Image image = ghostIcons[_imgid];
                if (ghoul.isEatable) {
                    image = eatGhostIcons[0];
                    if (eatTimer >= msToFrames(3000, 16)) {
                        if (eatTimer > msToFrames(3000, 16) & eatTimer < msToFrames(3500, 16)) {
                            image = eatGhostIcons[1];
                        }
                        if (eatTimer > msToFrames(4000, 16) & eatTimer < msToFrames(4500, 16)) {
                            image = eatGhostIcons[1];
                        }
                    }
                }
                if (!ghoul.isEaten()) g.drawImage(image, x, y, size, size, null);

                if (debugMode) {
                    if (ghoul.isEatable) g.setColor(debug_eatGhostHitboxColor);
                    else g.setColor(debug_ghostHitboxColor);

                    if (debug_hitboxes) {
                        if (debug_showHiddenHitboxes || (!debug_showHiddenHitboxes && !ghoul.isEaten())) {
                            g.drawRect(x, y, size, size);
                        }
                    }
                    if (debug_ghostDirection && !ghoul.isEaten()) {
                        int centerX = ghoul.getX() + (size / 2);
                        int centerY = ghoul.getY() + (size / 2);
                        int _currSteps = ghoul.getCurrentSteps();
                        int _drawSteps = _maxsteps - _currSteps;

                        int relativeX = 0, relativeY = 0;
                        if (ghoul.getDirection() == Ghost.leftDir) relativeX = -1;
                        else if (ghoul.getDirection() == Ghost.rightDir) relativeX = 1;
                        else if (ghoul.getDirection() == Ghost.upDir) relativeY = -1;
                        else if (ghoul.getDirection() == Ghost.downDir) relativeY = 1;

                        int endpointX = centerX + (relativeX * _drawSteps);
                        int endpointY = centerY + (relativeY * _drawSteps);

                        int _dotSize = 8;
                        int _dotHalf = (_dotSize / 2);
                        g.fillOval(endpointX - _dotHalf, endpointY - _dotHalf, _dotSize, _dotSize);
                        g.drawLine(centerX, centerY, endpointX, endpointY);
                    }
                }
            }

            if (paused) {
                Font gamepauseFont = new Font("Ariel", Font.BOLD, 35);
                g.setColor(Color.red);
                g.setFont(gamepauseFont);
                g.drawString("Game Paused", 80, 100);
            }
        } else {
            Font gameoverfont = new Font("Ariel", Font.BOLD, 80);
            g.setColor(Color.red);
            g.setFont(gameoverfont);
            g.drawString("GAME OVER!", gameOverPos[0], gameOverPos[1]);
            
            Font finalscorefont = new Font("Ariel", Font.PLAIN, 32);
            g.setColor(scoreColor);
            g.setFont(finalscorefont);
            g.drawString("Final Score: " + player.getScore(), gameOverPos[0] + 150, gameOverPos[1] + 50);
        }
    }
}
