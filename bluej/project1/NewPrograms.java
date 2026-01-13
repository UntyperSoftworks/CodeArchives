
/**
 * Window for opening programs: pythagorean solver or two-point solver
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NewPrograms implements ActionListener
{
    JFrame fr;
    Container c;
    
    JPanel p, p1, p2;
    
    JMenuBar menuBar;
    JMenu math, games, misc;
    JMenuItem pyth, twopt, numguess, dice, card, color, mouse;
    
    JLabel title;
    
    Color framergb = new Color(47, 137, 216);
    Color fieldrgb = Color.gray;
    Color buttonrgb = new Color(112, 177, 199);
    
    private JMenuItem createItem(String name, JMenu parent) {
        JMenuItem item = new JMenuItem(name);
        parent.add(item);
        item.addActionListener(this);
        item.setBackground(buttonrgb);
        return item;
    };
    
    public NewPrograms()
    {
        fr = new JFrame("Choose a Program");
        // 600, 500 bounds is too much for the computer
        // half of the values for now
        fr.setBounds(300, 250, 300, 200);
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
        
        title = new JLabel("Choose a Program");
        Font titlefont = new Font("Ariel", Font.BOLD, 20);
        title.setFont(titlefont);
        
        title.setForeground(Color.white);
        
        menuBar = new JMenuBar();
        
        math = new JMenu("Math");
        games = new JMenu("Games");
        misc = new JMenu("Misc");
        
        pyth = createItem("Pythagorean Theorem", math);
        twopt = createItem("Two-Point Solver", math);
        
        numguess = createItem("Number Guess", games);
        dice = createItem("Dice Roller", games);
        card = createItem("Card Drawer", games);
        
        color = createItem("Color Creator", misc);
        mouse = createItem("Mouse Stuff", misc);
        
        menuBar.add(math);
        menuBar.add(games);
        menuBar.add(misc);
        
        p.add(p1);
        p.add(p2);
        
        // title
        p1.add(title);
        
        // dropdown
        p2.add(menuBar);
        
        c.add(p);
        fr.show();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == pyth) { // pythagorean
            new PythagoreanSolver();
        };
        if (event.getSource() == twopt) { // two-point
            new TwoPointSolver();
        };
        if (event.getSource() == numguess) { // number guesser
            new NumberGuesser();
        };
        if (event.getSource() == dice) { // dice roller
            new DiceRoller();
        };
        if (event.getSource() == card) { // card drawer
            new CardDrawer();
        };
        if (event.getSource() == color) { // color creator
            new ColorCreator();
        };
        if (event.getSource() == mouse) { // mouse stuff
            new MouseStuff();
        };
    };
}
