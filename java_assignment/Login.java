package java_assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {
    
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton;

    public Login() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel userIDLabel = new JLabel("User ID:");
        userIDLabel.setBounds(50, 30, 80, 25);
        add(userIDLabel);

        userIDField = new JTextField();
        userIDField.setBounds(140, 30, 100, 25);
        add(userIDField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 60, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 60, 100, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(50, 100, 90, 25);
        loginButton.addActionListener(this);
        add(loginButton);

        backButton = new JButton("Back");
        backButton.setBounds(150, 100, 90, 25);
        backButton.addActionListener(this);
        add(backButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            login();
        } else if (e.getSource() == backButton) {
            new LandingPage();
            dispose();
        }
    }

    private void login() {
        String userID = userIDField.getText();
        String password = new String(passwordField.getPassword());

        if (userID.length() < 3 || password.length() < 3) {
            JOptionPane.showMessageDialog(this, "User ID and password must be at least 3 characters long.");
            return;
        }

        User user = FileHandler.authenticateUser(userID, password);
        if (user != null) {
            System.out.println("Login successful for user: " + user.getUserID() + ", Role: " + user.getRole());
            JOptionPane.showMessageDialog(this, "Login successful!");
            openUserPage(user);
        } else {
            System.out.println("Login failed for userID: " + userID);
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
        }
    }

    private void openUserPage(User user) {
        System.out.println("Opening page for role: " + user.getRole());
        switch (user.getRole()) {
            case "Customer":
                new PageCustomer(user.getUserID());
                break;
            case "Manager":
                new PageManager(user.getUserID());
                break;
            case "Scheduler":
                new PageScheduler(user.getUserID());
                break;
            case "admin":
                new PageAdmin("admin");
                break;
            case "superadmin":
                new PageAdmin("superadmin");
                break;
            default:
                System.out.println("Unknown role: " + user.getRole());
                break;
        }
        dispose();
    }
}