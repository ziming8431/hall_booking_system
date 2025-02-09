package java_assignment;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

public class hallMaintainance extends JFrame implements ActionListener {

    private CustomerIssue task;
    private JTextField issueField;
    private JTextField customerIDField;
    private JTextField descriptionField;
    private JTextField statusField;
    private JTextField hallNameField;
    private UtilDateModel startDateModel = new UtilDateModel();
    private UtilDateModel endDateModel = new UtilDateModel();
    private Properties properties = new Properties();
    private JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel, properties);
    private JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
    private JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel, properties);
    private JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Time format
    private String[] times = new String[24];
    private JComboBox<String> startTimeComboBox = new JComboBox<>();
    private JComboBox<String> endTimeComboBox = new JComboBox<>();
    private JTextArea commentsField = new JTextArea();
    private JButton backButton = new JButton("Back");
    private JButton saveAvaButton = new JButton("Save Schedule");
    private AssignedTasksDialog previousFrame;


    hallMaintainance(CustomerIssue selectedTask, AssignedTasksDialog previousFrame) {
        this.setTitle("Hall Maintenance");
        this.previousFrame = previousFrame;
        this.task = selectedTask;
        for (int i = 0; i < 24; i++) {
            String hour = String.format("%02d", i);
            times[i] = hour + ":00";
        }
        startTimeComboBox = new JComboBox<>(times);
        endTimeComboBox = new JComboBox<>(times);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(900, 600);
        this.setLayout(null);
        this.setLocationRelativeTo(null);

        JLabel issueLabel = new JLabel("Issue ID:");
        issueLabel.setBounds(30, 20, 120, 25);
        this.add(issueLabel);

        issueField = new JTextField(task.getIssueID());
        issueField.setBounds(180, 20, 320, 25);
        issueField.setEditable(false);
        this.add(issueField);

        // Customer ID
        JLabel customerIDLabel = new JLabel("Customer ID:");
        customerIDLabel.setBounds(30, 60, 120, 25);
        this.add(customerIDLabel);

        customerIDField = new JTextField(task.getCustomerID());
        customerIDField.setBounds(180, 60, 320, 25);
        customerIDField.setEditable(false);
        this.add(customerIDField);

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(30, 100, 120, 25);
        this.add(descriptionLabel);

        descriptionField = new JTextField(task.getDescription());
        descriptionField.setBounds(180, 100, 320, 25);
        descriptionField.setEditable(false);
        this.add(descriptionField);

        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(30, 140, 120, 25);
        this.add(statusLabel);

        statusField = new JTextField(task.getStatus());
        statusField.setBounds(180, 140, 320, 25);
        statusField.setEditable(false);
        this.add(statusField);

        // Hall Name
        JLabel hallNameLabel = new JLabel("Hall Name:");
        hallNameLabel.setBounds(30, 260, 120, 25);
        this.add(hallNameLabel);

        hallNameField = new JTextField(task.getHallName());
        hallNameField.setBounds(180, 260, 320, 25);
        hallNameField.setEditable(false);
        this.add(hallNameField);

        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");

        // Start Date
        JLabel startDateLabel = new JLabel("Set Maintenance Dates:");
        startDateLabel.setBounds(30, 300, 180, 25);
        this.add(startDateLabel);

        JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel, properties);
        JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        startDatePicker.setBounds(210, 300, 290, 25);
        this.add(startDatePicker);

        // Separator between start and end date
        JLabel separatorLabel = new JLabel("-");
        separatorLabel.setBounds(510, 300, 10, 25);
        this.add(separatorLabel);

        // End Date
        JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel, properties);
        JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
        endDatePicker.setBounds(520, 300, 290, 25);
        this.add(endDatePicker);

        // Start Time
        JLabel startTimeLabel = new JLabel("Start Time:");
        startTimeLabel.setBounds(30, 380, 120, 25);
        this.add(startTimeLabel);

        startTimeComboBox.setBounds(160, 380, 100, 25);
        this.add(startTimeComboBox);

        // Separator between start and end time
        JLabel separatorLabel2 = new JLabel("       ");
        separatorLabel.setBounds(270, 380, 10, 25);
        this.add(separatorLabel2);

        // End Time
        JLabel endTimeLabel = new JLabel("End Time:");
        endTimeLabel.setBounds(290, 380, 120, 25);
        this.add(endTimeLabel);

        endTimeComboBox.setBounds(410, 380, 100, 25);
        this.add(endTimeComboBox);

        // Comments
        JLabel commentsLabel = new JLabel("Comments:");
        commentsLabel.setBounds(30, 420, 120, 25);
        this.add(commentsLabel);

        commentsField = new JTextArea();
        commentsField.setLineWrap(true);
        commentsField.setWrapStyleWord(true);
        JScrollPane commentsScrollPane = new JScrollPane(commentsField);
        commentsScrollPane.setBounds(160, 420, 500, 50);
        this.add(commentsScrollPane);

        // Save Button
        saveAvaButton.setBounds(30, 490, 200, 30);
        saveAvaButton.addActionListener(this);
        this.add(saveAvaButton);

        // Back Button
        backButton.setBounds(240, 490, 200, 30);
        this.add(backButton);
        backButton.addActionListener(this);

        this.setVisible(true);
    }

    public void saveMaintainanceToFile(String hallName, String startDate, String endDate, LocalTime startTime, LocalTime endTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("hallMaintenance.txt", true))) {
            writer.println(hallName + ", " + startDate + ", " + endDate + ", " + startTime + ", " + endTime + ", ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean timeCheck(String hallName, LocalDate newStartDate, LocalDate newEndDate, LocalTime newStartTime, LocalTime newEndTime) {
        try (Scanner scanner = new Scanner(new File("hallMaintenance.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split("\\s*,\\s*");

                if (data.length < 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String existingHallName = data[0];
                LocalDate existingStartDate = LocalDate.parse(data[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate existingEndDate = LocalDate.parse(data[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime existingStartTime = LocalTime.parse(data[3], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime existingEndTime = LocalTime.parse(data[4], DateTimeFormatter.ofPattern("HH:mm"));

                if (hallName.equals(existingHallName)) {
                    if (!newEndDate.isBefore(existingStartDate) && !newStartDate.isAfter(existingEndDate)) {
                        if (!(newEndTime.isBefore(existingStartTime) || newStartTime.isAfter(existingEndTime))) {
                            return true; // Overlap found
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // No overlap
    }

    public boolean isTimeOverlapping2(String hallName, LocalDate newStartDate, LocalDate newEndDate, LocalTime newStartTime, LocalTime newEndTime) {
        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");

                if (data.length < 7) {
                    System.err.println("Skipping invalid line: " + String.join(",", data));
                    continue;
                }

                String existingHallName = data[0].trim();
                LocalDate existingStartDate = LocalDate.parse(data[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate existingEndDate = LocalDate.parse(data[4], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime existingStartTime = LocalTime.parse(data[5], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime existingEndTime = LocalTime.parse(data[6], DateTimeFormatter.ofPattern("HH:mm"));

                if (hallName.equals(existingHallName)) {
                    if (!newEndDate.isBefore(existingStartDate) && !newStartDate.isAfter(existingEndDate)) {
                        if (!(newEndTime.isBefore(existingStartTime) || newStartTime.isAfter(existingEndTime))) {
                            return true; // Overlap found
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // No overlap
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveAvaButton) {
            String hallName = hallNameField.getText();
            String startDate = startDatePicker.getJFormattedTextField().getText();
            String endDate = endDatePicker.getJFormattedTextField().getText();
            String startTime = (String) startTimeComboBox.getSelectedItem();
            String endTime = (String) endTimeComboBox.getSelectedItem();
            
            if (startTime == null || endTime == null) {
                JOptionPane.showMessageDialog(null, "Please select both start and end times.", "Time Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Date Format (Assuming yyyy-MM-dd)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);

            // Time Format (HH:mm)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            // Perform all validations
            String errorMessage = timeCheck(hallName, startLocalDate, endLocalDate, startLocalTime, endLocalTime, nowDate, nowTime);

            if (errorMessage != null) {
                JOptionPane.showMessageDialog(null, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If all validations pass, proceed to save the maintenance schedule
            saveMaintainanceToFile(hallName, startDate, endDate, startLocalTime, endLocalTime);
            JOptionPane.showMessageDialog(null, "Hall Maintenance schedule saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        }
    }

    private String timeCheck(String hallName, LocalDate startLocalDate, LocalDate endLocalDate,
                                               LocalTime startLocalTime, LocalTime endLocalTime, 
                                               LocalDate nowDate, LocalTime nowTime) {
        // Check if the selected date is in the past
        if (startLocalDate.isBefore(nowDate) || (startLocalDate.isEqual(nowDate) && startLocalTime.isBefore(nowTime))) {
            return "Start date and time cannot be in the past!";
        }

        if (endLocalDate.isBefore(nowDate) || (endLocalDate.isEqual(nowDate) && endLocalTime.isBefore(nowTime))) {
            return "End date and time cannot be in the past!";
        }

        // Validate that the end date is not before the start date
        if (endLocalDate.isBefore(startLocalDate)) {
            return "End date cannot be before start date!";
        }

        // If the dates are the same, validate that the end time is not before the start time
        if (startLocalDate.equals(endLocalDate) && endLocalTime.isBefore(startLocalTime)) {
            return "End time cannot be before start time!";
        }

        // Validate that the start time and end time are not the same
        if (startLocalTime.equals(endLocalTime)) {
            return "End time cannot be same as start time!";
        }

        if (timeCheck(hallName, startLocalDate, endLocalDate, startLocalTime, endLocalTime)) {
            return "This hall already has a maintenance scheduled within this time range!";
        }

        if (isTimeOverlapping2(hallName, startLocalDate, endLocalDate, startLocalTime, endLocalTime)) {
            return "This hall has a booking within this time range!\n Please select a different time range";
        }

        return null; // No validation errors
    }
}
