/**
 * space invaders demo graphics
 * 
 * @author Noah
 * @version 0.2
 */

/*
TODO:
currently tasks:
- Use instance classes in graphics

tasks for much later:
- Use `HashMap` from main class to set graphics config (debug stuff)
*/

import java.awt.*;
import javax.swing.*;

public class SpaceGraphics extends JPanel
{
    Image space,invader,bullet,ship,bang;
    boolean paused, ended;
    boolean aimline, trajectory, hitboxes, alienTrajectory, playerHitbox;
    int maxRockets, maxAlienRockets, boundY;

    long usingmem, maxmem;
    boolean canShowMemory;

    Alien[] aliens;

    boolean[] activeAlienRockets;
    int[] alienRocketX, alienRocketY;

    Missile[] rockets;
    Missile[] laserRockets;

    boolean[] activeRockets, activeLaserRockets;

    Spaceship player;
    
    public SpaceGraphics() {
        setBackground(Color.black);
        invader = Toolkit.getDefaultToolkit().getImage("alien2.png");
        space = Toolkit.getDefaultToolkit().getImage("space1.png");
        bullet = Toolkit.getDefaultToolkit().getImage("rocket.png");
        ship = Toolkit.getDefaultToolkit().getImage("spaceship.png");
        bang = Toolkit.getDefaultToolkit().getImage("bang.png");
        paused = false;
        ended = false;
        aimline = false; trajectory = false;
    }
    
    // debug methods
    public void showAim(boolean v) { aimline = v; }
    public void showTrajectory(boolean v) { trajectory = v; }
    public void showHitbox(boolean v) { hitboxes = v; }
    public void showAlienTrajectory(boolean v) { alienTrajectory = v; }
    public void showPlayerHitbox(boolean v) { playerHitbox = v; }
    public void showMemoryDebug(boolean v) { canShowMemory = v; }
    public void setMemory(long used, long max) {
        usingmem = used;
        maxmem = max;
    }
    
    // game methods
    public void setPause(boolean v) { paused = v; }
    public void setEnded() { if (!ended) ended = true;  }
    public void setBoundY(int y) { boundY = y; }

    public void setSpaceship(Spaceship plr) { player = plr; }

    // rocket methods v2
    public void setRocketInstances(Missile[] instances) {
        rockets = instances;
    }
    public void setLaserRocketInstances(Missile[] instances) {
        laserRockets = instances;
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
    public void setAliens(Alien[] table) {
        aliens = table;
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
    
    @Override public void paint(Graphics g) {
        super.paint(g);
        
        // preloads bang image
        g.drawImage(bang, -10, -10, 2, 2, null);
        g.drawImage(space, 0, 0, 700, 700, null);

        int px = player.getX();

        if (player.isCrashed()) {
            g.drawImage(bang, px, 500, 40, 40, null);
        } else {
            g.drawImage(ship, px, 500, 40, 40, null);
            if (playerHitbox) {
                g.setColor(Color.green);
                g.drawRect(px, 500, 40, 40);
            }
        }
        
        for (Alien alien : aliens) {
            boolean isAlive = alien.isAlive();
            int positionx = alien.getX();
            int positiony = alien.getY();
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
        g.drawString("Score: " + player.getScore(), 10, 120);
        
        if (aimline) {
            g.setColor(Color.blue);
            g.drawLine(px + 20, 0, px + 20, 500);
        }

        for (int i = 0; i < maxRockets; i++) {
            if (activeRockets[i]) {
                Missile currentRocket = rockets[i];
                int _rx = currentRocket.getX(), _ry = currentRocket.getY();
                g.drawImage(bullet, _rx - 15, _ry, 30, 50, null);
                if (trajectory) {
                    g.setColor(new Color(255, 0, 255));
                    g.drawLine(_rx, 0, _rx, _ry);
                }
            }
            if (activeLaserRockets[i]) {
                Missile currentLaserRocket = laserRockets[i];
                int _lrx = currentLaserRocket.getX(), _lry = currentLaserRocket.getY();
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

        if (canShowMemory) {
            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g.drawString(usingmem + " / " + maxmem + " MB", 10, 20);
        }
        
        if (ended) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Over!", 225, 350);
        } else if (paused) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Paused!", 200, 350);
        }
    }
}
