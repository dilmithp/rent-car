package com.carrentalsystem.ui;

import com.carrentalsystem.dao.CustomerDAO;
import com.carrentalsystem.dao.UserDAO;
import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.User;

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
import java.util.List;

public class CustomerManagementForm extends JFrame {

    private User currentUser;
    private CustomerDAO customerDAO;
    private UserDAO userDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JScrollPane tableScrollPane;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField licenseNumberField;
    private JComboBox<String> statusCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton backButton;

    private int selectedCustomerId = -1;
    private int selectedUserId = -1;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;
    private Color alternateRowColor = new Color(248, 249, 250);

    public CustomerManagementForm(User user) {
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadCustomerData();

        setTitle("Customer Management - Smart Car Rental System");
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

        JLabel titleLabel = new JLabel("Customer Management");
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

        fullNameField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        licenseNumberField = createStyledTextField();

        String[] statuses = {"active", "inactive"};
        statusCombo = createStyledComboBox(statuses);

        usernameField = createStyledTextField();
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        addressArea = new JTextArea(2, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        formPanel.add(createFieldPanel("Full Name", fullNameField));
        formPanel.add(createFieldPanel("Email", emailField));
        formPanel.add(createFieldPanel("Phone", phoneField));
        formPanel.add(createFieldPanel("License Number", licenseNumberField));
        formPanel.add(createFieldPanel("Username", usernameField));
        formPanel.add(createFieldPanel("Password", passwordField));
        formPanel.add(createFieldPanel("Status", statusCombo));
        formPanel.add(createFieldPanel("Address", addressScroll));
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

        addButton = createActionButton("Add Customer", successColor);
        updateButton = createActionButton("Update Customer", warningColor);
        deleteButton = createActionButton("Delete Customer", dangerColor);
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
        String[] columnNames = {"Customer ID", "Name", "Email", "Phone", "License No", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel) {
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

        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setRowHeight(35);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(new Color(230, 230, 230));
        customerTable.setSelectionBackground(new Color(184, 207, 229));
        customerTable.setSelectionForeground(secondaryColor);

        JTableHeader header = customerTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(secondaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < customerTable.getColumnCount(); i++) {
            customerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tableScrollPane = new JScrollPane(customerTable);
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
                addCustomer();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
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
                loadCustomerData();
                JOptionPane.showMessageDialog(CustomerManagementForm.this,
                        "Table refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getFullName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getLicenseNumber(),
                    customer.getStatus()
            };
            tableModel.addRow(row);
        }

        tableModel.fireTableDataChanged();
    }

    private void populateFormFromTable(int row) {
        selectedCustomerId = (int) tableModel.getValueAt(row, 0);
        Customer customer = customerDAO.getCustomerById(selectedCustomerId);

        if (customer != null) {
            selectedUserId = customer.getUserId();
            fullNameField.setText(customer.getFullName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            addressArea.setText(customer.getAddress());
            licenseNumberField.setText(customer.getLicenseNumber());
            statusCombo.setSelectedItem(customer.getStatus());

            User user = userDAO.getUserById(selectedUserId);
            if (user != null) {
                usernameField.setText(user.getUsername());
                passwordField.setText("");
            }
        }
    }

    private void addCustomer() {
        if (!validateForm()) {
            return;
        }

        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setPassword(new String(passwordField.getPassword()));
        user.setUserRole("customer");

        if (!userDAO.addUser(user)) {
            JOptionPane.showMessageDialog(this, "Failed to create user account!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User createdUser = userDAO.getUserByUsername(usernameField.getText().trim());

        if (createdUser == null) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve user!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer();
        customer.setUserId(createdUser.getUserId());
        customer.setFullName(fullNameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressArea.getText().trim());
        customer.setLicenseNumber(licenseNumberField.getText().trim());
        customer.setStatus(statusCombo.getSelectedItem().toString());

        if (customerDAO.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCustomerData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateFormForUpdate()) {
            return;
        }

        Customer customer = new Customer();
        customer.setCustomerId(selectedCustomerId);
        customer.setUserId(selectedUserId);
        customer.setFullName(fullNameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressArea.getText().trim());
        customer.setLicenseNumber(licenseNumberField.getText().trim());
        customer.setStatus(statusCombo.getSelectedItem().toString());

        if (customerDAO.updateCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCustomerData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.deleteCustomer(selectedCustomerId)) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomerData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedCustomerId = -1;
        selectedUserId = -1;
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        licenseNumberField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        statusCombo.setSelectedIndex(0);
        customerTable.clearSelection();
    }

    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            showValidationError("Full name is required!", fullNameField);
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showValidationError("Email is required!", emailField);
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showValidationError("Please enter a valid email address!", emailField);
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number is required!", phoneField);
            return false;
        }

        if (licenseNumberField.getText().trim().isEmpty()) {
            showValidationError("License number is required!", licenseNumberField);
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            showValidationError("Username is required!", usernameField);
            return false;
        }

        if (passwordField.getPassword().length == 0) {
            showValidationError("Password is required!", passwordField);
            return false;
        }

        return true;
    }

    private boolean validateFormForUpdate() {
        if (fullNameField.getText().trim().isEmpty()) {
            showValidationError("Full name is required!", fullNameField);
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showValidationError("Email is required!", emailField);
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showValidationError("Please enter a valid email address!", emailField);
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number is required!", phoneField);
            return false;
        }

        if (licenseNumberField.getText().trim().isEmpty()) {
            showValidationError("License number is required!", licenseNumberField);
            return false;
        }

        return true;
    }

    private void showValidationError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
        component.requestFocus();
    }
}
