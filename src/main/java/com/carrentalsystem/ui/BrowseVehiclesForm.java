package com.carrentalsystem.ui;

import com.carrentalsystem.models.User;
import javax.swing.*;

public class BrowseVehiclesForm extends JFrame {
    public BrowseVehiclesForm(User user) {
        setTitle("Browse Vehicles - Coming Soon");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Browse Vehicles functionality will be implemented here", SwingConstants.CENTER);
        add(label);
    }
}
