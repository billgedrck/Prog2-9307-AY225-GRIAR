import javax.swing.*;
import java.awt.*;

public class PrelimGradeCalculatorApp extends JFrame {

    private JTextField attendance, lab1, lab2, lab3;
    private JTextArea result;

    public PrelimGradeCalculatorApp() {
        setTitle("Prelim Grade Calculator");
        setSize(450, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main background
        JPanel background = new JPanel();
        background.setBackground(new Color(118, 75, 162)); // purple tone
        background.setLayout(new GridBagLayout());

        // Card panel
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(380, 480));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Prelim Exam Calculator");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your Class Standing data below");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        attendance = createInput("Attendance (0–100)");
        lab1 = createInput("Lab Work 1 (0–100)");
        lab2 = createInput("Lab Work 2 (0–100)");
        lab3 = createInput("Lab Work 3 (0–100)");

        JButton calculate = new JButton("Calculate");
        calculate.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculate.setBackground(new Color(102, 126, 234));
        calculate.setForeground(Color.WHITE);
        calculate.setFocusPainted(false);
        calculate.setFont(new Font("SansSerif", Font.BOLD, 14));
        calculate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        result = new JTextArea(8, 20);
        result.setEditable(false);
        result.setFont(new Font("SansSerif", Font.PLAIN, 13));
        result.setBackground(new Color(232, 240, 254));
        result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        calculate.addActionListener(e -> calculate());

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(attendance);
        card.add(lab1);
        card.add(lab2);
        card.add(lab3);
        card.add(Box.createVerticalStrut(15));
        card.add(calculate);
        card.add(Box.createVerticalStrut(15));
        card.add(result);

        background.add(card);
        add(background);
    }

    private JTextField createInput(String label) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createTitledBorder(label));
        return field;
    }

    private void calculate() {
        try {
            double a = Double.parseDouble(attendance.getText());
            double l1 = Double.parseDouble(lab1.getText());
            double l2 = Double.parseDouble(lab2.getText());
            double l3 = Double.parseDouble(lab3.getText());

            if (!valid(a, l1, l2, l3)) {
                JOptionPane.showMessageDialog(this,
                        "All grades must be between 0 and 100.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double labAvg = (l1 + l2 + l3) / 3;
            double classStanding = (a * 0.40) + (labAvg * 0.60);

            double pass = (75 - (classStanding * 0.30)) / 0.70;
            double excellent = (100 - (classStanding * 0.30)) / 0.70;

            pass = Math.min(pass, 100);
            excellent = Math.min(excellent, 100);

            String remark;
            if (pass > 100) remark = "Passing is no longer possible.";
            else if (pass <= 0) remark = "You already passed!";
            else remark = String.format("You need at least %.2f to pass.", pass);

            result.setText(
                    String.format(
                            "Lab Work Average: %.2f%n" +
                            "Class Standing: %.2f%n%n" +
                            "Prelim Exam to PASS: %.2f%n" +
                            "Prelim Exam for EXCELLENT: %.2f%n%n" +
                            "Remark: %s",
                            labAvg, classStanding, pass, excellent, remark
                    )
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean valid(double... values) {
        for (double v : values) {
            if (v < 0 || v > 100) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new PrelimGradeCalculatorApp().setVisible(true)
        );
    }
}
