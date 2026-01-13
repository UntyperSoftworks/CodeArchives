import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CardGraphics extends JPanel
{
    boolean drawn;
    int face, number;
    Image diamond, heart, club, spade;
    
    public CardGraphics() {
        setBackground(Color.green);
        drawn = false;
        face = 0; number = 0;
        diamond = Toolkit.getDefaultToolkit().getImage("diamond.png");
        heart = Toolkit.getDefaultToolkit().getImage("heart.png");
        club = Toolkit.getDefaultToolkit().getImage("club.png");
        spade = Toolkit.getDefaultToolkit().getImage("spade.png");
    };
    
    public void drawCard(int cardFace, int cardNum) {
        if (!drawn) drawn = true;
        face = cardFace; number = cardNum;
        //System.out.println(cardFace + ", " + cardNum);
    };
    
    public void paint(Graphics g) {
        super.paint(g);
        
        Font newfont = new Font("Arial", Font.BOLD, 36);
        g.setFont(newfont);
        g.drawString("Card Drawer", 300, 40);
        
        g.setColor(new Color(0, 200, 0));
        g.fillRoundRect(50, 50, 680, 650, 40, 40);
        
        if (drawn) {
            int posX = 270, posY = 180, sizeX = 250, sizeY = 400;
            int faceX = 100, faceY = 100;
            int fontSize = 24, offset = 10;
            Font numfont = new Font("Ariel", Font.BOLD, fontSize);
            
            int midX = (sizeX / 2) - (faceX / 2);
            int midY = (sizeY / 2) - (faceY / 2);
            
            g.setColor(Color.white);
            g.fillRoundRect(posX, posY, sizeX, sizeY, 40, 40);
            
            boolean isRed = false;
            if (face == 1 || face == 2) isRed = true;
            
            Image selected;
            if (face == 1) {
                selected = diamond;
            } else if (face == 2) {
                selected = heart;
            } else if (face == 3) {
                selected = club;
            } else selected = spade;
            
            String numberText = "";
            if (number == 1) {
                numberText = "A";
            } else if (number == 11) {
                numberText = "J";
            } else if (number == 12) {
                numberText = "Q";
            } else if (number == 13) {
                numberText = "K";
            } else numberText = Integer.toString(number);
            
            g.drawImage(selected, posX + midX, posY + midY, faceX, faceY, null);
            
            if (isRed) {
                g.setColor(Color.red);
            } else g.setColor(Color.black);
            
            g.setFont(numfont);
            g.drawString(
                numberText,
                posX + offset,
                posY + fontSize + offset
            );
            g.drawString(
                numberText,
                posX + sizeX - fontSize - (offset / 2),
                posY + sizeY - offset
            );
        };
    };
}
