/**
 * Pacman game demo
 *
 * @author Noah Y.
 * @version v0.15-beta
 */

/*
TODO:
completed tasks:
- add pacman current direction and queued direction to graphics
- change ghosts to go random directions (and on grid)
- add custom time duration for eatable ghosts
- replace some arrays with ArrayList
- (optimization) run pellet collision checks only within a certain distance from pacman
- draw keybind list when game is paused
- remove marked @Deprecated (unused) methods in classes
- subdivide game code with methods
- convert int speeds to doubles
- implement levels
- make pacman move valid directions freely from gridded positions
- create `SharedUtils` class for "shared" functions to use from other classes

current tasks:
- implement music and sounds
    - create start game cooldown & extend level cooldown
- create level cooldown timer in graphics
    - add note that ghosts are faster than you under timer
- add note in graphics when ending game with keybind (Y); double-click will end game
    - threshold should be 2-3 sec

tasks for much later:
- use MemoryRecoder class for memory debug
- implement HashMap for custom configuration
- organize configuration by appropriate sections
*/

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class PacmanV3 implements ActionListener, KeyListener {
    // App components
    JFrame frame;
    Container cont;
    JPanel panel, buttons;
    JButton start, debug, pause, endgame;
    PacV3Graphics graphics;

    // GAME CONFIGURATION
    boolean debugMode = false; // presets debug mode
    int ghostScore = 200; // number of score added when eating the blue ghost
    boolean randomPos = false; // [DO NOT ENABLE FOR NOW] if enabled, all pellets and ghosts will have random positions
    int[] startpos = {370, 610}; // starting position of pacman (please have 2 numbers for the x and y axes)
    double pacmanSpeed = 2.8; // pacman's pixels per frame
    int gameRuntimeStepMS = 16; // amount of milliseconds for each step to occur; lower amounts become faster
    double ghostSpeedStep = 2.0; // ghost pixels per frame
    boolean enableGhostMovement = true; // sets whether if ghosts can move
    int ghostEatTime = 5; // duration (in seconds) of eatable ghosts
    boolean ignoreWindowOutOfBounds = true; // if enabled, pacman will be able to move outside of window
    boolean griddedDirectionChange = true;
    boolean allowGhostCollisions = true;
    int pelletDistThreshold = 150;
    int ghostDistThreshold = 200;

    // Level configuration
    double ghostSpeedIncrease = 0.2; // ghost speed increase after next level
    int maxLives = 3; // number of pacman lives
    int nextLevelDelay = 5; // wait duration for next level (in seconds)
    int gameResetDelay = 3;
    
    // Internal variables
    int wx, wy, boundx, boundy;
    boolean paused, ended;
    boolean ghostEatableState;
    int eatableTimer = 0;
    boolean levelCooldown = false;
    int levelCooldownTimer = 0;
    int levelCooldownThreshold = 0;
    int currentLevel = 0;
    double newGhostSpeed = (ghostSpeedIncrease * currentLevel) + ghostSpeedStep;

    // Constants
    final int numOfGhosts = 4;
    final int ghostEatableDuration = SharedUtils.convertToSteps(ghostEatTime, gameRuntimeStepMS);
    final int[] ghostStartPosition = {370, 50};
    final int levelCooldownDuration = SharedUtils.convertToSteps(nextLevelDelay, gameRuntimeStepMS);
    final int gameResetDuration = SharedUtils.convertToSteps(gameResetDelay, gameRuntimeStepMS);

    // Game map and entities
    ArrayList<Pellet> pellets;
    ArrayList<Ghost> ghosts;
    Pacman player;
    ArrayList<GameBorder> borders;

    // Sound objects
    Clip beginClip, chompClip, deathClip, eatghostClip, intermissionClip;
    ArrayList<Clip> soundClips;

    // Border configuration
    // game border format -> {xpos, ypos, xsize, ysize}

    int[][] externalBorderConfig = new int[][]{
        {50, 50, 600, 600},
    };

    int[][] internalBorderConfig = new int[][]{
        {90, 90, 120, 80},
        {250, 90, 120, 80},
        {410, 90, 80, 240},
        {530, 90, 80, 240},
        {410, 370, 80, 240},
        {530, 370, 80, 240},
        {90, 210, 280, 40},
        {90, 290, 280, 40},
        {90, 370, 80, 160},
        {90, 570, 280, 40},
        {210, 370, 160, 80},
        {210, 490, 160, 40},
    };

    // Grid points for pacman/ghost movement
    int[][] griddedPos = new int[][]{
        {50, 50}, {50, 170}, {50, 250}, {50, 330}, {50, 530}, {50, 610},
        {210, 50}, {210, 170},
        {170, 330}, {170, 450}, {170, 530},
        {370, 50}, {370, 170}, {370, 250}, {370, 330}, {370, 450}, {370, 530}, {370, 610},
        {490, 50}, {490, 330}, {490, 610},
        {610, 50}, {610, 330}, {610, 610},
    };

    private int math_random(int min, int max) {
        return SharedUtils.randomNumber(min, max);
    }

    /**
     * Gets the rounded distance between two points
     */
    private int getDistance(int x1, int y1, int x2, int y2) {
        return SharedUtils.getDistance(x1, y1, x2, y2);
    }

    // Ghost eatable state methods

    int ghostScoreMultiplier = 1;
    private void stopEatableState() {
        ghostEatableState = false;
        eatableTimer = 0;
        ghostScoreMultiplier = 1;
        player.setSpeed(pacmanSpeed);
        for (Ghost ghoul : ghosts) {
            ghoul.setEatable(false);
            ghoul.setSpeed(newGhostSpeed);
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

    // Pellet configuration

    int[][] powerPelletConfig = {
        {57, 57},
        {614, 57},
        {57, 614},
        {614, 614},
    };

    // ref: {amount, start x, start y, axis (0 - x, 1 - y)}
    int[][] pelletConfig = {
        {13, 100, 60, 0},
        {13, 60, 100, 1},
        {13, 620, 100, 1},
        {7, 100, 620, 0},
        {5, 420, 620, 0},
        {7, 100, 180, 0},
        {7, 100, 260, 0},
        {7, 100, 340, 0},
        {7, 100, 540, 0},
        {5, 420, 340, 0},
        {4, 220, 460, 0},
        {2, 220, 100, 1},
        {4, 180, 380, 1},
        {6, 500, 380, 1},
        {6, 500, 100, 1},
        {13, 380, 100, 1},
    };

    // setup methods

    private void setupPellets() {
        pellets = new ArrayList<Pellet>();

        for (int[] ppos : pelletConfig) {
            int xmul = 0, ymul = 0;
            if (ppos[3] == 0) xmul = 40;
            else if (ppos[3] == 1) ymul = 40;
            for (int i = 0; i < ppos[0]; i++) {
                Pellet pill = new Pellet(
                    xmul * i + ppos[1],
                    ymul * i + ppos[2]
                );
                pellets.add(pill);
            }
        }

        for (int[] powpos : powerPelletConfig) {
            Pellet powerpill = new Pellet(powpos[0], powpos[1]);
            powerpill.powerPellet = true;
            pellets.add(powerpill);
        }
    }

    private void setupBorders() {
        borders = new ArrayList<GameBorder>();

        for (int[] c : externalBorderConfig) {
            GameBorder externalBd = new GameBorder(c[0], c[1], c[2], c[3]);
            borders.add(externalBd);
        }
        for (int[] c : internalBorderConfig) {
            GameBorder internalBd = new GameBorder(c[0], c[1], c[2], c[3]);
            internalBd.internal = true;
            borders.add(internalBd);
        }
    }

    private void setupGhosts() {
        // Setting up ghosts
        ghosts = new ArrayList<Ghost>();
        for (int _i = 0; _i < numOfGhosts; _i++) {
            Ghost ghoul;
            if (randomPos == false) {
                ghoul = new Ghost(ghostStartPosition[0], ghostStartPosition[1]);
            } else {
                int rx = math_random(50, 300), ry = math_random(50, 300);
                ghoul = new Ghost(rx, ry);
            }
            ghoul.setSpeed(newGhostSpeed);
            ghoul.setBorderTable(borders);
            ghoul.setGridPos(griddedPos);
            ghosts.add(ghoul);
        }
    }

    private void setupPacman() {
        player = new Pacman(startpos[0], startpos[1]);
        player.setSpeed(pacmanSpeed);
        player.setLives(maxLives);
    }

    private void initVars() {
        paused = true; ended = false;
        wx = 800; wy = 800;
        ghostEatableState = false;
    }

    private void setComponents() {
        graphics.setPellets(pellets);
        graphics.setGhosts(ghosts);

        graphics.setGameBorders(borders);
        player.setBorderTable(borders);

        player.setGrided(griddedDirectionChange);
        player.setGridPos(griddedPos);

        graphics.setGridPos(griddedPos);

        graphics.setLevel(currentLevel);
    }

    private void loadSoundClips() {
        try {
            beginClip = AudioSystem.getClip();
            chompClip = AudioSystem.getClip();
            deathClip = AudioSystem.getClip();
            eatghostClip = AudioSystem.getClip();
            intermissionClip = AudioSystem.getClip();

            soundClips = new ArrayList<Clip>();
            soundClips.add(beginClip);
            soundClips.add(chompClip);
            soundClips.add(deathClip);
            soundClips.add(eatghostClip);
            soundClips.add(intermissionClip);

            AudioInputStream _beginstr = AudioSystem.getAudioInputStream(new File("pacman_beginning.wav"));
            beginClip.open(_beginstr);

            AudioInputStream _chompstr = AudioSystem.getAudioInputStream(new File("pacman_chomp.wav"));
            chompClip.open(_chompstr);

            AudioInputStream _deathstr = AudioSystem.getAudioInputStream(new File("pacman_death.wav"));
            deathClip.open(_deathstr);
            
            AudioInputStream _eatgstr = AudioSystem.getAudioInputStream(new File("pacman_eatghost.wav"));
            eatghostClip.open(_eatgstr);

            AudioInputStream _interstr = AudioSystem.getAudioInputStream(new File("pacman_intermission.wav"));
            intermissionClip.open(_interstr);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Error with playing sound.");
        }
    }

    private void stopSounds() {
        for (Clip sound : soundClips) {
            sound.stop();
            sound.setMicrosecondPosition(0);
        }
    }

    // game leveling

    private void resetGhostSpeed() {
        newGhostSpeed = (ghostSpeedIncrease * currentLevel) + ghostSpeedStep;
        for (Ghost ghoul : ghosts) {
            ghoul.setSpeed(newGhostSpeed);
        }
    }

    private void resetPellets() {
        for (Pellet pill : pellets) {
            pill.reset();
        }
    }
    private void resetGhosts() {
        for (Ghost ghoul : ghosts) {
            ghoul.reset();
            ghoul.forceSetPosition(ghostStartPosition[0], ghostStartPosition[1]);
        }
    }
    private void resetPlayer() {
        stopEatableState();
        player.resetDirection();
        player.forceSetPosition(startpos[0], startpos[1]);
    }

    private void enableLevelCooldown(int threshold) {
        levelCooldown = true;
        levelCooldownThreshold = threshold;
        levelCooldownTimer = 0;
    }

    /**
     * Resets game into next level (if all pellets are consumed)
     */
    private void nextLevel() {
        resetPellets();
        resetGhosts();
        resetPlayer();

        stopSounds();
        intermissionClip.start();

        currentLevel += 1;
        graphics.setLevel(currentLevel);
        resetGhostSpeed();
        enableLevelCooldown(nextLevelDelay);
    }

    /**
     * Resets game without any level changes (if pacman collides to uneatable ghost)
     */
    private void resetGame() {
        resetPellets();
        resetGhosts();
        resetPlayer();

        stopSounds();
        deathClip.start();

        enableLevelCooldown(gameResetDelay);
    }

    // collision methods

    private void checkGhostCollisions(int px, int py, int px2, int py2, int centerX, int centerY) {
        for (Ghost ghoul : ghosts) {
            if (!ghoul.isEaten()) {
                int ghostSizeConst = ghoul.ghostSizeConst;
                int bx = ghoul.getX(), by = ghoul.getY();
                int cbx = bx + (ghostSizeConst / 2), cby = by + (ghostSizeConst / 2);

                // Only check collisions within certain distance
                int _distance = getDistance(centerX, centerY, cbx, cby);
                if (_distance <= ghostDistThreshold) {
                    ghoul._internalActive = true;
                    int bx2 = bx + ghostSizeConst, by2 = by + ghostSizeConst;
                    boolean collide = false;
                    if (px2 > bx && px < bx2) {
                        if (py2 > by && py < by2) {
                            collide = true;
                        }
                    }
                    if (collide) {
                        if (ghoul.isEatable) {
                            eatghostClip.start();
                            int newGhostScore = ghostScore * (int)Math.pow(2, ghostScoreMultiplier - 1);
                            ghoul.consume();
                            player.addScore(newGhostScore);
                            ghostScoreMultiplier += 1;
                        } else {
                            player.removeLive();
                            if (player.getLives() <= 0) {
                                stop();
                            } else {
                                resetGame();
                            }
                            break;
                        }
                    }
                } else ghoul._internalActive = false;
            }
        }
    }

    private void checkPelletCollisions(int px, int py, int px2, int py2, int centerX, int centerY) {
        for (Pellet pill : pellets) {
            if (pill != null && !pill.isEaten()) {
                int bx = pill.getX(), by = pill.getY();

                int sizeConst = 20;
                if (pill.powerPellet) sizeConst = 30; // for BIG pellet
                int cbx = bx + (sizeConst / 2), cby = by + (sizeConst / 2);

                // Only check collisions within certain distance
                int _distance = getDistance(centerX, centerY, cbx, cby);
                if (_distance <= pelletDistThreshold) {
                    pill._internalActive = true;
                    int bx2 = bx + sizeConst, by2 = by + sizeConst;

                    boolean collided = false;
                    if (px2 > bx && px < bx2) {
                        if (py2 > by && py < by2) {
                            collided = true;
                        }
                    }

                    if (collided) {
                        chompClip.start();
                        pill.consume();
                        player.addScore(pill.getScore());
                        if (pill.powerPellet) startEatableState();
                    }
                } else pill._internalActive = false;
            }
        }
    }

    private void stepEatTimer() {
        graphics.getEatTimer(eatableTimer);
        if (ghostEatableState) {
            if (eatableTimer >= ghostEatableDuration) {
                stopEatableState();
            } else eatableTimer += 1;
        }
    }

    private void stepLevelCooldown() {
        // graphics.setLevelCooldown(levelCooldown, levelCooldownTimer);
        if (levelCooldown) {
            int totalSteps = SharedUtils.convertToSteps(levelCooldownThreshold, gameRuntimeStepMS);
            if (levelCooldownTimer >= totalSteps) {
                levelCooldown = false;
            } else levelCooldownTimer += 1;
        }
    }

    private boolean checkPillsEaten() {
        boolean allEaten = false;

        int pills = 0;
        for (Pellet pill : pellets) {
            if (pill.isEaten()) pills += 1;
        }

        if (pills >= pellets.size()) allEaten = true;

        return allEaten;
    }

    private void queueDirection(int xdir, int ydir) {
        if (!paused && !levelCooldown) player.queueDirection(xdir, ydir);
    }

    private void runPacman() {
        Thread looper = new Thread();
        beginClip.start();

        while (ended == false) {
            try { looper.sleep(gameRuntimeStepMS); } catch (InterruptedException e) {}
            if (paused == false) {
                if (levelCooldown == false) player.move();

                int pacmanSize = player.sizeConst;

                // game window size checks
                if (!ignoreWindowOutOfBounds) {
                    if (player.getX() < 0) player.forceSetPosition(0, player.getY());
                    if (player.getX() + pacmanSize > boundx) player.forceSetPosition(boundx - pacmanSize, player.getY());

                    if (player.getY() < 0) player.forceSetPosition(player.getX(), 0);
                    if (player.getY() + pacmanSize > boundy) player.forceSetPosition(player.getX(), boundy - pacmanSize);
                }

                int px = player.getX(), py = player.getY();
                int px2 = px + pacmanSize, py2 = py + pacmanSize;
                int centerX = px + (pacmanSize / 2), centerY = py + (pacmanSize / 2);

                // audio loop resets
                for (Clip sound : soundClips) {
                    if (sound.getMicrosecondPosition() >= sound.getMicrosecondLength()) {
                        sound.setMicrosecondPosition(0);
                    }
                }

                // Eatable timer logic
                stepEatTimer();

                // Level cooldown
                stepLevelCooldown();

                // Pellet collisions
                checkPelletCollisions(px, py, px2, py2, centerX, centerY);

                // Ghost collisions
                if (allowGhostCollisions) checkGhostCollisions(px, py, px2, py2, centerX, centerY);

                // Ghost movement logic (refer to `Ghost` class)
                if (levelCooldown == false && enableGhostMovement) {
                    for (Ghost ghost : ghosts) {
                        ghost.move();
                    }
                }

                // Next level logic
                if (checkPillsEaten()) nextLevel();

                graphics.repaint();
            }
        }
    }

    public PacmanV3() {
        System.out.println("Due to the introduction of inheritances,\nPacman will receive a complete overhaul for\norganization and extra optimizations.");
        System.out.println("(This also applies to Space Invaders.)");

        // Assigning initialized variables
        initVars();

        // Loading sounds
        loadSoundClips();

        // Setting up pacman map/entities
        setupPacman();
        setupBorders();
        setupPellets();
        setupGhosts();

        // Setting up app layout

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
        endgame.setForeground(new Color(255, 0, 0));

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

        // Setting graphics/pacman components
        setComponents();

        runPacman();
    }

    // Game runtime methods

    private void resume() {
        paused = false;
        graphics.setPause(false);
        graphics.requestFocus();
    }

    private void debugToggle() {
        debugMode = (!debugMode);
        graphics.toggleDebug(debugMode);
        graphics.requestFocus();
    }

    private void pause() {
        paused = true;
        graphics.setPause(true);
        graphics.repaint();
        graphics._setStarted();
    }

    private void pauseToggle() {
        if (paused) resume();
        else pause();
    }

    private void stop() {
        if (ended == false) {
            stopSounds();
            deathClip.start();

            graphics.setEnded();
            graphics.repaint();
            ended = true;
        }
    }

    // App event handlers

    @Override public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        if (button == start) resume();
        else if (button == debug) debugToggle();
        else if (button == pause) pause();
        else if (button == endgame) stop();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) queueDirection(-1, 0);
        else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) queueDirection(1, 0);
        else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) queueDirection(0, 1);
        else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) queueDirection(0, -1);
        else if (code == KeyEvent.VK_ESCAPE) pauseToggle();
        else if (code == KeyEvent.VK_T) debugToggle();
        else if (code == KeyEvent.VK_Y) stop();
    }
}
