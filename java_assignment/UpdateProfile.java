package java_assignment;
 
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class UpdateProfile extends JFrame implements ActionListener {

    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JButton updateButton, backButton;
    private String userID;
    private ArrayList<String> lines=new ArrayList<>();

    public UpdateProfile(String userID){
        this.userID=userID;
        setupUI();
        loaduserData();
    }

    private void setupUI(){   //layout
        setTitle("Update Profile");
        setSize(290, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 20, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 130, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 60, 130, 25);
        add(passwordField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 100, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(100, 100, 130, 25);
        add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(10, 140, 80, 25);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(100, 140, 130, 25);
        add(phoneField);

        updateButton = new JButton("Update");
        updateButton.setBounds(10, 200, 100, 25);
        updateButton.addActionListener(this);
        add(updateButton);

        backButton = new JButton("Back");
        backButton.setBounds(160,200, 70, 25);
        backButton.addActionListener(this);
        add(backButton);

        setVisible(true);
    }


    private void loaduserData() {  //load user data from file
        lines.clear(); 
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
                String[] data = line.split(",");
                if (data[0].equals(userID)) {
                    usernameField.setText(data[2]);
                    passwordField.setText(data[3]);
                    emailField.setText(data[4]);
                    phoneField.setText(data[5]);
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: User data file not found.");
        }
    }
    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }



    private void updateuserData() { //check validation and update user data back to the file
        if (!validation()) {
            return;
        }
        String email = emailField.getText().trim();
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("users.txt"))) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] data = line.split(",");
                
                if (data[0].equals(userID)) {
                    line = String.join(",", 
                        data[0], 
                        data[1], 
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        phoneField.getText()
                    );
                    lines.set(i, line); 
                }
                writer.println(line);
            }
            JOptionPane.showMessageDialog(null, "Update Successful!");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating user data: " + e.getMessage());
        }
    }


    private boolean validation() { //validation for text fields
        if (usernameField.getText().isEmpty() || 
            passwordField.getPassword().length == 0 ||
            emailField.getText().isEmpty() ||
            phoneField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Text fields cannot be empty.");
            return false;
        }
        return true;
    }

    @Override  //action
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton) {
            updateuserData();
        } else if (e.getSource() == backButton) {
            this.dispose();
        } 
    }
    
}