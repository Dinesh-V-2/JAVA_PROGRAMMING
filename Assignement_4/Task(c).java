
import java.sql.*;
import java.util.*;
import java.io.*;

public class RegistrationDAO {

    public List<Object[]> getEvents() throws SQLException {
        List<Object[]> events = new ArrayList<>();

        String sql = "SELECT event_id, event_name FROM events ORDER BY event_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                events.add(new Object[]{
                    rs.getInt("event_id"),
                    rs.getString("event_name")
                });
            }
        }
        return events;
    }

    public void insert(String studentId,
                       String studentName,
                       int eventId) throws SQLException {

        String studentSql =
            "INSERT INTO students(student_id, student_name) " +
            "VALUES (?, ?) ON DUPLICATE KEY UPDATE " +
            "student_name = student_name";

        String registrationSql =
            "INSERT INTO registrations(student_id, event_id) " +
            "VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                        con.prepareStatement(studentSql)) {
                    ps.setString(1, studentId);
                    ps.setString(2, studentName);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                        con.prepareStatement(registrationSql)) {
                    ps.setString(1, studentId);
                    ps.setInt(2, eventId);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public List<Object[]> findPage(
            Integer eventId, int limit, int offset)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        String sql =
            "SELECT r.registration_id, s.student_id, " +
            "s.student_name, e.event_name, r.registered_at " +
            "FROM registrations r " +
            "JOIN students s ON r.student_id = s.student_id " +
            "JOIN events e ON r.event_id = e.event_id " +
            "WHERE (? IS NULL OR r.event_id = ?) " +
            "ORDER BY r.registration_id DESC LIMIT ? OFFSET ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (eventId == null) {
                ps.setNull(1, Types.INTEGER);
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(1, eventId);
                ps.setInt(2, eventId);
            }

            ps.setInt(3, limit);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getTimestamp(5)
                    });
                }
            }
        }
        return rows;
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public void exportCSV(Integer eventId, File file)
            throws Exception {

        String sql =
            "SELECT r.registration_id, s.student_id, " +
            "s.student_name, e.event_name, r.registered_at " +
            "FROM registrations r " +
            "JOIN students s ON r.student_id = s.student_id " +
            "JOIN events e ON r.event_id = e.event_id " +
            "WHERE (? IS NULL OR r.event_id = ?) " +
            "ORDER BY r.registration_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             BufferedWriter writer = new BufferedWriter(
                 new FileWriter(file))) {

            if (eventId == null) {
                ps.setNull(1, Types.INTEGER);
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(1, eventId);
                ps.setInt(2, eventId);
            }

            writer.write(
                "Registration ID,Student ID,Student Name,Event,Date");
            writer.newLine();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] values = {
                        String.valueOf(rs.getLong(1)),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        String.valueOf(rs.getTimestamp(5))
                    };

                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) writer.write(",");
                        writer.write(csv(values[i]));
                    }
                    writer.newLine();
                }
            }
        }
    }
}
