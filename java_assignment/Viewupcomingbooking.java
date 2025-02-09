package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Viewupcomingbooking extends JFrame implements ActionListener {
    private String userID;
    private JButton backButton;
    private JButton cancelbookingButton;
    private JFrame previousFrame;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    
    public Viewupcomingbooking(String userID, JFrame previousFrame){
        this.userID = userID;
        this.previousFrame = previousFrame;
        setupUI();
        loadBookings();
    }

    private void setupUI(){ //layout
        setTitle("Upcoming Bookings");
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
        cancelbookingButton = new JButton("Cancel Booking");
        backButton.addActionListener(this);
        cancelbookingButton.addActionListener(this);
        buttonPanel.add(backButton);
        buttonPanel.add(cancelbookingButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBookings(){
        tableModel.setRowCount(0);
        List<String[]> bookings = FileHandler.getUpcomingBookings(userID);
        for (String[] booking : bookings) {
            tableModel.addRow(booking);
        }
    }

    @Override //action
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == backButton){
            this.dispose();
            previousFrame.setVisible(true);
        } else if(e.getSource() == cancelbookingButton){
            cancelBooking();
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow != -1) {
            String[] bookingDetails = new String[7];
            for (int i = 0; i < 7; i++) {
                bookingDetails[i] = (String) tableModel.getValueAt(selectedRow, i);
            }
            
            LocalDate bookingDate = LocalDate.parse(bookingDetails[2]);
            LocalDate currentDate = LocalDate.now();
            
            long daysBetween = ChronoUnit.DAYS.between(currentDate, bookingDate);
            
            if (daysBetween >= 3) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to cancel this booking?", 
                    "Confirm Cancellation", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean cancelled = FileHandler.cancelBooking(bookingDetails);
                    if (cancelled) {
                        JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
                        loadBookings(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to cancel the booking. Please try again.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bookings can only be cancelled at least 3 days before the event.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
        }
    }
}


