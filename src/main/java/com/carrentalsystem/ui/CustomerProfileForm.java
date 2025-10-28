package com.carrentalsystem.ui;

import com.carrentalsystem.dao.CustomerDAO;
import com.carrentalsystem.dao.UserDAO;
import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerProfileForm extends JFrame {

    private User currentUser;
    private CustomerDAO customerDAO;
    private UserDAO userDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel profilePanel;
    private JPanel buttonPanel;

    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField licenseNumberField;
    private JTextField usernameField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private JButton updateProfileButton;
    private JButton changePasswordButton;
    private JButton backButton;

    private Customer currentCustomer;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;

    public CustomerProfileForm(User user) {
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadProfileData();

        setTitle("My Profile - Smart Car Rental System");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createProfilePanel();
        createButtonPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("My Profile");
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

    private void createProfilePanel() {
        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(lightBg);
        profilePanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        JPanel personalInfoCard = createCard("Personal Information");
        personalInfoCard.setLayout(new GridLayout(5, 2, 15, 15));

        fullNameField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        licenseNumberField = createStyledTextField();

        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        personalInfoCard.add(createFieldLabel("Full Name"));
        personalInfoCard.add(fullNameField);
        personalInfoCard.add(createFieldLabel("Email"));
        personalInfoCard.add(emailField);
        personalInfoCard.add(createFieldLabel("Phone"));
        personalInfoCard.add(phoneField);
        personalInfoCard.add(createFieldLabel("License Number"));
        personalInfoCard.add(licenseNumberField);
        personalInfoCard.add(createFieldLabel("Address"));
        personalInfoCard.add(addressScroll);

        JPanel accountInfoCard = createCard("Account Information");
        accountInfoCard.setLayout(new GridLayout(4, 2, 15, 15));

        usernameField = createStyledTextField();
        usernameField.setEditable(false);
        oldPasswordField = createStyledPasswordField();
        newPasswordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();

        accountInfoCard.add(createFieldLabel("Username"));
        accountInfoCard.add(usernameField);
        accountInfoCard.add(createFieldLabel("Old Password"));
        accountInfoCard.add(oldPasswordField);
        accountInfoCard.add(createFieldLabel("New Password"));
        accountInfoCard.add(newPasswordField);
        accountInfoCard.add(createFieldLabel("Confirm Password"));
        accountInfoCard.add(confirmPasswordField);

        profilePanel.add(personalInfoCard);
        profilePanel.add(Box.createVerticalStrut(20));
        profilePanel.add(accountInfoCard);
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        title,
                        0,
                        0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        secondaryColor
                ),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(secondaryColor);
        return label;
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return passwordField;
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(lightBg);

        updateProfileButton = createActionButton("Update Profile", successColor);
        changePasswordButton = createActionButton("Change Password", warningColor);

        buttonPanel.add(updateProfileButton);
        buttonPanel.add(changePasswordButton);
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
        button.setPreferredSize(new Dimension(180, 45));

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
        JScrollPane scrollPane = new JScrollPane(profilePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        setMinimumSize(new Dimension(700, 600));
    }

    private void setupListeners() {
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadProfileData() {
        currentCustomer = customerDAO.getCustomerByUserId(currentUser.getUserId());

        if (currentCustomer != null) {
            fullNameField.setText(currentCustomer.getFullName());
            emailField.setText(currentCustomer.getEmail());
            phoneField.setText(currentCustomer.getPhone());
            addressArea.setText(currentCustomer.getAddress());
            licenseNumberField.setText(currentCustomer.getLicenseNumber());
            usernameField.setText(currentUser.getUsername());
        }
    }

    private void updateProfile() {
        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentCustomer.setFullName(fullNameField.getText().trim());
        currentCustomer.setEmail(emailField.getText().trim());
        currentCustomer.setPhone(phoneField.getText().trim());
        currentCustomer.setAddress(addressArea.getText().trim());
        currentCustomer.setLicenseNumber(licenseNumberField.getText().trim());

        if (customerDAO.updateCustomer(currentCustomer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All password fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!currentUser.getPassword().equals(oldPassword)) {
            JOptionPane.showMessageDialog(this, "Old password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setPassword(newPassword);
        if (userDAO.updateUser(currentUser)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
