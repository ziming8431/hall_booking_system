package java_assignment;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SchedulerBooking extends Makehallbooking {

    public SchedulerBooking(String userID, JFrame previousFrame) {
        super(userID, previousFrame);
    }

    @Override
    protected boolean validateBookingTime(String startDate, String endDate, String startTime, String endTime) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalTime timeStart = LocalTime.parse(startTime);
            LocalTime timeEnd = LocalTime.parse(endTime);

            // Check if end date is before start date
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
                return false;
            }

            // Check if end time is before start time on the same day
            if (start.isEqual(end) && timeEnd.isBefore(timeStart)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time.");
                return false;
            }

            // No time restrictions for schedulers
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.");
            return false;
        }
    }
}