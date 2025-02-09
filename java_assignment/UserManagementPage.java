package java_assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class UserManagementPage extends JFrame implements ActionListener {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;
    private JComboBox<String> roleFilterComboBox;
    private TableRowSorter<DefaultTableModel> sorter;

    public UserManagementPage() {
        setupUI();
    }

    private void setupUI() {
        setTitle("User Management");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        String[] columns = {"User ID", "Username", "Role", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);
        
        loadUserData();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(50, 100, 500, 200);
        add(scrollPane);

        // Add role filter
        JLabel filterLabel = new JLabel("Filter by Role:");
        filterLabel.setBounds(50, 50, 100, 30);
        add(filterLabel);

        String[] roles = {"All", "Customer", "Manager", "Scheduler"};
        roleFilterComboBox = new JComboBox<>(roles);
        roleFilterComboBox.setBounds(150, 50, 150, 30);
        roleFilterComboBox.addActionListener(this);
        add(roleFilterComboBox);

        addButton = new JButton("Add");
        addButton.setBounds(50, 350, 100, 30);
        addButton.addActionListener(this);
        add(addButton);

        editButton = new JButton("Edit");
        editButton.setBounds(170, 350, 100, 30);
        editButton.addActionListener(this);
        add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(290, 350, 100, 30);
        deleteButton.addActionListener(this);
        add(deleteButton);

        backButton = new JButton("Back");
        backButton.setBounds(410, 350, 100, 30);
        backButton.addActionListener(this);
        add(backButton);

        setVisible(true);
    }

    private void loadUserData() {
        tableModel.setRowCount(0);
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 6 && !parts[1].equals("admin") && !parts[1].equals("superadmin")) {
                    tableModel.addRow(new Object[]{parts[0], parts[2], parts[1], parts[4], parts[5]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addUser();
        } else if (e.getSource() == editButton) {
            editUser();
        } else if (e.getSource() == deleteButton) {
            deleteUser();
        } else if (e.getSource() == backButton) {
            new PageAdmin("admin");
            dispose();
        }else if (e.getSource() == roleFilterComboBox) {
            filterUsersByRole();
        }
    }

    private void addUser() {
        String[] roles = {"Customer", "Manager", "Scheduler"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] message = {
            "Role:", roleBox,
            "Username:", usernameField,
            "Password:", passwordField,
            "Email:", emailField,
            "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String role = (String) roleBox.getSelectedItem();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String phone = phoneField.getText();

            String userID = FileHandler.generateUserID(role.substring(0, 1));
            User newUser = new User(userID, role, username, password, email, phone);
            FileHandler.addUser(newUser);
            loadUserData();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String userID = (String) tableModel.getValueAt(selectedRow, 0);
            User user = FileHandler.getUserByID(userID);

            JTextField usernameField = new JTextField(user.getUsername());
            JPasswordField passwordField = new JPasswordField(user.getPassword());
            JTextField emailField = new JTextField(user.getEmail());
            JTextField phoneField = new JTextField(user.getPhone());

            Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Email:", emailField,
                "Phone:", phoneField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Edit User", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                user.setUsername(usernameField.getText());
                user.setPassword(new String(passwordField.getPassword()));
                user.setEmail(emailField.getText());
                user.setPhone(phoneField.getText());

                FileHandler.updateUser(user);
                loadUserData();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String userID = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Delete User", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                FileHandler.deleteUser(userID);
                loadUserData();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }
    private void filterUsersByRole() {
        String selectedRole = (String) roleFilterComboBox.getSelectedItem();
        if (selectedRole.equals("All")) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(selectedRole, 2)); // 2 is the index of the Role column
        }
    }
}