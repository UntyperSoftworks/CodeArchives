/**
 * space invaders
 *
 * @author Noah
 * @version (see `_VERSION` in code)
 */

/*
TODO:
currently tasks:
- [DONE] Create game instances using separate classes (alien, player, rocket, etc.)

tasks for much later:
- Implement `HashMap` for configuration
- Add settings window for ALL configuration below
*/

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;

public class SpaceInvadersMain implements ActionListener, KeyListener {
    String _VERSION = "v0.10-beta";

    // menu instances
    JFrame f1;
    JPanel p1, btns;
    JButton start, stop, endd;
    SpaceGraphics g1;

    // game variables
    int centerpx, dirx;
    boolean gameState, ended;
    int boundx, boundy;
    long laserOldTick, rocketOldTick;
    long laserNewTick, rocketNewTick;
    Spaceship player;

    // constants (may change for appropriate updates)
    final int numOfAliens = 20;
    final int alienSizeConst = 20;
    final int alienRocketSizeConst = 10;
    final int playerYConst = 500;
    final int playerSizeConst = 40;

    // rocket array data
    Missile[] rockets, laserRockets;
    int rocketId, laserRocketId;
    boolean[] activeRockets, activeLaserRockets;

    // alien array data
    Alien[] aliens;
    boolean[] activeAlienRockets;
    int[] alienRocketX, alienRocketY;
    int alienRocketId, alienRocketTimer;

    // memory variables
    long usingMemory;
    long maxMemory;

    // config (player/rockets)
    int playerSpeed = 1;
    int rocketSpeed = 1;
    int laserRocketSpeed = 5;
    int maxRockets = 50;
    int rocketStartPositionY = playerYConst - 20;
    double rocketCooldown = 0; // 0.05
    double laserRocketCooldown = 0; // 0.5

    // config (alien)
    int alienRocketSpeed = 1;
    int maxAlienRockets = 20;
    int alienKillPoints = 10;
    double[] randomAlienShot = {0.2, 0.7};
    boolean alienRocketCollisions = true;

    // config (memory)
    long memoryThresholdMB = 200; // (in megabytes) runs garbage collector at certain memory size; lower values may break -> minimum is 40MB

    // config (keybinds)
    int[] moveLeftBinds = {KeyEvent.VK_A, KeyEvent.VK_LEFT};
    int[] moveRightBinds = {KeyEvent.VK_D, KeyEvent.VK_RIGHT};
    int[] rocketBinds = {KeyEvent.VK_SPACE, KeyEvent.VK_NUMPAD2, KeyEvent.VK_COMMA};
    int[] laserRocketBinds = {KeyEvent.VK_Z, KeyEvent.VK_NUMPAD3, KeyEvent.VK_PERIOD};
    int[] gameEndBinds = {KeyEvent.VK_T};
    int[] toggleGamePauseBinds = {KeyEvent.VK_R};

    // config (fun) - only intended for dev purposes
    boolean autoFireRockets = false;
    boolean autoFireLaserRockets = false;
    
    // config (debug)
    boolean showRocketAim = true;
    boolean showRocketTrajectory = false;
    boolean showAlienHitbox = false;
    boolean showAlienTrajectory = false;
    boolean showPlayerHitbox = false;
    boolean debugPrinting = false;
    boolean outputMemory = true;

    // config hash map
    HashMap<String, Object> configTable = new HashMap();

    // config setting
    public void loadSettings() {
        // player/rockets
        configTable.put("playerSpeed", (int)1);
        configTable.put("rocketSpeed", (int)1);
        configTable.put("laserRocketSpeed", (int)5);
        configTable.put("maxRockets", (int)50);
        configTable.put("rocketStartPositionY", (int)playerYConst - 20);
        configTable.put("rocketCooldown", (double)0.05);
        configTable.put("laserRocketCooldown", (double)0.5);

        // alien
        configTable.put("alienRocketSpeed", (int)1);
        configTable.put("maxAlienRockets", (int)20);
        configTable.put("alienKillPoints", (int)10);
        configTable.put("randomAlienShotMin", (double)0.2);
        configTable.put("randomAlienShotMax", (double)0.7);
        configTable.put("alienRocketCollisions", (boolean)true);

        // memory
        configTable.put("enableManualGC", (boolean)true);
        configTable.put("memoryThresholdMB", (int)200);

        // there isn't a way on how to set up arrays into hash maps
        // so keybinds will be constant unless changed in the code

        // fun
        configTable.put("autoFireRockets", (boolean)false);
        configTable.put("autoFireLaserRockets", (boolean)false);

        // debug
        configTable.put("showRocketAim", (boolean)true);
        configTable.put("showRocketTrajectory", (boolean)false);
        configTable.put("showAlienHitbox", (boolean)false);
        configTable.put("showAlienTrajectory", (boolean)false);
        configTable.put("showPlayerHitbox", (boolean)false);
        configTable.put("debugPrinting", (boolean)false);
        configTable.put("outputMemory", (boolean)true);
    }

