/**
 * space invaders
 *
 * @author Noah
 * @version 0.3
 */

/*
TODO:
- [done] Add missiles to aliens that shoot back randomly
- [done] Add laser missiles faster than normal missiles
- Change some variables (e.g. alien positions) to arrays for unit 6
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SpaceInvadersMain implements ActionListener, KeyListener
{
    JFrame f1;
    JPanel p1, btns;
    JButton start, stop, endd;
    SpaceGraphics g1;
    int px, dirx, rocketPos, rocketPosX;
    int laserRocketX, laserRocketPos;
    boolean gameState, ended, rocket, laserRocket, alienRocket;
    boolean e1, e2, e3, e4;
    int ap1, ap2, ap3, ap4;
    int alienRocketPos, alienRocketX;
    int boundy;
    
    // debug things
    boolean showRocketAim;
    boolean showRocketTrajectory;
    boolean showAlienHitbox;

    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public void spaceship() {
        Thread loop = new Thread();
        double fps = 60; // higher fps will increase speed
        int speed = 5;
        
        double ms1 = (1 / fps) * 1000;
        int ms = (int)(ms1);
        
        int rocketSpeed = 2;
        int laserRocketSpeed = 5;
        int alienRocketSpeed = 5;
        
        int sizeX = g1.getBounds().width;
        
        int z = 80, st = 40;
        for (int x = 0; x < 4; x++) {
            int bx1 = (z * x) + st;
            int id = x + 1;
            if (id == 1) {
                ap1 = bx1;
            } else if (id == 2) {
                ap2 = bx1;
            } else if (id == 3) {
                ap3 = bx1;
            } else if (id == 4) {
                ap4 = bx1;
            }
        }
        
        g1.applyAlienPos(ap1, ap2, ap3, ap4);
        
        while (!ended) {
            try {
                loop.sleep(ms);
            } catch (InterruptedException e) {}
            
            if (gameState) {
                px = px + (dirx * speed);
                int rightx = px + 40;
                if (px < 0) px = 0;
                if (rightx > sizeX) px = sizeX - 40;
                
                g1.movePlane(px);
                
                if (rocket) {
                    if (rocketPos < 0) {
                        noRocket();
                    } else {
                        int alienHitId = 0;
                        if (rocketPos <= 60) {
                            int sx = 40;
                            for (int x = 0; x < 4; x++) {
                                int id = x + 1;
                                if ((id == 1 && !e1) || (id == 2 && !e2) || (id == 3 && !e3) || (id == 4 && !e4)) {
                                    int bx1 = (z * x) + st;
                                    int bx2 = bx1 + sx;
                                    
                                    if (rocketPosX >= bx1 && rocketPosX <= bx2) {
                                        alienHitId = id;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (alienHitId == 0) {
                            g1.moveRocket(rocketPos);
                            rocketPos = rocketPos - rocketSpeed;
                        } else {
                            if (alienHitId == 1) e1 = true;
                            else if (alienHitId == 2) e2 = true;
                            else if (alienHitId == 3) e3 = true;
                            else if (alienHitId == 4) e4 = true;
                            noRocket();
                            g1.killAlien(alienHitId);
                            if (e1 && e2 && e3 && e4) ender();
                        }
                    }
                }

                if (laserRocket) {
                    if (laserRocketPos < 0) noLaserRocket();
                    else {
                        int hit = 0;
                        if (laserRocketPos <= 60) {
                            int sx = 40;
                            for (int x = 0; x < 4; x++) {
                                int id = x + 1;
                                if ((id == 1 && !e1) || (id == 2 && !e2) || (id == 3 && !e3) || (id == 4 && !e4)) {
                                    int bx1 = (z * x) + st;
                                    int bx2 = bx1 + sx;
                                    
                                    if (laserRocketX >= bx1 && laserRocketX <= bx2) {
                                        hit = id;
                                        break;
                                    }
                                }
                            }
                        }
                        if (hit == 0) {
                            g1.moveLaserRocket(laserRocketPos);
                            laserRocketPos -= laserRocketSpeed;
                        } else {
                            if (hit == 1) e1 = true;
                            else if (hit == 2) e2 = true;
                            else if (hit == 3) e3 = true;
                            else if (hit == 4) e4 = true;
                            noLaserRocket();
                            g1.killAlien(hit);
                            if (e1 && e2 && e3 && e4) ender();
                        }
                    }
                }

                if (alienRocket) {
                    alienRocketPos += alienRocketSpeed;
                    g1.moveAlienRocket(alienRocketPos);
                    if (alienRocketPos >= boundy) {
                        alienRocket = false;
                        g1.destroyAlienRocket();
                    } else {
                        boolean collide = false;
                        if (alienRocketPos > 500 && alienRocketPos < 540) {
                            int px2 = px + 40;
                            if (alienRocketX >= px && alienRocketX <= px2) {
                                collide = true;
                            }
                        }
                        if (collide) {
                            alienRocket = false;
                            g1.destroyAlienRocket();
                            ender();
                        }
                    }
                } else {
                    int x = math_random(0, 3);
                    int centre = (z * x) + st + 20;
                    alienRocketPos = 60;

                    g1.newAlienRocket(centre);
                    alienRocketX = centre;
                    alienRocket = true;
                }
                
                g1.repaint();
            }
        }
    }
    
    public SpaceInvadersMain()
    {
        dirx = 0;
        
        gameState = false;
        ended = false;
        showRocketAim = false;
        showRocketTrajectory = false;
        showAlienHitbox = false;
        
        e1 = false; e2 = false; e3 = false; e4 = false;
        
        ap1 = 0; ap2 = 0; ap3 = 0; ap4 = 0;
        
        f1 = new JFrame("Space Invaders Demo");
        f1.setSize(400,700);
        f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f1.setResizable(false);
        
        Container c1 = f1.getContentPane();
        
        px = 75;
        g1 = new SpaceGraphics(px);
        g1.addKeyListener(this);
        g1.showAim(showRocketAim);
        g1.showTrajectory(showRocketTrajectory);
        g1.showHitbox(showAlienHitbox);
        
        start = new JButton("Start");
        start.addActionListener(this);
        stop = new JButton("Pause");
        stop.addActionListener(this);
        endd = new JButton("End");
        endd.addActionListener(this);
        
        btns = new JPanel();
        btns.add(start);
        btns.add(stop);
        btns.add(endd);
        
        p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setSize(400,700);
        
        p1.add(g1, BorderLayout.CENTER);
        p1.add(btns, BorderLayout.SOUTH);
        
        c1.add(p1);
        f1.show();

        boundy = g1.getBounds().height;
        spaceship();
    }
    
    public void pauser() {
        if (!ended) {
            g1.setPause(true);
            g1.repaint();
        }
    }
    
    public void ender() {
        if (!ended) {
            ended = true;
            g1.setEnded();
            g1.repaint();
        }
    }
    
    public void noRocket() {
        rocket = false;
        g1.destroyRocket();
    }
    
    public void launchRocket() {
        rocket = true;
        g1.newRocket(px + 20);
        rocketPos = 480;
        rocketPosX = px + 20;
    }

    public void launchLaserRocket() {
        laserRocket = true;
        g1.newLaserRocket(px + 20);
        laserRocketPos = 480;
        laserRocketX = px + 20;
    }

    public void noLaserRocket() {
        laserRocket = false;
        g1.destroyLaserRocket();
    }
    
    public void actionPerformed(ActionEvent e) {
        Object b = e.getSource();
        if (b == start) {
            gameState = true;
            g1.setPause(false);
            g1.requestFocus();
        } else if (b == stop) {
            gameState = false;
            pauser();
        } else if (b == endd && !ended) ender();
    }
    
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        String key = KeyEvent.getKeyText(code);
        if (code == 65 || key.equals("Left")) dirx = -1;
        if (code == 68 || key.equals("Right")) dirx = 1;
        if (key.equals("Space")) launchRocket();
        if (code == 90) launchLaserRocket();
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
