import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NotPacmanGraphics extends JPanel
{
    Image pac, g1, g2, g3, g4, g5, cherry;
    int px, py;
    
    public NotPacmanGraphics(int x, int y) {
        setBackground(Color.black);
        pac = Toolkit.getDefaultToolkit().getImage("pacman.png");
        g1 = Toolkit.getDefaultToolkit().getImage("ghost1.png");
        g2 = Toolkit.getDefaultToolkit().getImage("ghost2.png");
        g3 = Toolkit.getDefaultToolkit().getImage("ghost3.png");
        g4 = Toolkit.getDefaultToolkit().getImage("ghost4.png");
        g5 = Toolkit.getDefaultToolkit().getImage("eatghost.png");
        cherry = Toolkit.getDefaultToolkit().getImage("cherry.png");
        px = x; py = y;
    };
    
    public void movePacman(int x, int y) {
        px = x;
        py = y;
    };
    
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.blue);
        
        g.drawRect(0, 0, 300, 200);
        g.drawRect(0, 200 + 70, 300, 200);
        g.drawRect(300 + 70, 0, 400, 200);
        g.drawRect(300 + 70, 200 + 70, 400, 200);
        g.drawRect(0, (200 + 120) * 2, 780, 100);
        g.drawRect(900, 0, 50, 700);
        
        g.setColor(Color.yellow);
        g.fillOval(300 + 20, 200 + 20, 30, 30);
        g.fillOval(300 + 28, 275 + 28, 15, 15);
        g.fillOval(450 + 28, 200 + 28, 15, 15);
        g.fillOval(500 + 28, 200 + 28, 15, 15);
        g.fillOval(550 + 28, 200 + 28, 15, 15);
        g.fillOval(300 + 28, 325 + 28, 15, 15);
        
        g.drawImage(pac, px, py, 50, 50, null);
        g.drawImage(g4, 100, 200 + 10, 50, 50, null);
        g.drawImage(g3, 300 + 10, 40, 50, 50, null);
        g.drawImage(g2, 620, 200 + 10, 50, 50, null);
        g.drawImage(g1, 300 + 10, 400, 50, 50, null);
        g.drawImage(g5, 400, 200 + 10, 50, 50, null);
        g.drawImage(cherry, 300 + 10, 120, 50, 50, null);
        
        for (int h1 = 0; h1 < 10; h1++) {
            g.fillOval((50 * h1) + 28, 500 + 28, 15, 15);
            g.fillOval((50 * h1) + 28, 550 + 28, 15, 15);
        };
        
        for (int v1 = 0; v1 < 10; v1++) {
            g.fillOval(800, (50 * v1) + 130, 15, 15);
            g.fillOval(850, (50 * v1) + 200, 15, 15);
        };
    };
}