    public void changeSetting() {

    }

    // integer rng
    private int math_random(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
    // double rng
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
        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                _alive += 1;
            }
        }
        return _alive;
    }

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
        
        int startPoint = 60;
        int posMultiply = 60;
        int halfNumofAliens = numOfAliens / 2;
        for (int id = 0; id < numOfAliens; id++) {
            int alieny = 20;
            if (id >= halfNumofAliens) alieny = 60;
            int _moddedId = (Math.floorMod(id, halfNumofAliens));
            int alienx = (posMultiply * _moddedId) + startPoint;
            aliens[id] = new Alien(alienx, alieny);
        }

        g1.setAliens(aliens);

        g1.setActiveAlienRockets(activeAlienRockets);
        g1.setAlienRockets(alienRocketX, alienRocketY);

        g1.setRocketInstances(rockets);
        g1.setLaserRocketInstances(laserRockets);
        g1.setActiveRockets(activeRockets);
        g1.setActiveLaserRockets(activeLaserRockets);

        g1.setMaxRockets(maxRockets);
        g1.setMaxAlienRockets(maxAlienRockets);

        g1.showMemoryDebug(outputMemory);

        MemoryRecorder _Recorder = new MemoryRecorder();
        
        while (!ended) {
            try {
                loop.sleep(ms);
            } catch (InterruptedException e) {}
            
            if (gameState) {
                int _numofalivealiens = getNumOfAliveAliens();
                if (_numofalivealiens <= 0) endGame();

                player.moveX(dirx);

                int px = player.getX();
                int rightx = px + playerSizeConst;
                if (px < 0) player.setX(0);
                if (rightx > boundx) player.setX(boundx - playerSizeConst);

                centerpx = px + 20;

                // very cool auto fire feature ;)
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
                        Missile currentRocket = rockets[id];
                        // rocket repositioning
                        if (currentRocket.getY() <= 0) {
                            currentRocket.setY(0);
                            activeRockets[id] = false;
                            printDebug("Destroyed rocket " + id);
                        } else {
                            currentRocket.moveUpY(rocketSpeed);
                        }
                        // rocket intersection check
                        int alienHitId = -1;
                        for (int _id2 = 0; _id2 < numOfAliens; _id2++) {
                            Alien alien = aliens[_id2];
                            if (alien.isAlive()) {
                                int apx = alien.getX(), apy = alien.getY();
                                if (currentRocket.getX() >= apx && currentRocket.getX() <= (apx + alienSizeConst)) {
                                    if (currentRocket.getY() >= apy && currentRocket.getY() <= (apy + alienSizeConst)) {
                                        alienHitId = _id2;
                                        break;
                                    }
                                }
                            }
                        }
                        if (alienHitId != -1) {
                            // destroy rocket on impact
                            activeRockets[id] = false;
                            currentRocket.setY(0);
                            // kill alien
                            aliens[alienHitId].destroy();
                            player.addScore(alienKillPoints);
                            printDebug("Attacked alien " + alienHitId + " by rocket " + id);
                        }
                    }
                    if (activeLaserRockets[id] == true) {
                        Missile currentLaserRocket = laserRockets[id];
                        // laser rocket intersection check
                        // laser rocket repositioning
                        if (currentLaserRocket.getY() <= 0) {
                            currentLaserRocket.setY(0);
                            activeLaserRockets[id] = false;
                            printDebug("Destroyed laser rocket " + id);
                        } else {
                            currentLaserRocket.moveUpY(laserRocketSpeed);
                        }
                        // laser rocket intersection check
                        int alienHitId = -1;
                        for (int _id2 = 0; _id2 < numOfAliens; _id2++) {
                            Alien alien = aliens[_id2];
                            if (alien.isAlive()) {
                                int apx = alien.getX(), apy = alien.getY();
                                if (currentLaserRocket.getX() >= apx && currentLaserRocket.getX() <= (apx + alienSizeConst)) {
                                    if (currentLaserRocket.getY() >= apy && currentLaserRocket.getY() <= (apy + alienSizeConst)) {
                                        alienHitId = _id2;
                                        break;
                                    }
                                }
                            }
                        }
                        if (alienHitId != -1) {
                            activeLaserRockets[id] = false;
                            currentLaserRocket.setY(0);
                            aliens[alienHitId].destroy();
                            player.addScore(alienKillPoints);
                            printDebug("Attacked alien " + alienHitId + " by laser rocket " + id);
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

                // alien rocket logic
                for (int id = 0; id < maxAlienRockets; id++) {
                    if (activeAlienRockets[id] == true) {
                        if (alienRocketY[id] + alienRocketSizeConst >= boundy) {
                            alienRocketY[id] = 0;
                            activeAlienRockets[id] = false;
                            printDebug("Destroyed alien rocket " + id);
                        } else {
                            alienRocketY[id] += alienRocketSpeed;
                        }

                        if (alienRocketCollisions) {
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
                }

                // memory handler
                long[] values = _Recorder.getMemory();
                usingMemory = values[0];
                if (outputMemory) {
                    maxMemory = values[1];
                    g1.setMemory(usingMemory, maxMemory);
                }
                if (usingMemory > memoryThresholdMB) System.gc();
                
                g1.repaint();
            }
        }
    }
    
    public SpaceInvadersMain() {
        //loadSettings();
        
        gameState = false;
        ended = false;

        int _px = (700 / 2) - (playerSizeConst / 2);
        player = new Spaceship(_px, playerYConst);
        player.setSpeed(playerSpeed);
        dirx = 0;

        rockets = new Missile[maxRockets];
        laserRockets = new Missile[maxRockets];
        activeRockets = new boolean[maxRockets];
        activeLaserRockets = new boolean[maxRockets];

        aliens = new Alien[numOfAliens];

        alienRocketX = new int[maxAlienRockets];
        alienRocketY = new int[maxAlienRockets];
        activeAlienRockets = new boolean[maxAlienRockets];

        laserOldTick = 0; rocketOldTick = 0;
        rocketId = 0; laserRocketId = 0;
        alienRocketTimer = 0;
        
        if (memoryThresholdMB < 40) memoryThresholdMB = 40;

        printBinds("Move Left", moveLeftBinds);
        printBinds("Move Right", moveRightBinds);
        printBinds("Launch Rocket", rocketBinds);
        printBinds("Launch Laser Rocket", laserRocketBinds);
        printBinds("End Game", gameEndBinds);
        printBinds("Pause Toggle", toggleGamePauseBinds);

        f1 = new JFrame("Space Invaders Demo (" + _VERSION + ")");
        f1.setSize(700,700);
        f1.setLocation(350, 50);
        f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f1.setResizable(false);
        
        Container c1 = f1.getContentPane();

        g1 = new SpaceGraphics();
        g1.addKeyListener(this);
        g1.showAim(showRocketAim);
        g1.showTrajectory(showRocketTrajectory);
        g1.showHitbox(showAlienHitbox);
        g1.showAlienTrajectory(showAlienTrajectory);
        g1.showPlayerHitbox(showPlayerHitbox);
        g1.setSpaceship(player);
        
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
        p1.setSize(700,700);
        
        p1.add(g1, BorderLayout.CENTER);
        p1.add(btns, BorderLayout.SOUTH);
        
        c1.add(p1);
        f1.show();

        boundx = g1.getBounds().width;
        boundy = g1.getBounds().height;
        spaceship();
    }

    private void resumeGame() {
        if (!ended) {
            gameState = true;
            g1.setPause(false);
            g1.requestFocus();
        }
    }
    
    private void pauseGame() {
        if (!ended) {
            gameState = false;
            g1.setPause(true);
            g1.repaint();
        }
    }
    
    private void endGame() {
        if (!ended) {
            ended = true;
            g1.setEnded();
            g1.repaint();
        }
    }

    private void killPlayer() {
        if (!ended) {
            endGame();
            player.crash();
            g1.setEnded();
        }
    }

    private void setRocketPosition(int id, int x, int y) {
        activeRockets[id] = true;
        rockets[id] = new Missile(x, y);
    }

    private void createRocket() {
        rocketNewTick = getTick();
        long rocketTickElapsed = (rocketNewTick - rocketOldTick);
        long rocketCooldownMs = toMilliseconds(rocketCooldown);
        if (rocketTickElapsed >= rocketCooldownMs) {
            rocketOldTick = rocketNewTick;
            if (activeRockets[rocketId] == true) {
                // in some cases, if the rocket with the current id already exists, it will reset the position
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
        laserRockets[id] = new Missile(x, y);
    }

    private void createLaserRocket() {
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

    private void createAlienRocket() {
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
                Alien alien = aliens[_i];
                if (alien.isAlive()) {
                    _aliveAlienId[_aliveAlienCurrentId] = _i;
                    _aliveAlienCurrentId += 1;
                }
            }

            chosenAlienId = randomIntFromArray(_aliveAlienId);
            if (chosenAlienId != -1) {
                Alien alien = aliens[chosenAlienId];
                _ax = alien.getX() + (alienSizeConst / 2);
                _ay = alien.getY() + alienSizeConst;
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
