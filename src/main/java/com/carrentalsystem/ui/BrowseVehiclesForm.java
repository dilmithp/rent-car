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

public class BrowseVehiclesForm extends JFrame {

    private User currentUser;
    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;
    private BookingDAO bookingDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel filterPanel;
    private JScrollPane tableScrollPane;
    private JPanel bookingPanel;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilterCombo;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JButton filterButton;
    private JButton clearFilterButton;

    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JButton calculateButton;
    private JTextField totalDaysField;
    private JTextField totalAmountField;
    private JButton bookNowButton;
    private JButton backButton;

    private int selectedVehicleId = -1;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;
    private Color alternateRowColor = new Color(248, 249, 250);

    public BrowseVehiclesForm(User user) {
        this.currentUser = user;
        this.vehicleDAO = new VehicleDAO();
        this.customerDAO = new CustomerDAO();
        this.bookingDAO = new BookingDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadVehicleData();

        setTitle("Browse Available Vehicles - Smart Car Rental System");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createFilterPanel();
        createTable();
        createBookingPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Browse Available Vehicles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Customer: " + currentUser.getUsername());
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

    private void createFilterPanel() {
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(cardBg);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(15, 30, 15, 30)
        ));

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(secondaryColor);

        String[] types = {"All Types", "sedan", "suv", "van", "luxury", "economy"};
        typeFilterCombo = new JComboBox<>(types);
        typeFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeFilterCombo.setPreferredSize(new Dimension(120, 35));

        JLabel priceLabel = new JLabel("Price Range:");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(secondaryColor);

        minPriceField = new JTextField(8);
        minPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        minPriceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel toLabel = new JLabel("to");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        maxPriceField = new JTextField(8);
        maxPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        maxPriceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        filterButton = createFilterButton("Apply Filter", primaryColor);
        clearFilterButton = createFilterButton("Clear", new Color(108, 117, 125));

        filterPanel.add(filterLabel);
        filterPanel.add(typeFilterCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(priceLabel);
        filterPanel.add(minPriceField);
        filterPanel.add(toLabel);
        filterPanel.add(maxPriceField);
        filterPanel.add(filterButton);
        filterPanel.add(clearFilterButton);
    }

