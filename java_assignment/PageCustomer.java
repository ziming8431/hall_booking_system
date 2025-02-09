package java_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PageCustomer extends JFrame implements ActionListener {
    private String userID;
    private JButton reportIssueButton, updateProfileButton, makeBookingButton, 
                    viewBookingHistoryButton, viewUpcomingBookingButton, logoutButton, 
                    viewHallDetailsButton; // Add this line
    private JPanel mainPanel;
    private final Color BUTTON_COLOR = new Color(70, 130, 180);
    private final Color BUTTON_HOVER_COLOR = new Color(60, 110, 160); // Darker shade for hover

    public PageCustomer(String userID) {
        this.userID = userID;
        setupUI();
    }

    private void setupUI() {
        setTitle("Customer Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(createTitleLabel("Customer Dashboard"), gbc);

        reportIssueButton = createStyledButton("Report Issue", new ImageIcon("path/to/report_icon.png"));
        updateProfileButton = createStyledButton("Update Profile", new ImageIcon("path/to/profile_icon.png"));
        makeBookingButton = createStyledButton("Make Booking", new ImageIcon("path/to/booking_icon.png"));
        viewBookingHistoryButton = createStyledButton("Booking History", new ImageIcon("path/to/history_icon.png"));
        viewUpcomingBookingButton = createStyledButton("Upcoming Bookings", new ImageIcon("path/to/upcoming_icon.png"));
        logoutButton = createStyledButton("Logout", new ImageIcon("path/to/logout_icon.png"));
        viewHallDetailsButton = createStyledButton("View Hall Details", new ImageIcon("path/to/details_icon.png")); // Add this line

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(reportIssueButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(updateProfileButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(viewBookingHistoryButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(makeBookingButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(viewUpcomingBookingButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewHallDetailsButton, gbc); // Add this line

        // Add a new row for the logout button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span across both columns
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the button
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        mainPanel.add(logoutButton, gbc);

        setVisible(true);
    }

    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener(this);
        
        if (text.equals("Logout")) {
            button.setPreferredSize(new Dimension(150, 50));
        } else {
            button.setPreferredSize(new Dimension(300, 60));
        }
        
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        
        button.setIconTextGap(10);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(new Color(50, 50, 50));
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reportIssueButton) {
            reportIssue();
        } else if (e.getSource() == updateProfileButton) {
            new UpdateProfile(userID);
        } else if (e.getSource() == makeBookingButton) {
            new Makehallbooking(userID, this);
            this.setVisible(false);
        } else if (e.getSource() == viewBookingHistoryButton) {
            new Viewbookinghistory(userID, this);
            this.setVisible(false);
        } else if (e.getSource() == viewUpcomingBookingButton) {
            new Viewupcomingbooking(userID, this);
            this.setVisible(false);
        } else if (e.getSource() == logoutButton) {
            logout();
        } else if (e.getSource() == viewHallDetailsButton) {
            new HallDetailsPage(this, userID);
            this.setVisible(false);
        }
    }

    private void reportIssue() {
        JTextField descriptionField = new JTextField();
        JComboBox<String> hallNameComboBox = new JComboBox<>(getHallNames());

        Object[] message = {
            "Description (max 200 characters):", descriptionField,
            "Hall Name:", hallNameComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Report Issue", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String description = descriptionField.getText().trim();
            String hallName = (String) hallNameComboBox.getSelectedItem();

            if (validateInputs(description, hallName)) {
                String issueID = FileHandler.generateIssueID();
                CustomerIssue newIssue = new CustomerIssue(issueID, userID, description, "Reported", "", hallName);
                FileHandler.addCustomerIssue(newIssue);
                JOptionPane.showMessageDialog(this, "Issue reported successfully. Issue ID: " + issueID);
            }
        }
    }

    private String[] getHallNames() {
        Set<String> uniqueHallNames = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length > 0) {
                    uniqueHallNames.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading hall names from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return uniqueHallNames.toArray(new String[0]);
    }

    private boolean validateInputs(String description, String hallName) {
        if (description.isEmpty() || hallName == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }

        if (description.length() > 200) {
            JOptionPane.showMessageDialog(this, "Description must be 200 characters or less.");
            return false;
        }

        return true;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LandingPage();
            dispose();
        }
    }
}