package com.carrentalsystem.ui;

import com.carrentalsystem.models.User;
import javax.swing.*;

public class CustomerProfileForm extends JFrame {
    public CustomerProfileForm(User user) {
        setTitle("My Profile - Coming Soon");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Profile functionality will be implemented here", SwingConstants.CENTER);
        add(label);
    }
}
