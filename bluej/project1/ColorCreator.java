/**
 * Color picker with text labels
 *
 * @author Noah Yang
 * @version 0.1
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ColorCreator implements ActionListener, ChangeListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3, p4, p5;
    
    JLabel rt, gt, bt, title;
    JSlider rs, gs, bs;
    JTextField rv, gv, bv;
    JButton newcolor, random;
    int red, green, blue;
    int rsval, gsval, bsval;
    Color initColor = Color.gray;
    
    public ColorCreator() {       
        fr = new JFrame("Color Creator");
        fr.setBounds(150, 100, 400, 300);
        fr.setResizable(false);
        //fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(initColor);
        GridLayout grid = new GridLayout(5,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        
        p1.setBackground(initColor);
        p2.setBackground(initColor);
        p3.setBackground(initColor);
        p4.setBackground(initColor);
        p5.setBackground(initColor);
        
        title = new JLabel("Color Creator");
        Font tfnt = new Font("Ariel", Font.BOLD, 20);
        title.setFont(tfnt);
        
        rt = new JLabel("Red Value: ");
        gt = new JLabel("Green Value: ");
        bt = new JLabel("Blue Value: ");
        
        rs = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        rs.addChangeListener(this);
        rs.setMajorTickSpacing(50);       
        rs.setPaintTicks(true);              
        rs.setPaintLabels(true);
        rs.setBackground(initColor);
        
        gs = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        gs.addChangeListener(this);
        gs.setMajorTickSpacing(50);       
        gs.setPaintTicks(true);
        gs.setPaintLabels(true);
        gs.setBackground(initColor);
        
        bs = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        bs.addChangeListener(this);
        bs.setMajorTickSpacing(50);       
        bs.setPaintTicks(true);              
        bs.setPaintLabels(true);
        bs.setBackground(initColor);
        
        rv = new JTextField(3);
        gv = new JTextField(3);
        bv = new JTextField(3);
        
        rv.setText("0");
        gv.setText("0");
        bv.setText("0");
        
        newcolor = new JButton("Create Color (from fields)");
        random = new JButton("Random Color");
        newcolor.addActionListener(this);
        random.addActionListener(this);
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        p.add(p5);
        
        p1.add(title);
        
        p2.add(rt);
        p2.add(rv);
        p2.add(rs);
        
        p3.add(gt);
        p3.add(gv);
        p3.add(gs);
        
        p4.add(bt);
        p4.add(bv);
        p4.add(bs);
        
        p5.add(newcolor);
        p5.add(random);
        
        c.add(p);
        fr.show();
    }
    
    private void changeBg(Color rgb) {
        p.setBackground(rgb);
        p1.setBackground(rgb);
        p2.setBackground(rgb);
        p3.setBackground(rgb);
        p4.setBackground(rgb);
        p5.setBackground(rgb);
        rs.setBackground(rgb);
        gs.setBackground(rgb);
        bs.setBackground(rgb);
    };
    
    private int rngV2(int min, int max) {
        int result = 0;
        int range = (max - min) + 1;
        result = (int)(Math.random() * range) + min;
        return result;
    };
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == newcolor) {
            try {
                red = (int)Double.parseDouble(rv.getText());
                green = (int)Double.parseDouble(gv.getText());
                blue = (int)Double.parseDouble(bv.getText());
                rs.setValue(red);
                gs.setValue(green);
                bs.setValue(blue);
                Color newbg = new Color(red, green, blue);
                changeBg(newbg);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number for all fields");
            }
        };
        if (event.getSource() == random) {
            // range 0 - 255
            //red = (int)(Math.random() * 256);
            //green = (int)(Math.random() * 256);
            //blue = (int)(Math.random() * 256);
            red = rngV2(0, 255);
            green = rngV2(0, 255);
            blue = rngV2(0, 255);
            rv.setText(Integer.toString(red));
            gv.setText(Integer.toString(green));
            bv.setText(Integer.toString(blue));
            rs.setValue(red);
            gs.setValue(green);
            bs.setValue(blue);
            
            // stateChanged already changes color
            //Color newbg = new Color(red, green, blue);
            //changeBg(newbg);
        };
    };
    
    public void stateChanged(ChangeEvent event) {
        if (event.getSource() == rs) {
            rv.setText(Integer.toString(rs.getValue()));
        };
        if (event.getSource() == gs) {
            gv.setText(Integer.toString(gs.getValue()));
        };
        if (event.getSource() == bs) {
            bv.setText(Integer.toString(bs.getValue()));
        };
        try {
            red = (int)Double.parseDouble(rv.getText());
            green = (int)Double.parseDouble(gv.getText());
            blue = (int)Double.parseDouble(bv.getText());
            Color newbg = new Color(red, green, blue);
            changeBg(newbg);
        } catch (NumberFormatException e) {}
    };
}
