
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class RegistrationFrame extends JFrame {

    RegistrationDAO dao = new RegistrationDAO();
    RegistrationService service = new RegistrationService();

    JTextField studentId = new JTextField(15);
    JTextField studentName = new JTextField(15);

    JComboBox<EventItem> eventBox = new JComboBox<>();
    JComboBox<EventItem> filterBox = new JComboBox<>();

    JLabel status = new JLabel("Ready");
    JLabel tableStatus = new JLabel("No records loaded");

    JButton registerButton = new JButton("Register");

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Reg ID", "Student ID", "Name",
                     "Event", "Registered At"}, 0) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    int currentPage = 0;
    final int PAGE_SIZE = 20;

    static class EventItem {
        int id;
        String name;

        EventItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public RegistrationFrame() {
        super("Campus Event Registration System");

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student Registration", createStudentPanel());
        tabs.addTab("Committee View", createCommitteePanel());

        add(tabs);

        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> dispose());
        file.add(exit);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Campus Event Registration System\nCSA09 - CO4"));
        help.add(about);

        menu.add(file);
        menu.add(help);
        setJMenuBar(menu);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);

        loadEvents();
    }

    JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        form.add(new JLabel("Student ID:"));
        form.add(studentId);

        form.add(new JLabel("Student Name:"));
        form.add(studentName);

        form.add(new JLabel("Select Event:"));
        form.add(eventBox);

        JButton clear = new JButton("Clear");
        form.add(registerButton);
        form.add(clear);

        panel.add(form, BorderLayout.NORTH);
        panel.add(status, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> register());

        clear.addActionListener(e -> {
            studentId.setText("");
            studentName.setText("");
            status.setText("Form cleared");
        });

        return panel;
    }

    JPanel createCommitteePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton filter = new JButton("Apply Filter");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");

        top.add(new JLabel("Event:"));
        top.add(filterBox);
        top.add(filter);
        top.add(refresh);
        top.add(export);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(model)),
                  BorderLayout.CENTER);

        JButton prev = new JButton("Previous");
        JButton next = new JButton("Next");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(tableStatus);
        bottom.add(prev);
        bottom.add(next);

        panel.add(bottom, BorderLayout.SOUTH);

        filter.addActionListener(e -> {
            currentPage = 0;
            loadPage();
        });

        refresh.addActionListener(e -> loadPage());

        prev.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadPage();
            }
        });

        next.addActionListener(e -> {
            if (model.getRowCount() == PAGE_SIZE) {
                currentPage++;
                loadPage();
            }
        });

        export.addActionListener(e -> exportCSV());

        return panel;
    }

    Integer selectedFilter() {
        EventItem item = (EventItem) filterBox.getSelectedItem();
        return item == null || item.id == 0 ? null : item.id;
    }

    void loadEvents() {
        new SwingWorker<List<Object[]>, Void>() {
            protected List<Object[]> doInBackground() throws Exception {
                return dao.getEvents();
            }

            protected void done() {
                try {
                    List<Object[]> events = get();

                    filterBox.addItem(new EventItem(0, "All Events"));

                    for (Object[] row : events) {
                        int id = (Integer) row[0];
                        String name = (String) row[1];

                        eventBox.addItem(new EventItem(id, name));
                        filterBox.addItem(new EventItem(id, name));
                    }

                    loadPage();

                } catch (Exception e) {
                    status.setText("Database connection failed");
                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "Could not load events. Check MySQL settings.");
                }
            }
        }.execute();
    }

    void register() {
        String id = studentId.getText().trim();
        String name = studentName.getText().trim();
        EventItem selected = (EventItem) eventBox.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || selected == null) {
            JOptionPane.showMessageDialog(
                this, "Complete all required fields");
            return;
        }

        int eventId = selected.id;
        int confirm = JOptionPane.showConfirmDialog(
            this, "Register for " + selected.name + "?",
            "Confirm Registration", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        registerButton.setEnabled(false);
        status.setText("Saving registration...");

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.register(id, name, eventId);
                return null;
            }

            protected void done() {
                registerButton.setEnabled(true);

                try {
                    get();
                    status.setText("Registration successful");
                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "Registration successful!");

                    studentId.setText("");
                    studentName.setText("");
                    loadPage();

                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    String message = cause instanceof
                        IllegalArgumentException
                        ? cause.getMessage()
                        : "Registration failed. Check database connection.";

                    status.setText(message);
                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this, message);
                }
            }
        }.execute();
    }

    void loadPage() {
        Integer eventId = selectedFilter();
        int requestedPage = currentPage;

        tableStatus.setText("Loading...");

        new SwingWorker<List<Object[]>, Void>() {
            protected List<Object[]> doInBackground() throws Exception {
                return dao.findPage(
                    eventId, PAGE_SIZE, requestedPage * PAGE_SIZE);
            }

            protected void done() {
                try {
                    List<Object[]> rows = get();

                    model.setRowCount(0);
                    for (Object[] row : rows)
                        model.addRow(row);

                    tableStatus.setText(
                        "Page " + (requestedPage + 1) +
                        " | Loaded: " + rows.size());

                } catch (Exception e) {
                    tableStatus.setText("Unable to load records");
                }
            }
        }.execute();
    }

    void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("registrations.csv"));

        if (chooser.showSaveDialog(this) !=
                JFileChooser.APPROVE_OPTION)
            return;

        File file = chooser.getSelectedFile();
        Integer eventId = selectedFilter();

        tableStatus.setText("Exporting...");

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                dao.exportCSV(eventId, file);
                return null;
            }

            protected void done() {
                try {
                    get();
                    tableStatus.setText("Export completed");
                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "CSV exported successfully");

                } catch (Exception e) {
                    tableStatus.setText("Export failed");
                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "Unable to export CSV");
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new RegistrationFrame().setVisible(true));
    }
}
