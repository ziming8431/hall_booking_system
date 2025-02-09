package java_assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerIssuesDialog extends JDialog implements ActionListener {
    private JTable issuesTable;
    private DefaultTableModel tableModel;
    private JButton assignButton;
    private JButton updateStatusButton;
    private JButton backButton;

    public CustomerIssuesDialog(JFrame parent, List<CustomerIssue> issues) {
        super(parent, "Customer Issues", true);
        setupUI(issues);
    }

    private void setupUI(List<CustomerIssue> issues) {
        setSize(1000, 600);
        setLayout(new BorderLayout());

        String[] columnNames = {"Issue ID", "Customer ID", "Description", "Status", "Assigned Scheduler", "Hall Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        for (CustomerIssue issue : issues) {
            Object[] row = {issue.getIssueID(), issue.getCustomerID(), issue.getDescription(),
                    issue.getStatus(), issue.getAssignedSchedulerID(), issue.getHallName()};
            tableModel.addRow(row);
        }

        issuesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        assignButton = new JButton("Assign Scheduler");
        assignButton.addActionListener(this);
        updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(this);
        backButton = new JButton("Back");
        backButton.addActionListener(this);

        buttonPanel.add(assignButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
            return;
        }

        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue first.");
            return;
        }

        String issueID = (String) tableModel.getValueAt(selectedRow, 0);
        CustomerIssue selectedIssue = getIssueById(issueID);

        if (e.getSource() == assignButton) {
            assignScheduler(selectedIssue);
        } else if (e.getSource() == updateStatusButton) {
            updateIssueStatus(selectedIssue);
        }
    }

    private CustomerIssue getIssueById(String issueID) {
        List<CustomerIssue> issues = FileHandler.getCustomerIssues();
        for (CustomerIssue issue : issues) {
            if (issue.getIssueID().equals(issueID)) {
                return issue;
            }
        }
        return null;
    }

    private void assignScheduler(CustomerIssue issue) {
        List<User> schedulers = FileHandler.getSchedulers();
        JComboBox<User> schedulerComboBox = new JComboBox<>(schedulers.toArray(new User[0]));
        schedulerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User user = (User) value;
                    setText(user.getUserID() + " - " + user.getUsername());
                }
                return this;
            }
        });

        int result = JOptionPane.showConfirmDialog(this, schedulerComboBox, "Select Scheduler", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            User selectedScheduler = (User) schedulerComboBox.getSelectedItem();
            issue.setAssignedSchedulerID(selectedScheduler.getUserID());
            issue.setStatus("Assigned");
            FileHandler.updateCustomerIssue(issue);
            JOptionPane.showMessageDialog(this, "Scheduler assigned successfully.");
            refreshIssuesTable();
        }
    }

    private void updateIssueStatus(CustomerIssue issue) {
        String[] statuses = {"In Progress", "Done", "Closed", "Cancelled"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(issue.getStatus());

        int result = JOptionPane.showConfirmDialog(this, statusComboBox, "Update Status", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) statusComboBox.getSelectedItem();
            issue.setStatus(newStatus);
            FileHandler.updateCustomerIssue(issue);
            JOptionPane.showMessageDialog(this, "Status updated successfully.");
            refreshIssuesTable();
        }
    }

    private void refreshIssuesTable() {
        List<CustomerIssue> updatedIssues = FileHandler.getCustomerIssues();
        tableModel.setRowCount(0);
        for (CustomerIssue issue : updatedIssues) {
            Object[] row = {issue.getIssueID(), issue.getCustomerID(), issue.getDescription(),
                    issue.getStatus(), issue.getAssignedSchedulerID(), issue.getHallName()};
            tableModel.addRow(row);
        }
    }
}