package com.carrentalsystem.ui;

import com.carrentalsystem.dao.BookingDAO;
import com.carrentalsystem.dao.CustomerDAO;
import com.carrentalsystem.dao.VehicleDAO;
import com.carrentalsystem.models.Booking;
import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.User;
import com.carrentalsystem.models.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentForm extends JFrame {

    private User currentUser;
    private Booking booking;
    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;

    private JLabel bookingIdLabel;
    private JLabel vehicleLabel;
    private JLabel amountLabel;
    private JLabel paymentStatusLabel;
    private JLabel selectedFileLabel;
    private JTextField filePathField;

    private JButton selectFileButton;
    private JButton uploadButton;
    private JButton viewSlipButton;
    private JButton backButton;

    private File selectedFile;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;

    public PaymentForm(User user, Booking booking) {
        this.currentUser = user;
        this.booking = booking;
        this.bookingDAO = new BookingDAO();
        this.customerDAO = new CustomerDAO();
        this.vehicleDAO = new VehicleDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadBookingDetails();

        setTitle("Payment - Smart Car Rental System");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createContent();
        createButtonPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Payment Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Customer: " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setForeground(Color.WHITE);

        rightPanel.add(userLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }

    private void createContent() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(lightBg);
        contentPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(cardBg);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        bookingIdLabel = createInfoLabel("Booking ID: ");
        vehicleLabel = createInfoLabel("Vehicle: ");
        amountLabel = createInfoLabel("Total Amount: ");
        paymentStatusLabel = createInfoLabel("Payment Status: ");

        formPanel.add(bookingIdLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(vehicleLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(paymentStatusLabel);
        formPanel.add(Box.createVerticalStrut(25));

        JPanel uploadPanel = new JPanel();
        uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));
        uploadPanel.setBackground(cardBg);
        uploadPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel uploadLabel = new JLabel("Upload Payment Slip:");
        uploadLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uploadLabel.setForeground(secondaryColor);
        uploadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fileSelectPanel = new JPanel(new BorderLayout(10, 0));
        fileSelectPanel.setBackground(cardBg);
        fileSelectPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        filePathField = new JTextField();
        filePathField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filePathField.setEditable(false);
        filePathField.setBackground(Color.WHITE);
        filePathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        selectFileButton = createButton("Browse", primaryColor);
        selectFileButton.setPreferredSize(new Dimension(120, 45));

        fileSelectPanel.add(filePathField, BorderLayout.CENTER);
        fileSelectPanel.add(selectFileButton, BorderLayout.EAST);

        selectedFileLabel = new JLabel(" ");
        selectedFileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        selectedFileLabel.setForeground(new Color(100, 100, 100));
        selectedFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        uploadPanel.add(uploadLabel);
        uploadPanel.add(Box.createVerticalStrut(10));
        uploadPanel.add(fileSelectPanel);
        uploadPanel.add(Box.createVerticalStrut(5));
        uploadPanel.add(selectedFileLabel);

        formPanel.add(uploadPanel);

        contentPanel.add(formPanel);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(secondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(lightBg);

        uploadButton = createButton("Upload & Complete Payment", successColor);
        viewSlipButton = createButton("View Uploaded Slip", warningColor);
        backButton = createButton("Back", secondaryColor);

        uploadButton.setPreferredSize(new Dimension(220, 45));
        viewSlipButton.setPreferredSize(new Dimension(200, 45));
        backButton.setPreferredSize(new Dimension(150, 45));

        buttonPanel.add(uploadButton);
        buttonPanel.add(viewSlipButton);
        buttonPanel.add(backButton);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupLayout() {
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupListeners() {
        selectFileButton.addActionListener(e -> selectFile());
        uploadButton.addActionListener(e -> uploadPaymentSlip());
        viewSlipButton.addActionListener(e -> viewPaymentSlip());
        backButton.addActionListener(e -> dispose());
    }

    private void loadBookingDetails() {
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        String vehicleInfo = vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getVehicleNumber() + ")" : "N/A";

        bookingIdLabel.setText("Booking ID: #" + booking.getBookingId());
        vehicleLabel.setText("Vehicle: " + vehicleInfo);
        amountLabel.setText("Total Amount: LKR " + booking.getTotalAmount());

        String paymentStatus = booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "pending";
        paymentStatusLabel.setText("Payment Status: " + paymentStatus.toUpperCase());

        if (paymentStatus.equals("completed")) {
            paymentStatusLabel.setForeground(successColor);
        } else if (paymentStatus.equals("pending")) {
            paymentStatusLabel.setForeground(warningColor);
        } else {
            paymentStatusLabel.setForeground(secondaryColor);
        }

        if (booking.getPaymentSlipPath() != null && !booking.getPaymentSlipPath().isEmpty()) {
            filePathField.setText(booking.getPaymentSlipPath());
            selectedFileLabel.setText("Payment slip already uploaded");
            viewSlipButton.setEnabled(true);
        } else {
            viewSlipButton.setEnabled(false);
        }

        if (paymentStatus.equals("completed")) {
            uploadButton.setEnabled(false);
            selectFileButton.setEnabled(false);
        }
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Payment Slip");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (JPG, PNG, PDF)", "jpg", "jpeg", "png", "pdf");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            selectedFileLabel.setText("Selected: " + selectedFile.getName() + " (" + formatFileSize(selectedFile.length()) + ")");
            selectedFileLabel.setForeground(successColor);
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    private void uploadPaymentSlip() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a payment slip file first!",
                    "No File Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate file size (max 5MB)
        if (selectedFile.length() > 5 * 1024 * 1024) {
            JOptionPane.showMessageDialog(this,
                    "File size must be less than 5MB!",
                    "File Too Large",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Upload payment slip and complete payment?\n\nThis action cannot be undone.",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Create uploads directory if it doesn't exist
            String uploadsDir = "uploads/payment_slips";
            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileExtension = getFileExtension(selectedFile.getName());
            String fileName = "payment_" + booking.getBookingId() + "_" + timestamp + "." + fileExtension;
            Path targetPath = uploadPath.resolve(fileName);

            // Copy file to uploads directory
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Update database
            String relativePath = uploadsDir + "/" + fileName;
            boolean success = bookingDAO.updatePaymentStatus(booking.getBookingId(), "completed", relativePath);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Payment slip uploaded successfully!\nPayment status: COMPLETED",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Reload booking details
                booking = bookingDAO.getBookingById(booking.getBookingId());
                loadBookingDetails();
                selectedFile = null;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update payment status in database!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error uploading file: " + e.getMessage(),
                    "Upload Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPaymentSlip() {
        if (booking.getPaymentSlipPath() == null || booking.getPaymentSlipPath().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No payment slip available!",
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File slipFile = new File(booking.getPaymentSlipPath());
            if (!slipFile.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Payment slip file not found on disk!",
                        "File Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open file with default application
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(slipFile);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot open file. Desktop not supported.\nFile location: " + slipFile.getAbsolutePath(),
                        "Cannot Open",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error opening file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
}

