package com.carrentalsystem.ui;

import com.carrentalsystem.dao.VehicleDAO;
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
import java.util.List;

public class VehicleManagementForm extends JFrame {

    private User currentUser;
    private VehicleDAO vehicleDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JScrollPane tableScrollPane;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    private JTextField vehicleNumberField;
    private JTextField brandField;
    private JTextField modelField;
    private JComboBox<String> vehicleTypeCombo;
    private JTextField yearField;
    private JTextField colorField;
    private JTextField dailyRateField;
    private JComboBox<String> statusCombo;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton backButton;

    private int selectedVehicleId = -1;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;
    private Color alternateRowColor = new Color(248, 249, 250);

    public VehicleManagementForm(User user) {
        this.currentUser = user;
        this.vehicleDAO = new VehicleDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadVehicleData();

        setTitle("Vehicle Management - Smart Car Rental System");
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

        JLabel titleLabel = new JLabel("Vehicle Management");
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

        vehicleNumberField = createStyledTextField();
        brandField = createStyledTextField();
        modelField = createStyledTextField();

        String[] vehicleTypes = {"sedan", "suv", "van", "luxury", "economy"};
        vehicleTypeCombo = createStyledComboBox(vehicleTypes);

        yearField = createStyledTextField();
        colorField = createStyledTextField();
        dailyRateField = createStyledTextField();

        String[] statuses = {"available", "rented", "maintenance"};
        statusCombo = createStyledComboBox(statuses);

        formPanel.add(createFieldPanel("Vehicle Number", vehicleNumberField));
        formPanel.add(createFieldPanel("Brand", brandField));
        formPanel.add(createFieldPanel("Model", modelField));
        formPanel.add(createFieldPanel("Vehicle Type", vehicleTypeCombo));
        formPanel.add(createFieldPanel("Year", yearField));
        formPanel.add(createFieldPanel("Color", colorField));
        formPanel.add(createFieldPanel("Daily Rate (LKR)", dailyRateField));
        formPanel.add(createFieldPanel("Status", statusCombo));
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

        addButton = createActionButton("Add Vehicle", successColor);
        updateButton = createActionButton("Update Vehicle", warningColor);
        deleteButton = createActionButton("Delete Vehicle", dangerColor);
        clearButton = createActionButton("Clear Form", new Color(108, 117, 125));
        refreshButton = createActionButton("Refresh Table", primaryColor);

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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicle();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVehicle();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteVehicle();
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
                loadVehicleData();
                JOptionPane.showMessageDialog(VehicleManagementForm.this,
                        "Table refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    private void loadVehicleData() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();

        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                    vehicle.getVehicleId(),
                    vehicle.getVehicleNumber(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getVehicleType(),
                    vehicle.getYear(),
                    vehicle.getColor(),
                    "LKR " + vehicle.getDailyRate(),
                    vehicle.getAvailabilityStatus()
            };
            tableModel.addRow(row);
        }

        tableModel.fireTableDataChanged();
    }

    private void populateFormFromTable(int row) {
        selectedVehicleId = (int) tableModel.getValueAt(row, 0);
        vehicleNumberField.setText(tableModel.getValueAt(row, 1).toString());
        brandField.setText(tableModel.getValueAt(row, 2).toString());
        modelField.setText(tableModel.getValueAt(row, 3).toString());
        vehicleTypeCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        yearField.setText(tableModel.getValueAt(row, 5).toString());
        colorField.setText(tableModel.getValueAt(row, 6).toString());
        String rateText = tableModel.getValueAt(row, 7).toString().replace("LKR ", "");
        dailyRateField.setText(rateText);
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 8).toString());
    }

    private void addVehicle() {
        if (!validateForm()) {
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(vehicleNumberField.getText().trim());
        vehicle.setBrand(brandField.getText().trim());
        vehicle.setModel(modelField.getText().trim());
        vehicle.setVehicleType(vehicleTypeCombo.getSelectedItem().toString());
        vehicle.setYear(Integer.parseInt(yearField.getText().trim()));
        vehicle.setColor(colorField.getText().trim());
        vehicle.setDailyRate(new BigDecimal(dailyRateField.getText().trim()));
        vehicle.setAvailabilityStatus(statusCombo.getSelectedItem().toString());

        if (vehicleDAO.addVehicle(vehicle)) {
            JOptionPane.showMessageDialog(this, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadVehicleData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicle() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(selectedVehicleId);
        vehicle.setVehicleNumber(vehicleNumberField.getText().trim());
        vehicle.setBrand(brandField.getText().trim());
        vehicle.setModel(modelField.getText().trim());
        vehicle.setVehicleType(vehicleTypeCombo.getSelectedItem().toString());
        vehicle.setYear(Integer.parseInt(yearField.getText().trim()));
        vehicle.setColor(colorField.getText().trim());
        vehicle.setDailyRate(new BigDecimal(dailyRateField.getText().trim()));
        vehicle.setAvailabilityStatus(statusCombo.getSelectedItem().toString());

        if (vehicleDAO.updateVehicle(vehicle)) {
            JOptionPane.showMessageDialog(this, "Vehicle updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadVehicleData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteVehicle() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this vehicle?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (vehicleDAO.deleteVehicle(selectedVehicleId)) {
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadVehicleData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedVehicleId = -1;
        vehicleNumberField.setText("");
        brandField.setText("");
        modelField.setText("");
        vehicleTypeCombo.setSelectedIndex(0);
        yearField.setText("");
        colorField.setText("");
        dailyRateField.setText("");
        statusCombo.setSelectedIndex(0);
        vehicleTable.clearSelection();
    }

    private boolean validateForm() {
        if (vehicleNumberField.getText().trim().isEmpty()) {
            showValidationError("Vehicle number is required!", vehicleNumberField);
            return false;
        }

        if (brandField.getText().trim().isEmpty()) {
            showValidationError("Brand is required!", brandField);
            return false;
        }

        if (modelField.getText().trim().isEmpty()) {
            showValidationError("Model is required!", modelField);
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (year < 1900 || year > 2030) {
                showValidationError("Please enter a valid year (1900-2030)!", yearField);
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Year must be a valid number!", yearField);
            return false;
        }

        try {
            BigDecimal rate = new BigDecimal(dailyRateField.getText().trim());
            if (rate.compareTo(BigDecimal.ZERO) <= 0) {
                showValidationError("Daily rate must be greater than 0!", dailyRateField);
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Daily rate must be a valid number!", dailyRateField);
            return false;
        }

        return true;
    }

    private void showValidationError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
        component.requestFocus();
    }
}
