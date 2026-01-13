/**
 * Solves hypotenuse with pythagorean theorem
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PythagoreanSolver implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3, p4;
    
    JLabel title, legA, legB, hypotenuse;
    JTextField legAValue, legBValue;
    JButton calculate, clear;
    
    double la, lb, lc, result;
    
    public PythagoreanSolver()
    {
        fr = new JFrame("Pythagorean Solver");
        fr.setBounds(100, 100, 300, 250);
        fr.setResizable(false);
        //fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(Color.red);
        GridLayout grid = new GridLayout(4,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        
        p1.setBackground(Color.red);
        p2.setBackground(Color.red);
        p3.setBackground(Color.red);
        p4.setBackground(Color.red);
        
        title = new JLabel("Pythagorean Solver");
        Font titlefont = new Font("Ariel", Font.ITALIC, 20);
        title.setFont(titlefont);
        
        legA = new JLabel("Leg A");
        legAValue = new JTextField(8);
        legB = new JLabel("Leg B");
        legBValue = new JTextField(8);
        
        calculate = new JButton("Calculate");
        calculate.addActionListener(this);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        
        hypotenuse = new JLabel("Hypotenuse = ");
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        
        // title
        p1.add(title);
        
        // leg values
        p2.add(legA);
        p2.add(legAValue);
        p2.add(legB);
        p2.add(legBValue);
        
        // buttons
        p3.add(calculate);
        p3.add(clear);
        
        // result
        p4.add(hypotenuse);
        
        c.add(p);
        fr.show();
    }
    
    public void pythagorean() {
        try {
            la = Double.parseDouble(legAValue.getText());
            lb = Double.parseDouble(legBValue.getText());
            lc = Math.pow(la, 2) + Math.pow(lb, 2);
            result = Math.sqrt(lc);
            hypotenuse.setText("Hypotenuse = " + result);
        } catch (NumberFormatException e) {
            hypotenuse.setText("Please enter a number");
        };
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == calculate) {
            pythagorean();
        };
        if (event.getSource() == clear) {
            legAValue.setText("");
            legBValue.setText("");
        };
    };
}
