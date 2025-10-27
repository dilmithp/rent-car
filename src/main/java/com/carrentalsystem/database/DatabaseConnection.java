package com.carrentalsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing database connections
 * Ensures only one connection instance throughout the application
 */
public class DatabaseConnection {

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // MAMP MySQL connection details for Mac
    private static final String HOST = "localhost";
    private static final String PORT = "8889"; // MAMP default MySQL port
    private static final String DATABASE = "car_rental_system";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
    private static final String USERNAME = "root"; // MAMP default username
    private static final String PASSWORD = "root"; // MAMP default password

    /**
     * Private constructor to prevent instantiation
     * Establishes connection to MySQL database
     */
    private DatabaseConnection() {
        try {
            // Load MySQL JDBC Driver (optional for newer versions)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Database connected successfully!");
            System.out.println("Connected to: " + DATABASE + " on port " + PORT);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database!");
            System.err.println("Make sure MAMP MySQL server is running on port " + PORT);
            e.printStackTrace();
        }
    }

    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get the active database connection
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or null, reconnect if needed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("🔄 Database reconnected!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking/reconnecting to database!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection!");
            e.printStackTrace();
        }
    }

    /**
     * Test method to verify database connection
     */
    public static void testConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        Connection conn = dbConnection.getConnection();

        if (conn != null) {
            System.out.println("✅ Connection test successful!");
            try {
                System.out.println("Database: " + conn.getCatalog());
                System.out.println("Connection valid: " + conn.isValid(5));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Connection test failed!");
        }
    }
}
