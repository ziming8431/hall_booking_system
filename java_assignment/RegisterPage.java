package java_assignment;

import java.awt.event.*;
import javax.swing.*;

public class RegisterPage extends JFrame implements ActionListener {

    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;

    public RegisterPage() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Register");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 20, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 165, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 50, 165, 25);
        add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm:");
        confirmPasswordLabel.setBounds(10, 80, 80, 25);
        add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(100, 80, 165, 25);
        add(confirmPasswordField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 110, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(100, 110, 165, 25);
        add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(10, 140, 80, 25);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(100, 140, 165, 25);
        add(phoneField);

        registerButton = new JButton("Register");
        registerButton.setBounds(10, 180, 100, 25);
        registerButton.addActionListener(this);
        add(registerButton);

        backButton = new JButton("Back");
        backButton.setBounds(120, 180, 100, 25);
        backButton.addActionListener(this);
        add(backButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            registerUser();
        } else if (e.getSource() == backButton) {
            new LandingPage();
            dispose();
        }
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
 
        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.length() < 3 || password.length() < 3) {
            JOptionPane.showMessageDialog(this, "Username and password must be at least 3 characters long!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid phone number format!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userID = FileHandler.generateUserID("C");
        User newUser = new User(userID, "Customer", username, password, email, phone);
        FileHandler.addUser(newUser);

        JOptionPane.showMessageDialog(this, "Registration successful! Your User ID is: " + userID);
        new Login();
        dispose();
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        // Simple phone number validation (assumes a 10-digit number)
        return phone.matches("\\d{10}");
    }
}