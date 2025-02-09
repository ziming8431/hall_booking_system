package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingManagementPage extends JFrame implements ActionListener {
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JTextField customerIDField;
    private JButton applyFilterButton;
    private JButton backButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private PageAdmin previousPage;

    public BookingManagementPage(PageAdmin previousPage) {
        this.previousPage = previousPage;
        setupUI();
        loadBookings("All", "");
    }

    private void setupUI() {
        setTitle("Booking Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create table
        String[] columns = {"Hall Name", "User ID", "Username", "Start Date", "End Date", "Start Time", "End Time", "Total Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create filter panel
        JPanel filterPanel = new JPanel();
        JLabel filterLabel = new JLabel("Filter:");
        String[] filterOptions = {"All", "Upcoming", "Past"};
        filterComboBox = new JComboBox<>(filterOptions);
        
        JLabel customerIDLabel = new JLabel("Customer ID:");
        customerIDField = new JTextField(10);
        
        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(this);

        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(customerIDLabel);
        filterPanel.add(customerIDField);
        filterPanel.add(applyFilterButton);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        buttonPanel.add(backButton);

        // Add panels to frame
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBookings(String filter, String customerID) {
        tableModel.setRowCount(0);
        List<String[]> bookings = FileHandler.getAllBookings();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String[] booking : bookings) {
            LocalDate bookingStartDate = LocalDate.parse(booking[3], formatter);
            LocalDate bookingEndDate = LocalDate.parse(booking[4], formatter);
            String status = bookingEndDate.isBefore(currentDate) ? "Past" : "Upcoming";
            
            boolean matchesFilter = filter.equals("All") || 
                                    (filter.equals("Upcoming") && status.equals("Upcoming")) ||
                                    (filter.equals("Past") && status.equals("Past"));
            
            boolean matchesCustomerID = customerID.isEmpty() || booking[1].equals(customerID);
            
            if (matchesFilter && matchesCustomerID) {
                String[] rowData = new String[]{
                    booking[0], // Hall Name
                    booking[1], // User ID
                    booking[2], // Username
                    booking[3], // Start Date
                    booking[4], // End Date
                    booking[5], // Start Time
                    booking[6], // End Time
                    booking[7], // Total Price
                    status
                };
                tableModel.addRow(rowData);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyFilterButton) {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            String customerID = customerIDField.getText().trim();
            loadBookings(selectedFilter, customerID);
        } else if (e.getSource() == backButton) {
            this.dispose(); // Close the BookingManagementPage window
            previousPage.returnFromBookingManagement(); // Make the PageAdmin visible again
        }
    }
}