package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Viewbookinghistory extends JFrame implements ActionListener {
    private String userID;
    private JButton backButton;
    private JButton viewreceipt;
    private JFrame previousFrame;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    
    public Viewbookinghistory(String userID, JFrame previousFrame){
        this.userID = userID;
        this.previousFrame = previousFrame;
        setupUI();
        loadBookings();
    }

    private void setupUI(){ //layout
        setTitle("Booking History");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create table
        String[] columns = {"Hall Name", "Username", "Start Date", "End Date", "Start Time", "End Time", "Total Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This makes all cells non-editable
            }
        };
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("Back");
        viewreceipt = new JButton("View Receipt");
        backButton.addActionListener(this);
        viewreceipt.addActionListener(this);
        buttonPanel.add(backButton);
        buttonPanel.add(viewreceipt);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<String[]> bookings = FileHandler.getBookingHistory(userID);
        for (String[] booking : bookings) {
            tableModel.addRow(booking);
        }
    }

    @Override 
    public void actionPerformed(ActionEvent e){ //action
        if(e.getSource() == backButton){
            this.dispose();
            previousFrame.setVisible(true);
        } else if(e.getSource() == viewreceipt){
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow != -1) {
                String[] bookingDetails = new String[7];
                for (int i = 0; i < 7; i++) {
                    bookingDetails[i] = (String) tableModel.getValueAt(selectedRow, i);
                }
                showBookingReceipt(bookingDetails);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking to view its receipt.");
            }
        }
    }

    private void showBookingReceipt(String[] bookingDetails) {
        JTextField hallNameField = new JTextField(bookingDetails[0]);
        JTextField usernameField = new JTextField(bookingDetails[1]);
        JTextField startDateField = new JTextField(bookingDetails[2]);
        JTextField endDateField = new JTextField(bookingDetails[3]);
        JTextField startTimeField = new JTextField(bookingDetails[4]);
        JTextField endTimeField = new JTextField(bookingDetails[5]);
        JTextField totalPriceField = new JTextField(bookingDetails[6]);

        hallNameField.setEditable(false);
        usernameField.setEditable(false);
        startDateField.setEditable(false);
        endDateField.setEditable(false);
        startTimeField.setEditable(false);
        endTimeField.setEditable(false);
        totalPriceField.setEditable(false);

        Object[] message = {
            "Hall name:", hallNameField,
            "Username:", usernameField,
            "Start date:", startDateField,
            "End date:", endDateField,
            "Start time:", startTimeField,
            "End time:", endTimeField,
            "Total price:", totalPriceField
        };

        JOptionPane.showMessageDialog(this, message, "Booking Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}








