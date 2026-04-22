
/* Pacman demo graphics internal */

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class PacV3Graphics extends JPanel {
    // Graphics variables
    Image pacmanImage;
    Image[] ghostIcons;
    Image[] eatGhostIcons;
    boolean paused, ended;
    boolean debugMode;

    boolean _unstarted = true;
    boolean levelCooldown = false;
    int levelCooldownTimer = 0;
    int currentLevel = 0;

    ArrayList<Pellet> pellets;
    ArrayList<Ghost> ghosts;
    Pacman player;

    ArrayList<GameBorder> borders;

    int[][] griddedPos;

    // Graphics configuration
    boolean debug_hitboxes = true;
    boolean debug_showHiddenHitboxes = false;
    boolean debug_ghostDirection = true;
    boolean debug_griddedPos = true;
    boolean debug_pacmanDirection = true;
    boolean debug_queuedDirection = true;

    Color debug_pacmanHitboxColor = Color.green;
    Color debug_pelletHitboxColor = new Color(255, 0, 255);
    Color debug_ghostHitboxColor = Color.red;
    Color debug_eatGhostHitboxColor = new Color(255, 0, 255);
    
    Color scoreColor = Color.green;
    int[] gameOverPos = {150, 300};

    // Internal variables

    int eatTimer = 0;
    private int msToFrames(int ms, int step) {
        return SharedUtils.msToFrames(ms, step);
    }

    // Graphics methods
    public void toggleDebug(boolean v) { debugMode = v; }
    public void setPause(boolean v) { paused = v; }
    public void setEnded() { if (ended == false) ended = true; }
    public void setPacman(Pacman plr) { player = plr; }
    
    public void setPellets(ArrayList<Pellet> p) { pellets = p; }
    public void setGhosts(ArrayList<Ghost> g) { ghosts = g; }
    public void setGameBorders(ArrayList<GameBorder> b) { borders = b; }

    public void getEatTimer(int x) { eatTimer = x; }

    public void _setStarted() { if (_unstarted) _unstarted = false; }

    public void setGridPos(int[][] gp) { griddedPos = gp; }
    
    public void setLevelCooldown(boolean state, int s) {
        levelCooldown = state;
        levelCooldownTimer = s;
    }
    public void setLevel(int lvl) { currentLevel = lvl; }

    private Image getImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(name);
    }

    public PacV3Graphics() {
        paused = false; ended = false;
        debugMode = false;

        setBackground(Color.black);

        // Fetching pacman/ghost images
        pacmanImage = getImage("pacman.png");

        ghostIcons = new Image[4];
        for (int i = 0; i < 4; i++) {
            String name = "ghost" + Integer.toString(i + 1) + ".png";
            ghostIcons[i] = getImage(name);
        }
        
        eatGhostIcons = new Image[2];
        eatGhostIcons[0] = getImage("eatghost.png");
        eatGhostIcons[1] = getImage("eatghost2.png");
    }

    @Override public void paint(Graphics g) {
        super.paint(g);
        
        if (ended == false) {
            int px = player.getX(), py = player.getY();
            int score = player.getScore();

            Font scorefont = new Font("Ariel", Font.BOLD, 20);
            g.setColor(scoreColor);
            g.setFont(scorefont);
            g.drawString("Score: " + score, 40, 40);

            Font lvlfont = new Font("Ariel", Font.PLAIN, 16);
            g.setFont(lvlfont);
            g.drawString("Level " + currentLevel, 150, 680);

            int _lives = player.getLives();
            if (_lives <= 1) g.setColor(Color.red);
            g.drawString("Lives: " + _lives, 450, 680);

            g.drawImage(pacmanImage, px, py, 40, 40, null);

            // pellet render
            for (Pellet pill : pellets) {
                if (pill != null) {
                    int size = 20;
                    if (pill.powerPellet) size = 30;
                    int x = pill.getX(), y = pill.getY();
                    boolean isCollected = pill.isEaten();
                    if (!isCollected) {
                        if (pill.powerPellet) g.setColor(new Color(255, 255, 100));
                        else g.setColor(Color.yellow);
                        g.fillOval(x, y, size, size);
                    }

                    // Pellet hitbox debug
                    if (debugMode && debug_hitboxes && pill._internalActive) {
                        if (debug_showHiddenHitboxes || (!debug_showHiddenHitboxes && !isCollected)) {
                            g.setColor(debug_pelletHitboxColor);
                            g.drawRect(x, y, size, size);
                        }
                    }
                }
            }

            // ghost render
            for (int id = 0; id < ghosts.size(); id++) {
                Ghost ghoul = ghosts.get(id);
                int size = 40;
                int x = ghoul.getX(), y = ghoul.getY();

                int _imgid = Math.floorMod(id, 4);
                Image image = ghostIcons[_imgid];
                if (ghoul.isEatable) {
                    image = eatGhostIcons[0];
                    if (eatTimer >= msToFrames(3000, 16)) {
                        if ((eatTimer > msToFrames(3000, 16) & eatTimer < msToFrames(3500, 16)) || (eatTimer > msToFrames(4000, 16) & eatTimer < msToFrames(4500, 16))) {
                            image = eatGhostIcons[1];
                        }
                    }
                }
                if (!ghoul.isEaten()) g.drawImage(image, x, y, size, size, null);

                // Ghost debug render
                if (debugMode) {
                    if (ghoul.isEatable) g.setColor(debug_eatGhostHitboxColor);
                    else g.setColor(debug_ghostHitboxColor);

                    if (debug_hitboxes && ghoul._internalActive) {
                        if (debug_showHiddenHitboxes || (!debug_showHiddenHitboxes && !ghoul.isEaten())) {
                            g.drawRect(x, y, size, size);
                        }
                    }
                    if (debug_ghostDirection && !ghoul.isEaten()) {
                        int centerX = ghoul.getX() + (size / 2);
                        int centerY = ghoul.getY() + (size / 2);
                        int _drawSteps = 50;

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

            // border render
            for (GameBorder border : borders) {
                g.setColor(Color.blue);
                g.drawRect(border.xpos, border.ypos, border.xsize, border.ysize);
            }

            // gridded position render
            if (debugMode && debug_griddedPos && griddedPos != null) {
                g.setColor(new Color(255, 50, 50));
                for (int[] pos : griddedPos) {
                    int currSpeed = (int)player.getSpeed();
                    int halfSpeed = currSpeed / 2;

                    g.fillRect(pos[0] - halfSpeed, pos[1] - halfSpeed, currSpeed, currSpeed);
                }
            }

            // pacman debug render
            if (debugMode) {
                if (debug_hitboxes) {
                    g.setColor(debug_pacmanHitboxColor);
                    g.drawRect(px, py, player.sizeConst, player.sizeConst);
                }

                // direction debug
                int halfSize = player.sizeConst / 2;
                int cx = px + halfSize, cy = py + halfSize;

                if (debug_pacmanDirection) {
                    // current direction render
                    int[] pacDir = player.getDirection();
                    int cx2 = cx + (50 * pacDir[0]), cy2 = cy + (50 * pacDir[1]);
                    g.setColor(new Color(0, 255, 0));
                    g.drawLine(cx, cy, cx2, cy2);
                    g.fillOval(cx2 - 5, cy2 - 5, 10, 10);
                }

                if (debug_queuedDirection) {
                    // queue direction render
                    int[] queueDir = player.getQueueDirection();
                    int cx3 = cx + (50 * queueDir[0]), cy3 = cy + (50 * queueDir[1]);
                    g.setColor(new Color(255, 50, 50));
                    g.drawLine(cx, cy, cx3, cy3);
                    g.fillOval(cx3 - 5, cy3 - 5, 10, 10);
                }
            }

            // Checks if game is paused
            if (paused) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, 1000, 1000);

                g.setColor(scoreColor);
                g.setFont(scorefont);
                g.drawString("Score: " + score, 40, 40);

                g.setColor(Color.red);
                if (_unstarted) {
                    Font unstartedFont = new Font("Ariel", Font.BOLD, 40);
                    g.setFont(unstartedFont);
                    g.drawString("Press Start Button", 210, 500);
                } else {
                    Font gamepauseFont = new Font("Ariel", Font.BOLD, 35);
                    g.setFont(gamepauseFont);
                    g.drawString("Game Paused", 36, 80);
                }

                // keybind list render
                Font bindFont = new Font("Ariel", Font.PLAIN, 16);
                g.setColor(new Color(255, 255, 255, 200));
                g.setFont(bindFont);

                g.drawString("Move - WASD/Arrow Keys", 40, 120);
                g.drawString("Toggle Pause - Escape", 40, 140);
                g.drawString("Toggle Debug - T", 40, 160);
                g.drawString("End Game - Y", 40, 180);
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
