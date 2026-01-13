
/**
 * Window for opening programs: pythagorean solver or two-point solver
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Programs implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2;
    
    JButton b1, b2, b3, b4, b5, b6, b7;
    JLabel title;
    
    Color framergb = new Color(47, 137, 216);
    Color fieldrgb = Color.gray;
    Color buttonrgb = new Color(112, 177, 199);
    
    public Programs()
    {
        fr = new JFrame("Choose a Program");
        // 600, 500 bounds is too much for the computer
        // half of the values for now
        fr.setBounds(300, 250, 400, 400);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setResizable(false);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(framergb);
        GridLayout grid = new GridLayout(2,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel(); 
        
        p1.setBackground(framergb);
        p2.setBackground(framergb);
        
        b1 = new JButton("Pythagorean Theorem Solver");
        b2 = new JButton("Two-Point Solver");
        b3 = new JButton("Number Guesser");
        b4 = new JButton("Dice Roller");
        b5 = new JButton("Card Drawer");
        b6 = new JButton("Color Creator");
        b7 = new JButton("Mouse Stuff");
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);
        b7.addActionListener(this);
        
        b1.setBackground(buttonrgb);
        b2.setBackground(buttonrgb);
        b3.setBackground(buttonrgb);
        b4.setBackground(buttonrgb);
        b5.setBackground(buttonrgb);
        b6.setBackground(buttonrgb);
        b7.setBackground(buttonrgb);
        
        title = new JLabel("Choose a Program");
        Font titlefont = new Font("Ariel", Font.BOLD, 20);
        title.setFont(titlefont);
        
        title.setForeground(Color.white);
        
        p.add(p1);
        p.add(p2);
        
        // title
        p1.add(title);
        
        // buttons
        p2.add(b1);
        p2.add(b2);
        p2.add(b3);
        p2.add(b4);
        p2.add(b5);
        p2.add(b6);
        p2.add(b7);
        
        c.add(p);
        fr.show();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == b1) { // pythagorean
            new PythagoreanSolver();
        };
        if (event.getSource() == b2) { // two-point
            new TwoPointSolver();
        };
        if (event.getSource() == b3) { // number guesser
            new NumberGuesser();
        };
        if (event.getSource() == b4) { // dice roller
            new DiceRoller();
        };
        if (event.getSource() == b5) { // card drawer
            new CardDrawer();
        };
        if (event.getSource() == b6) { // color creator
            new ColorCreator();
        };
        if (event.getSource() == b7) { // mouse stuff
            new MouseStuff();
        };
    };
}
