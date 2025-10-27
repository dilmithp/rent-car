package com.carrentalsystem.ui;

import com.carrentalsystem.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard extends JFrame {

    private User currentUser;
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JButton browseVehiclesButton;
    private JButton myBookingsButton;
    private JButton myProfileButton;
    private JButton logoutButton;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupListeners();

        setTitle("Customer Dashboard - Smart Car Rental System");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 240, 240));

        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        browseVehiclesButton = createStyledButton("Browse Available Vehicles");
        myBookingsButton = createStyledButton("My Bookings");
        myProfileButton = createStyledButton("My Profile");
        logoutButton = createStyledButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void setupLayout() {
        welcomeLabel.setBounds(50, 20, 500, 30);
        browseVehiclesButton.setBounds(150, 80, 300, 40);
        myBookingsButton.setBounds(150, 130, 300, 40);
        myProfileButton.setBounds(150, 180, 300, 40);
        logoutButton.setBounds(150, 240, 300, 40);

        mainPanel.add(welcomeLabel);
        mainPanel.add(browseVehiclesButton);
        mainPanel.add(myBookingsButton);
        mainPanel.add(myProfileButton);
        mainPanel.add(logoutButton);

        add(mainPanel);
    }

    private void setupListeners() {
        browseVehiclesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BrowseVehiclesForm(currentUser).setVisible(true);
            }
        });

        myBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerBookingsForm(currentUser).setVisible(true);
            }
        });

        myProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerProfileForm(currentUser).setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        CustomerDashboard.this,
                        "Are you sure you want to logout?",
                        "Logout Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginForm().setVisible(true);
                }
            }
        });
    }
}
