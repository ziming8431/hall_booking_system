package java_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PageAdmin extends JFrame implements ActionListener {

    private String role;
    private JButton manageUsersButton, manageAdminsButton, manageBookingButton, logoutButton;
    private JPanel mainPanel;
    private final Color BUTTON_COLOR = new Color(70, 130, 180);
    private final Color BUTTON_HOVER_COLOR = new Color(60, 110, 160); // Darker shade for hover

    public PageAdmin(String role) {
        this.role = role;
        setupUI();
    }

    private void setupUI() {
        setTitle(role.equals("superadmin") ? "Superadmin Dashboardd" : "Admin Dashboard");
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
        mainPanel.add(createTitleLabel(role.equals("superadmin") ? "Superadmin Dashboard" : "Admin Dashboard"), gbc);

        if (role.equals("superadmin")) {
            manageAdminsButton = createStyledButton("Manage Admins", new ImageIcon("path/to/admin_icon.png"));
            gbc.gridy++;
            gbc.gridwidth = 2;
            mainPanel.add(manageAdminsButton, gbc);
        } else if (role.equals("admin")) {
            manageUsersButton = createStyledButton("Manage Users", new ImageIcon("path/to/users_icon.png"));
            manageBookingButton = createStyledButton("Manage Booking", new ImageIcon("path/to/booking_icon.png"));
            
            gbc.gridy++;
            gbc.gridwidth = 1;
            mainPanel.add(manageUsersButton, gbc);
            
            gbc.gridx = 1;
            mainPanel.add(manageBookingButton, gbc);
        }

        logoutButton = createStyledButton("Logout", new ImageIcon("path/to/logout_icon.png"));
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
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
        if (role.equals("superadmin") && e.getSource() == manageAdminsButton) {
            new AdminManagementPage();
            dispose();
        } else if (role.equals("admin") && e.getSource() == manageUsersButton) {
            new UserManagementPage();
            dispose();
        } else if (role.equals("admin") && e.getSource() == manageBookingButton) {
            new BookingManagementPage(this);
            setVisible(false); // Hide the PageAdmin window instead of disposing it
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LandingPage();
            dispose();
        }
    }

    public void returnFromBookingManagement() {
        setVisible(true); // Make the PageAdmin window visible again
    }

    public String getRole() {
        return this.role;
    }
}