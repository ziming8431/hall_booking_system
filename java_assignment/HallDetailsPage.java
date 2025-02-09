package java_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class HallDetailsPage extends JFrame {

    private JFrame previousFrame;
    private String userID;

    public HallDetailsPage(JFrame previousFrame, String userID) {
        this.previousFrame = previousFrame;
        this.userID = userID;
        setTitle("Hall Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Hall Details", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));

        mainPanel.add(createHallPanel("Auditorium", "A large hall with seating for 1000, ideal for conferences, concerts, and corporate events. Booking Rate is RM 300.00 per hour", "C:\\Users\\ASUS\\Downloads\\java_pre\\java_assignment\\audi.png"));
        mainPanel.add(createHallPanel("Banquet Hall", "A stylish space for up to 300 guests, perfect for weddings, corporate dinners, and seminars. Booking Rate is RM 100.00 per hour", "C:\\Users\\ASUS\\Downloads\\java_pre\\java_assignment\\banq.png"));
        mainPanel.add(createHallPanel("Meeting Room", "A small, professional room for 30 people, suitable for business meetings, workshops, and interviews. Booking Rate is RM 50.00 per hour", "C:\\Users\\ASUS\\Downloads\\java_pre\\java_assignment\\meeting.png"));

        add(mainPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            this.dispose();
            previousFrame.setVisible(true);
        });
        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHallPanel(String hallType, String details, String imagePath) {
        JPanel hallPanel = new JPanel();
        hallPanel.setLayout(new BorderLayout(20, 20));

        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(280, 155, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        hallPanel.add(imageLabel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());

        JLabel hallNameLabel = new JLabel(hallType);
        hallNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        detailsPanel.add(hallNameLabel, BorderLayout.NORTH);

        JTextArea hallDetailsArea = new JTextArea(details);
        hallDetailsArea.setWrapStyleWord(true);
        hallDetailsArea.setLineWrap(true);
        hallDetailsArea.setEditable(false);
        hallDetailsArea.setOpaque(false);
        hallDetailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        hallDetailsArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        detailsPanel.add(hallDetailsArea, BorderLayout.CENTER);

        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel ratingLabel = new JLabel("Rating: ");
        ratingPanel.add(ratingLabel);

        // Load and display the average rating
        try {
            HallRating hallRating = new HallRating(hallType);
            int averageRating = hallRating.calculateAverageRating();
            updateStarRating(ratingPanel, averageRating);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JButton makeRatingButton = new JButton("Make Rating");
        ratingPanel.add(makeRatingButton);

        makeRatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String input = JOptionPane.showInputDialog("Rate " + hallType + " from 0 to 5:");
                    int rating = Integer.parseInt(input);
                    if (rating >= 0 && rating <= 5) {
                        HallRating hallRating = new HallRating(hallType);
                        try {
                            hallRating.recordRating(rating, userID);
                            int averageRating = hallRating.calculateAverageRating();
                            updateStarRating(ratingPanel, averageRating);
                            JOptionPane.showMessageDialog(null, "Rating recorded successfully!");
                        } catch (IllegalStateException ex) {
                            JOptionPane.showMessageDialog(null, "You have already rated this hall.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a number between 0 and 5.");
                    }
                } catch (IOException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please try again.");
                }
            }
        });

        detailsPanel.add(ratingPanel, BorderLayout.SOUTH);

        hallPanel.add(detailsPanel, BorderLayout.CENTER);

        return hallPanel;
    }

    private void updateStarRating(JPanel ratingPanel, int averageRating) {
        // Remove existing star labels
        Component[] components = ratingPanel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i] instanceof JLabel && ((JLabel) components[i]).getText().equals("★")) {
                ratingPanel.remove(components[i]);
            }
        }

        // Add new star labels based on the average rating
        for (int i = 1; i <= 5; i++) {
            JLabel starLabel = new JLabel("★");
            starLabel.setForeground(i <= averageRating ? Color.YELLOW : Color.LIGHT_GRAY);
            ratingPanel.add(starLabel, 1); // Add after the "Rating: " label
        }

        ratingPanel.revalidate();
        ratingPanel.repaint();
    }
}
