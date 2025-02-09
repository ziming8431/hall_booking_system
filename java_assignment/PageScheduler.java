package java_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PageScheduler extends JFrame implements ActionListener {
    private JButton addHallButton, viewHallButton, setAvailabilityButton, viewTasksButton, logoutButton;
    private String userID;
    private JPanel mainPanel;
    private final Color BUTTON_COLOR = new Color(70, 130, 180);
    private final Color BUTTON_HOVER_COLOR = new Color(60, 110, 160); // Darker shade for hover

    public PageScheduler(String userID) {
        this.userID = userID;
        setupUI();
    }

    private void setupUI() {
        setTitle("Scheduler Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addHallButton = createStyledButton("Add New Hall", new ImageIcon("path/to/add_icon.png"));
        viewHallButton = createStyledButton("View Hall Information", new ImageIcon("path/to/view_icon.png"));
        setAvailabilityButton = createStyledButton("Set Hall Availability", new ImageIcon("path/to/calendar_icon.png"));
        viewTasksButton = createStyledButton("View Maintenance Tasks", new ImageIcon("path/to/task_icon.png"));
        logoutButton = createStyledButton("Logout", new ImageIcon("path/to/logout_icon.png"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(createTitleLabel("Scheduler Dashboard"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(addHallButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewHallButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(setAvailabilityButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewTasksButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Change to NONE for logout button
        gbc.anchor = GridBagConstraints.CENTER; // Center the logout button
        mainPanel.add(logoutButton, gbc);

        setVisible(true);
    }

    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // More solid appearance
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
        if (e.getSource() == addHallButton) {
            new AddHallFrame(this);
            this.setVisible(false);
        } else if (e.getSource() == viewHallButton) {
            new viewHallFrame(this);
            this.setVisible(false);
        } else if (e.getSource() == setAvailabilityButton) {
            new hallAvailabilityFrame(this);
            this.setVisible(false);
        } else if (e.getSource() == viewTasksButton) {
            viewAssignedTasks();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }

    protected void viewAssignedTasks() {
        List<CustomerIssue> assignedTasks = FileHandler.getAssignedTasks(userID);
        AssignedTasksDialog dialog = new AssignedTasksDialog(this, assignedTasks, userID);
        this.setVisible(false); // Hide the PageScheduler window
        dialog.setVisible(true);
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

    // Add this method to reopen PageScheduler
    public static void reopenPageScheduler(String userID) {
        new PageScheduler(userID);
    }

    public void returnFromDialog() {
        this.setVisible(true);
    }
}



