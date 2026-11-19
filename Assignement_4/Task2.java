
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class AppointmentFrame extends JFrame {

    private final JTextField patientId = new JTextField(15);

    private final JComboBox<String> department =
        new JComboBox<>(new String[]{
            "Select Department",
            "Cardiology", "Neurology", "Orthopedics"
        });

    private final JComboBox<String> doctor =
        new JComboBox<>();

    private final JComboBox<String> timeSlot =
        new JComboBox<>(new String[]{
            "Select Slot", "09:00 AM",
            "10:00 AM", "11:00 AM", "02:00 PM"
        });

    private final JLabel status =
        new JLabel("Enter appointment details");

    private final JButton book = new JButton("Book");
    private volatile boolean bookingInProgress = false;

    private final Map<String, String[]> doctors =
        new HashMap<>();

    public AppointmentFrame() {
        super("Hospital Appointment Desk");

        doctors.put("Cardiology",
            new String[]{"Dr. Kumar", "Dr. Priya"});
        doctors.put("Neurology",
            new String[]{"Dr. Ravi", "Dr. Meena"});
        doctors.put("Orthopedics",
            new String[]{"Dr. Arun", "Dr. Divya"});

        setLayout(new BorderLayout(10, 10));

        JPanel heading = new JPanel();
        heading.add(new JLabel(
            "Hospital Outpatient Appointment Desk"));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        form.add(new JLabel("Patient ID:"));
        form.add(patientId);

        form.add(new JLabel("Department:"));
        form.add(department);

        form.add(new JLabel("Doctor:"));
        form.add(doctor);

        form.add(new JLabel("Time Slot:"));
        form.add(timeSlot);

        JButton clear = new JButton("Clear");
        form.add(book);
        form.add(clear);

        add(form, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        department.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                refreshDoctors();
        });

        book.addActionListener(e -> bookAppointment());

        clear.addActionListener(e -> {
            if (bookingInProgress) return;
            patientId.setText("");
            department.setSelectedIndex(0);
            timeSlot.setSelectedIndex(0);
            status.setText("Form cleared");
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    int choice = JOptionPane.showConfirmDialog(
                        AppointmentFrame.this,
                        "Booking is in progress. Close anyway?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION)
                        dispose();
                } else {
                    dispose();
                }
            }
        });

        refreshDoctors();
        setSize(550, 350);
        setLocationRelativeTo(null);
    }

    private void refreshDoctors() {
        doctor.removeAllItems();

        String selected = (String) department.getSelectedItem();
        String[] list = doctors.get(selected);

        if (list == null || list.length == 0) {
            doctor.addItem("No doctors available");
            doctor.setEnabled(false);
        } else {
            for (String name : list)
                doctor.addItem(name);
            doctor.setEnabled(true);
        }
    }

    private void bookAppointment() {
        String id = patientId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Enter patient ID");
            return;
        }

        if (department.getSelectedIndex() == 0 ||
            !doctor.isEnabled() ||
            timeSlot.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(
                this, "Complete all appointment selections");
            return;
        }

        String dept = (String) department.getSelectedItem();
        String doctorName = (String) doctor.getSelectedItem();
        String slot = (String) timeSlot.getSelectedItem();

        bookingInProgress = true;
        book.setEnabled(false);
        status.setText("Processing appointment...");

        SwingWorker<String, Void> worker =
            new SwingWorker<>() {

            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(1200);
                return "APT-" + System.currentTimeMillis();
            }

            @Override
            protected void done() {
                bookingInProgress = false;
                book.setEnabled(true);

                if (!isDisplayable()) return;

                try {
                    String confirmation = get();
                    String message =
                        "Appointment Confirmed!\n" +
                        "Patient ID: " + id + "\n" +
                        "Department: " + dept + "\n" +
                        "Doctor: " + doctorName + "\n" +
                        "Slot: " + slot + "\n" +
                        "Confirmation: " + confirmation;

                    status.setText("Booking successful");
                    JOptionPane.showMessageDialog(
                        AppointmentFrame.this, message);

                } catch (Exception ex) {
                    status.setText("Booking failed");
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new AppointmentFrame().setVisible(true));
    }
}
