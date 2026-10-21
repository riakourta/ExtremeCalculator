import javax.swing.;
import java.awt.;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI {


    private Calculator myCalc;

    private JFrame frame;
    private JTextField display;
    private double num1 = 0;
    private String operator = "";

    public CalculatorGUI() {

        myCalc = new Calculator();


        frame = new JFrame("Our Calc<3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
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
        frame.setVisible(true);
    }

