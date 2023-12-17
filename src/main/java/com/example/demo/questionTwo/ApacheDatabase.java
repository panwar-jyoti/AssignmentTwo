package com.example.demo.questionTwo;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ApacheDatabase {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/EmployeeInterview";
    private static final String USER = "root";
    private static final String PASSWORD = "Krishna@123";

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public static void main(String[] args) {
        // Extracted data from Excel or other source
        ExcelReader excelReader=new ExcelReader();
        List<Employee> Employees=excelReader.getEmployeeList();
        System.out.println(Employees.get(1));
        // Insert data into the database using parallel streams
        for (Employee employee : Employees) {
            ApacheDatabase.insertEmployee(employee);
        }
    }
    private static void insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (IDate, Imonth,team,PanelName,round,skill,Itime,Clocation,Plocation,Cname) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, employee.getDate());
            statement.setDate(2, employee.getMonth());
            statement.setString(3,employee.getTeam());
            statement.setString(4,employee.getPanelName());
            statement.setString(5,employee.getRound());
            statement.setString(6,employee.getSkill());
            statement.setTime(7,employee.getTime());
            statement.setString(8, employee.getCurrentLoc());
            statement.setString(9, employee.getPreferredLoc());
            statement.setString(10, employee.getCandidateName());
            // Execute the insert statement
            statement.executeUpdate();
            System.out.println("Inserting");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}