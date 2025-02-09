package java_assignment;
import java.io.*;
import java.util.*;

public class HallRating extends Rating {

    private static final String FILE_NAME = "Rating.txt";

    public HallRating(String hallType) {
        super(hallType);
    }

    // Save the rating for the hall to the file
    @Override
    public void saveRating(int rating, String userID) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            writer.println(hallType + ":" + rating + ":" + userID);
        }
    }

    // Calculate the average rating for this hall type
    @Override
    public int calculateAverageRating() throws IOException {
        int sum = 0;
        int count = 0;

        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                if (parts[0].equals(hallType)) {
                    sum += Integer.parseInt(parts[1]);
                    count++;
                }
            }
        }

        if (count == 0) return 0;

        double mean = (double) sum / count;
        return (int) Math.round(mean);  // Round to the nearest integer
    }

    @Override
    public void recordRating(int rating, String userID) throws IOException {
        if (!hasUserRated(userID)) {
            saveRating(rating, userID);
            int averageRating = calculateAverageRating();
            System.out.println("The new average rating for " + hallType + " is " + averageRating);
        } else {
            throw new IllegalStateException("User has already rated this hall.");
        }
    }

    private boolean hasUserRated(String userID) throws IOException {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                if (parts[0].equals(hallType) && parts[2].equals(userID)) {
                    return true;
                }
            }
        }
        return false;
    }
}
