
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Converter extends JFrame {

    JTextField amount = new JTextField(10);
    JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});
    JLabel result = new JLabel("Result");

    public Converter() {
        super("Converter D - Lambda");
        setLayout(new FlowLayout());

        JButton button = new JButton("Convert");

        add(new JLabel("INR:"));
        add(amount);
        add(target);
        add(button);
        add(result);

        ActionListener handler = e -> convert();

        button.addActionListener(handler);
        amount.addActionListener(handler);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 160);
        setLocationRelativeTo(null);
    }

    void convert() {
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (!Double.isFinite(inr) || inr < 0)
                throw new NumberFormatException();

            String currency = (String) target.getSelectedItem();
            double rate = currency.equals("USD") ? 0.012 : 0.011;

            String message = String.format(
                "%s %.2f", currency, inr * rate);
            result.setText(message);

            JOptionPane.showMessageDialog(
                this, message, "Conversion Result",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(
                this, "Invalid amount",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Converter().setVisible(true));
    }
}