    private JButton createFilterButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

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
        String[] columnNames = {"ID", "Vehicle Number", "Brand", "Model", "Type", "Year", "Color", "Daily Rate", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTable = new JTable(tableModel) {
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

        vehicleTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vehicleTable.setRowHeight(35);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setShowVerticalLines(false);
        vehicleTable.setGridColor(new Color(230, 230, 230));
        vehicleTable.setSelectionBackground(new Color(184, 207, 229));
        vehicleTable.setSelectionForeground(secondaryColor);

        JTableHeader header = vehicleTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(secondaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < vehicleTable.getColumnCount(); i++) {
            vehicleTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tableScrollPane = new JScrollPane(vehicleTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
    }

    private void createBookingPanel() {
        bookingPanel = new JPanel(new GridLayout(1, 7, 15, 0));
        bookingPanel.setBackground(cardBg);
        bookingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        startDateSpinner = createDateSpinner(new java.util.Date());
        endDateSpinner = createDateSpinner(new java.util.Date());

        totalDaysField = createStyledTextField();
        totalDaysField.setEditable(false);

        totalAmountField = createStyledTextField();
        totalAmountField.setEditable(false);

        calculateButton = createActionButton("Calculate", warningColor);
        bookNowButton = createActionButton("Book Now", successColor);

        bookingPanel.add(createFieldPanel("Start Date", startDateSpinner));
        bookingPanel.add(createFieldPanel("End Date", endDateSpinner));
        bookingPanel.add(createFieldPanel("Total Days", totalDaysField));
        bookingPanel.add(createFieldPanel("Total Amount", totalAmountField));
        bookingPanel.add(createFieldPanel("", calculateButton));
        bookingPanel.add(createFieldPanel("", bookNowButton));
    }

    private JSpinner createDateSpinner(java.util.Date date) {
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return spinner;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        if (!labelText.isEmpty()) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(secondaryColor);
            panel.add(label, BorderLayout.NORTH);
        }

        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(lightBg);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(lightBg);
        tableContainer.setBorder(new EmptyBorder(0, 30, 10, 30));
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        contentPanel.add(bookingPanel, BorderLayout.SOUTH);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setMinimumSize(new Dimension(1000, 600));
    }

    private void setupListeners() {
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });

        clearFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilter();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });

        bookNowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookVehicle();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = vehicleTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedVehicleId = (int) tableModel.getValueAt(selectedRow, 0);
                }
            }
        });
    }

    private void loadVehicleData() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();

        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                    vehicle.getVehicleId(),
                    vehicle.getVehicleNumber(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getVehicleType(),
                    vehicle.getYear(),
                    vehicle.getColor(),
                    "LKR " + vehicle.getDailyRate() + "/day",
                    vehicle.getAvailabilityStatus()
            };
            tableModel.addRow(row);
        }

        tableModel.fireTableDataChanged();
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();

        String selectedType = typeFilterCombo.getSelectedItem().toString();
        String minPrice = minPriceField.getText().trim();
        String maxPrice = maxPriceField.getText().trim();

        for (Vehicle vehicle : vehicles) {
            boolean matchesType = selectedType.equals("All Types") || vehicle.getVehicleType().equals(selectedType);
            boolean matchesPrice = true;

            if (!minPrice.isEmpty() && !maxPrice.isEmpty()) {
                try {
                    BigDecimal min = new BigDecimal(minPrice);
                    BigDecimal max = new BigDecimal(maxPrice);
                    matchesPrice = vehicle.getDailyRate().compareTo(min) >= 0 &&
                            vehicle.getDailyRate().compareTo(max) <= 0;
                } catch (NumberFormatException e) {
                    matchesPrice = true;
                }
            }

            if (matchesType && matchesPrice) {
                Object[] row = {
                        vehicle.getVehicleId(),
                        vehicle.getVehicleNumber(),
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getVehicleType(),
                        vehicle.getYear(),
                        vehicle.getColor(),
                        "LKR " + vehicle.getDailyRate() + "/day",
                        vehicle.getAvailabilityStatus()
                };
                tableModel.addRow(row);
            }
        }

        tableModel.fireTableDataChanged();
    }

    private void clearFilter() {
        typeFilterCombo.setSelectedIndex(0);
        minPriceField.setText("");
        maxPriceField.setText("");
        loadVehicleData();
    }

    private void calculateTotal() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

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

            Vehicle vehicle = vehicleDAO.getVehicleById(selectedVehicleId);
            if (vehicle != null) {
                BigDecimal totalAmount = vehicle.getDailyRate().multiply(new BigDecimal(days));
                totalDaysField.setText(String.valueOf(days));
                totalAmountField.setText("LKR " + totalAmount);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookVehicle() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (totalDaysField.getText().trim().isEmpty() || totalAmountField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please calculate the total first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer profile not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.Date bookingDate = new java.util.Date();
            java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

            String amountText = totalAmountField.getText().replace("LKR ", "").trim();

            Booking booking = new Booking();
            booking.setCustomerId(customer.getCustomerId());
            booking.setVehicleId(selectedVehicleId);
            booking.setBookingDate(new Date(bookingDate.getTime()));
            booking.setStartDate(new Date(startDate.getTime()));
            booking.setEndDate(new Date(endDate.getTime()));
            booking.setTotalDays(Integer.parseInt(totalDaysField.getText()));
            booking.setTotalAmount(new BigDecimal(amountText));
            booking.setBookingStatus("pending");

            if (bookingDAO.addBooking(booking)) {
                JOptionPane.showMessageDialog(this,
                        "Booking request submitted successfully!\nStatus: Pending Admin Approval",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                totalDaysField.setText("");
                totalAmountField.setText("");
                selectedVehicleId = -1;
                vehicleTable.clearSelection();
                loadVehicleData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
