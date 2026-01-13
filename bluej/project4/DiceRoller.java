/**
 * Write a description of class DiceRoller here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DiceRoller implements ActionListener
{
    JFrame frame;
    Container con;
    JPanel main, btns;
    DiceGraphics gr;
    JButton roll;
    
    public DiceRoller() {
        frame = new JFrame("Dice Roller");
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        con = frame.getContentPane();
        
        roll = new JButton("Roll");
        roll.addActionListener(this);
        
        btns = new JPanel();
        btns.add(roll);
        
        gr = new DiceGraphics();
        
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
        if (e.getSource() == roll) {
            int newValue = rngV2(1, 6);
            gr.rollDice(newValue);
            gr.repaint();
        };
    };
}
