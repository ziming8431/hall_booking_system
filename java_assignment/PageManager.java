package java_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PageManager extends JFrame implements ActionListener {

    private String userID;
    private JButton viewIssuesButton, viewSalesDashboardButton, logoutButton;
    private JPanel mainPanel;
    private final Color BUTTON_COLOR = new Color(70, 130, 180);
    private final Color BUTTON_HOVER_COLOR = new Color(60, 110, 160); // Darker shade for hover

    public PageManager(String userID) {
        this.userID = userID;
        setupUI();
    }

    private void setupUI() {
        setTitle("Manager Dashboard");
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
        mainPanel.add(createTitleLabel("Manager Dashboard"), gbc);

        viewIssuesButton = createStyledButton("View Customer Issues", new ImageIcon("path/to/issues_icon.png"));
        viewSalesDashboardButton = createStyledButton("View Sales Dashboard", new ImageIcon("path/to/sales_icon.png"));
        logoutButton = createStyledButton("Logout", new ImageIcon("path/to/logout_icon.png"));

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(viewIssuesButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewSalesDashboardButton, gbc);

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
        if (e.getSource() == viewIssuesButton) {
            viewCustomerIssues();
        } else if (e.getSource() == viewSalesDashboardButton) {
            viewSalesDashboard();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }

    private void viewCustomerIssues() {
        List<CustomerIssue> issues = FileHandler.getCustomerIssues();
        new CustomerIssuesDialog(this, issues);
    }

    private void viewSalesDashboard() {
       new SalesDashboard(this);
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


