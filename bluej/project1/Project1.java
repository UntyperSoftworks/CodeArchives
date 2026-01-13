
/**
 * A login window which the correct username & password leads to programs
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Project1 implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2, p3, p4, p5;
    
    JTextField tf;
    JPasswordField pass;
    JButton b1, b2;
    JLabel title, lab, passText, message;
    String info, info2;
    
    Color framergb = new Color(47, 137, 216);
    Color fieldrgb = Color.gray;
    Color buttonrgb = new Color(57, 113, 162);
    
    public void closeSelf() {
        fr.dispose();
    };
    
    public Project1()
    {
        fr = new JFrame("Login");
        // 600, 500 bounds is too much for the computer
        // half of the values for now
        fr.setBounds(300, 250, 300, 400);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setResizable(false);
        c = fr.getContentPane();
        
        p = new JPanel();
        p.setBackground(framergb);
        GridLayout grid = new GridLayout(5,1);
        p.setLayout(grid);
        
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        
        p1.setBackground(framergb);
        p2.setBackground(framergb);
        p3.setBackground(framergb);
        p4.setBackground(framergb);
        p5.setBackground(framergb);
        
        tf = new JTextField(10);
        b1 = new JButton("Ok");
        b2 = new JButton("Cancel");
        lab = new JLabel("Username");
        passText = new JLabel("Password");
        pass = new JPasswordField(10);
        
        pass.setBackground(Color.yellow);
        pass.setForeground(fieldrgb);
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        
        b1.setBackground(buttonrgb);
        b2.setBackground(buttonrgb);
        
        title = new JLabel("Program Login");
        Font titlefont = new Font("Ariel", Font.ITALIC, 20);
        title.setFont(titlefont);
        
        title.setForeground(Color.white);
        
        message = new JLabel("");
        
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        p.add(p5);
        
        // title
        p1.add(title);
        
        // username
        p2.add(lab);
        p2.add(tf);
        
        // password
        p3.add(passText);
        p3.add(pass);
        
        // buttons
        p4.add(b1);
        p4.add(b2);
        
        // message
        p5.add(message);
        
        c.add(p);
        fr.show();
    }
    
    public void runLoginLogic() {
        info = tf.getText();
        info2 = pass.getText();
        if (info.equalsIgnoreCase("Bill") && info2.equals("Smith")) {
            //title.setText("Both Match");
            message.setText("Access Granted");
            new NewPrograms();
            closeSelf(); // doesn't fully close the app
        } else {
            message.setText("Access DENIED");
        };
    };
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == b1) {
            runLoginLogic();
        };
        if (event.getSource() == b2) {
            closeSelf();
        };
    };
}
