package java_assignment;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FileHandler {
    private static final String USER_FILE = "users.txt";
    private static final String ADMIN_FILE = "admin.txt";
    private static final String ISSUES_FILE = "customer_issues.txt";
    public static List<Admin> allAdmins = new ArrayList<>();

    public static void addUser(User user) {
        List<User> users = getAllUsers();
        users.add(user);
        writeUsers(users);
    }

    public static void updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(updatedUser.getUserID())) {
                users.set(i, updatedUser);
                break;
            }
        }
        writeUsers(users);
    }

    public static void deleteUser(String userID) {
        List<User> users = getAllUsers();
        users.removeIf(user -> user.getUserID().equals(userID));
        writeUsers(users);
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(USER_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static void writeUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s",
                        user.getUserID(), user.getRole(), user.getUsername(),
                        user.getPassword(), user.getEmail(), user.getPhone()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateUserID(String rolePrefix) {
        List<User> users = getAllUsers();
        int maxID = 0;
        for (User user : users) {
            if (user.getUserID().startsWith(rolePrefix)) {
                int id = Integer.parseInt(user.getUserID().substring(1));
                if (id > maxID) {
                    maxID = id;
                }
            }
        }
        return String.format("%s%03d", rolePrefix, maxID + 1);
    }

    public static User getUserByID(String userID) {
        for (User user : getAllUsers()) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }
    public static void writeAdmins() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADMIN_FILE))) {
            for (Admin admin : allAdmins) {
                writer.println(String.format("%s,%s,%s,%s",
                        admin.getUserID(), admin.getRole(), admin.getUsername(), admin.getPassword()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Admin> getAllAdmins() {
        allAdmins.clear();
        try (Scanner scanner = new Scanner(new File(ADMIN_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    allAdmins.add(new Admin(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allAdmins;
    }

    public static User authenticateUser(String userID, String password) {
        // First, check among admins
        for (Admin admin : getAllAdmins()) {
            if (admin.getUserID().equals(userID) && admin.getPassword().equals(password)) {
                System.out.println("Admin authenticated: " + admin.getUserID() + ", Role: " + admin.getRole());
                return new User(admin.getUserID(), admin.getRole(), admin.getUsername(), admin.getPassword(), "", "");
            }
        }

        // If not found in admins, check among regular users
        for (User user : getAllUsers()) {
            if (user.getUserID().equals(userID) && user.getPassword().equals(password)) {
                System.out.println("User authenticated: " + user.getUserID() + ", Role: " + user.getRole());
                return user;
            }
        }

        System.out.println("Authentication failed for userID: " + userID);
        return null;
    }

    public static List<CustomerIssue> getCustomerIssues() {
        List<CustomerIssue> issues = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(ISSUES_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    issues.add(new CustomerIssue(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return issues;
    }

    private static void writeCustomerIssues(List<CustomerIssue> issues) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ISSUES_FILE))) {
            for (CustomerIssue issue : issues) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s",
                    issue.getIssueID(), issue.getCustomerID(), issue.getDescription(),
                    issue.getStatus(), issue.getAssignedSchedulerID(), issue.getHallName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateIssueID() {
        List<CustomerIssue> issues = getCustomerIssues();
        int maxID = 0;
        for (CustomerIssue issue : issues) {
            try {
                int id = Integer.parseInt(issue.getIssueID().substring(1));
                if (id > maxID) {
                    maxID = id;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                System.out.println("Invalid issue ID format: " + issue.getIssueID());
            }
        }
        return String.format("I%03d", maxID + 1);
    }

    public static void addCustomerIssue(CustomerIssue newIssue) {
        List<CustomerIssue> issues = getCustomerIssues();
        issues.add(newIssue);
        writeCustomerIssues(issues);
    }

    public static List<User> getSchedulers() {
        List<User> schedulers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (user.getRole().equals("Scheduler")) {
                schedulers.add(user);
            }
        }
        return schedulers;
    }

    public static List<CustomerIssue> getAssignedTasks(String schedulerID) {
        List<CustomerIssue> assignedTasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(ISSUES_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[4].equals(schedulerID)) {
                    assignedTasks.add(new CustomerIssue(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assignedTasks;
    }

    public static void updateCustomerIssue(CustomerIssue updatedIssue) {
        List<CustomerIssue> issues = getCustomerIssues();
        boolean found = false;
        for (int i = 0; i < issues.size(); i++) {
            if (issues.get(i).getIssueID().equals(updatedIssue.getIssueID())) {
                issues.set(i, updatedIssue);
                found = true;
                break;
            }
        }
        if (!found) {
            issues.add(updatedIssue);
        }
        writeCustomerIssues(issues);
    }

    public static List<String[]> getHallAvailabilityData() {
        List<String[]> hallData = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("hallAvailability.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(", ");
                if (data.length >= 8) {
                    hallData.add(new String[]{
                        data[0],  // Hall name
                        data[1],  // Hall type
                        data[2],  // Price
                        data[3],  // Capacity
                        data[4],  // Start date
                        data[5],  // End date
                        data[6],  // Start time
                        data[7]   // End time
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hallData;
    }

    public static List<String> getUniqueHallNames() {
        List<String> hallNames = new ArrayList<>();
        List<String[]> hallData = getHallAvailabilityData();
        for (String[] row : hallData) {
            if (!hallNames.contains(row[0])) {
                hallNames.add(row[0]);
            }
        }
        return hallNames;
    }

    public static boolean isHallAvailable(String hallName, String startDate, String endDate, String startTime, String endTime) {
        try {
            LocalDate bookingStartDate = LocalDate.parse(startDate);
            LocalDate bookingEndDate = LocalDate.parse(endDate);
            LocalTime bookingStartTime = LocalTime.parse(startTime);
            LocalTime bookingEndTime = LocalTime.parse(endTime);

            try (Scanner scanner = new Scanner(new File("hallAvailability.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] hall = line.split(", ");
                    if (hall[0].equals(hallName)) {
                        LocalDate availStartDate = LocalDate.parse(hall[4]);
                        LocalDate availEndDate = LocalDate.parse(hall[5]);
                        LocalTime availStartTime = LocalTime.parse(hall[6]);
                        LocalTime availEndTime = LocalTime.parse(hall[7]);

                        System.out.println("Comparing with availability: " + availStartDate + " " + availStartTime + " to " + availEndDate + " " + availEndTime);

                        // Check if the booking dates are within the available dates
                        if (bookingStartDate.compareTo(availStartDate) >= 0 && bookingEndDate.compareTo(availEndDate) <= 0) {
                            // Check if the booking times are within the available times
                            if (bookingStartTime.compareTo(availStartTime) >= 0 && bookingEndTime.compareTo(availEndTime) <= 0) {
                                // Check for conflicts with existing bookings
                                if (!hasBookingConflict(hallName, bookingStartDate, bookingEndDate, bookingStartTime, bookingEndTime)) {
                                    System.out.println("Hall is available!");
                                    return true;
                                } else {
                                    System.out.println("Booking conflict found.");
                                }
                            } else {
                                System.out.println("Booking time is outside available time range.");
                            }
                        } else {
                            System.out.println("Booking date is outside available date range.");
                        }
                    }
                }
            }
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Error parsing date or time: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Hall is not available.");
        return false;
    }

    private static boolean hasBookingConflict(String hallName, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        List<String[]> bookings = getAllBookings();
        for (String[] booking : bookings) {
            if (booking[0].equals(hallName)) {
                try {
                    LocalDate bookingStartDate = LocalDate.parse(booking[3]);
                    LocalDate bookingEndDate = LocalDate.parse(booking[4]);
                    LocalTime bookingStartTime = LocalTime.parse(booking[5]);
                    LocalTime bookingEndTime = LocalTime.parse(booking[6]);

                    System.out.println("Checking conflict with existing booking: " + bookingStartDate + " " + bookingStartTime + " to " + bookingEndDate + " " + bookingEndTime);

                    if (!(endDate.isBefore(bookingStartDate) || startDate.isAfter(bookingEndDate))) {
                        if (!(endTime.isBefore(bookingStartTime) || startTime.isAfter(bookingEndTime))) {
                            System.out.println("Conflict found!");
                            return true; // Conflict found
                        }
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Error parsing date or time in existing booking: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("No conflict found.");
        return false; // No conflict
    }

    public static boolean saveBooking(String hallName, String userID, String username, String startDate, String endDate, String startTime, String endTime, double totalPrice) {
        if (isHallAvailable(hallName, startDate, endDate, startTime, endTime)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Bookings.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%.2f\n", hallName, userID, username, startDate, endDate, startTime, endTime, totalPrice));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<String[]> getBookingHistory(String userID) {
        List<String[]> bookings = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 8 && parts[1].equals(userID)) {
                    LocalDate endDate = LocalDate.parse(parts[4], formatter);
                    if (endDate.isBefore(currentDate) || endDate.isEqual(currentDate)) {
                        bookings.add(new String[]{parts[0], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static List<String[]> getUpcomingBookings(String userID) {
        List<String[]> bookings = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 8 && parts[1].equals(userID)) {
                    LocalDate endDate = LocalDate.parse(parts[4], formatter);
                    if (endDate.isAfter(currentDate)) {
                        bookings.add(new String[]{parts[0], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static List<String[]> getBookedTimeSlots(String hallName) {
        List<String[]> bookedSlots = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals(hallName)) {
                    bookedSlots.add(new String[]{parts[3], parts[4], parts[5], parts[6], parts[2]}); // startDate, endDate, startTime, endTime, username
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookedSlots;
    }

    public static boolean cancelBooking(String[] bookingDetails) {
        List<String> updatedBookings = new ArrayList<>();
        boolean bookingFound = false;
        
        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (!parts[0].equals(bookingDetails[0]) || !parts[2].equals(bookingDetails[1]) || 
                    !parts[3].equals(bookingDetails[2]) || !parts[4].equals(bookingDetails[3]) || 
                    !parts[5].equals(bookingDetails[4])) {
                    updatedBookings.add(line);
                } else {
                    bookingFound = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        if (bookingFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Bookings.txt"))) {
                for (String booking : updatedBookings) {
                    writer.write(booking);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }

    // Add this method to the FileHandler class
    public static List<String[]> getAllBookings() {
        List<String[]> bookings = new ArrayList<>();
        File bookingsFile = new File("Bookings.txt");
        
        if (!bookingsFile.exists()) {
            try {
                bookingsFile.createNewFile();
                System.out.println("Created new Bookings.txt file");
            } catch (IOException e) {
                System.err.println("Error creating Bookings.txt file: " + e.getMessage());
                e.printStackTrace();
            }
            return bookings; // Return empty list if file was just created
        }
        
        try (Scanner scanner = new Scanner(bookingsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                bookings.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading Bookings.txt: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
}