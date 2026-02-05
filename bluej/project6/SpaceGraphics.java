/**
 * space invaders demo graphics
 * 
 * @author Noah
 * @version 0.2
 */

import java.awt.*;
import javax.swing.*;

public class SpaceGraphics extends JPanel
{
    Image space,invader,bullet,player,bang;
    int px, score = 0;
    boolean paused, ended, playerDied;
    boolean aimline, trajectory, hitboxes, alienTrajectory;
    //boolean h1, h2, h3, h4;
    int maxRockets, maxAlienRockets, boundY;

    int[] alienPosX, alienPosY;
    boolean[] aliveAliens;
    boolean[] activeAlienRockets;
    int[] alienRocketX, alienRocketY;

    boolean[] activeRockets, activeLaserRockets;
    int[] rocketX, rocketY;
    int[] laserRocketX, laserRocketY;
    
    public SpaceGraphics(int x)
    {
        alienPosX = new int[10]; alienPosY = new int[10];

        setBackground(Color.black);
        invader = Toolkit.getDefaultToolkit().getImage("alien2.png");
        space = Toolkit.getDefaultToolkit().getImage("space1.png");
        bullet = Toolkit.getDefaultToolkit().getImage("rocket.png");
        player = Toolkit.getDefaultToolkit().getImage("spaceship.png");
        bang = Toolkit.getDefaultToolkit().getImage("bang.png");
        px = x;
        paused = false;
        ended = false;
        aimline = false; trajectory = false;
    }
    
    // debug methods
    public void showAim(boolean v) {
        aimline = v;
    }
    public void showTrajectory(boolean v) {
        trajectory = v;
    }
    public void showHitbox(boolean v) {
        hitboxes = v;
    }
    public void showAlienTrajectory(boolean v) {
        alienTrajectory = v;
    }
    
    // game methods
    public void setPause(boolean v) {
        paused = v;
    }
    public void setEnded() {
        if (!ended) ended = true;
    }
    public void setBoundY(int y) {
        boundY = y;
    }
    
    // player methods
    public void movePlane(int x) {
        px = x;
    };
    public void addScore(int add) {
        score += add;
    }
    public void setPlayerDied(boolean v) {
        playerDied = v;
    }

    // rocket methods v2
    public void setRockets(int[] x, int[] y) {
        rocketX = x;
        rocketY = y;
    }
    public void setLaserRockets(int[] x, int[] y) {
        laserRocketX = x;
        laserRocketY = y;
    }

    public void setActiveRockets(boolean[] active) {
        activeRockets = active;
    }
    public void setActiveLaserRockets(boolean[] active) {
        activeLaserRockets = active;
    }

    public void setMaxRockets(int value) {
        maxRockets = value;
    }

    // alien methods
    public void setAlienPos(int[] x, int[] y) {
        alienPosX = x;
        alienPosY = y;
    }
    public void setAliveAliens(boolean[] states) {
        aliveAliens = states;
    }

    public void setActiveAlienRockets(boolean[] active) {
        activeAlienRockets = active;
    }
    public void setAlienRockets(int[] x, int[] y) {
        alienRocketX = x;
        alienRocketY = y;
    }

    public void setMaxAlienRockets(int value) {
        maxAlienRockets = value;
    }
    
    @Override public void paint(Graphics g)
    {
        super.paint(g);
        
        // preloads bang image
        g.drawImage(bang, -10, -10, 2, 2, null);
        g.drawImage(space, 0, 0, 400, 700, null);

        if (playerDied) {
            g.drawImage(bang, px, 500, 40, 40, null);
        } else {
            g.drawImage(player, px, 500, 40, 40, null);
        }
        
        for (int id = 0; id < alienPosX.length; id++) {
            boolean isAlive = aliveAliens[id];
            int positionx = alienPosX[id];
            int positiony = alienPosY[id];
            if (isAlive) {
                g.drawImage(invader, positionx, positiony, 20, 20, null);
                if (hitboxes) {
                    g.setColor(Color.red);
                    g.drawRect(positionx, positiony, 20, 20);
                }
            } else {
                g.drawImage(bang, positionx, positiony, 20, 20, null);
            }
        }
        
        g.setColor(Color.green);
        g.setFont(new Font("Ariel", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 120);
        
        if (aimline) {
            g.setColor(Color.blue);
            g.drawLine(px + 20, 0, px + 20, 500);
        }

        for (int i = 0; i < maxRockets; i++) {
            if (activeRockets[i]) {
                int _rx = rocketX[i], _ry = rocketY[i];
                g.drawImage(bullet, _rx - 15, _ry, 30, 50, null);
                if (trajectory) {
                    g.setColor(new Color(255, 0, 255));
                    g.drawLine(_rx, 0, _rx, _ry);
                }
            }
            if (activeLaserRockets[i]) {
                int _lrx = laserRocketX[i], _lry = laserRocketY[i];
                g.drawImage(bullet, _lrx - 15, _lry, 30, 50, null);
                if (trajectory) {
                    g.setColor(Color.green);
                    g.drawLine(_lrx, 0, _lrx, _lry);
                }
            }
        }
        
        for (int _a = 0; _a < maxAlienRockets; _a++) {
            if (activeAlienRockets[_a] == true) {
                int _arx = alienRocketX[_a], _ary = alienRocketY[_a];
                g.setColor(Color.red);
                g.fillOval(_arx - 5, _ary, 10, 10);
                if (alienTrajectory) {
                    g.setColor(Color.red);
                    g.drawLine(_arx, _ary + 10, _arx, boundY);
                }
            }
        }
        
        if (ended) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Over!", 80, 350);
        } else if (paused) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Paused!", 60, 350);
        }
    }
}
