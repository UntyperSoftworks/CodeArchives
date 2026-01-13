/**
 * space invaders demo graphics
 * 
 * @author Noah
 * @version 0.1.1
 */

import java.awt.*;
import javax.swing.*;

public class SpaceGraphics extends JPanel
{
    Image space,invader,bullet,player,bang;
    int px, score = 0, rx, ry, rx2, ry2, arx, ary;
    boolean paused, ended, rocket, laserRocket, alienRocket;
    boolean aimline, trajectory, hitboxes;
    boolean h1, h2, h3, h4;
    int ap1, ap2, ap3, ap4;
    
    public SpaceGraphics(int x)               
    {
        setBackground(Color.black);
        invader = Toolkit.getDefaultToolkit().getImage("alien2.png");
        space = Toolkit.getDefaultToolkit().getImage("space1.png");
        bullet = Toolkit.getDefaultToolkit().getImage("rocket.png");
        player = Toolkit.getDefaultToolkit().getImage("spaceship.png");
        bang = Toolkit.getDefaultToolkit().getImage("bang.png");
        px = x;
        rx = 0; ry = 0;
        paused = false;
        ended = false;
        aimline = false; trajectory = false;
        rocket = false; laserRocket = false;
        h1 = false; h2 = false; h3 = false; h4 = false;
        ap1 = 0; ap2 = 0; ap3 = 0; ap4 = 0;
    }
    
    public void applyAlienPos(int v1, int v2, int v3, int v4) {
        ap1 = v1;
        ap2 = v2;
        ap3 = v3;
        ap4 = v4;
    }
    
    public void showAim(boolean v) {
        aimline = v;
    }
    
    public void showTrajectory(boolean v) {
        trajectory = v;
    }
    
    public void showHitbox(boolean v) {
        hitboxes = v;
    }
    
    public void killAlien(int id) {
        if (id == 1) h1 = true;
        else if (id == 2) h2 = true;
        else if (id == 3) h3 = true;
        else if (id == 4) h4 = true;
        score = score + 10;
    }
    
    public void setPause(boolean v) {
        paused = v;
    }
    
    public void setEnded() {
        if (!ended) ended = true;
    }
    
    public void movePlane(int x) {
        px = x;
    };
    
    // rocket functions
    
    public void newRocket(int x) {
        rocket = true;
        rx = x;
    }
    
    public void destroyRocket() {
        rocket = false;
    }
    
    public void moveRocket(int y) {
        ry = y;
    }

    public void newLaserRocket(int x) {
        laserRocket = true;
        rx2 = x;
    }

    public void destroyLaserRocket() {
        laserRocket = false;
    }

    public void moveLaserRocket(int y) {
        ry2 = y;
    }

    public void newAlienRocket(int x) {
        alienRocket = true;
        arx = x;
    }

    public void destroyAlienRocket() {
        alienRocket = false;
    }

    public void moveAlienRocket(int y) {
        ary = y;
    }
    
    @Override public void paint(Graphics g)
    {
        super.paint(g);
        
        g.drawImage(space,0,0,400,700,null);
        g.drawImage(player,px,500,40,40,null);
        
        int y1 = 20;
        
        if (h1 == false) {
            g.drawImage(invader, ap1, y1, 40, 40, null);
            if (hitboxes) {
                g.setColor(Color.red);
                g.drawRect(ap1, y1, 40, 40);
            }
        } else g.drawImage(bang, ap1, y1, 40, 40, null);
        if (h2 == false) {
            g.drawImage(invader, ap2, y1, 40, 40, null);
            if (hitboxes) {
                g.setColor(Color.red);
                g.drawRect(ap2, y1, 40, 40);
            }
        } else g.drawImage(bang, ap2, y1, 40, 40, null);
        if (h3 == false) {
            g.drawImage(invader, ap3, y1, 40, 40, null);
            if (hitboxes) {
                g.setColor(Color.red);
                g.drawRect(ap3, y1, 40, 40);
            }
        } else g.drawImage(bang, ap3, y1, 40, 40, null);
        if (h4 == false) {
            g.drawImage(invader, ap4, y1, 40, 40, null);
            if (hitboxes) {
                g.setColor(Color.red);
                g.drawRect(ap4, y1, 40, 40);
            }
        } else g.drawImage(bang, ap4, y1, 40, 40, null);
        
        g.setColor(Color.green);
        g.setFont(new Font("Ariel", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 120);
        
        if (aimline) {
            g.setColor(Color.blue);
            g.drawLine(px + 20, 0, px + 20, 500);
        }
        
        if (rocket) {
            g.drawImage(bullet, rx - 15, ry, 30, 50, null);
            if (trajectory) {
                g.setColor(new Color(255, 0, 255));
                g.drawLine(rx, 0, rx, ry);
            }
        }

        if (laserRocket) {
            g.drawImage(bullet, rx2 - 15, ry2, 30, 50, null);
            if (trajectory) {
                g.setColor(Color.red);
                g.drawLine(rx2, 0, rx2, ry);
            }
        }

        if (alienRocket) {
            g.setColor(Color.red);
            g.fillOval(arx, ary, 10, 10);
        }
        
        if (ended) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Over!", 80, 350);
        } else if (paused) {
            g.setFont(new Font("Impact", Font.PLAIN, 48));
            g.setColor(Color.red);
            g.drawString("Game Paused!", 80, 350);
        }
    }
}
