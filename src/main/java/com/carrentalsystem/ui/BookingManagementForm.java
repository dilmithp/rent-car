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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

public class BookingManagementForm extends JFrame {

    private User currentUser;
    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JScrollPane tableScrollPane;

    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> customerCombo;
    private JComboBox<String> vehicleCombo;
    private JSpinner bookingDateSpinner;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextField totalDaysField;
    private JTextField totalAmountField;
    private JComboBox<String> statusCombo;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton calculateButton;
    private JButton backButton;

    private int selectedBookingId = -1;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;
    private Color alternateRowColor = new Color(248, 249, 250);

    public BookingManagementForm(User user) {
        this.currentUser = user;
        this.bookingDAO = new BookingDAO();
        this.customerDAO = new CustomerDAO();
        this.vehicleDAO = new VehicleDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadBookingData();

        setTitle("Booking Management - Smart Car Rental System");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createForm();
        createButtonPanel();
        createTable();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Booking Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Admin: " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setForeground(Color.WHITE);

        backButton = createHeaderButton("Back to Dashboard", secondaryColor);

        rightPanel.add(userLabel);
        rightPanel.add(backButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }

    private JButton createHeaderButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));

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

    private void createForm() {
        formPanel = new JPanel(new GridLayout(4, 4, 15, 15));
        formPanel.setBackground(cardBg);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        List<Customer> customers = customerDAO.getAllCustomers();
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerCombo.setBackground(Color.WHITE);
        customerCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        for (Customer customer : customers) {
            customerCombo.addItem(customer.getCustomerId() + " - " + customer.getFullName());
        }

        List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
        vehicleCombo = new JComboBox<>();
        vehicleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleCombo.setBackground(Color.WHITE);
        vehicleCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        for (Vehicle vehicle : vehicles) {
            vehicleCombo.addItem(vehicle.getVehicleId() + " - " + vehicle.getBrand() + " " + vehicle.getModel());
        }

        bookingDateSpinner = createDateSpinner(new java.util.Date());
        startDateSpinner = createDateSpinner(new java.util.Date());
        endDateSpinner = createDateSpinner(new java.util.Date());

        totalDaysField = createStyledTextField();
        totalDaysField.setEditable(false);

        totalAmountField = createStyledTextField();
        totalAmountField.setEditable(false);

        String[] statuses = {"pending", "confirmed", "completed", "cancelled"};
        statusCombo = createStyledComboBox(statuses);

        formPanel.add(createFieldPanel("Customer", customerCombo));
        formPanel.add(createFieldPanel("Vehicle", vehicleCombo));
        formPanel.add(createFieldPanel("Booking Date", bookingDateSpinner));
        formPanel.add(createFieldPanel("Start Date", startDateSpinner));
        formPanel.add(createFieldPanel("End Date", endDateSpinner));
        formPanel.add(createFieldPanel("Total Days", totalDaysField));
        formPanel.add(createFieldPanel("Total Amount (LKR)", totalAmountField));
        formPanel.add(createFieldPanel("Status", statusCombo));
    }

    private JSpinner createDateSpinner(java.util.Date date) {
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return spinner;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return comboBox;
    }

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(secondaryColor);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(lightBg);

        calculateButton = createActionButton("Calculate Total", new Color(155, 89, 182));
        addButton = createActionButton("Add Booking", successColor);
        updateButton = createActionButton("Update Booking", warningColor);
        deleteButton = createActionButton("Delete Booking", dangerColor);
        clearButton = createActionButton("Clear Form", new Color(108, 117, 125));
        refreshButton = createActionButton("Refresh Table", primaryColor);

        buttonPanel.add(calculateButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 45));

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

    private void createTable() {
        String[] columnNames = {"Booking ID", "Customer", "Vehicle", "Booking Date", "Start Date", "End Date", "Days", "Amount", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : alternateRowColor);
                } else {
                    c.setBackground(new Color(184, 207, 229));
                }
                return c;
            }
        };

        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookingTable.setRowHeight(35);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setShowVerticalLines(false);
        bookingTable.setGridColor(new Color(230, 230, 230));
        bookingTable.setSelectionBackground(new Color(184, 207, 229));
        bookingTable.setSelectionForeground(secondaryColor);

        JTableHeader header = bookingTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(secondaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < bookingTable.getColumnCount(); i++) {
            bookingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tableScrollPane = new JScrollPane(bookingTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setPreferredSize(new Dimension(0, 250));
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(lightBg);

        JPanel formAndButtonPanel = new JPanel();
        formAndButtonPanel.setLayout(new BoxLayout(formAndButtonPanel, BoxLayout.Y_AXIS));
        formAndButtonPanel.setBackground(lightBg);

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(lightBg);
        formContainer.setBorder(new EmptyBorder(20, 30, 10, 30));
        formContainer.add(formPanel, BorderLayout.CENTER);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(lightBg);
        buttonContainer.setBorder(new EmptyBorder(10, 30, 10, 30));
        buttonContainer.add(buttonPanel, BorderLayout.CENTER);

        formAndButtonPanel.add(formContainer);
        formAndButtonPanel.add(buttonContainer);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(lightBg);
        tableContainer.setBorder(new EmptyBorder(10, 30, 30, 30));
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        contentPanel.add(formAndButtonPanel, BorderLayout.NORTH);
        contentPanel.add(tableContainer, BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setMinimumSize(new Dimension(1000, 600));
    }

    private void setupListeners() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotalAmount();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBooking();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBooking();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBooking();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBookingData();
                JOptionPane.showMessageDialog(BookingManagementForm.this,
                        "Table refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookingTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    private void calculateTotalAmount() {
        try {
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            LocalDate start = LocalDate.parse(new Date(startDate.getTime()).toString());
            LocalDate end = LocalDate.parse(new Date(endDate.getTime()).toString());

            long days = ChronoUnit.DAYS.between(start, end);

            if (days <= 0) {
                JOptionPane.showMessageDialog(this, "End date must be after start date!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (vehicleCombo.getSelectedItem() != null) {
                String vehicleSelection = vehicleCombo.getSelectedItem().toString();
                int vehicleId = Integer.parseInt(vehicleSelection.split(" - ")[0]);
                Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);

                if (vehicle != null) {
                    BigDecimal totalAmount = vehicle.getDailyRate().multiply(new BigDecimal(days));
                    totalDaysField.setText(String.valueOf(days));
                    totalAmountField.setText(totalAmount.toString());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating amount: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookingData() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();

        for (Booking booking : bookings) {
            Customer customer = customerDAO.getCustomerById(booking.getCustomerId());
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());

            Object[] row = {
                    booking.getBookingId(),
                    customer != null ? customer.getFullName() : "N/A",
                    vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() : "N/A",
                    booking.getBookingDate(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    booking.getTotalDays(),
                    "LKR " + booking.getTotalAmount(),
                    booking.getBookingStatus()
            };
            tableModel.addRow(row);
        }

        tableModel.fireTableDataChanged();
    }

    private void populateFormFromTable(int row) {
        selectedBookingId = (int) tableModel.getValueAt(row, 0);
        Booking booking = bookingDAO.getBookingById(selectedBookingId);

        if (booking != null) {
            Customer customer = customerDAO.getCustomerById(booking.getCustomerId());
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());

            if (customer != null) {
                customerCombo.setSelectedItem(customer.getCustomerId() + " - " + customer.getFullName());
            }

            if (vehicle != null) {
                vehicleCombo.setSelectedItem(vehicle.getVehicleId() + " - " + vehicle.getBrand() + " " + vehicle.getModel());
            }

            bookingDateSpinner.setValue(new java.util.Date(booking.getBookingDate().getTime()));
            startDateSpinner.setValue(new java.util.Date(booking.getStartDate().getTime()));
            endDateSpinner.setValue(new java.util.Date(booking.getEndDate().getTime()));
            totalDaysField.setText(String.valueOf(booking.getTotalDays()));
            totalAmountField.setText(booking.getTotalAmount().toString());
            statusCombo.setSelectedItem(booking.getBookingStatus());
        }
    }

    private void addBooking() {
        if (!validateForm()) {
            return;
        }

        try {
            String customerSelection = customerCombo.getSelectedItem().toString();
            int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);

            String vehicleSelection = vehicleCombo.getSelectedItem().toString();
            int vehicleId = Integer.parseInt(vehicleSelection.split(" - ")[0]);

            java.util.Date bookingDate = (java.util.Date) bookingDateSpinner.getValue();
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            Booking booking = new Booking();
            booking.setCustomerId(customerId);
            booking.setVehicleId(vehicleId);
            booking.setBookingDate(new Date(bookingDate.getTime()));
            booking.setStartDate(new Date(startDate.getTime()));
            booking.setEndDate(new Date(endDate.getTime()));
            booking.setTotalDays(Integer.parseInt(totalDaysField.getText()));
            booking.setTotalAmount(new BigDecimal(totalAmountField.getText()));
            booking.setBookingStatus(statusCombo.getSelectedItem().toString());

            if (bookingDAO.addBooking(booking)) {
                vehicleDAO.updateVehicleStatus(vehicleId, "rented");
                JOptionPane.showMessageDialog(this, "Booking added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooking() {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            String customerSelection = customerCombo.getSelectedItem().toString();
            int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);

            String vehicleSelection = vehicleCombo.getSelectedItem().toString();
            int vehicleId = Integer.parseInt(vehicleSelection.split(" - ")[0]);

            java.util.Date bookingDate = (java.util.Date) bookingDateSpinner.getValue();
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            Booking booking = new Booking();
            booking.setBookingId(selectedBookingId);
            booking.setCustomerId(customerId);
            booking.setVehicleId(vehicleId);
            booking.setBookingDate(new Date(bookingDate.getTime()));
            booking.setStartDate(new Date(startDate.getTime()));
            booking.setEndDate(new Date(endDate.getTime()));
            booking.setTotalDays(Integer.parseInt(totalDaysField.getText()));
            booking.setTotalAmount(new BigDecimal(totalAmountField.getText()));
            booking.setBookingStatus(statusCombo.getSelectedItem().toString());

            if (bookingDAO.updateBooking(booking)) {
                JOptionPane.showMessageDialog(this, "Booking updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBooking() {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this booking?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingDAO.deleteBooking(selectedBookingId)) {
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedBookingId = -1;
        if (customerCombo.getItemCount() > 0) customerCombo.setSelectedIndex(0);
        if (vehicleCombo.getItemCount() > 0) vehicleCombo.setSelectedIndex(0);
        bookingDateSpinner.setValue(new java.util.Date());
        startDateSpinner.setValue(new java.util.Date());
        endDateSpinner.setValue(new java.util.Date());
        totalDaysField.setText("");
        totalAmountField.setText("");
        statusCombo.setSelectedIndex(0);
        bookingTable.clearSelection();
    }

    private boolean validateForm() {
        if (customerCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (vehicleCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (totalDaysField.getText().trim().isEmpty() || totalAmountField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please calculate the total amount first!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
