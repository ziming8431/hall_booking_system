package java_assignment;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class hallAvailabilityFrame extends JFrame implements ActionListener {
    private String[] columnNames = {"Hall Name", "Hall Type", "Hall Rate/Hr", "Capacity"};
    private DefaultTableModel model2 = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // All cells are not editable
        }
    };
    private JTable viewHall2 = new JTable(model2);
    //TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private String[] hallTypes = {"All", "Auditorium", "Banquet Hall", "Meeting Room"};
    private JComboBox hallTypeBox = new JComboBox<>(hallTypes);
    private JButton saveAvaButton = new JButton("Save Availability");
    private JTextField hallNameField;
    private JTextField hallPriceField;
    private JTextField capacityField;
    private JFrame previousFrame;
    // Date and time models for start/end times
    private UtilDateModel startDateModel = new UtilDateModel();
    private UtilDateModel endDateModel = new UtilDateModel();
    private Properties properties = new Properties();
    private JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel, properties);
    private JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
    private JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel, properties);
    private JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
    private String[] times = new String[11];
    // Create JComboBox for start time and end time
    private JComboBox<String> startTimeComboBox = new JComboBox<>();
    private JComboBox<String> endTimeComboBox = new JComboBox<>();
    private JTextArea commentsField = new JTextArea();
    private JButton backButton = new JButton("Back");
    private JButton backButton2 = new JButton("Back");
    private JFrame availabilityFrame = new JFrame("Set Hall Availability");
    JButton setAvailabilityButton = new JButton("Set Hall Availability");
    private boolean isSettingAvailability = false;
    private JTextField hallTypeField;  // Add this field

    protected hallAvailabilityFrame(JFrame previousFrame) {

        this.previousFrame = previousFrame;
        for (int i = 8; i <= 18; i++) {
            String hour = String.format("%02d", i);  // Format the hour with two digits
            times[i - 8] = hour + ":00";             // Add only full-hour increments (e.g., 08:00, 09:00, ..., 18:00)
        }

        // Initialize the JComboBox with the times array
        startTimeComboBox = new JComboBox<>(times);
        endTimeComboBox = new JComboBox<>(times);

        this.setTitle("Set Hall Availability");
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        // Scroll pane for the table
        ArrayList<String[]> hallData = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                // Split the line into columns based on comma delimiter
                String[] data = line.split(",");
                hallData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] row : hallData) {
            model2.addRow(row);
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model2);
        viewHall2.setRowSorter(sorter);
        JTextField searchBar = new JTextField(15);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchBar.getText();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JLabel searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);
        searchPanel.add(searchBar);

        JLabel filterLabel = new JLabel("Filter by Hall Type:");
        searchPanel.add(filterLabel);
        searchPanel.add(hallTypeBox);

        // Adding action listener to filter by hall type
        hallTypeBox.addActionListener(e -> {
            String selectedType = (String) hallTypeBox.getSelectedItem();
            if (selectedType.equals("All")) {
                sorter.setRowFilter(null); // Show all rows
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selectedType, 1)); // Filter by Hall Type (column index 1)
            }
        });
        

        // Add the table to a scroll pane (in case there are many rows)
        JScrollPane scrollPane = new JScrollPane(viewHall2);

        // Add a button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton("Back");
        buttonPanel.add(backButton);
        buttonPanel.add(setAvailabilityButton);
        backButton.addActionListener(this);
        setAvailabilityButton.addActionListener(e -> {
            isSettingAvailability = true;
            JOptionPane.showMessageDialog(this, "Please double-click on a hall to set its availability.", "Set Hall Availability", JOptionPane.INFORMATION_MESSAGE);
        });
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.add(searchPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        // Add a MouseListener to capture row clicks
        viewHall2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSettingAvailability && e.getClickCount() == 2) {
                    int row = viewHall2.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String name = (String) model2.getValueAt(row, 0);
                        String type = (String) model2.getValueAt(row, 1);
                        String price = (String) model2.getValueAt(row, 2);
                        String capacity = (String) model2.getValueAt(row, 3);
                        setHallAvailable(row, name, type, price, capacity);  // Open availability setting
                        isSettingAvailability = false; // Reset the flag
                    }
                }
            }
        });
        this.setVisible(true);
    }

    private void setHallAvailable(int row, String name, String type, String price, String capacity) {
        this.setLocationRelativeTo(null);
        availabilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        availabilityFrame.setSize(600, 600);
        availabilityFrame.setLayout(null);
        availabilityFrame.setLocationRelativeTo(null);

        

        availabilityFrame.getContentPane().removeAll();
        startDateModel.setValue(null);
        endDateModel.setValue(null);

        // Reset time combo boxes
        startTimeComboBox.setSelectedIndex(0);
        endTimeComboBox.setSelectedIndex(0);

        // Clear comments
        commentsField.setText("");

        // Hall name, type, price, and capacity fields
        JLabel hallNameLabel = new JLabel("Hall name:");
        hallNameLabel.setBounds(30, 20, 120, 25);
        availabilityFrame.add(hallNameLabel);

        hallNameField = new JTextField(name);
        hallNameField.setBounds(180, 20, 320, 25);
        hallNameField.setEditable(false);
        availabilityFrame.add(hallNameField);

        JLabel hallTypeLabel = new JLabel("Hall type:");
        hallTypeLabel.setBounds(30, 60, 120, 25);
        availabilityFrame.add(hallTypeLabel);

        hallTypeField = new JTextField(type);  // Use the type parameter
        hallTypeField.setBounds(180, 60, 320, 25);
        hallTypeField.setEditable(false);
        availabilityFrame.add(hallTypeField);

        JLabel hallPriceLabel = new JLabel("Hall Rate/Hr:");
        hallPriceLabel.setBounds(30, 100, 120, 25);
        availabilityFrame.add(hallPriceLabel);

        hallPriceField = new JTextField(price);
        hallPriceField.setBounds(180, 100, 320, 25);
        hallPriceField.setEditable(false);
        availabilityFrame.add(hallPriceField);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setBounds(30, 140, 120, 25);
        availabilityFrame.add(capacityLabel);

        capacityField = new JTextField(capacity);
        capacityField.setBounds(180, 140, 320, 25);
        capacityField.setEditable(false);
        availabilityFrame.add(capacityField);


        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");


        // Start date picker
        JLabel startDateLabel = new JLabel("Set start date:");
        startDateLabel.setBounds(30, 180, 120, 25);
        availabilityFrame.add(startDateLabel);

        JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel, properties);
        JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        startDatePicker.setBounds(180, 180, 320, 30);
        availabilityFrame.add(startDatePicker);

        //End Date picker
        JLabel endDateLabel = new JLabel("Set end date:");
        endDateLabel.setBounds(30, 220, 120, 25);
        availabilityFrame.add(endDateLabel);

        JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel, properties);
        JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());
        endDatePicker.setBounds(180, 220, 320, 30);
        availabilityFrame.add(endDatePicker);


        // Start time label

        JLabel startTimeLabel = new JLabel("Set start time:");
        startTimeLabel.setBounds(30, 260, 120, 25);
        availabilityFrame.add(startTimeLabel);

