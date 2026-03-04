/**
 * Pacman game demo
 *
 * @author Noah Y.
 * @version v0.6-beta
 */

/*
TODO:
currently tasks:
- [DONE] set direction and (newer) move methods in Ghost class
- [DONE] record currentSteps in Ghost class
- [DONE] add big pellet functionality to make ghosts eatable
- [DONE] change Ghost class to check if consumed when attempting to move
- [DONE] use different speed between uneatable and eatable ghosts

tasks for much later:
- implement different speeds for each separate ghost
- add custom time duration for eatable ghosts
- implement HashMap for custom configuration
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PacmanV3 implements ActionListener, KeyListener {
    // APP VARIABLES
    JFrame frame;
    Container cont;
    JPanel panel, buttons;
    JButton start, debug, pause, endgame;
    PacV3Graphics graphics;

    //int px, py, wx, wy, dirx, diry, boundx, boundy;
    int dirx, diry, wx, wy, boundx, boundy;

    boolean paused, ended;
    boolean ghostEatableState;
    int eatableTimer = 0;

    // CONFIGURATION
    boolean debugMode = false; // presets debug mode
    int ghostScore = 200; // number of score added when eating the blue ghost
    boolean randomPos = false; // [DO NOT ENABLE FOR NOW] if enabled, all pellets and ghosts will have random positions
    int numOfPellets = 100; // amount of pellets in the game
    int numOfPowerPellets = 4;
    int[] startpos = {100, 100}; // starting position of pacman (please have 2 numbers for the x and y axes)
    int pacmanSpeed = 3; // pacman's pixels per frame
    int gameRuntimeStepMS = 16; // amount of milliseconds for each step to occur; lower amounts become faster
    int maxGhostSteps = 50; // amount of steps (pixels) ghost take before changing directions
    int ghostSpeedStep = 2; // ghost pixels per frame
    boolean enableGhostMovement = true; // sets whether if ghosts can move
    int ghostEatTime = 5; // duration (in seconds) of eatable ghosts

    final int numOfGhosts = 4;
    int ghostEatableDuration = (int)((ghostEatTime * 1000) / gameRuntimeStepMS);

    // note: if there are less preset positions than number of pellets,
    // the rest will have random positions instead
    int[][] pelletPositionConfig = new int[][]{};
    // note: each position corresponds to the ghost id;
    // please make every ghost have a preset position
    // because i didn't implement the rest to have random positions (yet)
    int[][] ghostPositionConfig = new int[][]{
        {600, 300},
        {600, 350},
        {600, 400},
        {600, 450},
    };

    Pellet[] pellets;
    Ghost[] ghosts;
    Pacman player;

    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private int randomNumberArray(int[] t) {
        int len = t.length;
        int index = math_random(0, len - 1);
        return t[index];
    }

    int ghostScoreMultiplier = 1;
    private void stopEatableState() {
        ghostEatableState = false;
        eatableTimer = 0;
        ghostScoreMultiplier = 1;
        player.setSpeed(pacmanSpeed);
        for (Ghost ghoul : ghosts) {
            ghoul.setEatable(false);
            ghoul.setSpeed(ghostSpeedStep);
        }
    }
    private void startEatableState() {
        ghostEatableState = true;
        eatableTimer = 0;
        player.setSpeed(pacmanSpeed + 1);
        for (Ghost ghoul : ghosts) {
            ghoul.setEatable(true);
            ghoul.setSpeed(1);
        }
    }

    private void runPacman() {
        Thread looper = new Thread();
        int ms = gameRuntimeStepMS;

        while (ended == false) {
            try { looper.sleep(ms); } catch (InterruptedException e) {}
            if (paused == false) {
                player.moveDirection(dirx, diry);

                if (player.getX() < 0) player.forceSetPosition(0, player.getY());
                if (player.getX() + 40 > boundx) player.forceSetPosition(boundx - 40, player.getY());

                if (player.getY() < 0) player.forceSetPosition(player.getX(), 0);
                if (player.getY() + 40 > boundy) player.forceSetPosition(player.getX(), boundy - 40);

                int px = player.getX();
                int py = player.getY();
                int px2 = px + 40, py2 = py + 40;

                // eatable state
                graphics.getEatTimer(eatableTimer);
                if (ghostEatableState) {
                    if (eatableTimer >= ghostEatableDuration) {
                        stopEatableState();
                    } else eatableTimer += 1;
                }

                // pellet collisions
                for (int id = 0; id < numOfPellets; id++) {
                    Pellet pill = pellets[id];
                    if (!pill.isEaten()) {
                        int bx = pill.getX(), by = pill.getY();

                        int sizeConst = 20;
                        if (pill.powerPellet) sizeConst = 40; // for BIG pellet
                        int bx2 = bx + sizeConst, by2 = by + sizeConst;
                        
                        boolean collided = false;
                        if (px2 > bx && px < bx2) {
                            if (py2 > by && py < by2) {
                                collided = true;
                            }
                        }
                        if (collided) {
                            pill.consume();
                            player.addScore(pill.getScore());

                            if (pill.powerPellet) startEatableState();
                        }
                    }
                }

                // ghost collisions
                int ghostSizeConst = 40;
                for (Ghost ghoul : ghosts) {
                    if (!ghoul.isEaten()) {
                        int bx = ghoul.getX(), by = ghoul.getY();
                        int bx2 = bx + ghostSizeConst, by2 = by + ghostSizeConst;
                        boolean collide = false;
                        if (px2 > bx && px < bx2) {
                            if (py2 > by && py < by2) {
                                collide = true;
                            }
                        }
                        if (collide) {
                            if (ghoul.isEatable) {
                                int newGhostScore = ghostScore * (int)Math.pow(2, ghostScoreMultiplier - 1);
                                ghoul.consume();
                                player.addScore(newGhostScore);
                                ghostScoreMultiplier += 1;
                            } else {
                                graphics.setEnded();
                                ended = true;
                                break;
                            }
                        }
                    }
                }

                // GHOST MOVEMENT LOGIC:

                if (enableGhostMovement) {
                    Ghost ghost1 = ghosts[0];
                    ghost1.move();

                    boolean oobleft1 = (ghost1.getX() < 0);
                    boolean oobright1 = (ghost1.getX() + ghostSizeConst > boundx);
                    boolean oobup1 = (ghost1.getY() + ghostSizeConst > boundy);
                    boolean oobdown1 = (ghost1.getY() + ghostSizeConst > boundy);
                    if (oobleft1) {
                        ghost1.changeDirectionByInt(Ghost.rightDir);
                        ghost1.resetSteps();
                    } else if (oobright1) {
                        ghost1.changeDirectionByInt(Ghost.leftDir);
                        ghost1.resetSteps();
                    } else if (oobup1) {
                        ghost1.changeDirectionByInt(Ghost.downDir);
                        ghost1.resetSteps();
                    } else if (oobdown1) {
                        ghost1.changeDirectionByInt(Ghost.upDir);
                        ghost1.resetSteps();
                    } else {
                        if (ghost1.getCurrentSteps() >= maxGhostSteps) {
                            if (ghost1.isEatable) {
                                ghost1.toFleeingDirection(px, py);
                                ghost1.resetSteps();
                            } else {
                                int direction1 = ghost1.getDirection();
                                if (direction1 == Ghost.leftDir) ghost1.changeDirectionByInt(Ghost.rightDir);
                                else ghost1.changeDirectionByInt(Ghost.leftDir);
                                ghost1.resetSteps();
                            }
                        }
                    }

                    Ghost ghost2 = ghosts[1];
                    ghost2.move();
                    
                    boolean oobleft2 = (ghost2.getX() < 0);
                    boolean oobright2 = (ghost2.getX() + ghostSizeConst > boundx);
                    boolean oobup2 = (ghost2.getY() < 0);
                    boolean oobdown2 = (ghost2.getY() + ghostSizeConst > boundy);
                    if (oobleft2) {
                        int[] _newdir = {Ghost.rightDir,Ghost.upDir,Ghost.downDir};
                        ghost2.changeDirectionByInt(randomNumberArray(_newdir));
                        ghost2.resetSteps();
                    } else if (oobright2) {
                        int[] _newdir = {Ghost.leftDir,Ghost.upDir,Ghost.downDir};
                        ghost2.changeDirectionByInt(randomNumberArray(_newdir));
                        ghost2.resetSteps();
                    } else if (oobup2) {
                        int[] _newdir = {Ghost.leftDir,Ghost.rightDir,Ghost.downDir};
                        ghost2.changeDirectionByInt(randomNumberArray(_newdir));
                        ghost2.resetSteps();
                    } else if (oobdown2) {
                        int[] _newdir = {Ghost.leftDir,Ghost.rightDir,Ghost.upDir};
                        ghost2.changeDirectionByInt(randomNumberArray(_newdir));
                        ghost2.resetSteps();
                    } else {
                        if (ghost2.getCurrentSteps() >= maxGhostSteps) {
                            if (ghost2.isEatable) {
                                ghost2.toFleeingDirection(px, py);
                                ghost2.resetSteps();
                            } else {
                                ghost2.toRandomDirectionV2();
                                ghost2.resetSteps();
                            }
                        }
                    }

                    Ghost ghost3 = ghosts[2];
                    ghost3.move();

                    boolean oobleft3 = (ghost3.getX() < 0);
                    boolean oobright3 = (ghost3.getX() + ghostSizeConst > boundx);
                    boolean oobup3 = (ghost3.getY() < 0);
                    boolean oobdown3 = (ghost3.getY() + ghostSizeConst > boundy);
                    if (oobup3) {
                        ghost3.changeDirectionByInt(Ghost.downDir);
                        ghost3.resetSteps();
                    } else if (oobdown3) {
                        ghost3.changeDirectionByInt(Ghost.upDir);
                        ghost3.resetSteps();
                    }  else if (oobleft3) {
                        ghost3.changeDirectionByInt(Ghost.rightDir);
                        ghost3.resetSteps();
                    }  else if (oobright3) {
                        ghost3.changeDirectionByInt(Ghost.leftDir);
                        ghost3.resetSteps();
                    } else {
                        if (ghost3.getCurrentSteps() >= maxGhostSteps) {
                            int direction3 = ghost3.getDirection();
                            if (direction3 == Ghost.upDir) ghost3.changeDirectionByInt(Ghost.downDir);
                            else ghost3.changeDirectionByInt(Ghost.upDir);
                            ghost3.resetSteps();
                        }
                    }

                    Ghost ghost4 = ghosts[3];
                    ghost4.move();
                    
                    boolean oobleft4 = (ghost4.getX() < 0);
                    boolean oobright4 = (ghost4.getX() + ghostSizeConst > boundx);
                    boolean oobup4 = (ghost4.getY() < 0);
                    boolean oobdown4 = (ghost4.getY() + ghostSizeConst > boundy);
                    if (oobleft4) {
                        ghost4.changeDirectionByInt(Ghost.rightDir);
                        ghost4.resetSteps();
                    } else if (oobright4) {
                        ghost4.changeDirectionByInt(Ghost.leftDir);
                        ghost4.resetSteps();
                    } else if (oobup4) {
                        ghost4.changeDirectionByInt(Ghost.downDir);
                        ghost4.resetSteps();
                    } else if (oobdown4) {
                        ghost4.changeDirectionByInt(Ghost.upDir);
                        ghost4.resetSteps();
                    } else {
                        if (ghost4.getCurrentSteps() >= maxGhostSteps) {
                            if (ghost4.isEatable) {
                                ghost4.toFleeingDirection(px, py);
                                ghost4.resetSteps();
                            } else {
                                ghost4.toChasingDirection(px, py);
                                ghost4.resetSteps();
                            }
                        }
                    }
                }

                graphics.repaint();
            }
        }
    }

    public PacmanV3() {
        dirx = 0; diry = 0;
        paused = true; ended = false;
        wx = 800; wy = 800;
        //px = startpos[0]; py = startpos[1];
        ghostEatableState = false;

        player = new Pacman(startpos[0], startpos[1]);
        player.setSpeed(pacmanSpeed);

        // pellet setup v2
        pellets = new Pellet[numOfPellets];
        for (int _i = 0; _i < numOfPellets; _i++) {
            if (randomPos == false) {
                if (_i + 1 <= pelletPositionConfig.length) {
                    int[] pos = pelletPositionConfig[_i];
                    pellets[_i] = new Pellet(pos[0], pos[1]);
                } else {
                    int rx = math_random(10, 400), ry = math_random(10, 700);
                    pellets[_i] = new Pellet(rx, ry);
                }
            } else {
                int rx = math_random(10, 500), ry = math_random(10, 700);
                pellets[_i] = new Pellet(rx, ry);
            }
        }

        // power pellet capping
        if (numOfPowerPellets > numOfPellets) numOfPowerPellets = numOfPellets;

        boolean[] selectedPowerPills = new boolean[numOfPellets];
        for (int _x = 0; _x > selectedPowerPills.length; _x++) {
            selectedPowerPills[_x] = false;
        }
        for (int _i = 0; _i < numOfPowerPellets; _i++) {
            int _n;
            while (true) { 
                _n = math_random(1, numOfPellets) - 1;
                if (selectedPowerPills[_n] == false && pellets[_n].powerPellet == false) {
                    selectedPowerPills[_n] = true;
                    pellets[_n].powerPellet = true;
                    break;
                }
            }
        }

        // ghost setup v2

        ghosts = new Ghost[numOfGhosts];
        for (int _i = 0; _i < ghosts.length; _i++) {
            if (randomPos == false) {
                int[] pos = ghostPositionConfig[_i];
                ghosts[_i] = new Ghost(pos[0], pos[1]);
            } else {
                int rx = math_random(50, 300), ry = math_random(50, 300);
                ghosts[_i] = new Ghost(rx, ry);
            }
            ghosts[_i].setSpeed(ghostSpeedStep);
        }

        frame = new JFrame("Pacman (Demo Build)");
        frame.setBounds(20, 20, wx, wy);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cont = frame.getContentPane();

        graphics = new PacV3Graphics();
        graphics.addKeyListener(this);
        graphics.toggleDebug(debugMode);
        graphics.setPause(paused);
        graphics.setPacman(player);

        start = new JButton("Start");
        start.addActionListener(this);
        debug = new JButton("Toggle Debug");
        debug.addActionListener(this);
        debug.setForeground(new Color(0, 0, 255));
        pause = new JButton("Pause");
        pause.addActionListener(this);
        endgame = new JButton("End");
        endgame.addActionListener(this);

        buttons = new JPanel();
        buttons.add(start);
        buttons.add(debug);
        buttons.add(pause);
        buttons.add(endgame);

        panel = new JPanel();
        panel.setSize(wx, wy);
        panel.setLayout(new BorderLayout());
        panel.add(graphics, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        cont.add(panel);
        frame.show();

        boundx = graphics.getBounds().width;
        boundy = graphics.getBounds().height;

        graphics.setPellets(pellets);
        graphics.setGhosts(ghosts);

        graphics.setMaxGhostSteps(maxGhostSteps);

        runPacman();
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        if (button == start) {
            paused = false;
            graphics.setPause(false);
            graphics.requestFocus();
        } else if (button == debug) {
            debugMode = (!debugMode);
            graphics.toggleDebug(debugMode);
            graphics.requestFocus();
        } else if (button == pause) {
            paused = true;
            graphics.setPause(true);
            graphics.repaint();
        } else if (button == endgame) {
            if (ended == false) {
                graphics.setEnded();
                graphics.repaint();
                ended = true;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) diry = 0;
        else if (code == KeyEvent.VK_S || code == KeyEvent.VK_W || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP) dirx = 0;
        
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) dirx = -1;
        else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) dirx = 1;
        else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) diry = 1;
        else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) diry = -1;
    }
}
