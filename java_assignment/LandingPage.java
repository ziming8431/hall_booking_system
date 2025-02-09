package java_assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LandingPage extends JFrame implements ActionListener {

    private JButton loginButton;
    private JButton registerButton;

    public LandingPage() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Welcome to Hall Symphony Inc.");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to Hall Booking System");
        welcomeLabel.setBounds(40, 30, 220, 25);
        add(welcomeLabel);

        loginButton = new JButton("Login");
        loginButton.setBounds(50, 80, 90, 25);
        loginButton.addActionListener(this);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(160, 80, 90, 25);
        registerButton.addActionListener(this);
        add(registerButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            new Login();
            dispose();
        } else if (e.getSource() == registerButton) {
            new RegisterPage();
            dispose();
        }
    }
}