import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DiceGraphics extends JPanel
{
    boolean rolled;
    int face;
    public DiceGraphics() {
        setBackground(Color.yellow);
        rolled = false;
        face = 0;
    };
    
    public void rollDice(int value) {
        if (!rolled) rolled = true;
        face = value;
        //System.out.println(value);
    };
    
    public void paint(Graphics g) {
        super.paint(g);
        
        Font newfont = new Font("Arial", Font.BOLD, 36);
        g.setFont(newfont);
        g.drawString("Dice Roller", 300, 40);
        
        g.setColor(Color.red);
        g.fillRoundRect(50, 50, 680, 650, 40, 40);
        
        g.setColor(new Color(160, 160, 160));
        g.fillRoundRect(70, 70, 640, 610, 40, 40);
        
        if (rolled) {
            int posX = 300, posY = 300, sizeX = 200, sizeY = 200;
            int dotX = 30, dotY = 30;
            int offset = 50;
            
            int midX = (sizeX / 2) - (dotX / 2), midY = (sizeY / 2) - (dotY / 2);
            
            g.setColor(Color.white);
            g.fillRoundRect(posX, posY, sizeX, sizeY, 10, 10); // initial dice
            
            g.setColor(Color.black);
            if (face == 1) {
                g.fillOval(posX + midX, posY + midY, dotX, dotY);
            } else if (face == 2) {
                g.fillOval(posX + midX - offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY + offset, dotX, dotY);
            } else if (face == 3) {
                g.fillOval(posX + midX - offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX, posY + midY, dotX, dotY);
            } else if (face == 4) {
                g.fillOval(posX + midX - offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX - offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY - offset, dotX, dotY);
            } else if (face == 5) {
                g.fillOval(posX + midX - offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX - offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX, posY + midY, dotX, dotY);
            } else { // if face is 6
                g.fillOval(posX + midX - offset, posY + midY - offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY, dotX, dotY);
                g.fillOval(posX + midX - offset, posY + midY + offset, dotX, dotY);
                g.fillOval(posX + midX - offset, posY + midY, dotX, dotY);
                g.fillOval(posX + midX + offset, posY + midY - offset, dotX, dotY);
            }
        };
    };
}
