/**
 * Pacman game demo
 *
 * @author Noah Y.
 * @version 3.0
 */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import javax.swing.*;

public class PacmanV3 implements ActionListener, KeyListener {
    // APP VARIABLES
    JFrame frame;
    Container cont;
    JPanel panel, buttons;
    JButton start, debug, pause, endgame;
    PacV3Graphics graphics;

    // TODO: convert variables into arrays in unit 6 (i assume)
    int px, py, wx, wy, dirx, diry, boundx, boundy;

    boolean paused, ended, blueGhostConsumed;

    /*
        Note:
        I'm using arrays in unit 5 (even though they'll be introduced in unit 6)
        because I don't want to repeatedly use variables
        and they're too tedious to do so.
        Just a heads up...
    */

    // CONFIGURATION
    boolean debugMode = false; // presets debug mode
    int normalScore = 10; // number of score added when eating an orb
    int extraScore = 50; // number of score added when eating a big orb
    int ghostScore = 200; // number of score added when eating the blue ghost
    boolean randomPos = false; // [DO NOT ENABLE FOR NOW] if enabled, all pellets and ghosts will have random positions
    int pellets = 5; // amount of pellets in the game
    int[] startpos = {100, 200}; // starting position of pacman (please have 2 numbers for the x and y axes)
    int pacmanSpeed = 2; // pacman's pixels per frame
    int gameRuntimeStepMS = 8; // amount of milliseconds for each step to occur; lower amounts become faster
    int ghostSteps = 50; // amount of steps (pixels) ghost take before changing directions
    int ghostSpeed = 2; // ghosts' pixels per frame
    boolean enableGhostMovement = true; // sets whether if ghosts can move

    // note: if there are less preset positions than number of pellets,
    // the rest will have random positions instead
    int[][] pelletPositionConfig = new int[][]{
        {50, 50},
        {100, 50},
        {50, 100},
        {100, 100},
    };
    // note: each position corresponds to the ghost id;
    // please make every ghost have a preset position
    // because i didn't implement the rest to have random positions (yet)
    int[][] ghostPositionConfig = new int[][]{
        {300, 100},
        {400, 300},
        {300, 350},
        {350, 400},
        {500, 500},
    };

    int[][] oldpositions = new int[5][2];
    int[][] predictpositions = new int[5][2];
    int[] directions = new int[]{
        1,
        1,
        1,
        1,
        1,
    };

    int[][] pelletpos, ghostpos;
    boolean[] collected;

    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private int randomNumberArray(int[] t) {
        int len = t.length;
        int index = math_random(0, len - 1);
        return t[index];
    }

    private int chasePacmanDirection(int x, int y) {
        int dir = 1;
        int _rng = math_random(1, 2);
        if (_rng == 1) {
            if (x < px) dir = 2;
            else if (x > px) dir = 1;
        } else {
            if (y < py) dir = 4;
            else if (y > py) dir = 3;
        }
        return dir;
    }

    private int fleePacmanDirection(int x, int y) {
        int dir = 0;
        int _rng = math_random(1, 2);
        if (_rng == 1) {
            if (x < px) dir = 1;
            else if (x > px) dir = 2;
        } else {
            if (y < py) dir = 3;
            else if (y > py) dir = 4;
        }
        return dir;
    }

