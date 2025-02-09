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

public class ViewBookings extends JFrame implements ActionListener {
    private String userID;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private JComboBox<String> filterComboBox;
    private JFrame previousFrame;

    public ViewBookings(String userID, JFrame previousFrame) {
        this.userID = userID;
        this.previousFrame = previousFrame;
        setupUI();
        loadBookings();
    }

    private void setupUI() {
        setTitle("Booking Information");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create table
        String[] columns = {"Hall Name", "Start Date", "End Date", "Start Time", "End Time", "Total Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create filter panel
        JPanel filterPanel = new JPanel();
        JLabel filterLabel = new JLabel("Filter:");
        String[] filterOptions = {"All", "Upcoming", "Past"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(this);
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);

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

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<String[]> bookings = FileHandler.getBookingHistory(userID);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String[] booking : bookings) {
            LocalDate bookingDate = LocalDate.parse(booking[1], formatter);
            String status = bookingDate.isAfter(currentDate) ? "Upcoming" : "Past";
            String[] rowData = new String[]{booking[0], booking[1], booking[2], booking[3], booking[4], booking[5], status};
            tableModel.addRow(rowData);
        }
    }

    private void filterBookings(String filter) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(sorter);

        if (!filter.equals("All")) {
            sorter.setRowFilter(RowFilter.regexFilter(filter, 6)); // Filter based on the "Status" column
        } else {
            sorter.setRowFilter(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        } else if (e.getSource() == filterComboBox) {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            filterBookings(selectedFilter);
        }
    }
}