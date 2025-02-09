package java_assignment;
import java.io.*;

public abstract class Rating {
    protected String hallType;

    public Rating(String hallType) {
        this.hallType = hallType;
    }

    public abstract void recordRating(int rating, String userID) throws IOException;
    
    public abstract int calculateAverageRating() throws IOException;

    public abstract void saveRating(int rating, String userID) throws IOException;
}
