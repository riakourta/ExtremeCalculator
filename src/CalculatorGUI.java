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
        frame.setSize(300, 450);
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

        //Πάνελ μετατροπέα
        JPanel converterPanel = new JPanel();
        converterPanel.setLayout(new FlowLayout());

        String[] currencies = {"EUR", "USD", "GBP", "JPY"};
        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);

        convertButton = new JButton("Convert");

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(display.getText().isEmpty()){
                    return;
                }

                final double amount;
                try {
                    amount = Double.parseDouble(display.getText());
                }catch (NumberFormatException ex) {
                    display.setText("Error");
                    return;
                }
                final String from =  fromCurrency.getSelectedItem().toString();
                final String to = toCurrency.getSelectedItem().toString();

                convertButton.setEnabled(false);
                display.setText("Wait...");

                SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
                    @Override
                    protected Double doInBackground() {
                        return myConverter.convert(amount, from, to);
                    }

                    @Override
                    protected void done() {
                        try {
                            double result = get();
                            display.setText(String.valueOf(result));
                        } catch (InterruptedException | ExecutionException ex) {
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

        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.charAt(0) >= '0' && command.charAt(0) <= '9') {
                if (display.getText().equals("Wait...") || display.getText().equals("Error") || display.getText().equals("API Error")) {
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
                double num2 = Double.parseDouble(display.getText());
                double result = 0;


                if (operator.equals("+")) {
                    result = myCalc.add(num1, num2);
                } else if (operator.equals("-")) {
                    result = myCalc.subtract(num1, num2);
                } else if (operator.equals("*")) {
                    result = myCalc.multiply(num1, num2);
                } else if (operator.equals("/")) {
                    result = myCalc.divide(num1, num2);
                }

                display.setText(String.valueOf(result));
            }
            else {
                if (!display.getText().isEmpty()) {
                    num1 = Double.parseDouble(display.getText());
                    operator = command;
                    display.setText("");
                }
            }
        }
    }
}