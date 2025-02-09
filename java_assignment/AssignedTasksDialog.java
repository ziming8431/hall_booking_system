package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AssignedTasksDialog extends JFrame implements ActionListener {
    String[] columnNames = {"Issue ID", "Customer ID", "Description", "Status", "Hall Name"};
    private JTable tasksTable;
    private DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // All cells are not editable
        }
    };
    private JButton updateStatusButton;
    private JButton setMaintenanceScheduleButton;
    private JButton backButton;
    private String schedulerID;
    private PageScheduler previousFrame;
    private boolean isSettingSchedule = false;

    public AssignedTasksDialog(PageScheduler previousFrame, List<CustomerIssue> tasks, String schedulerID)  {
        super("Assigned Tasks");
        this.schedulerID = schedulerID;
        this.previousFrame = previousFrame;
        setupUI(tasks);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupUI(List<CustomerIssue> tasks) {
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        for (CustomerIssue task : tasks) {
            Object[] row = {task.getIssueID(), task.getCustomerID(), task.getDescription(),
                    task.getStatus(), task.getHallName()};
            tableModel.addRow(row);
        }

        tasksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tasksTable);
        add(scrollPane, BorderLayout.CENTER);

        tasksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSettingSchedule && e.getClickCount() == 2 && tasksTable.getSelectedRow() != -1) {
                    int selectedRow = tasksTable.getSelectedRow();
                    String issueID = (String) tableModel.getValueAt(selectedRow, 0);
                    CustomerIssue selectedTask = getTaskById(issueID);
                    new hallMaintainance(selectedTask, AssignedTasksDialog.this);
                    isSettingSchedule = false; // Reset the flag
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(this);
        setMaintenanceScheduleButton = new JButton("Set Maintenance Schedule");
        setMaintenanceScheduleButton.addActionListener(this);

        backButton = new JButton("Back");
        backButton.addActionListener(this);

        buttonPanel.add(updateStatusButton);
        buttonPanel.add(setMaintenanceScheduleButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            this.dispose();
            previousFrame.setVisible(true);
        } else if (e.getSource() == updateStatusButton) {
            int selectedRow = tasksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a task first.");
                return;
            }

            String issueID = (String) tableModel.getValueAt(selectedRow, 0);
            CustomerIssue selectedTask = getTaskById(issueID);
            updateTaskStatus(selectedTask);
        } else if (e.getSource() == setMaintenanceScheduleButton) {
            isSettingSchedule = true;
            JOptionPane.showMessageDialog(this, "Please double-click on a task to set its maintenance schedule.");
        }
    }

    private CustomerIssue getTaskById(String issueID) {
        List<CustomerIssue> tasks = FileHandler.getAssignedTasks(schedulerID);
        for (CustomerIssue task : tasks) {
            if (task.getIssueID().equals(issueID)) {
                return task;
            }
        }
        return null;
    }

    private void updateTaskStatus(CustomerIssue task) {
        String[] statuses = {"Assigned", "Done"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(task.getStatus());

        int result = JOptionPane.showConfirmDialog(this, statusComboBox, "Update Status", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusComboBox.getSelectedItem();
            task.setStatus(newStatus);
            FileHandler.updateCustomerIssue(task);
            JOptionPane.showMessageDialog(this, "Status updated successfully.");
            refreshTasksTable();
        }
    }

    private void refreshTasksTable() {
        List<CustomerIssue> updatedTasks = FileHandler.getAssignedTasks(schedulerID);
        tableModel.setRowCount(0);
        for (CustomerIssue task : updatedTasks) {
            Object[] row = {task.getIssueID(), task.getCustomerID(), task.getDescription(),
                    task.getStatus(), task.getHallName()};
            tableModel.addRow(row);
        }
    }

    public void showDialog() {
        setVisible(true);
    }
}