
import java.sql.*;

public class RegistrationService {

    private final RegistrationDAO dao = new RegistrationDAO();

    public void register(String studentId,
                         String studentName,
                         int eventId) throws Exception {

        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException(
                "Student ID is required");

        if (studentName == null || studentName.isBlank())
            throw new IllegalArgumentException(
                "Student name is required");

        if (studentId.length() > 20 || studentName.length() > 100)
            throw new IllegalArgumentException(
                "Input exceeds allowed length");

        if (eventId <= 0)
            throw new IllegalArgumentException(
                "Select a valid event");

        try {
            dao.insert(studentId.trim(), studentName.trim(), eventId);

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062)
                throw new IllegalArgumentException(
                    "Student is already registered for this event");
            throw e;
        }
    }
}
