import javax.swing.*;
import java.awt.*;  
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Attendance Tracking System
 * A Java Swing application for recording student attendance with e-signature generation
 * @author Your Name
 * @version 1.0
 */
public class AttendanceTrackerApp extends JFrame {
    
    // UI Components
    private JTextField nameField;
    private JTextField courseYearField;
    private JTextField timeInField;
    private JTextField signatureField;
    private JButton submitButton;
    private JButton clearButton;
    private JTextArea displayArea;
    
    // Date and time formatter
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructor - initializes the UI components
     */
    public AttendanceTrackerApp() {
        // Set up the main frame
        setTitle("Attendance Tracking System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        
        // Initialize components
        initComponents();
        
        // Make frame visible
        setVisible(true);
    }
    
    /**
     * Initializes all UI components and layouts
     */
    private void initComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Attendance Tracking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel with input fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Attendance Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Attendance Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Attendance Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        
        // Course/Year
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel courseLabel = new JLabel("Course/Year:");
        courseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        courseYearField = new JTextField(20);
        courseYearField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(courseYearField, gbc);
        
        // Time In (auto-filled)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel timeLabel = new JLabel("Time In:");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        timeInField = new JTextField(20);
        timeInField.setFont(new Font("Arial", Font.PLAIN, 14));
        timeInField.setEditable(false); // Read-only field
        timeInField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(timeInField, gbc);
        
        // E-Signature (auto-generated)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel signatureLabel = new JLabel("E-Signature:");
        signatureLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(signatureLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        signatureField = new JTextField(20);
        signatureField.setFont(new Font("Arial", Font.PLAIN, 12));
        signatureField.setEditable(false); // Read-only field
        signatureField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(signatureField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        submitButton = new JButton("Submit Attendance");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener(e -> submitAttendance());
        
        clearButton = new JButton("Clear Form");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(231, 76, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        
        // Display area for submitted records
        displayArea = new JTextArea(8, 40);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Attendance Records"));
        
        // Bottom panel combining buttons and display area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    /**
     * Handles attendance submission
     * Validates input, generates timestamp and e-signature
     */
    private void submitAttendance() {
        // Get input values
        String name = nameField.getText().trim();
        String courseYear = courseYearField.getText().trim();
        
        // Validate input fields
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter attendance name!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (courseYear.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter course/year!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            courseYearField.requestFocus();
            return;
        }
        
        // Get current system date and time
        LocalDateTime now = LocalDateTime.now();
        String timeIn = now.format(formatter);
        timeInField.setText(timeIn);
        
        // Generate e-signature
        String signature = generateSignature(name, courseYear, timeIn);
        signatureField.setText(signature);
        
        // Display attendance record
        String record = String.format(
            "════════════════════════════════════════════════════\n" +
            "Name:        %s\n" +
            "Course/Year: %s\n" +
            "Time In:     %s\n" +
            "Signature:   %s\n" +
            "════════════════════════════════════════════════════\n\n",
            name, courseYear, timeIn, signature
        );
        
        displayArea.append(record);
        
        // Show success message
        JOptionPane.showMessageDialog(this, 
            "Attendance submitted successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Scroll to bottom of display area
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }
    
    /**
     * Generates a unique e-signature based on input parameters
     * Uses SHA-256 hashing algorithm for signature generation
     * @param name Student name
     * @param courseYear Course and year information
     * @param timeIn Timestamp of attendance
     * @return Base64 encoded signature string
     */
    private String generateSignature(String name, String courseYear, String timeIn) {
        // Combine all fields to create unique signature input
        String data = name + courseYear + timeIn + System.currentTimeMillis();
        
        try {
            // Use SHA-256 hashing algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            
            // Encode to Base64 and truncate for readability
            String signature = Base64.getEncoder().encodeToString(hash);
            
            // Return first 16 characters for display purposes
            return signature.substring(0, 16).toUpperCase();
            
        } catch (Exception e) {
            // Fallback to simple hash if encryption fails
            return String.format("%08X", data.hashCode()).toUpperCase();
        }
    }
    
    /**
     * Clears all form fields and resets the form
     */
    private void clearForm() {
        nameField.setText("");
        courseYearField.setText("");
        timeInField.setText("");
        signatureField.setText("");
        nameField.requestFocus();
    }
    
    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Create and display the attendance tracker
            new AttendanceTrackerApp();
        });
    }
}