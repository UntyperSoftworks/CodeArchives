/**
 * Dice roller
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CardDrawer implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3;
    
    JLabel title, card, drawnt;
    JButton drawcard;
    
    String value, suit;
    
    int cardsDrawn = 0;
    
    public CardDrawer()
    {
        fr = new JFrame("Card Drawer");
        fr.setBounds(150, 100, 350, 200);
        fr.setResizable(false);
        //fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(Color.red);
        GridLayout grid = new GridLayout(3,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        
        p1.setBackground(Color.red);
        p2.setBackground(Color.red);
        p3.setBackground(Color.red);
        
        title = new JLabel("Card Drawer");
        Font titlefont = new Font("Ariel", Font.BOLD, 20);
        title.setFont(titlefont);
        
        drawcard = new JButton("Draw a Card");
        drawcard.addActionListener(this);
        drawnt = new JLabel("Cards: 0");
        
        card = new JLabel("");
        Font cardfont = new Font("Ariel", Font.ITALIC, 14);
        card.setFont(cardfont);
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        
        p1.add(title);
        
        p2.add(drawcard);
        p2.add(drawnt);
        
        p3.add(card);
        
        c.add(p);
        fr.show();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == drawcard) {
            cardsDrawn = cardsDrawn + 1;
            drawnt.setText("Cards : " + cardsDrawn);
            
            int x = (int)(Math.random() * 4) + 1;
            int y = (int)(Math.random() * 13) + 1;
            
            if (x == 1) suit = "Hearts";
            if (x == 2) suit = "Spades";
            if (x == 3) suit = "Diamonds";
            if (x == 4) suit = "Clubs";
            
            if (y == 1) value = "Ace";
            if (y == 2) value = "2";
            if (y == 3) value = "3";
            if (y == 4) value = "4";
            if (y == 5) value = "5";
            if (y == 6) value = "6";
            if (y == 7) value = "7";
            if (y == 8) value = "8";
            if (y == 9) value = "9";
            if (y == 10) value = "10";
            if (y == 11) value = "Jack";
            if (y == 12) value = "Queen";
            if (y == 13) value = "King";
            
            card.setText(value + " of " + suit);
        };
    };
}
