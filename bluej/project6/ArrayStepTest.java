import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//TODO: show all instance positions with text labels

public class ArrayStepTest implements KeyListener {
    // main instances
    int instanceSize;
    int[] instancePos;
    boolean[] activeInstances;
    int index;
    boolean ended;
    
    // config
    int instanceLaunchPos = 400;

    // ui instances
    JFrame frame;
    long _aa;

    private void runloop() {
        Thread loop = new Thread();
        int ms = 5;
        while (!ended) {
            try {
                loop.sleep(ms);
            } catch (InterruptedException e) {}
            
            System.out.println(_aa);
            
            for (int i = 0; i < instanceSize; i++) {
                if (activeInstances[i]) {
                    String v2Text = String.format("Instance %d: %d", i, instancePos[i]);
                    System.out.println(v2Text);
                    if (instancePos[i] <= 0) {
                        // resets to zero if below
                        instancePos[i] = 0;
                        activeInstances[i] = false;
                        System.out.println("Instance " + i + " reached to end");
                    } else {
                        instancePos[i] -= 1;
                    }
                }
            }
        }
    }

    public ArrayStepTest() {
        instanceSize = 100;
        instancePos = new int[instanceSize];
        activeInstances = new boolean[instanceSize];
        
        _aa = System.currentTimeMillis();

        index = 0;
        ended = false;
        
        frame = new JFrame("do inputs here");
        frame.setSize(400, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setBackground(Color.red);
        frame.addKeyListener(this);

        System.out.println("Press space to create instance");
        System.out.println("Press R to end program");

        frame.show();

        runloop();
    }

    public void addInstance() {
        if (activeInstances[index] == true) {
            System.out.println("instance " + index + " already exists; resetting position");
        }
        activeInstances[index] = true;
        instancePos[index] = instanceLaunchPos;
        System.out.println("added instance " + index);

        // modulus is really important to prevent index being out of bounds
        index = Math.floorMod(index + 1, instancePos.length);
    }

    public void endLoop() {
        if (!ended) {
            ended = true;
            System.out.println("Ended ArrayStepTest");
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        String key = KeyEvent.getKeyText(code);
        if (key.equals("Space")) addInstance();
        else if (code == KeyEvent.VK_R) endLoop();
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