    private void runPacman() {
        Thread looper = new Thread();
        int ms = gameRuntimeStepMS;

        while (ended == false) {
            try { looper.sleep(ms); } catch (InterruptedException e) {}
            if (paused == false) {
                int dx1 = dirx * pacmanSpeed;
                int dy1 = diry * pacmanSpeed;
                px = px + dx1;
                py = py + dy1;

                if (px < 0) px = 0;
                if (px + 40 > boundx) px = boundx - 40;

                if (py < 0) py = 0;
                if (py + 40 > boundy) py = boundy - 40;

                graphics.movePacman(px, py);
                //graphics.setDirection(dirx, diry);

                int px2 = px + 40, py2 = py + 40;

                // pellet checker
                
                // (Obj1.Right > Obj2.Left AND Obj1.Left < Obj2.Right)
                // AND (Obj1.Bottom > Obj2.Top AND Obj1.Top < Obj2.Bottom)
                for (int id = 0; id < pellets; id++) {
                    if (collected[id] == false) {
                        int[] pos = pelletpos[id];
                        int bx = pos[0], by = pos[1];

                        int sizeConst = 20;
                        if (id == 1) sizeConst = 40; // for BIG pellet
                        int bx2 = bx + sizeConst, by2 = by + sizeConst;
                        
                        boolean collide = false;
                        if (px2 > bx && px < bx2) {
                            if (py2 > by && py < by2) {
                                collide = true;
                            }
                        }
                        if (collide) {
                            collected[id] = true;
                            graphics.collectPellet(id);
                            if (id == 1) graphics.addScore(extraScore);
                            else graphics.addScore(normalScore);
                            break;
                        }
                    }
                }

                // ghost checker (kills you on collision)
                // excluding blue ghost because we are gonna eat him up :)
                int ghostSizeConst = 40;
                for (int id = 0; id < 4; id++) {
                    int[] pos = ghostpos[id];
                    int bx = pos[0], by = pos[1];
                    int bx2 = bx + ghostSizeConst, by2 = by + ghostSizeConst;

                    boolean collide = false;
                    if (px2 > bx && px < bx2) {
                        if (py2 > by && py < by2) {
                            collide = true;
                        }
                    }

                    if (collide) {
                        graphics.setEnded();
                        ended = true;
                        break;
                    }
                }

                // 5th ghost (consumable; gives us 200 points)
                if (blueGhostConsumed == false) {
                    int[] eatingpos = ghostpos[4];
                    int ex = eatingpos[0], ey = eatingpos[1];
                    int ex2 = ex + ghostSizeConst, ey2 = ey + ghostSizeConst;

                    boolean eaten = false;
                    if (px2 > ex && px < ex2) {
                        if (py2 > ey && py < ey2) {
                            eaten = true;
                        }
                    }

                    if (eaten && blueGhostConsumed == false) {
                        blueGhostConsumed = true;
                        graphics.eatGhost();
                        graphics.addScore(ghostScore);
                    }
                }

                // GHOST MOVEMENT LOGIC:

                if (enableGhostMovement) {
                    // ghost 1 - yellow - moves left and right
                    int _gid1 = 0;
                    if (directions[_gid1] == 1) {
                        ghostpos[_gid1][0] -= ghostSpeed;
                    } else if (directions[_gid1] == 2) {
                        ghostpos[_gid1][0] += ghostSpeed;
                    }
                    boolean oobleft1 = (ghostpos[_gid1][0] < 0);
                    boolean oobright1 = (ghostpos[_gid1][0] + ghostSizeConst > boundx);
                    if (oobleft1) {
                        directions[_gid1] = 2;
                        // for some reason, changing an array to another will always change to that
                        // so cloning it will not constantly change it, i dont know why java is like this :/
                        oldpositions[_gid1] = ghostpos[_gid1].clone();
                    } else if (oobright1) {
                        directions[_gid1] = 1;
                        oldpositions[_gid1] = ghostpos[_gid1].clone();
                    } else {
                        int dist1 = Math.abs(ghostpos[_gid1][0] - oldpositions[_gid1][0]);
                        if (dist1 >= ghostSteps) {
                            if (directions[_gid1] == 1) directions[_gid1] = 2;
                            else directions[_gid1] = 1;
                            oldpositions[_gid1] = ghostpos[_gid1].clone();
                        }
                    }

                    // ghost 2 - green - moves randomly in left-right or up-down
                    int _gid2 = 1;
                    if (directions[1] == 1) {
                        ghostpos[_gid2][0] -= ghostSpeed;
                    } else if (directions[_gid2] == 2) {
                        ghostpos[_gid2][0] += ghostSpeed;
                    } else if (directions[_gid2] == 3) {
                        ghostpos[_gid2][1] -= ghostSpeed;
                    } else if (directions[_gid2] == 4) {
                        ghostpos[_gid2][1] += ghostSpeed;
                    }
                    boolean oobleft2 = (ghostpos[_gid2][0] < 0);
                    boolean oobright2 = (ghostpos[_gid2][0] + ghostSizeConst > boundx);
                    boolean oobup2 = (ghostpos[_gid2][1] < 0);
                    boolean oobdown2 = (ghostpos[_gid2][1] + ghostSizeConst > boundy);
                    if (oobleft2) {
                        int[] newdir = {2,3,4};
                        directions[_gid2] = randomNumberArray(newdir);
                        oldpositions[_gid2] = ghostpos[_gid2].clone();
                    } else if (oobright2) {
                        int[] newdir = {1,3,4};
                        directions[_gid2] = randomNumberArray(newdir);
                        oldpositions[_gid2] = ghostpos[_gid2].clone();
                    } else if (oobup2) {
                        int[] newdir = {1,2,4};
                        directions[_gid2] = randomNumberArray(newdir);
                        oldpositions[_gid2] = ghostpos[_gid2].clone();
                    } else if (oobdown2) {
                        int[] newdir = {1,2,3};
                        directions[_gid2] = randomNumberArray(newdir);
                        oldpositions[_gid2] = ghostpos[_gid2].clone();
                    } else {
                        int dist2x = Math.abs(ghostpos[_gid2][0] - oldpositions[_gid2][0]);
                        int dist2y = Math.abs(ghostpos[_gid2][1] - oldpositions[_gid2][1]);
                        if (dist2x >= ghostSteps || dist2y >= ghostSteps) {
                            int[] newdir = new int[3];
                            int _id = 0;
                            for (int i = 0; i < 4; i++) {
                                int _i = i + 1;
                                if (_i != directions[_gid2]) {
                                    Array.setInt(newdir, _id, _i);
                                    _id++;
                                }
                            }
                            directions[_gid2] = randomNumberArray(newdir);
                            oldpositions[_gid2] = ghostpos[_gid2].clone();
                        }
                    }

                    // ghost 3 - pink - moves up and down
                    int _gid3 = 2;
                    if (directions[_gid3] == 1) {
                        ghostpos[_gid3][1] -= ghostSpeed;
                    } else if (directions[_gid3] == 2) {
                        ghostpos[_gid3][1] += ghostSpeed;
                    }
                    boolean oobup3 = (ghostpos[_gid3][1] < 0);
                    boolean oobdown3 = (ghostpos[_gid3][1] + ghostSizeConst > boundy);
                    if (oobup3) {
                        directions[_gid3] = 2;
                        oldpositions[_gid3] = ghostpos[_gid3].clone();
                    } else if (oobdown3) {
                        directions[_gid3] = 1;
                        oldpositions[_gid3] = ghostpos[_gid3].clone();
                    } else {
                        int dist1 = Math.abs(ghostpos[_gid3][1] - oldpositions[_gid3][1]);
                        if (dist1 >= ghostSteps) {
                            if (directions[_gid3] == 1) directions[_gid3] = 2;
                            else directions[_gid3] = 1;
                            oldpositions[_gid3] = ghostpos[_gid3].clone();
                        }
                    }

                    // ghost 4 - red - chases pacman
                    int _gid4 = 3;
                    if (directions[_gid4] == 1) {
                        ghostpos[_gid4][0] -= ghostSpeed;
                    } else if (directions[_gid4] == 2) {
                        ghostpos[_gid4][0] += ghostSpeed;
                    } else if (directions[_gid4] == 3) {
                        ghostpos[_gid4][1] -= ghostSpeed;
                    } else if (directions[_gid4] == 4) {
                        ghostpos[_gid4][1] += ghostSpeed;
                    }
                    boolean oobleft4 = (ghostpos[_gid4][0] < 0);
                    boolean oobright4 = (ghostpos[_gid4][0] + ghostSizeConst > boundx);
                    boolean oobup4 = (ghostpos[_gid4][1] < 0);
                    boolean oobdown4 = (ghostpos[_gid4][1] + ghostSizeConst > boundy);
                    if (oobleft4) {
                        directions[_gid4] = 2;
                        oldpositions[_gid4] = ghostpos[_gid4].clone();
                    } else if (oobright4) {
                        directions[_gid4] = 1;
                        oldpositions[_gid4] = ghostpos[_gid4].clone();
                    } else if (oobup4) {
                        directions[_gid4] = 4;
                        oldpositions[_gid4] = ghostpos[_gid4].clone();
                    } else if (oobdown4) {
                        directions[_gid4] = 3;
                        oldpositions[_gid4] = ghostpos[_gid4].clone();
                    } else {
                        int dist4x = Math.abs(ghostpos[_gid4][0] - oldpositions[_gid4][0]);
                        int dist4y = Math.abs(ghostpos[_gid4][1] - oldpositions[_gid4][1]);
                        if (dist4x >= ghostSteps || dist4y >= ghostSteps) {
                            int[] p = ghostpos[_gid4];
                            directions[_gid4] = chasePacmanDirection(p[0], p[1]);
                            oldpositions[_gid4] = ghostpos[_gid4].clone();
                        }
                    }

                    // ghost 5 - blue - runs away from pacman
                    // moves ghost UNLESS he is consumed
                    if (blueGhostConsumed == false) {
                        int _gid5 = 4;
                        if (directions[_gid5] == 1) {
                            ghostpos[_gid5][0] -= ghostSpeed;
                        } else if (directions[_gid5] == 2) {
                            ghostpos[_gid5][0] += ghostSpeed;
                        } else if (directions[_gid5] == 3) {
                            ghostpos[_gid5][1] -= ghostSpeed;
                        } else if (directions[_gid5] == 4) {
                            ghostpos[_gid5][1] += ghostSpeed;
                        }
                        boolean oobleft5 = (ghostpos[_gid5][0] < 0);
                        boolean oobright5 = (ghostpos[_gid5][0] + ghostSizeConst > boundx);
                        boolean oobup5 = (ghostpos[_gid5][1] < 0);
                        boolean oobdown5 = (ghostpos[_gid5][1] + ghostSizeConst > boundy);
                        if (oobleft5) {
                            directions[_gid5] = 2;
                            oldpositions[_gid5] = ghostpos[_gid5].clone();
                        } else if (oobright5) {
                            directions[_gid5] = 1;
                            oldpositions[_gid5] = ghostpos[_gid5].clone();
                        } else if (oobup5) {
                            directions[_gid5] = 4;
                            oldpositions[_gid5] = ghostpos[_gid5].clone();
                        } else if (oobdown5) {
                            directions[_gid5] = 3;
                            oldpositions[_gid5] = ghostpos[_gid5].clone();
                        } else {
                            int dist5x = Math.abs(ghostpos[_gid5][0] - oldpositions[_gid5][0]);
                            int dist5y = Math.abs(ghostpos[_gid5][1] - oldpositions[_gid5][1]);
                            if (dist5x >= ghostSteps || dist5y >= ghostSteps) {
                                int[] p = ghostpos[_gid5];
                                directions[_gid5] = fleePacmanDirection(p[0], p[1]);
                                oldpositions[_gid5] = ghostpos[_gid5].clone();
                            }
                        }
                    }
                }

                graphics.setGhostPositions(ghostpos);
                graphics.repaint();
            }
        }
    }

