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
import java.util.List;

public class CustomerBookingsForm extends JFrame {

    private User currentUser;
    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JScrollPane tableScrollPane;
    private JPanel buttonPanel;

    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private JButton refreshButton;
    private JButton cancelButton;
    private JButton paymentButton;
    private JButton backButton;

    private int selectedBookingId = -1;

    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 73, 94);
    private Color successColor = new Color(46, 204, 113);
    private Color dangerColor = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color cardBg = Color.WHITE;
    private Color alternateRowColor = new Color(248, 249, 250);

    public CustomerBookingsForm(User user) {
        this.currentUser = user;
        this.bookingDAO = new BookingDAO();
        this.customerDAO = new CustomerDAO();
        this.vehicleDAO = new VehicleDAO();

        initializeComponents();
        setupLayout();
        setupListeners();
        loadBookingData();

        setTitle("My Bookings - Smart Car Rental System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(lightBg);

        createHeader();
        createTable();
        createButtonPanel();
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("My Bookings");
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

    private void createTable() {
        String[] columnNames = {"Booking ID", "Vehicle", "Booking Date", "Start Date", "End Date", "Days", "Amount", "Status"};
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
        bookingTable.setRowHeight(40);
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
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(lightBg);

        refreshButton = createActionButton("Refresh List", primaryColor);
        paymentButton = createActionButton("Make Payment", successColor);
        cancelButton = createActionButton("Cancel Selected Booking", dangerColor);

        buttonPanel.add(refreshButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(cancelButton);
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
        button.setPreferredSize(new Dimension(200, 45));

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
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(lightBg);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(lightBg);
        tableContainer.setBorder(new EmptyBorder(30, 30, 20, 30));
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        contentPanel.add(tableContainer, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setMinimumSize(new Dimension(900, 500));
    }

    private void setupListeners() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBookingData();
                JOptionPane.showMessageDialog(CustomerBookingsForm.this,
                        "Bookings list refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPaymentForm();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
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
                    selectedBookingId = (int) tableModel.getValueAt(selectedRow, 0);
                }
            }
        });
    }

    private void loadBookingData() {
        tableModel.setRowCount(0);

        Customer customer = customerDAO.getCustomerByUserId(currentUser.getUserId());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Booking> bookings = bookingDAO.getBookingsByCustomer(customer.getCustomerId());

        for (Booking booking : bookings) {
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());

            String vehicleInfo = vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getVehicleNumber() + ")" : "N/A";

            Object[] row = {
                    booking.getBookingId(),
                    vehicleInfo,
                    booking.getBookingDate(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    booking.getTotalDays() + " days",
                    "LKR " + booking.getTotalAmount(),
                    booking.getBookingStatus().toUpperCase()
            };
            tableModel.addRow(row);
        }

        tableModel.fireTableDataChanged();
    }

    private void cancelBooking() {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = bookingDAO.getBookingById(selectedBookingId);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (booking.getBookingStatus().equals("completed") || booking.getBookingStatus().equals("cancelled")) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a " + booking.getBookingStatus() + " booking!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingDAO.updateBookingStatus(selectedBookingId, "cancelled")) {
                vehicleDAO.updateVehicleStatus(booking.getVehicleId(), "available");
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedBookingId = -1;
                bookingTable.clearSelection();
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openPaymentForm() {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to make payment!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = bookingDAO.getBookingById(selectedBookingId);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (booking.getBookingStatus().equals("cancelled")) {
            JOptionPane.showMessageDialog(this, "Cannot make payment for a cancelled booking!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentForm paymentForm = new PaymentForm(currentUser, booking);
        paymentForm.setVisible(true);
        paymentForm.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadBookingData(); // Refresh the table after payment form is closed
            }
        });
    }
}
