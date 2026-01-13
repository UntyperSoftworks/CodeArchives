
/**
 * Solves midpoint, slope, & distance with two coordinates
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TwoPointSolver implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3, p4;
    
    JLabel title, slope, midpoint, distance;
    JTextField xs1, ys1, xs2, ys2;
    JButton calculate, clear;
    
    double x1, y1, x2, y2, slp, midpx, midpy, dist;
    
    public TwoPointSolver()
    {
        fr = new JFrame("2 Point Solver");
        fr.setBounds(100, 100, 300, 400);
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
        
        title = new JLabel("Algebra Two-Point Solver");
        Font titlefont = new Font("Ariel", Font.ITALIC, 20);
        title.setFont(titlefont);
        
        int chars = 2;
        xs1 = new JTextField(chars);
        ys1 = new JTextField(chars);
        xs2 = new JTextField(chars);
        ys2 = new JTextField(chars);
        
        calculate = new JButton("Calculate");
        calculate.addActionListener(this);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        
        slope = new JLabel("Slope = ");
        midpoint = new JLabel("Midpoint = ");
        distance = new JLabel("Distance = ");
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        
        // title
        p1.add(title);
        
        // values
        p2.add(new JLabel("("));
        p2.add(xs1);
        p2.add(new JLabel(","));
        p2.add(ys1);
        p2.add(new JLabel(")"));
        
        p2.add(new JLabel("("));
        p2.add(xs2);
        p2.add(new JLabel(","));
        p2.add(ys2);
        p2.add(new JLabel(")"));
        
        // buttons
        p3.add(calculate);
        p3.add(clear);
        
        // results
        p4.add(slope);
        p4.add(midpoint);
        p4.add(distance);
        
        c.add(p);
        fr.show();
    }
    
    public void clearText() {
        xs1.setText("");
        ys1.setText("");
        xs2.setText("");
        ys2.setText("");
    }
    
    public double getDist(double x1, double y1, double x2, double y2) {
        double res = 0;
        double xpow = Math.pow((x2 - x1), 2);
        double ypow = Math.pow((y2 - y1), 2);
        res = Math.sqrt(xpow + ypow);
        return res;
    }
    
    public double getSlope(double x1, double y1, double x2, double y2) {
        double m = 0;
        m = (y2 - y1) / (x2 - x1);
        return m;
    }
    
    public double getMidpoint(double x1, double x2) {
        double mdx = 0;
        mdx = (x1 + x2) / 2;
        return mdx;
    }
    
    public void twopoint() {
        String xx1, yy1, xx2, yy2;
        xx1 = xs1.getText();
        yy1= ys1.getText();
        xx2 = xs2.getText();
        yy2 = ys2.getText();
        try {
            x1 = Double.parseDouble(xx1);
            y1 = Double.parseDouble(yy1);
            x2 = Double.parseDouble(xx2);
            y2 = Double.parseDouble(yy2);
            
            dist = getDist(x1, y1, x2, y2);
            slp = getSlope(x1, y1, x2, y2);
            midpx = getMidpoint(x1, x2);
            midpy = getMidpoint(y1, y2);
            
            //slope.setText("Slope = " + slp);
            midpoint.setText("Midpoint = (" + midpx + ", " + midpy + ")");
            //distance.setText("Distance = " + dist);
            
            // alts
            String slpV2 = String.format("%.2f", slp);
            slope.setText("Slope = " + slpV2);
            
            String distV2 = String.format("%.2f", dist);
            distance.setText("Distance = " + distV2);
        } catch (NumberFormatException e) {
            slope.setText("Slope = ");
            distance.setText("Distance = ");
            midpoint.setText("Please enter a number!");
        };
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == calculate) {
            twopoint();
        } else if (event.getSource() == clear) {
            clearText();
        };
    }
}