    public PacmanV3() {
        dirx = 0; diry = 0;
        paused = true; ended = false; blueGhostConsumed = false;
        wx = 800; wy = 800;
        px = startpos[0]; py = startpos[1];

        // pellet setup
        if (pellets < 1) pellets = 1; // prevents erroring
        pelletpos = new int[pellets][2];
        collected = new boolean[pellets];
        for (int _i = 0; _i < pellets; _i++) {
            collected[_i] = false;
            if (randomPos == false) {
                try {
                    int[] pos = pelletPositionConfig[_i];
                    pelletpos[_i][0] = pos[0];
                    pelletpos[_i][1] = pos[1];
                } catch (Exception e) { // if index is out of bounds from array
                    int rx = math_random(50, 500);
                    int ry = math_random(50, 500);
                    pelletpos[_i][0] = rx;
                    pelletpos[_i][1] = ry;
                }
            } else {
                int rx = math_random(50, 500);
                int ry = math_random(50, 500);
                pelletpos[_i][0] = rx;
                pelletpos[_i][1] = ry;
            }
        }

        // ghost setup
        ghostpos = new int[5][2];
        for (int _i = 0; _i < 5; _i++) {
            if (randomPos == false) {
                int[] pos = ghostPositionConfig[_i];
                ghostpos[_i][0] = pos[0];
                ghostpos[_i][1] = pos[1];
            } else {
                int rx = math_random(50, 300);
                int ry = math_random(50, 300);
                ghostpos[_i][0] = rx;
                ghostpos[_i][1] = ry;
            }
            oldpositions[_i][0] = ghostpos[_i][0];
            oldpositions[_i][1] = ghostpos[_i][1];
        }

        frame = new JFrame("Pacman (Demo Build)");
        frame.setBounds(20, 20, wx, wy);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cont = frame.getContentPane();

        graphics = new PacV3Graphics(startpos[0], startpos[1], pellets);
        graphics.addKeyListener(this);
        graphics.toggleDebug(debugMode);
        graphics.setPellets(pellets);
        graphics.setPause(paused);

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

        graphics.setPelletPositions(pelletpos);
        graphics.setGhostPositions(ghostpos);

        runPacman();
    }

    public void actionPerformed(ActionEvent e) {
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
        } else if (button == endgame) {
            if (ended == false) ended = true;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == 65 || code == 68 || code == 37 || code == 39) diry = 0;
        else if (code == 83 || code == 87 || code == 38 || code == 40) dirx = 0;
        
        if (code == 65 || code == 37) dirx = -1;
        else if (code == 68 || code == 39) dirx = 1;
        else if (code == 83 || code == 40) diry = 1;
        else if (code == 87 || code == 38) diry = -1;
    }
}
