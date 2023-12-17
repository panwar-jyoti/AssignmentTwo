package com.example.demo.questionTwo;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        ExcelReader excelReader=new ExcelReader();
        List<Employee> Employees=excelReader.getEmployeeList();
    }

}