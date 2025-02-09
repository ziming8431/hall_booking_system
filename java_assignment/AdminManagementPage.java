package java_assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class AdminManagementPage extends JFrame implements ActionListener {

    private JTable adminTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;

    public AdminManagementPage() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Admin Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        String[] columns = {"User ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
        public boolean isCellEditable(int row, int column) {
            return false; // All cells are not editable
        }
    };
        adminTable = new JTable(tableModel);
        loadAdminData();

        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.setBounds(50, 50, 500, 200);
        add(scrollPane);

        addButton = new JButton("Add");
        addButton.setBounds(50, 300, 100, 30);
        addButton.addActionListener(this);
        add(addButton);

        editButton = new JButton("Edit");
        editButton.setBounds(170, 300, 100, 30);
        editButton.addActionListener(this);
        add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(290, 300, 100, 30);
        deleteButton.addActionListener(this);
        add(deleteButton);

        backButton = new JButton("Back");
        backButton.setBounds(410, 300, 100, 30);
        backButton.addActionListener(this);
        add(backButton);

        setVisible(true);
    }

    private void loadAdminData() {
        tableModel.setRowCount(0);
        try (Scanner scanner = new Scanner(new File("admin.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if (data.length == 4 && !data[1].equals("superadmin")) {
                    tableModel.addRow(new Object[]{data[0], data[2], data[1]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addAdmin();
        } else if (e.getSource() == editButton) {
            editAdmin();
        } else if (e.getSource() == deleteButton) {
            deleteAdmin();
        } else if (e.getSource() == backButton) {
            new PageAdmin("superadmin");
            dispose();
        }
    }

    private void addAdmin() {
        JTextField userIDField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "User ID:", userIDField,
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Admin", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String userID = userIDField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userID.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            Admin newAdmin = new Admin(userID, "admin", username, password);
            FileHandler.allAdmins.add(newAdmin);
            FileHandler.writeAdmins();
            loadAdminData();
            JOptionPane.showMessageDialog(this, "Admin added successfully!");
        }
    }

    private void editAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow != -1) {
            String userID = (String) tableModel.getValueAt(selectedRow, 0);
            Admin admin = FileHandler.allAdmins.stream()
                    .filter(a -> a.getUserID().equals(userID))
                    .findFirst()
                    .orElse(null);

            if (admin != null) {
                JTextField usernameField = new JTextField(admin.getUsername());
                JPasswordField passwordField = new JPasswordField(admin.getPassword());

                Object[] message = {
                    "Username:", usernameField,
                    "Password:", passwordField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Edit Admin", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    admin.setUsername(usernameField.getText());
                    admin.setPassword(new String(passwordField.getPassword()));

                    FileHandler.writeAdmins();
                    loadAdminData();
                    JOptionPane.showMessageDialog(this, "Admin updated successfully!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an admin to edit.");
        }
    }

    private void deleteAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow != -1) {
            String userID = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this admin?", 
                "Delete Admin", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                FileHandler.allAdmins.removeIf(admin -> admin.getUserID().equals(userID));
                FileHandler.writeAdmins();
                loadAdminData();
                JOptionPane.showMessageDialog(this, "Admin deleted successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an admin to delete.");
        }
    }
}