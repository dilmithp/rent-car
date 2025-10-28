package com.carrentalsystem.ui;

import com.carrentalsystem.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerDashboard extends JFrame {

    private User currentUser;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel cardPanel;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color accentColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color infoColor = new Color(155, 89, 182);
    private Color dangerColor = new Color(220, 53, 69);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setTitle("Customer Dashboard - Smart Car Rental System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createCardPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("SMART CAR RENTAL - Customer Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        userIcon.setForeground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Customer: " + currentUser.getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.WHITE);

        JButton logoutBtn = createHeaderButton("Logout", dangerColor);
        logoutBtn.addActionListener(e -> {
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
        });

        userPanel.add(userIcon);
        userPanel.add(usernameLabel);
        userPanel.add(logoutBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
    }

    private JButton createHeaderButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));

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

    private void createCardPanel() {
        cardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(60, 50, 50, 50));

        cardPanel.add(createDashboardCard("🚗", "Browse Vehicles", "View & reserve available cars", primaryColor,
                e -> new BrowseVehiclesForm(currentUser).setVisible(true)));

        cardPanel.add(createDashboardCard("📋", "My Bookings", "Manage your reservations", accentColor,
                e -> new CustomerBookingsForm(currentUser).setVisible(true)));

        cardPanel.add(createDashboardCard("📝", "My Profile", "Update your details", infoColor,
                e -> new CustomerProfileForm(currentUser).setVisible(true)));

        cardPanel.add(createDashboardCard("📞", "Support", "Contact support / FAQ", warningColor,
                e -> JOptionPane.showMessageDialog(this, "For support: Call +94-123-4567 or email help@smartrent.com", "Support", JOptionPane.INFORMATION_MESSAGE)));
    }

    private JPanel createDashboardCard(String icon, String title, String subtitle, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(secondaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accentColor, 2),
                        new EmptyBorder(24, 24, 24, 24)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        new EmptyBorder(25, 25, 25, 25)
                ));
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    private void setupLayout() {
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
        setMinimumSize(new Dimension(800, 500));
    }
}
