package java_assignment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class viewHallFrame extends JFrame implements ActionListener {
    private JFrame previousFrame;
    private String[] columnNames = {"Hall Name", "Hall Type", "Hall Rate/Hr", "Capacity"};
    private DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // All cells are not editable
        }
    };
    private JTable viewHalls = new JTable(model);
    //TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private String[] hallTypes = {"Auditorium", "Banquet Hall", "Meeting Room"};
    private JComboBox hallTypeBox = new JComboBox<>(hallTypes);
    private JButton editHall = new JButton("Update Hall");
    private JButton deleteHall = new JButton("Delete Hall");
    private JButton backButton = new JButton("Back");

    protected viewHallFrame(JFrame previousFrame) {
        this.previousFrame = previousFrame;
        this.setLocationRelativeTo(null);
        this.setTitle("Hall Details");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null); // Center the frame
        this.setLayout(new BorderLayout());

        // Read and parse data from file
        model.setRowCount(0);
        ArrayList<String[]> hallData = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] data = line.split(",");
                hallData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] row : hallData) {
            model.addRow(row);
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        viewHalls.setRowSorter(sorter);
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
        JScrollPane scrollPane = new JScrollPane(viewHalls);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        editHall.addActionListener(this);
        buttonPanel.add(editHall);

        deleteHall.addActionListener(this);
        buttonPanel.add(deleteHall);


        backButton.addActionListener((ActionListener) this);
        buttonPanel.add(backButton);

        this.add(searchPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        // Make the frame visible
        this.setVisible(true);

    }

    private void updateTable(int row, String name, String type, String price, String capacity) {
        JFrame updateFrame = new JFrame("Update Record");
        updateFrame.setTitle("Hall Booking System");
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setResizable(false);
        updateFrame.setSize(400, 250);
        updateFrame.setLayout(null);
        updateFrame.setLocationRelativeTo(null);

        JLabel hallNameLabel = new JLabel("Change Hall Name:");
        hallNameLabel.setBounds(30, 20, 120, 25);
        updateFrame.add(hallNameLabel);

        JTextField hallNameField = new JTextField(name);
        hallNameField.setBounds(180, 20, 160, 25);
        updateFrame.add(hallNameField);


        JLabel hallTypeLabel = new JLabel("Change Hall Type:");
        hallTypeLabel.setBounds(30, 60, 120, 25);
        updateFrame.add(hallTypeLabel);

        JComboBox<String> hallTypeBox = new JComboBox<>(hallTypes);
        hallTypeBox.setBounds(180, 60, 160, 25);
        hallTypeBox.setSelectedItem(type);
        updateFrame.add(hallTypeBox);

        JLabel hallPriceLabel = new JLabel("Hall Rate/Hr:");
        hallPriceLabel.setBounds(30, 100, 120, 25);
        updateFrame.add(hallPriceLabel);

        JTextField hallPriceField = new JTextField(price);
        hallPriceField.setBounds(180, 100, 160, 25);
        updateFrame.add(hallPriceField);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setBounds(30, 140, 120, 25);
        updateFrame.add(capacityLabel);

        JTextField capacityField = new JTextField(capacity);
        capacityField.setBounds(180, 140, 160, 25);
        updateFrame.add(capacityField);

        hallTypeBox.addActionListener(e -> {
            setDefaultPrice(hallTypeBox, hallPriceField);
            setCapacity(hallTypeBox, capacityField);
        });

        JButton saveButton = new JButton("Save changes");
        saveButton.setBounds(150, 180, 100, 30);
        saveButton.addActionListener(e -> {
            // Convert row index to model index
            int modelRow = viewHalls.convertRowIndexToModel(row);

            // Validate row index
            if (modelRow < 0 || modelRow >= model.getRowCount()) {
                JOptionPane.showMessageDialog(updateFrame, "Invalid row index. Cannot update the table.", "Error", JOptionPane.ERROR_MESSAGE);
                updateFrame.dispose();
                return;
            }


            // Update the JTable with new data
            try {
                if (StringCheck(hallNameField.getText())) {
                    model.setValueAt(hallNameField.getText(), modelRow, 0); // Hall Name
                    model.setValueAt(hallTypeBox.getSelectedItem(), modelRow, 1); // Hall Type
                    model.setValueAt(hallPriceField.getText(), modelRow, 2); // Hall Price
                    model.setValueAt(capacityField.getText(), modelRow, 3); // Capacity
                    System.out.println("Table updated"); // Debugging print statement


                    try (BufferedWriter hallFile = new BufferedWriter(new FileWriter("Hall.txt"))) {
                        for (int i = 0; i < model.getRowCount(); i++) {
                            for (int j = 0; j < 4; j++) {
                                hallFile.write(model.getValueAt(i, j).toString());
                                if (j < 3) {
                                    hallFile.write(",");
                                }
                            }
                            hallFile.write("\n");
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null, "Hall Updated Successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
                    updateFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, " Hall Name must contain a minimum of 4 characters, consisting  (a-z) or (A-Z) characters only  ", "Error", JOptionPane.WARNING_MESSAGE);
                }


            } catch (Exception ex) {
                ex.printStackTrace(); // Print stack trace if an exception occurs
            }
        });

        updateFrame.add(saveButton);
        updateFrame.setVisible(true);
    }


    private void setDefaultPrice(JComboBox<String> hallTypeBox, JTextField hallPriceField) {
        String selectedHallType = (String) hallTypeBox.getSelectedItem();
        hallPriceField.setEditable(false);
        switch (selectedHallType) {
            case "Auditorium":
                hallPriceField.setText("300");
                break;
            case "Banquet Hall":
                hallPriceField.setText("100");
                break;
            case "Meeting Room":
                hallPriceField.setText("50");
                break;
        }
    }


    private void setCapacity(JComboBox<String> hallTypeBox, JTextField capacityField) {
        String selectedHallType = (String) hallTypeBox.getSelectedItem();
        capacityField.setEditable(false);
        switch (selectedHallType) {
            case "Auditorium":
                capacityField.setText("1000");
                break;
            case "Banquet Hall":
                capacityField.setText("300");
                break;
            case "Meeting Room":
                capacityField.setText("30");
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




    private boolean isEditMode = false;
    private boolean isDeleteMode = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editHall) {
            isEditMode = true;
            isDeleteMode = false;
            JOptionPane.showMessageDialog(this, "Editing mode enabled, please select a record to update.", "Edit Mode Enabled", JOptionPane.INFORMATION_MESSAGE);

            viewHalls.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEditMode && e.getClickCount() == 2) {
                        JTable target = (JTable) e.getSource();
                        int row = viewHalls.rowAtPoint(e.getPoint());
                        String name = (String) target.getValueAt(row, 0);
                        String type = (String) target.getValueAt(row, 1);
                        String price = (String) target.getValueAt(row, 2);
                        String capacity = (String) target.getValueAt(row, 3);
                        updateTable(row, name, type, price, capacity);
                        isEditMode = false; // Reset edit mode after action
                    }
                }
            });

        } else if (e.getSource() == deleteHall) {
            isDeleteMode = true;
            isEditMode = false; // edit mode is off
            JOptionPane.showMessageDialog(this, "Please double-click on a record to delete.", "Delete Hall", JOptionPane.INFORMATION_MESSAGE);

            viewHalls.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isDeleteMode && e.getClickCount() == 2) {
                        JTable target = (JTable) e.getSource();
                        int result = JOptionPane.showConfirmDialog(null, "Confirm Delete", "Delete Hall", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            int row = viewHalls.rowAtPoint(e.getPoint());
                            model.removeRow(row);
                            try (BufferedWriter hallFile = new BufferedWriter(new FileWriter("Hall.txt"))) {
                                for (int i = 0; i < model.getRowCount(); i++) {
                                    for (int j = 0; j < 4; j++) {
                                        hallFile.write(model.getValueAt(i, j).toString());
                                        if (j < 3) { // Adjusted to avoid trailing comma
                                            hallFile.write(",");
                                        }
                                    }
                                    hallFile.write("\n");
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        isDeleteMode = false; // Reset delete mode after action
                    }
                }
            });

        } else if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        }
    }
}