// End time label
        JLabel endTimeLabel = new JLabel("Set end time:");
        endTimeLabel.setBounds(30, 300, 120, 25);
        availabilityFrame.add(endTimeLabel);


// Create JComboBox for start time and end time
        startTimeComboBox.setBounds(180, 260, 320, 25);
        availabilityFrame.add(startTimeComboBox);

        endTimeComboBox.setBounds(180, 300, 320, 25);
        availabilityFrame.add(endTimeComboBox);


        JLabel commentsLabel = new JLabel("Comments:");
        commentsLabel.setBounds(30, 340, 120, 25);
        availabilityFrame.add(commentsLabel);


        JTextArea commentsField = new JTextArea();
        commentsField.setBounds(30, 370, 550, 100);  // Wider to accommodate multiple lines of text
        commentsField.setLineWrap(true);
        commentsField.setWrapStyleWord(true);
        availabilityFrame.add(commentsField);


        // Save button
        saveAvaButton.setBounds(30, 490, 200, 30);
        saveAvaButton.addActionListener(this);
        availabilityFrame.add(saveAvaButton);


        backButton2.setBounds(240, 490, 200, 30);
        availabilityFrame.add(backButton2);
        backButton2.addActionListener(this);

        availabilityFrame.setVisible(true);


    }

    public void saveAvailabilityToFile(String hallName, String hallType, String price, String capacity,
                                       String startDate, String endDate, LocalTime startTime, LocalTime endTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("hallAvailability.txt", true))) {
            writer.println(hallName + ", " + hallType + ", " + price + ", " + capacity + ", "
                    + startDate + ", " + endDate + ", " + startTime + ", " + endTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean timeCheck(String hallName, LocalDate newStartDate, LocalDate newEndDate, LocalTime newStartTime, LocalTime newEndTime) {
        File file = new File("hallAvailability.txt");

        // Check if the file is empty
        if (file.length() == 0) {
            System.out.println("Availability file is empty. Skipping time check.");
            return false; // No overlap, as there is no data
        }

        try (Scanner scanner = new Scanner(new File("hallAvailability.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(", ");

                // Ensure the line has at least 8 fields (hallName, hallType, price, capacity, startDate, endDate, startTime, endTime)
                if (data.length < 8) {
                    System.out.println("Skipping invalid line: " + line); // Debugging line
                    continue; // Skip to the next line if the format is invalid
                }
                String existingHallName = data[0];
                LocalDate existingStartDate = LocalDate.parse(data[4]);
                LocalDate existingEndDate = LocalDate.parse(data[5]);
                LocalTime existingStartTime = LocalTime.parse(data[6]);
                LocalTime existingEndTime = LocalTime.parse(data[7]);

                // Check if it's the same hall
                if (hallName.equals(existingHallName)) {
                    // Check if the dates overlap
                    if (!newEndDate.isBefore(existingStartDate) && !newStartDate.isAfter(existingEndDate)) {
                        // If dates overlap, check for time overlap
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

    private boolean validateDateTime(LocalDate startLocalDate, LocalDate endLocalDate, LocalTime startLocalTime, LocalTime endLocalTime) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        // Check if the start date/time is in the past
        if (startLocalDate.isBefore(nowDate) || (startLocalDate.isEqual(nowDate) && startLocalTime.isBefore(nowTime))) {
            showError("Start date and time cannot be in the past! Please select a present time.");
            return false; // Validation failed
        }

        // Check if the end date is before the start date
        if (endLocalDate.isBefore(startLocalDate)) {
            showError("End date cannot be before start date!");
            return false; // Validation failed
        }

        // Check if the start and end times on the same day are valid
        if (startLocalDate.equals(endLocalDate) && endLocalTime.isBefore(startLocalTime)) {
            showError("End time cannot be before start time!");
            return false; // Validation failed
        }

        // Check if start and end times are the same
        if (startLocalTime.equals(endLocalTime)) {
            showError("Start time and end time cannot be the same.");
            return false; // Validation failed
        }

        return true; // Validation successful
    }



    public boolean isTimeOverlapping2(String hallName, LocalDate newStartDate, LocalDate newEndDate, LocalTime newStartTime, LocalTime newEndTime) {
        File file = new File("hallMaintenance.txt");

        // Check if the file is empty
        if (file.length() == 0) {
            System.out.println("Maintenance file is empty. Skipping time overlap check.");
            return false; // No overlap, as there is no data
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split("\\s*,\\s*");

                // Ensure the line has the expected number of fields (at least 5)
                if (data.length < 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String existingHallName = data[0];
                LocalDate existingStartDate = LocalDate.parse(data[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate existingEndDate = LocalDate.parse(data[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime existingStartTime = LocalTime.parse(data[3], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime existingEndTime = LocalTime.parse(data[4], DateTimeFormatter.ofPattern("HH:mm"));

                // Check if it's the same hall
                if (hallName.equals(existingHallName)) {
                    // Check if the dates overlap
                    if (!newEndDate.isBefore(existingStartDate) && !newStartDate.isAfter(existingEndDate)) {
                        // If dates overlap, check for time overlap
                        if (!(newEndTime.isBefore(existingStartTime) || newStartTime.isAfter(existingEndTime))) {
                            System.out.println("Overlap found!");
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
            String hallType = hallTypeField.getText();  // Get the hall type from the field, not the combo box
            String price = hallPriceField.getText();
            String capacity = capacityField.getText();
            String startDate = startDatePicker.getJFormattedTextField().getText();
            String endDate = endDatePicker.getJFormattedTextField().getText();
            String startTime = (String) startTimeComboBox.getSelectedItem();
            String endTime = (String) endTimeComboBox.getSelectedItem();

            // Validate time selections
            if (startTime == null || endTime == null) {
                showError("Please select both start and end times.");
                return;
            }

            // Parse dates and times
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            // Date and time validations
            if (!validateDateTime(startLocalDate, endLocalDate, startLocalTime, endLocalTime)) {
                return; // Exit if validation fails
            }


            // Check for overlapping times
            if (timeCheck(hallName, startLocalDate, endLocalDate, startLocalTime, endLocalTime)) {
                showError("This hall already has an availability within this time range!");
                return;
            }

            if (isTimeOverlapping2(hallName, startLocalDate, endLocalDate, startLocalTime, endLocalTime)) {
                showError("This hall has a maintenance operation within this time range! Please select a different time.");
                return;
            }

            // If all validations pass, save availability
            saveAvailabilityToFile(hallName, hallType, price, capacity, startDate, endDate, startLocalTime, endLocalTime);
            JOptionPane.showMessageDialog(null, "Hall availability saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            refreshHallData();
            availabilityFrame.dispose();
            
            // Reset fields after successful save
            resetFields();
            

        } else if (e.getSource() == backButton2) {
            availabilityFrame.dispose();
        } else if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        }
    }

    // Utility method to show error messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Method to reset fields after successful save
    private void resetFields() {
        hallNameField.setText("");
        hallTypeBox.setSelectedIndex(0);
        hallPriceField.setText("");
        capacityField.setText("");
        startDatePicker.getJFormattedTextField().setText("");
        endDatePicker.getJFormattedTextField().setText("");
        startTimeComboBox.setSelectedIndex(0);
        endTimeComboBox.setSelectedIndex(0);
        commentsField.setText("");
    }

    // Method to refresh hall data
    private void refreshHallData() {
        model2.setRowCount(0); // Clear existing data
        ArrayList<String[]> hallData = new ArrayList<>();
    
        // Load hall data from Hall.txt (which contains the base hall data)
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                // Split the line into columns based on comma delimiter
                String[] data = line.split(",");
                hallData.add(data);  // Add data to hallData list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Populate the table only with unique rows from Hall.txt
        for (String[] row : hallData) {
            model2.addRow(row);  // Add only from Hall.txt, not hallAvailability.txt
        }
    }
    
    
}

