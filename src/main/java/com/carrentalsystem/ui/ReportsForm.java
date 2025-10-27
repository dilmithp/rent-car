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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

public class ReportsForm extends JFrame {

    private User currentUser;
    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;
    private BookingDAO bookingDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel statsPanel;
    private JPanel detailsPanel;
    private JButton backButton;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Color infoColor = new Color(155, 89, 182);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;

    public ReportsForm(User user) {
        this.currentUser = user;
        this.vehicleDAO = new VehicleDAO();
        this.customerDAO = new CustomerDAO();
        this.bookingDAO = new BookingDAO();

        initializeComponents();
        setupLayout();
        loadStatistics();

        setTitle("Reports & Analytics - Smart Car Rental System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createStatsPanel();
        createDetailsPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Reports & Analytics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Admin: " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setForeground(Color.WHITE);

        backButton = createHeaderButton("Back to Dashboard", secondaryColor);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

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

    private void createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(lightBg);
        statsPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
    }

    private void createDetailsPanel() {
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(lightBg);
        detailsPanel.setBorder(new EmptyBorder(10, 30, 30, 30));
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(accentColor);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        iconPanel.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(secondaryColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetailCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(secondaryColor);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setForeground(new Color(70, 70, 70));
        contentArea.setBackground(cardBg);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        card.add(contentArea, BorderLayout.SOUTH);

        return card;
    }

    private void loadStatistics() {
        List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        List<Vehicle> availableVehicles = vehicleDAO.getAvailableVehicles();
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        List<Customer> activeCustomers = customerDAO.getActiveCustomers();
        List<Booking> allBookings = bookingDAO.getAllBookings();
        List<Booking> pendingBookings = bookingDAO.getPendingBookings();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Booking booking : allBookings) {
            if (booking.getBookingStatus().equals("completed")) {
                totalRevenue = totalRevenue.add(booking.getTotalAmount());
            }
        }

        int rentedVehicles = allVehicles.size() - availableVehicles.size();

        statsPanel.add(createStatCard(
                "Total Vehicles",
                String.valueOf(allVehicles.size()),
                availableVehicles.size() + " available, " + rentedVehicles + " rented",
                primaryColor,
                "🚗"
        ));

        statsPanel.add(createStatCard(
                "Total Customers",
                String.valueOf(allCustomers.size()),
                activeCustomers.size() + " active customers",
                successColor,
                "👥"
        ));

        statsPanel.add(createStatCard(
                "Total Bookings",
                String.valueOf(allBookings.size()),
                pendingBookings.size() + " pending approval",
                warningColor,
                "📋"
        ));

        statsPanel.add(createStatCard(
                "Total Revenue",
                "LKR " + totalRevenue,
                "From completed bookings",
                infoColor,
                "💰"
        ));

        StringBuilder vehicleBreakdown = new StringBuilder();
        vehicleBreakdown.append("Sedan: ").append(vehicleDAO.getVehiclesByType("sedan").size()).append("   |   ");
        vehicleBreakdown.append("SUV: ").append(vehicleDAO.getVehiclesByType("suv").size()).append("   |   ");
        vehicleBreakdown.append("Van: ").append(vehicleDAO.getVehiclesByType("van").size()).append("   |   ");
        vehicleBreakdown.append("Luxury: ").append(vehicleDAO.getVehiclesByType("luxury").size()).append("   |   ");
        vehicleBreakdown.append("Economy: ").append(vehicleDAO.getVehiclesByType("economy").size());

        detailsPanel.add(createDetailCard("Vehicle Breakdown by Type", vehicleBreakdown.toString()));
        detailsPanel.add(Box.createVerticalStrut(15));

        long completedBookings = allBookings.stream().filter(b -> b.getBookingStatus().equals("completed")).count();
        long confirmedBookings = allBookings.stream().filter(b -> b.getBookingStatus().equals("confirmed")).count();
        long cancelledBookings = allBookings.stream().filter(b -> b.getBookingStatus().equals("cancelled")).count();

        String bookingStatus = "Completed: " + completedBookings + "   |   " +
                "Confirmed: " + confirmedBookings + "   |   " +
                "Pending: " + pendingBookings.size() + "   |   " +
                "Cancelled: " + cancelledBookings;

        detailsPanel.add(createDetailCard("Booking Status Overview", bookingStatus));
        detailsPanel.add(Box.createVerticalStrut(15));

        BigDecimal avgBookingAmount = allBookings.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(new BigDecimal(Math.max(completedBookings, 1)), 2, BigDecimal.ROUND_HALF_UP);

        String revenueInfo = "Total Revenue: LKR " + totalRevenue + "\n" +
                "Average Booking Amount: LKR " + avgBookingAmount + "\n" +
                "Total Completed Bookings: " + completedBookings;

        detailsPanel.add(createDetailCard("Revenue Statistics", revenueInfo));
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(lightBg);

        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setMinimumSize(new Dimension(900, 500));
    }
}
