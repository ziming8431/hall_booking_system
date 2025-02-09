package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import java.io.*;

import java.util.Scanner;


public class Makehallbooking extends JFrame implements ActionListener {

    private JTable hallTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton backButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private String userID;
    private String username;
    private JFrame previousFrame;

    private static final LocalTime OPERATING_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime OPERATING_END_TIME = LocalTime.of(18, 0);

    public Makehallbooking(String userID, JFrame previousFrame) {
        this.userID = userID;
        this.previousFrame = previousFrame;
        User user = FileHandler.getUserByID(userID);
        this.username = user.getUsername();
        
        // Ensure Bookings.txt exists before setting up the UI
        ensureBookingsFileExists();
        
        setupUI();
    }

    private void ensureBookingsFileExists() {
        File bookingsFile = new File("Bookings.txt");
        if (!bookingsFile.exists()) {
            try {
                bookingsFile.createNewFile();
                System.out.println("Created new Bookings.txt file");
            } catch (IOException e) {
                System.err.println("Error creating Bookings.txt file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setupUI() {
        setTitle("Hall Booking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Update the column names
        String[] columns = {"Hall name", "Hall type", "Price", "Capacity", "Start date", "End date", "Start time", "End time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This makes all cells non-editable
            }
        };
        hallTable = new JTable(tableModel);
        hallTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        hallTable.setRowSorter(sorter);
        
        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(hallTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create the filter panel
        JPanel filterPanel = new JPanel();

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Booking");
        backButton = new JButton("Back");
        addButton.addActionListener(this);
        backButton.addActionListener(this);
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        // Add panels to the frame
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load the data
        loadHallAvailabilityData();

        setVisible(true);
    }

    private void loadHallAvailabilityData() {
        tableModel.setRowCount(0);
        List<String[]> hallData = FileHandler.getHallAvailabilityData();
        for (String[] row : hallData) {
            tableModel.addRow(row);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addBooking();
        } else if (e.getSource() == backButton) {
            dispose();
            previousFrame.setVisible(true);
        }
    }

    private void addBooking() {
        int selectedRow = hallTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a hall from the table first.");
            return;
        }

        String hallName = (String) tableModel.getValueAt(selectedRow, 0);
        String hallType = (String) tableModel.getValueAt(selectedRow, 1);
        String pricePerHour = (String) tableModel.getValueAt(selectedRow, 2);
        String capacity = (String) tableModel.getValueAt(selectedRow, 3);

        JDialog bookingDialog = new JDialog(this, "Book Hall", true);
        bookingDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(bookingDialog, gbc, "Hall Name:", hallName, 0, false);
        addLabelAndField(bookingDialog, gbc, "Hall Type:", hallType, 1, false);
        addLabelAndField(bookingDialog, gbc, "Price per Hour:", pricePerHour, 2, false);
        addLabelAndField(bookingDialog, gbc, "Capacity:", capacity, 3, false);
        addLabelAndField(bookingDialog, gbc, "Username:", username, 4, false);

        // Add date picker for start date
        gbc.gridx = 0; gbc.gridy = 5;
        bookingDialog.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField startDateField = new JTextField();
        bookingDialog.add(startDateField, gbc);

        // Add date picker for end date
        gbc.gridx = 0; gbc.gridy = 6;
        bookingDialog.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField endDateField = new JTextField();
        bookingDialog.add(endDateField, gbc);

        // Add time picker for start time
        gbc.gridx = 0; gbc.gridy = 7;
        bookingDialog.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        JComboBox<String> startTimeComboBox = createTimeComboBox();
        bookingDialog.add(startTimeComboBox, gbc);

        // Add time picker for end time
        gbc.gridx = 0; gbc.gridy = 8;
        bookingDialog.add(new JLabel("End Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        JComboBox<String> endTimeComboBox = createTimeComboBox();
        bookingDialog.add(endTimeComboBox, gbc);

        // Add available time slots
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        JTextArea availableSlots = new JTextArea(5, 30);
        availableSlots.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(availableSlots);
        bookingDialog.add(scrollPane, gbc);
        updateAvailableSlots(hallName, availableSlots);

        // Add OK and Cancel buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        bookingDialog.add(buttonPanel, gbc);

        okButton.addActionListener(e -> {
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            String startTime = (String) startTimeComboBox.getSelectedItem();
            String endTime = (String) endTimeComboBox.getSelectedItem();


            if (validateBookingTime(startDate, endDate, startTime, endTime)) {
                if (isHallAvailable(hallName, startDate, endDate, startTime, endTime)) {
                    showPaymentPage(hallName, hallType, startDate, endDate, startTime, endTime);
                    bookingDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(bookingDialog, "The selected hall is not available for the specified date and time. Please choose a different time or date.");
                }
            }
        });

        cancelButton.addActionListener(e -> bookingDialog.dispose());

        bookingDialog.pack();
        bookingDialog.setLocationRelativeTo(this);
        bookingDialog.setVisible(true);
    }

    protected boolean validateBookingTime(String startDate, String endDate, String startTime, String endTime) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalTime timeStart = LocalTime.parse(startTime);
            LocalTime timeEnd = LocalTime.parse(endTime);

            // Check if end date is before start date
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
                return false;
            }

            // Check if start time and end time are the same
            if (timeStart.equals(timeEnd)) {
                JOptionPane.showMessageDialog(this, "Start time and end time cannot be the same.");
                return false;
            }

            // Check if end time is before start time on the same day
            if (start.isEqual(end) && timeEnd.isBefore(timeStart)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time.");
                return false;
            }

            // Check if booking times are within operating hours
            if (timeStart.isBefore(OPERATING_START_TIME) || timeEnd.isAfter(OPERATING_END_TIME)) {
                JOptionPane.showMessageDialog(this, "Booking times must be between 08:00 and 18:00.");
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.");
            return false;
        }
    }

    private boolean isHallAvailable(String hallName, String startDate, String endDate, String startTime, String endTime) {
        LocalDate bookingStartDate = LocalDate.parse(startDate);
        LocalDate bookingEndDate = LocalDate.parse(endDate);
        LocalTime bookingStartTime = LocalTime.parse(startTime);
        LocalTime bookingEndTime = LocalTime.parse(endTime);

        // Check hall availability
        boolean isAvailable = false;
        try (Scanner scanner = new Scanner(new File("hallAvailability.txt"))) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(", ");
                if (data[0].equals(hallName)) {
                    LocalDate availStartDate = LocalDate.parse(data[4]);
                    LocalDate availEndDate = LocalDate.parse(data[5]);
                    LocalTime availStartTime = LocalTime.parse(data[6]);
                    LocalTime availEndTime = LocalTime.parse(data[7]);

                    if (!bookingStartDate.isBefore(availStartDate) && !bookingEndDate.isAfter(availEndDate) &&
                        !bookingStartTime.isBefore(availStartTime) && !bookingEndTime.isAfter(availEndTime)) {
                        isAvailable = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!isAvailable) {
            return false;
        }

        // Check existing bookings
        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                if (data[0].equals(hallName)) {
                    LocalDate existingStartDate = LocalDate.parse(data[3]);
                    LocalDate existingEndDate = LocalDate.parse(data[4]);
                    LocalTime existingStartTime = LocalTime.parse(data[5]);
                    LocalTime existingEndTime = LocalTime.parse(data[6]);

                    // Check if the dates overlap
                    if (!(bookingEndDate.isBefore(existingStartDate) || bookingStartDate.isAfter(existingEndDate))) {
                        // If dates overlap, check if times overlap
                        if (bookingStartDate.isEqual(existingStartDate) && bookingEndDate.isEqual(existingEndDate)) {
                            if (!(bookingEndTime.isBefore(existingStartTime) || bookingStartTime.isAfter(existingEndTime))) {
                                return false; // Conflict found
                            }
                        } else {
                            // If dates are different but overlapping, it's still a conflict
                            return false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true; // No conflict found
    }

    private void updateAvailableSlots(String hallName, JTextArea availableSlots) {
        List<String[]> availabilityData = FileHandler.getHallAvailabilityData();
        StringBuilder sb = new StringBuilder("Available time slots:\n");
        
        for (String[] data : availabilityData) {
            if (data[0].equals(hallName)) {
                String availableStart = data[4] + " " + data[6];
                String availableEnd = data[5] + " " + data[7];
                sb.append(String.format("%s to %s\n", availableStart, availableEnd));
            }
        }
        
        sb.append("\nBooked time slots:\n");
        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String[] booking = scanner.nextLine().split(",");
                if (booking[0].equals(hallName)) {
                    sb.append(String.format("%s to %s %s - %s\n", 
                        booking[3], booking[4], booking[5], booking[6]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.append("no slot is booked\n");
        }
        
        availableSlots.setText(sb.toString());
    }

    private JComboBox<String> createTimeComboBox() {
        String[] times = new String[24];
        for (int i = 0; i < 24; i++) {
            times[i] = String.format("%02d:00", i);
        }
        return new JComboBox<>(times);
    }

    private void addLabelAndField(JDialog dialog, GridBagConstraints gbc, String label, String value, int gridy, boolean editable) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        dialog.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(value);
        field.setEditable(editable);
        field.setPreferredSize(new Dimension(250, 25)); // Make the text field wider
        dialog.add(field, gbc);
    }

    private void showPaymentPage(String hallName, String hallType, String startDate, String endDate, String startTime, String endTime) {
        JDialog paymentDialog = new JDialog(this, "Payment Confirmation", true);
        paymentDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(paymentDialog, gbc, "Hall name:", hallName, 0, false);
        addLabelAndField(paymentDialog, gbc, "Hall type:", hallType, 1, false);
        addLabelAndField(paymentDialog, gbc, "Username:", username, 2, false);
        addLabelAndField(paymentDialog, gbc, "Start date:", startDate, 3, false);
        addLabelAndField(paymentDialog, gbc, "End date:", endDate, 4, false);
        addLabelAndField(paymentDialog, gbc, "Start time:", startTime, 5, false);
        addLabelAndField(paymentDialog, gbc, "End time:", endTime, 6, false);

        // Calculate total price and duration
        double totalPrice = calculateTotalPrice(hallName, startDate, endDate, startTime, endTime);
        long totalDays = ChronoUnit.DAYS.between(LocalDate.parse(startDate), LocalDate.parse(endDate)) + 1;
        double totalHours = calculateTotalHours(startDate, endDate, startTime, endTime);

        addLabelAndField(paymentDialog, gbc, "Total Days:", String.valueOf(totalDays), 7, false);
        addLabelAndField(paymentDialog, gbc, "Total Hours:", String.format("%.2f", totalHours), 8, false);
        addLabelAndField(paymentDialog, gbc, "Price per Hour:", String.format("RM%.2f", getPricePerHour(hallName)), 9, false);
        addLabelAndField(paymentDialog, gbc, "Total Price:", String.format("RM%.2f", totalPrice), 10, false);

        JButton confirmButton = new JButton("Confirm Payment");
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        paymentDialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            if (FileHandler.saveBooking(hallName, userID, username, startDate, endDate, startTime, endTime, totalPrice)) {
                JOptionPane.showMessageDialog(paymentDialog, "Booking confirmed successfully!");
                paymentDialog.dispose();
                showBookingReceipt(hallName, startDate, endDate, startTime, endTime);
                loadHallAvailabilityData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(paymentDialog, "Booking failed. The selected time is not available.");
            }
        });

        paymentDialog.pack();
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setVisible(true);
    }

    private double calculateTotalPrice(String hallName, String startDate, String endDate, String startTime, String endTime) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalTime timeStart = LocalTime.parse(startTime);
            LocalTime timeEnd = LocalTime.parse(endTime);

            long days = ChronoUnit.DAYS.between(start, end) + 1; // Include both start and end days
            double pricePerHour = getPricePerHour(hallName);
            
            double totalPrice = 0;
            for (int i = 0; i < days; i++) {
                LocalDate currentDate = start.plusDays(i);
                LocalTime currentStartTime = (currentDate.equals(start)) ? timeStart : LocalTime.parse(startTime);
                LocalTime currentEndTime = (currentDate.equals(end)) ? timeEnd : LocalTime.parse(endTime);
                
                double hoursToday = ChronoUnit.HOURS.between(currentStartTime, currentEndTime);
                totalPrice += hoursToday * pricePerHour;
            }

            return totalPrice;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return 0.0; // Return 0 if there's an error parsing the dates/times
        }
    }

    private double getPricePerHour(String hallName) {
        List<String[]> hallData = FileHandler.getHallAvailabilityData();
        for (String[] hall : hallData) {
            if (hall[0].equals(hallName)) {
                return Double.parseDouble(hall[2]); // Price is in the third column (index 2)
            }
        }
        // If the hall is not found, return a default value or throw an exception
        throw new IllegalArgumentException("Hall not found: " + hallName);
    }

    private void showBookingReceipt(String hallName, String startDate, String endDate, String startTime, String endTime) {
        JDialog receiptDialog = new JDialog(this, "Booking Receipt", true);
        receiptDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(receiptDialog, gbc, "Hall name:", hallName, 0, false);
        addLabelAndField(receiptDialog, gbc, "Username:", username, 1, false);
        addLabelAndField(receiptDialog, gbc, "Start date:", startDate, 2, false);
        addLabelAndField(receiptDialog, gbc, "End date:", endDate, 3, false);
        addLabelAndField(receiptDialog, gbc, "Start time:", startTime, 4, false);
        addLabelAndField(receiptDialog, gbc, "End time:", endTime, 5, false);

        double totalPrice = calculateTotalPrice(hallName, startDate, endDate, startTime, endTime);
        long totalDays = ChronoUnit.DAYS.between(LocalDate.parse(startDate), LocalDate.parse(endDate)) + 1;
        double totalHours = calculateTotalHours(startDate, endDate, startTime, endTime);

        addLabelAndField(receiptDialog, gbc, "Total Days:", String.valueOf(totalDays), 6, false);
        addLabelAndField(receiptDialog, gbc, "Total Hours:", String.format("%.2f", totalHours), 7, false);
        addLabelAndField(receiptDialog, gbc, "Price per Hour:", String.format("RM%.2f", getPricePerHour(hallName)), 8, false);
        addLabelAndField(receiptDialog, gbc, "Total Price:", String.format("RM%.2f", totalPrice), 9, false);

        JButton closeButton = new JButton("Close");
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        receiptDialog.add(closeButton, gbc);

        closeButton.addActionListener(e -> receiptDialog.dispose());

        receiptDialog.pack();
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.setVisible(true);
    }

    private double calculateTotalHours(String startDate, String endDate, String startTime, String endTime) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalTime timeStart = LocalTime.parse(startTime);
            LocalTime timeEnd = LocalTime.parse(endTime);

            long days = ChronoUnit.DAYS.between(start, end) + 1; // Include both start and end days
            
            double totalHours = 0;
            for (int i = 0; i < days; i++) {
                LocalDate currentDate = start.plusDays(i);
                LocalTime currentStartTime = (currentDate.equals(start)) ? timeStart : LocalTime.parse(startTime);
                LocalTime currentEndTime = (currentDate.equals(end)) ? timeEnd : LocalTime.parse(endTime);
                
                double hoursToday = ChronoUnit.HOURS.between(currentStartTime, currentEndTime);
                totalHours += hoursToday;
            }

            return totalHours;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return 0.0; // Return 0 if there's an error parsing the dates/times
        }
    }
}
