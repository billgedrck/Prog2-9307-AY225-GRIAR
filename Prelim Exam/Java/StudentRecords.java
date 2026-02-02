package Java;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentRecords extends JFrame {

    DefaultTableModel model;
    JTable table;
    // Input fields
    JTextField txtId, txtFirstName, txtLastName, txtLab1, txtLab2, txtLab3, txtExam, txtAttendance;
    int autoIdCounter = 2039;

    public StudentRecords() {

        this.setTitle("Records - Bill Gedrick D. Griar 23-0426-860");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Table
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // FIXED HEADERS
        String[] headers = {"StudentID", "First Name", "Last Name", "LAB WORK 1", "LAB WORK 2",
                "LAB WORK 3", "PRELIM EXAM", "ATTENDANCE"};
        model.setColumnIdentifiers(headers);

        loadCSV(); // load CSV data under fixed headers

        // INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(2, 8, 5, 5));

        txtId = new JTextField(10);
        txtFirstName = new JTextField(10);
        txtLastName = new JTextField(10);
        txtLab1 = new JTextField(5);
        txtLab2 = new JTextField(5);
        txtLab3 = new JTextField(5);
        txtExam = new JTextField(5);
        txtAttendance = new JTextField(5);

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");

        // Labels
        inputPanel.add(new JLabel("StudentID"));
        inputPanel.add(new JLabel("First Name"));
        inputPanel.add(new JLabel("Last Name"));
        inputPanel.add(new JLabel("LAB 1"));
        inputPanel.add(new JLabel("LAB 2"));
        inputPanel.add(new JLabel("LAB 3"));
        inputPanel.add(new JLabel("Prelim Exam"));
        inputPanel.add(new JLabel("Attendance"));

        // Text fields
        inputPanel.add(txtId);
        inputPanel.add(txtFirstName);
        inputPanel.add(txtLastName);
        inputPanel.add(txtLab1);
        inputPanel.add(txtLab2);
        inputPanel.add(txtLab3);
        inputPanel.add(txtExam);
        inputPanel.add(txtAttendance);

        // Buttons panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnDelete);

        add(inputPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // ADD button
        btnAdd.addActionListener(e -> addStudent());

        // DELETE button
        btnDelete.addActionListener(e -> deleteSelectedRows());

        // ENTER navigation
        txtId.addActionListener(e -> txtFirstName.requestFocus());
        txtFirstName.addActionListener(e -> txtLastName.requestFocus());
        txtLastName.addActionListener(e -> txtLab1.requestFocus());
        txtLab1.addActionListener(e -> txtLab2.requestFocus());
        txtLab2.addActionListener(e -> txtLab3.requestFocus());
        txtLab3.addActionListener(e -> txtExam.requestFocus());
        txtExam.addActionListener(e -> txtAttendance.requestFocus());
        txtAttendance.addActionListener(e -> addStudent());

        setVisible(true);
    }

    // Add new row
    private void addStudent() {
        String id = txtId.getText().trim();
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String lab1 = txtLab1.getText().trim();
        String lab2 = txtLab2.getText().trim();
        String lab3 = txtLab3.getText().trim();
        String exam = txtExam.getText().trim();
        String attendance = txtAttendance.getText().trim();

        // Auto-generate ID if empty
        if (id.isEmpty()) {
            id = "25-" + autoIdCounter + "-225";
            autoIdCounter++;
        }

        // Capitalize names
        if (!first.isEmpty()) first = capitalize(first);
        if (!last.isEmpty()) last = capitalize(last);

        // Default numeric values
        if (lab1.isEmpty()) lab1 = "0";
        if (lab2.isEmpty()) lab2 = "0";
        if (lab3.isEmpty()) lab3 = "0";
        if (exam.isEmpty()) exam = "0";
        if (attendance.isEmpty()) attendance = "0";

        model.addRow(new String[]{id, first, last, lab1, lab2, lab3, exam, attendance});

        // --- PERMANENTLY SAVE TO CSV ---
        saveToCSV(id, first, last, lab1, lab2, lab3, exam, attendance);

        // Clear fields
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtLab1.setText("");
        txtLab2.setText("");
        txtLab3.setText("");
        txtExam.setText("");
        txtAttendance.setText("");

        txtId.requestFocus();
    }

    // Delete selected rows
    private void deleteSelectedRows() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the selected rows?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }
        }
    }

    private String capitalize(String str) {
        if (str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    // Load CSV under fixed headers
    private void loadCSV() {
        try {
            InputStream is = getClass().getResourceAsStream("class_records.csv");
            if (is == null) throw new FileNotFoundException("class_records.csv not found in java folder");

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip the first row if it contains actual headers in the CSV
                if (firstLine) {
                    firstLine = false;
                    continue; // ignore first line
                }
                String[] data = line.split(",", -1);
                model.addRow(data);
            }

            br.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // --- SAVE NEW STUDENT PERMANENTLY TO CSV ---
    private void saveToCSV(String id, String first, String last, String lab1, String lab2,
                           String lab3, String exam, String attendance) {
        try (FileWriter fw = new FileWriter("class_records.csv", true);  // append mode
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(String.join(",", id, first, last, lab1, lab2, lab3, exam, attendance));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new StudentRecords();
    }
}
