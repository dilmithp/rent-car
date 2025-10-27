package com.carrentalsystem.ui;

import com.carrentalsystem.models.User;
import javax.swing.*;

public class CustomerBookingsForm extends JFrame {
    public CustomerBookingsForm(User user) {
        setTitle("My Bookings - Coming Soon");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("My Bookings functionality will be implemented here", SwingConstants.CENTER);
        add(label);
    }
}
