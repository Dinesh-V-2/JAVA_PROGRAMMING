
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class LibrarySearch extends JFrame {
    JTextField searchField = new JTextField(20);
    JComboBox<String> category = new JComboBox<>(
        new String[]{"All", "Computer Science",
                     "Literature", "Science"});
    JLabel status = new JLabel("Enter a keyword");
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"Book ID", "Title", "Author", "Category"}, 0) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    JTable table = new JTable(model);

    String[][] books = {
        {"B101", "Data Structures", "S. Lipschutz", "Computer Science"},
        {"B205", "Algorithms", "T. Cormen", "Computer Science"},
        {"B310", "Java Programming", "Herbert Schildt", "Computer Science"},
        {"B401", "The Alchemist", "Paulo Coelho", "Literature"},
        {"B502", "Physics Fundamentals", "H. Young", "Science"}
    };

    java.util.List<String[]> filtered = new ArrayList<>();
    int page = 0;
    final int PAGE_SIZE = 3;

    public LibrarySearch() {
        super("Campus Library Search");
        setLayout(new BorderLayout(8, 8));

        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEFT));
        title.add(new JLabel("CAMPUS LIBRARY SEARCH"));
        add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton search = new JButton("Search");
        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(category);
        searchPanel.add(search);

        JPanel body = new JPanel(new BorderLayout());
        body.add(searchPanel, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton prev = new JButton("Prev");
        JButton next = new JButton("Next");
        JButton reserve = new JButton("Reserve");
        navigation.add(prev);
        navigation.add(next);
        navigation.add(reserve);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(navigation, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        category.addActionListener(e -> performSearch());

        prev.addActionListener(e -> {
            if (page > 0) {
                page--;
                showPage();
            }
        });

        next.addActionListener(e -> {
            if ((page + 1) * PAGE_SIZE < filtered.size()) {
                page++;
                showPage();
            }
        });

        reserve.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(
                    this, "Select a book first");
            } else {
                String titleName = table.getValueAt(row, 1).toString();
                JOptionPane.showMessageDialog(
                    this, "Reservation requested for: " + titleName);
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(560, 320));
        setSize(850, 520);
        setLocationRelativeTo(null);
        performSearch();
    }

    void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selected = (String) category.getSelectedItem();

        filtered.clear();

        for (String[] book : books) {
            boolean matchesKeyword =
                book[1].toLowerCase().contains(keyword) ||
                book[2].toLowerCase().contains(keyword);

            boolean matchesCategory =
                selected.equals("All") || book[3].equals(selected);

            if (matchesKeyword && matchesCategory)
                filtered.add(book);
        }

        page = 0;
        showPage();
    }

    void showPage() {
        model.setRowCount(0);
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filtered.size());

        for (int i = start; i < end; i++)
            model.addRow(filtered.get(i));

        status.setText("Showing " +
            (filtered.isEmpty() ? 0 : start + 1) +
            "-" + end + " of " + filtered.size() + " books");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new LibrarySearch().setVisible(true));
    }
}
