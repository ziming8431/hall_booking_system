package java_assignment;

public class Admin {
    private String userID;
    private String username;
    private String password;
    private String role;

    public Admin(String userID, String role, String username, String password) {
        this.userID = userID;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}