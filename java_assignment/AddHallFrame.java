package java_assignment;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class AddHallFrame extends JFrame implements ActionListener {
    private JTextField hallNameField;
    private JTextField hallPriceField;
    private JTextField capacityField;
    private JButton addButton;
    private JButton backButton;
    private JComboBox<String> hallTypeBox;
    private JFrame previousFrame;

    public AddHallFrame(JFrame previousFrame) {
        this.previousFrame = previousFrame;
        this.setTitle("Hall Booking System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(400, 250);
        this.setLayout(null);
        this.setLocationRelativeTo(null);

        JLabel hallNameLabel = new JLabel("Enter hall name:");
        hallNameLabel.setBounds(30, 20, 120, 25);
        this.add(hallNameLabel);

        hallNameField = new JTextField();
        hallNameField.setBounds(180, 20, 160, 25);
        this.add(hallNameField);


        JLabel hallTypeLabel = new JLabel("Hall Type:");
        hallTypeLabel.setBounds(30, 60, 120, 25);
        this.add(hallTypeLabel);

        String[] hallTypes = {"Auditorium", "Banquet Hall", "Meeting Room"};
        hallTypeBox = new JComboBox<>(hallTypes);
        hallTypeBox.setBounds(180, 60, 160, 25);
        this.add(hallTypeBox);
        hallTypeBox.addActionListener(this);

        JLabel hallPriceLabel = new JLabel("Hall Rate/Hr:");
        hallPriceLabel.setBounds(30, 100, 120, 25);
        this.add(hallPriceLabel);

        hallPriceField = new JTextField();
        hallPriceField.setBounds(180, 100, 160, 25);
        this.add(hallPriceField);
        setDefaultPrice();


        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setBounds(30, 140, 120, 25);
        this.add(capacityLabel);

        capacityField = new JTextField();
        capacityField.setBounds(180, 140, 160, 25);
        this.add(capacityField);
        setCapacity();

        addButton = new JButton("Add");
        addButton.setBounds(150, 180, 100, 30);
        addButton.addActionListener(this);
        this.add(addButton);

        backButton = new JButton("Back");
        backButton.setBounds(260, 180, 100, 30);
        backButton.addActionListener(this);
        this.add(backButton);


        this.setVisible(true);


    }

    private void setDefaultPrice() {
        String selectedHallType = (String) hallTypeBox.getSelectedItem();
        switch (selectedHallType) {
            case ("Auditorium"):
                hallPriceField.setText("300");
                hallPriceField.setEditable(false);
                break;
            case ("Banquet Hall"):
                hallPriceField.setText("100");
                hallPriceField.setEditable(false);
                break;
            case ("Meeting Room"):
                hallPriceField.setText("50");
                hallPriceField.setEditable(false);
                break;
        }
    }

    private void setCapacity() {
        String selectedHallType = (String) hallTypeBox.getSelectedItem();
        switch (selectedHallType) {
            case ("Auditorium"):
                capacityField.setText("1000");
                capacityField.setEditable(false);
                break;
            case ("Banquet Hall"):
                capacityField.setText("300");
                capacityField.setEditable(false);
                break;
            case ("Meeting Room"):
                capacityField.setText("30");
                capacityField.setEditable(false);
                break;
        }
    }

    private boolean StringCheck(String input) {
        if (input != null) {  // First, check if the input is not null
            input = input.replaceAll(" ", "");  // Remove all spaces
            if (input.matches("[a-zA-Z]{4,}")) {  // Check if the remaining string has at least 4 alphabetic characters
                return true;
            }
        }
        return false;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (StringCheck(hallNameField.getText())) {
                String hallName = hallNameField.getText().trim();
                String hallType = (String) hallTypeBox.getSelectedItem();
                String capacity = capacityField.getText();
                String hallPrice = hallPriceField.getText();
                String[] hallInfo = {hallName, hallType, hallPrice, capacity};

                if (isHallNameExists(hallName)) {
                    JOptionPane.showMessageDialog(null, "A hall with this name already exists. Please choose a different name.", "Duplicate Hall Name", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PrintWriter hallFile = new PrintWriter(new FileWriter("Hall.txt", true))) {
                    String hallInfoLine = String.join(",", hallInfo);
                    hallFile.write(hallInfoLine);
                    hallFile.write("\n");
                    JOptionPane.showMessageDialog(null, "Hall Added Successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding hall: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Hall Name must contain a minimum of 4 characters, consisting of (a-z) or (A-Z) characters only.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == hallTypeBox) {
            setDefaultPrice();
            setCapacity();
        } else if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        }
    }

    private boolean isHallNameExists(String hallName) {
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(hallName.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking hall names: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void clearFields() {
        hallNameField.setText("");
        hallTypeBox.setSelectedIndex(0);
        setDefaultPrice();
        setCapacity();
    }
}


