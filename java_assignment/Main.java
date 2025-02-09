package java_assignment;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        initializeApplication();
        new LandingPage();
    }

    private static void initializeApplication() {
        File adminFile = new File("admin.txt");
        if (!adminFile.exists() || adminFile.length() == 0) {
            Admin superAdmin = new Admin("A001", "superadmin", "superadmin", "123");
            FileHandler.allAdmins.add(superAdmin);
            FileHandler.writeAdmins();
        } else {
            FileHandler.getAllAdmins();
            if (FileHandler.allAdmins.isEmpty()) {
                Admin superAdmin = new Admin("A001", "superadmin", "superadmin", "123");
                FileHandler.allAdmins.add(superAdmin);
                FileHandler.writeAdmins();
            }
        }

        File usersFile = new File("C:/Users/Afsar003/IdeaProjects/Final_need_check/CheckLastTimV0/java_assignment/users.txt");
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating users.txt: " + e.getMessage());
            }
        } else {
            FileHandler.getAllUsers();
        }
    }
}