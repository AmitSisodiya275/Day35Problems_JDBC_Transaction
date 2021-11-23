package com.bridgelab.jdbctransaction;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DBDemo {

	public static void main(String[] args) {
		String jdbcUrl = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Amit@#$987";

		Connection connection;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		listDrivers();
		try {
			System.out.println("Connecting to databases : " + jdbcUrl);
			connection = DriverManager.getConnection(jdbcUrl, userName, password);
			System.out.println("Connection is Successfull !!!!!! " + connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		System.out.println("List of the drivers is : ");
		while (driverList.hasMoreElements()) {
			System.out.println(driverList.nextElement().getClass().getName());
		}
	}
}
