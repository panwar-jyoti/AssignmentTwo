package com.example.demo.questionTwo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static void main(String[] args) {
        // JDBC URL, username, and password of MySQL server
        String url
                = "jdbc:mysql://localhost:3306/prac"; // table details
        String user = "root"; // MySQL credentials
        String password = "acc0@user";

        // Establish a connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}