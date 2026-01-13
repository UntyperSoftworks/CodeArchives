/**
 * Shows pac-man
 *
 * @author Noah
 * @version 0.1
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NotPacman implements KeyListener
{
    JFrame fr;
    Container c;
    
    JPanel p;
    NotPacmanGraphics Pacmin;
    
    int px, py;
    
    public NotPacman() {
        fr = new JFrame("Pacman");
        fr.setBounds(20, 20, 1000, 800);
        fr.setResizable(false);
        fr.addKeyListener(this);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setSize(1000, 800);
        p.setLayout(new BorderLayout());
        
        px = 220; py = 210;
        
        Pacmin = new NotPacmanGraphics(px, py);
        p.add(Pacmin);
        
        c.add(p);
        fr.show();
    }
    
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int offset = 10;
        
        if (key == 87 || key == 38) { // up/w
            py = py - offset;
        } else if (key == 83 || key == 40) { // down/s
            py = py + offset;
        } else if (key == 65 || key == 37) { // left/a
            px = px - offset;
        } else if (key == 68 || key == 39) { // right/d
            px = px + offset;
        }
        
        Pacmin.movePacman(px, py);
        Pacmin.repaint();
    }
}
