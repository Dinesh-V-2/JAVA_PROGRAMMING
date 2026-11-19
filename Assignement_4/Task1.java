
import javax.swing.*;
import java.awt.*;

class FeeService {
    public String pay(String regNo, String amountText) {
        if (regNo == null || regNo.trim().isEmpty())
            return "Enter registration number";

        try {
            double amount = Double.parseDouble(amountText.trim());
            if (!Double.isFinite(amount) || amount <= 0)
                return "Enter a positive amount";

            return String.format(
                "Payment successful for %s: Rs. %.2f",
                regNo, amount);

        } catch (NumberFormatException e) {
            return "Enter a valid amount";
        }
    }
}

public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(15);
    private final JTextField amount = new JTextField(15);
    private final JLabel status =
            new JLabel("Enter payment details");

    private final FeeService service = new FeeService();
    private String lastReceipt = null;

    public FeeWindow() {
        super("Student Fee Payment");

        setLayout(new GridLayout(4, 2, 10, 10));

        JButton pay = new JButton("Pay");
        JButton clear = new JButton("Clear");
        JButton receipt = new JButton("Receipt");

        add(new JLabel("Registration Number:"));
        add(regNo);
        add(new JLabel("Amount (Rs.):"));
        add(amount);

        add(pay);
        add(clear);
        add(receipt);
        add(status);

        pay.addActionListener(e -> {
            String message = service.pay(
                    regNo.getText(), amount.getText());

            status.setText(message);

            if (message.startsWith("Payment successful")) {
                lastReceipt = message;
                JOptionPane.showMessageDialog(this, message);
            } else {
                JOptionPane.showMessageDialog(
                        this, message, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Fields cleared");
        });

        receipt.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    lastReceipt == null
                    ? "No successful payment yet"
                    : lastReceipt);
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 250);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new FeeWindow().setVisible(true));
    }
}
