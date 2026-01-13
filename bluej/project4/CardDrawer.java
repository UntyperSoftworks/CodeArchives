/**
 * Write a description of class CardDrawer here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CardDrawer implements ActionListener
{
    JFrame frame;
    Container con;
    JPanel main, btns;
    CardGraphics gr;
    JButton draw;
    
    public CardDrawer() {
        frame = new JFrame("Card Drawer");
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        con = frame.getContentPane();
        
        draw = new JButton("Draw New Card");
        draw.addActionListener(this);
        
        btns = new JPanel();
        btns.add(draw);
        
        gr = new CardGraphics();
        
        main = new JPanel();
        main.setLayout(new BorderLayout());
        main.setSize(700, 700);
        main.add(gr, BorderLayout.CENTER);
        main.add(btns, BorderLayout.SOUTH);
        
        con.add(main);
        frame.show();
    };
    
    private int rngV2(int min, int max) {
        int range = (max - min) + 1;
        int result = (int)(Math.random() * range) + min;
        return result;
    };
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == draw) {
            int x = rngV2(1, 4);
            int y = rngV2(1, 13);
            gr.drawCard(x, y);
            gr.repaint();
        };
    };
}
