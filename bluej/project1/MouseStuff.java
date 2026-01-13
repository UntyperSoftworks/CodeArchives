/**
 * Mouse events
 *
 * @author Noah Yang
 * @version 0.1
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MouseStuff implements ActionListener, MouseListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3;
    
    JLabel title;
    JTextField field;
    JButton b1, b2;
    
    Color initColor = Color.lightGray;
    
    public MouseStuff() {       
        fr = new JFrame("Color Creator");
        fr.setBounds(150, 100, 400, 250);
        fr.setResizable(false);
        //fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(initColor);
        GridLayout grid = new GridLayout(3,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        
        p1.setBackground(initColor);
        p2.setBackground(initColor);
        p3.setBackground(initColor);
        
        title = new JLabel("Mouse Stuff");
        Font f = new Font("Ariel", Font.BOLD, 20);
        title.setFont(f);
        
        title.addMouseListener(this);
        
        b1 = new JButton("Press for red, release for yellow");
        b2 = new JButton("Enter to fill the JTextField");
        field = new JTextField(16);
        
        b1.addActionListener(this);
        b1.addMouseListener(this);
        b2.addActionListener(this);
        b2.addMouseListener(this);
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        
        p1.add(title);
        
        p2.add(b1);
        p2.add(b2);
        
        p3.add(field);
        
        c.add(p);
        fr.show();
    }
    
    public void actionPerformed(ActionEvent e) {};
    
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == b1) {
            b1.setBackground(Color.red);
        };
    };
    
    public void mouseClicked(MouseEvent e) {};
    
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == b1) {
            b1.setBackground(Color.yellow);
        };
    };
    
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == title) {
            title.setText("Noah Yang");
        };
        if (e.getSource() == b2) {
            field.setText("Centennial Cougars");
        };
    };
    
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == title) {
            title.setText("10th Grade");
        };
    };
}
