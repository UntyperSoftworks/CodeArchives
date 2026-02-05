/**
 * space invaders
 *
 * @author Noah
 * @version v0.5-beta
 */

/*
TODO:
- [DONE] Use arrays for creating multiple rockets (use both main and graphics classes)
- [DONE] Implement array intersection from rockets and aliens
- Alien rockets fire back from a random alien

- Add settings window for ALL configuration below (will do much later on)
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
    int px, centerpx, dirx;
    boolean gameState, ended;
    int boundy;
    int numOfAliens = 10;
    int alienSizeConst = 20;
    int alienRocketSizeConst = 10;
    int playerYConst = 500;
    int playerSizeConst = 40;
    long laserOldTick, rocketOldTick;
    long laserNewTick, rocketNewTick;

    // rocket array data
    int[] rocketX, rocketY;
    int[] laserRocketX, laserRocketY;
    int rocketId, laserRocketId;
    boolean[] activeRockets, activeLaserRockets;

    // alient array data
    boolean[] aliveAliens;
    int[] alienPosX, alienPosY;
    boolean[] activeAlienRockets;
    int[] alienRocketX, alienRocketY;
    int alienRocketId, alienRocketTimer;
    double[] randomAlienShot = {0.3, 1};

    // config
    int playerSpeed = 1;
    int rocketSpeed = 1;
    int laserRocketSpeed = 3;
    int alienRocketSpeed = 1;
    int rocketStartPositionY = playerYConst - 20;
    int maxRockets = 100;
    int maxAlienRockets = 20;
    double rocketCooldown = 0.05;
    double laserRocketCooldown = 0.5;
    int alienKillPoints = 10;

    // keybinds
    int[] moveLeftBinds = {KeyEvent.VK_A, KeyEvent.VK_LEFT};
    int[] moveRightBinds = {KeyEvent.VK_D, KeyEvent.VK_RIGHT};
    int[] rocketBinds = {KeyEvent.VK_SPACE, KeyEvent.VK_NUMPAD2, KeyEvent.VK_COMMA};
    int[] laserRocketBinds = {KeyEvent.VK_Z, KeyEvent.VK_NUMPAD3, KeyEvent.VK_PERIOD};
    int[] gameEndBinds = {KeyEvent.VK_T};
    int[] toggleGamePauseBinds = {KeyEvent.VK_R};

    // fun features
    // this is only intended for dev purposes
    boolean autoFireRockets = false;
    boolean autoFireLaserRockets = false;
    
    // debug config
    boolean showRocketAim = true;
    boolean showRocketTrajectory = true;
    boolean showAlienHitbox = true;
    boolean showAlienTrajectory = false;
    boolean debugPrinting = false;

    // integer
    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
    // double
    private double math_random(double min, double max) {
        double range = (max - min) + 1;
        return (double)(Math.random() * range) + min;
    }

    private int randomIntFromArray(int[] t) {
        int _n = math_random(0, t.length - 1);
        return t[_n];
    }

    private long getTick() {
        return System.currentTimeMillis();
    }
    private long toMilliseconds(double seconds) {
        return (long)(seconds * 1000);
    }
    private boolean intArray_find(int[] t, int value) {
        boolean state = false;
        if (t.length > 0) {
            for (int i = 0; i < t.length; i++) {
                int v = t[i];
                if (v == value) {
                    state = true;
                    break;
                }
            }
        }
        return state;
    }
    private int getAlienRocketTimer(double ms) {
        double _min = randomAlienShot[0], _max = randomAlienShot[1];
        return (int)(math_random(_min, _max) / (ms / 1000));
    }
    private int getNumOfAliveAliens() {
        int _alive = 0;
        for (int _i = 0; _i < aliveAliens.length; _i++) {
            if (aliveAliens[_i] == true) {
                _alive += 1;
            }
        }
        return _alive;
    }

    // just like in Luau, variadic parameters also exist ;)
    private void printDebug(String... strings) {
        if (debugPrinting) {
            String text = "";
            for (String s : strings) {
                if (text.length() <= 0) text += s;
                else text += " " + s;
            }
            System.out.println(text);
        }
    }

    private void printBinds(String name, int[] binds) {
        String keys = "";
        if (binds.length > 0) {
            for (int _i = 0; _i < binds.length; _i++) {
                int code = binds[_i];
                String tkey = KeyEvent.getKeyText(code);
                if (keys.length() <= 0) keys = tkey;
                else keys += ", " + tkey;
            }
        }
        if (keys.length() <= 0) keys = "none";
        System.out.println(name + ": " + keys);
    }

    private void spaceship() {
        Thread loop = new Thread();
        int ms = 5;

        g1.setBoundY(boundy);
        
        int sizeX = g1.getBounds().width;
        int startPoint = 60;
        int posMultiply = 60;
        for (int id = 0; id < numOfAliens; id++) {
            int alieny = 20;
            if (id >= 5) alieny = 60;
            alienPosY[id] = alieny;
            int _moddedId = (Math.floorMod(id, 5));
            int alienx = (posMultiply * _moddedId) + startPoint;
            alienPosX[id] = alienx;
            aliveAliens[id] = true;
        }

        g1.setAlienPos(alienPosX, alienPosY);
        g1.setAliveAliens(aliveAliens);
        g1.setActiveAlienRockets(activeAlienRockets);
        g1.setAlienRockets(alienRocketX, alienRocketY);

        g1.setRockets(rocketX, rocketY);
        g1.setLaserRockets(laserRocketX, laserRocketY);
        g1.setActiveRockets(activeRockets);
        g1.setActiveLaserRockets(activeLaserRockets);

        g1.setMaxRockets(maxRockets);
        g1.setMaxAlienRockets(maxAlienRockets);
        
        while (!ended) {
            try {
                loop.sleep(ms);
            } catch (InterruptedException e) {}
            
            if (gameState) {
                int _numofalivealiens = getNumOfAliveAliens();
                if (_numofalivealiens <= 0) endGame();

                px = px + (dirx * playerSpeed);
                int rightx = px + playerSizeConst;
                if (px < 0) px = 0;
                if (rightx > sizeX) px = sizeX - 40;

                centerpx = px + 20;
                
                g1.movePlane(px);

                // funny auto fire feature ;)
                if (autoFireRockets) {
                    rocketNewTick = getTick();
                    long elapsed = (rocketNewTick - rocketOldTick);
                    if (elapsed >= toMilliseconds(rocketCooldown)) createRocket();
                }
                if (autoFireLaserRockets) {
                    laserNewTick = getTick();
                    long elapsed = (laserNewTick - laserOldTick);
                    if (elapsed >= toMilliseconds(laserRocketCooldown)) createLaserRocket();
                }

                // rocket logic rewrite (using arrays)
                for (int id = 0; id < maxRockets; id++) {
                    if (activeRockets[id] == true) {
                        // rocket repositioning
                        if (rocketY[id] <= 0) {
                            rocketY[id] = 0;
                            activeRockets[id] = false;
                            printDebug("Destroyed rocket " + id);
                        } else {
                            rocketY[id] -= rocketSpeed;
                        }
                        // rocket intersection check
                        int alientHitId = -1;
                        for (int _id2 = 0; _id2 < numOfAliens; _id2++) {
                            if (aliveAliens[_id2]) {
                                int apx = alienPosX[_id2], apy = alienPosY[_id2];
                                if (rocketX[id] >= apx && rocketX[id] <= (apx + alienSizeConst)) {
                                    if (rocketY[id] >= apy && rocketY[id] <= (apy + alienSizeConst)) {
                                        alientHitId = _id2;
                                        break;
                                    }
                                }
                            }
                        }
                        if (alientHitId != -1) {
                            // destroy rocket on impact
                            activeRockets[id] = false;
                            rocketY[id] = 0;
                            // kill alien
                            aliveAliens[alientHitId] = false;
                            g1.addScore(alienKillPoints);
                            printDebug("Attacked alien " + alientHitId + " by rocket " + id);
                        }
                    }
                    if (activeLaserRockets[id] == true) {
                        // laser rocket intersection check
                        // laser rocket repositioning
                        if (laserRocketY[id] <= 0) {
                            laserRocketY[id] = 0;
                            activeLaserRockets[id] = false;
                            printDebug("Destroyed laser rocket " + id);
                        } else {
                            laserRocketY[id] -= laserRocketSpeed;
                        }
                        // laser rocket intersection check
                        int alientHitId = -1;
                        for (int _id2 = 0; _id2 < numOfAliens; _id2++) {
                            if (aliveAliens[_id2]) {
                                int apx = alienPosX[_id2], apy = alienPosY[_id2];
                                if (laserRocketX[id] >= apx && laserRocketX[id] <= (apx + alienSizeConst)) {
                                    if (laserRocketY[id] >= apy && laserRocketY[id] <= (apy + alienSizeConst)) {
                                        alientHitId = _id2;
                                        break;
                                    }
                                }
                            }
                        }
                        if (alientHitId != -1) {
                            activeLaserRockets[id] = false;
                            laserRocketY[id] = 0;
                            aliveAliens[alientHitId] = false;
                            g1.addScore(alienKillPoints);
                            printDebug("Attacked alien " + alientHitId + " by laser rocket " + id);
                        }
                    }
                }

                // alien attackers
                alienRocketTimer -= 1;
                if (alienRocketTimer <= 0) {
                    alienRocketTimer = getAlienRocketTimer(ms);
                    //printDebug("new alien timer: " + alienRocketTimer);
                    createAlienRocket();
                }

                for (int id = 0; id < maxAlienRockets; id++) {
                    if (activeAlienRockets[id] == true) {
                        if (alienRocketY[id] + alienRocketSizeConst >= boundy) {
                            alienRocketY[id] = 0;
                            activeAlienRockets[id] = false;
                            printDebug("Destroyed alien rocket " + id);
                        } else {
                            alienRocketY[id] += alienRocketSpeed;
                        }

                        int _top = alienRocketY[id], _bottom = alienRocketY[id] + alienRocketSizeConst;
                        int _left = alienRocketX[id], _right = alienRocketX[id] + alienRocketSizeConst;
                        int _playerleft = px, _playerright = rightx;
                        int _playertop = playerYConst, _playerbottom = playerYConst + playerSizeConst;
                        if (_bottom > _playertop && _top < _playerbottom) {
                            if (_right > _playerleft && _left < _playerright) {
                                alienRocketY[id] = 0;
                                activeAlienRockets[id] = false;
                                printDebug("Destroyed alien rocket " + id);
                                killPlayer();
                            }
                        }
                    }
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

        rocketX = new int[maxRockets];
        rocketY = new int[maxRockets];
        laserRocketX = new int[maxRockets];
        laserRocketY = new int[maxRockets];
        activeRockets = new boolean[maxRockets];
        activeLaserRockets = new boolean[maxRockets];

        alienPosX = new int[numOfAliens];
        alienPosY = new int[numOfAliens];
        aliveAliens = new boolean[numOfAliens];
        alienRocketX = new int[maxAlienRockets];
        alienRocketY = new int[maxAlienRockets];
        activeAlienRockets = new boolean[maxAlienRockets];

        laserOldTick = 0; rocketOldTick = 0;
        rocketId = 0; laserRocketId = 0;
        alienRocketTimer = 0;

        printBinds("Move Left", moveLeftBinds);
        printBinds("Move Right", moveRightBinds);
        printBinds("Launch Rocket", rocketBinds);
        printBinds("Launch Laser Rocket", laserRocketBinds);
        printBinds("End Game", gameEndBinds);
        printBinds("Pause Toggle", toggleGamePauseBinds);
        
        f1 = new JFrame("Space Invaders Demo");
        f1.setSize(400,700);
        f1.setLocation(350, 50);
        f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f1.setResizable(false);
        
        Container c1 = f1.getContentPane();
        
        px = 75;
        g1 = new SpaceGraphics(px);
        g1.addKeyListener(this);
        g1.showAim(showRocketAim);
        g1.showTrajectory(showRocketTrajectory);
        g1.showHitbox(showAlienHitbox);
        g1.showAlienTrajectory(showAlienTrajectory);
        
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

    public void resumeGame() {
        if (!ended) {
            gameState = true;
            g1.setPause(false);
            g1.requestFocus();
        }
    }
    
    public void pauseGame() {
        if (!ended) {
            gameState = false;
            g1.setPause(true);
            g1.repaint();
        }
    }
    
    public void endGame() {
        if (!ended) {
            ended = true;
            g1.setEnded();
            g1.repaint();
        }
    }

    public void killPlayer() {
        if (!ended) {
            endGame();
            g1.setPlayerDied(true);
            g1.setEnded();
        }
    }

    private void setRocketPosition(int id, int x, int y) {
        activeRockets[id] = true;
        rocketX[id] = x;
        rocketY[id] = y;
    }

    public void createRocket() {
        rocketNewTick = getTick();
        long rocketTickElapsed = (rocketNewTick - rocketOldTick);
        long rocketCooldownMs = toMilliseconds(rocketCooldown);
        if (rocketTickElapsed >= rocketCooldownMs) {
            rocketOldTick = rocketNewTick;
            if (activeRockets[rocketId] == true) {
                // in rare cases, if the rocket with the current id already exists, it will reset the position
                // it also applies to laser rockets
                printDebug("Rocket " + rocketId + " already exists; resetting position");
            }
            setRocketPosition(rocketId, centerpx, rocketStartPositionY);
            printDebug("Added rocket " + rocketId);

            // modulus is important to prevent getting out-of-bounds exceptions
            rocketId = Math.floorMod(rocketId + 1, maxRockets);
        }
    }

    private void setLaserRocketPosition(int id, int x, int y) {
        activeLaserRockets[id] = true;
        laserRocketX[id] = x;
        laserRocketY[id] = y;
    }

    public void createLaserRocket() {
        laserNewTick = getTick();
        long laserTickElapsed = (laserNewTick - laserOldTick);
        long laserCooldownMs = toMilliseconds(laserRocketCooldown);
        if (laserTickElapsed >= laserCooldownMs) {
            laserOldTick = laserNewTick;
            if (activeLaserRockets[laserRocketId] == true) {
                printDebug("Laser rocket " + laserRocketId + " already exists; resetting position");
            }
            setLaserRocketPosition(laserRocketId, centerpx, rocketStartPositionY);
            printDebug("Added laser rocket " + laserRocketId);

            laserRocketId = Math.floorMod(laserRocketId + 1, maxRockets);
        }
    }

    private void setAlienRocketPosition(int id, int x, int y) {
        activeAlienRockets[id] = true;
        alienRocketX[id] = x;
        alienRocketY[id] = y;
    }

    public void createAlienRocket() {
        if (activeAlienRockets[alienRocketId] == true) {
            printDebug("Alien rocket " + alienRocketId + " already exists; resetting position");
        }
        int _ax, _ay;
        int chosenAlienId;

        int _aliveAlienLen = getNumOfAliveAliens();

        if (_aliveAlienLen > 0) {
            int[] _aliveAlienId = new int[_aliveAlienLen];
            int _aliveAlienCurrentId = 0;

            for (int _i = 0; _i < numOfAliens; _i++) {
                if (aliveAliens[_i] == true) {
                    _aliveAlienId[_aliveAlienCurrentId] = _i;
                    _aliveAlienCurrentId += 1;
                }
            }

            chosenAlienId = randomIntFromArray(_aliveAlienId);
            if (chosenAlienId != -1) {
                _ax = alienPosX[chosenAlienId] + (alienSizeConst / 2);
                _ay = alienPosY[chosenAlienId] + alienSizeConst;
                setAlienRocketPosition(alienRocketId, _ax, _ay);

                printDebug("Added alien rocket " + alienRocketId + " from alien " + chosenAlienId);
                alienRocketId = Math.floorMod(alienRocketId + 1, maxAlienRockets);
            } else {
                printDebug("chosen alien is -1?");
            }
        } else {
            printDebug("No aliens are alive!");
        }
        
    }
    
    @Override public void actionPerformed(ActionEvent e) {
        Object b = e.getSource();
        if (b.equals(start)) {
            resumeGame();
        } else if (b.equals(stop)) {
            pauseGame();
        } else if (b.equals(endd)) endGame();
    }
    
    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (intArray_find(moveLeftBinds, code)) dirx = -1;
        if (intArray_find(moveRightBinds, code)) dirx = 1;
        if (intArray_find(rocketBinds, code)) createRocket();
        if (intArray_find(laserRocketBinds, code)) createLaserRocket();
        if (intArray_find(gameEndBinds, code)) endGame();
        if (intArray_find(toggleGamePauseBinds, code)) {
            if (gameState) pauseGame();
            else resumeGame();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
