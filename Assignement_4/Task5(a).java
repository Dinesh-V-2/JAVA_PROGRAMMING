
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConverterA extends JFrame implements ActionListener {

    JTextField amount = new JTextField(10);
    JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});
    JLabel result = new JLabel("Result");

    public ConverterA() {
        super("Converter A - ActionListener");
        setLayout(new FlowLayout());

        JButton convert = new JButton("Convert");

        add(new JLabel("INR:"));
        add(amount);
        add(target);
        add(convert);
        add(result);

        convert.addActionListener(this);
        amount.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 160);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        convert();
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
            JOptionPane.showMessageDialog(this, message);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new ConverterA().setVisible(true));
    }
}
