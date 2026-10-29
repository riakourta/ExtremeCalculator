import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class CalculatorGUI {

    private CurrencyConverter myConverter;
    private Calculator myCalc;

    private JFrame frame;
    private JTextField display;
    private JTextArea historyArea;

    private double num1 = 0;
    private String operator = "";

    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JButton convertButton;

    public CalculatorGUI() {

        myCalc = new Calculator();
        myConverter = new CurrencyConverter();

        frame = new JFrame("Our Calc<3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Αυξήθηκε το πλάτος για να χωράει το ιστορικό δεξιά
        frame.setSize(520, 450);
        frame.setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        frame.add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "C", "0", "=", "+"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);

        // Περιοχή ιστορικού
        historyArea = new JTextArea(10, 15);
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("History")
        );

        frame.add(scrollPane, BorderLayout.EAST);

        // Πάνελ μετατροπέα
        JPanel converterPanel = new JPanel();
        converterPanel.setLayout(new FlowLayout());

        String[] currencies = {"EUR", "USD", "GBP", "JPY"};

        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);

        convertButton = new JButton("Convert");

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                final double amount;

                try {
                    amount = Double.parseDouble(display.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Μη έγκυρη εισαγωγή", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                final String from =
                        fromCurrency.getSelectedItem().toString();

                final String to =
                        toCurrency.getSelectedItem().toString();

                convertButton.setEnabled(false);
                display.setText("Wait...");

                SwingWorker<Double, Void> worker =
                        new SwingWorker<Double, Void>() {

                            @Override
                            protected Double doInBackground() {
                                return myConverter.convert(amount, from, to);
                            }

                            @Override
                            protected void done() {
                                try {
                                    double result = get();
                                    if (result == 0.0) {
                                        display.setText("");
                                        JOptionPane.showMessageDialog(frame, "Σφάλμα δικτύου: Αδύνατη η λήψη ισοτιμιών", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    display.setText(String.valueOf(result));

                                    // Καταγραφή της μετατροπής στο ιστορικό
                                    historyArea.append(
                                            amount + " " + from
                                                    + " = "
                                                    + result + " " + to
                                                    + "\n"
                                    );

                                    // Μετακίνηση στην τελευταία εγγραφή
                                    historyArea.setCaretPosition(
                                            historyArea.getDocument().getLength()
                                    );

                                } catch (InterruptedException |
                                         ExecutionException ex) {

                                    display.setText("API Error");

                                } finally {
                                    convertButton.setEnabled(true);
                                }
                            }
                        };

                worker.execute();
            }
        });

        converterPanel.add(fromCurrency);
        converterPanel.add(toCurrency);
        converterPanel.add(convertButton);

        frame.add(converterPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String command = e.getActionCommand();

            if (command.charAt(0) >= '0' &&
                    command.charAt(0) <= '9') {

                if (display.getText().equals("Wait...") ||
                        display.getText().equals("Error") ||
                        display.getText().equals("API Error")) {

                    display.setText("");
                }

                display.setText(display.getText() + command);
            }

            else if (command.equals("C")) {
                display.setText("");
                num1 = 0;
                operator = "";
            }

            else if (command.equals("=")) {

                if (operator.isEmpty()) {
                    return;
                }

                double num2;

                try {
                    num2 = Double.parseDouble(display.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Μη έγκυρη εισαγωγή", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double result = 0;

                if (operator.equals("+")) {
                    result = myCalc.add(num1, num2);

                } else if (operator.equals("-")) {
                    result = myCalc.subtract(num1, num2);

                } else if (operator.equals("*")) {
                    result = myCalc.multiply(num1, num2);

                } else if (operator.equals("/")) {
                    try {
                        result = myCalc.divide(num1, num2);
                    } catch (ArithmeticException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                        display.setText("");
                        num1 = 0;
                        operator = "";
                        return;
                    }
                }

                display.setText(String.valueOf(result));

                // Καταγραφή της μαθηματικής πράξης
                historyArea.append(
                        num1 + " " + operator + " "
                                + num2 + " = " + result + "\n"
                );

                // Μετακίνηση στην τελευταία εγγραφή
                historyArea.setCaretPosition(
                        historyArea.getDocument().getLength()
                );
            }

            else {
                try {
                    num1 = Double.parseDouble(display.getText());
                    operator = command;
                    display.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Μη έγκυρη εισαγωγή", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }
}