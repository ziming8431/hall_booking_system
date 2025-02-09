package java_assignment;

public class CustomerIssue {
    private String issueID;
    private String customerID;
    private String description;
    private String status;
    private String assignedSchedulerID;
    private String hallName;

    public CustomerIssue(String issueID, String customerID, String description, String status, String assignedSchedulerID, String hallName) {
        this.issueID = issueID;
        this.customerID = customerID;
        this.description = description;
        this.status = status;
        this.assignedSchedulerID = assignedSchedulerID;
        this.hallName = hallName;
    }

    // Getters
    public String getIssueID() { return issueID; }
    public String getCustomerID() { return customerID; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getAssignedSchedulerID() { return assignedSchedulerID; }
    public String getHallName() { return hallName; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setAssignedSchedulerID(String assignedSchedulerID) { this.assignedSchedulerID = assignedSchedulerID; }

    @Override
    public String toString() {
        return String.format("Issue ID: %s, Customer: %s, Hall: %s, Status: %s", issueID, customerID, hallName, status);
    }
}