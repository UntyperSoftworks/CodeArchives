/**
 * Number guessing game
 *
 * @author Noah Yang
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NumberGuesser implements ActionListener
{
    JFrame fr;
    Container c;

    JPanel p, p1, p2, p3, p4;

    JLabel desc, results, guesst, title;
    JTextField input;
    JButton submit, restart;

    String info;
    int inputNumber;
    int answer = (int)(Math.random() * 100) + 1;
    int guesses = 0;
    boolean correct = false;

    public NumberGuesser()
    {
        fr = new JFrame("Number Guesser");
        fr.setBounds(150, 100, 350, 250);
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
        
        title = new JLabel("Number Guesser");
        Font tfnt = new Font("Ariel", Font.BOLD, 20);
        title.setFont(tfnt);

        desc = new JLabel("Guess a Number between 1-100");
        guesst = new JLabel("Guesses: 0");
        input = new JTextField(10);
        submit = new JButton("Submit");
        restart = new JButton("Restart");
        results = new JLabel("");
        submit.addActionListener(this);
        restart.addActionListener(this);

        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        
        p1.add(title);

        p2.add(desc);

        p3.add(input);
        p3.add(submit);
        p3.add(restart);

        p4.add(guesst);
        p4.add(results);

        c.add(p);
        fr.show();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == submit && correct == false) {
            info = input.getText();
            try {
                inputNumber = (int)Double.parseDouble(info);
                input.setText(Integer.toString(inputNumber));
                if (inputNumber >= 1 && inputNumber <= 100) {
                    if (inputNumber < answer) {
                        results.setText("Answer is higher");
                        answer = answer + 1;
                        if (answer > 100) answer = 100;
                    };
                    if (inputNumber > answer) {
                        results.setText("Answer is lower");
                        answer = answer + 1;
                        if (answer > 100) answer = 100;
                    };
                    if (inputNumber == answer) {
                        results.setText("Correct!");
                        correct = true;
                    };
                    guesses = guesses + 1;
                    guesst.setText("Guesses: " + guesses);
                } else {
                    results.setText("Number must be in range (1-100)");
                };
            } catch (NumberFormatException e) {
                results.setText("Please enter a number (integer)");
            }
        };
        if (event.getSource() == restart) {
            answer = (int)(Math.random() * 100) + 1;
            guesses = 0;
            input.setText("");
            guesst.setText("Guesses: 0");
            results.setText("Restarted!");
            correct = false;
        };
    };
}
