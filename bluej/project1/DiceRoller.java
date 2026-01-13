/**
 * Dice roller
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DiceRoller implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3;
    
    JLabel title, die1, die2, rollt;
    JButton rolldice;
    
    int rolls = 0;
    
    public DiceRoller()
    {
        fr = new JFrame("Dice Roller");
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
        
        title = new JLabel("Dice Roller");
        Font titlefont = new Font("Ariel", Font.BOLD, 20);
        title.setFont(titlefont);
        
        rolldice = new JButton("Roll the Dice");
        die1 = new JLabel("Die 1 = ?");
        die2 = new JLabel("Die 2 = ?");
        rollt = new JLabel("Rolls: 0");
        rolldice.addActionListener(this);
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        
        p1.add(title);
        
        p2.add(rolldice);
        p2.add(rollt);
        
        p3.add(die1);
        p3.add(die2);
        
        c.add(p);
        fr.show();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == rolldice) {
            rolls = rolls + 1;
            rollt.setText("Rolls: " + rolls);
            int dicenum1 = (int)(Math.random() * 6) + 1;
            int dicenum2 = (int)(Math.random() * 6) + 1;
            die1.setText("Die 1 = " + dicenum1);
            die2.setText("Die 2 = " + dicenum2);
        };
    };
}
