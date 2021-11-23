package com.bridgelab.jdbctransaction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBIOService {

	private PreparedStatement EmployeePayrollPreparedStatement;

	private static EmployeePayrollDBIOService employeePayrollDBIOService;

	private EmployeePayrollDBIOService() {

	}

	public static EmployeePayrollDBIOService getInstance() {
		if (employeePayrollDBIOService == null) {
			employeePayrollDBIOService = new EmployeePayrollDBIOService();
		}
		return employeePayrollDBIOService;
	}

	private Connection getConnection() {
		String url = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String username = "root";
		String password = "Amit@#$987";

		Connection connection = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public List<EmployeePayroll> readData() throws ConnectivityIssueException {
		String sql = "select * from employee_payroll;";
		return this.getEmployeePayrollDataUsingSql(sql);
	}

	public int updateEmployeeSalary(String name, double salary) {
		return this.updateEmployeeSalaryUsingStatement(name, salary);
	}

	private int updateEmployeeSalaryUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayroll> getEmployeePayrollData(String name) {
		List<EmployeePayroll> employeePayrollData = null;
		if (this.EmployeePayrollPreparedStatement == null) {
			this.preparedStatementForEmployeePayroll();
		}
		try {
			EmployeePayrollPreparedStatement.setString(1, name);
			ResultSet resultSet = EmployeePayrollPreparedStatement.executeQuery();
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	private List<EmployeePayroll> getEmployeePayrollData(ResultSet resultSet) throws SQLException {
		List<EmployeePayroll> employeePayrollList = new ArrayList<>();
		while (resultSet.next()) {
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			double salary = resultSet.getDouble("salary");
			LocalDate date = resultSet.getDate("start_date").toLocalDate();
			employeePayrollList.add(new EmployeePayroll(id, name, salary, date));
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeePayroll() {
		String sql = "select * from employee_payroll where name = ?";
		try {
			Connection connection = this.getConnection();
			EmployeePayrollPreparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<EmployeePayroll> getEmployeePayrollDataFromDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("select * from employee_payroll where start_date between '%s' and '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataUsingSql(sql);
	}

	private List<EmployeePayroll> getEmployeePayrollDataUsingSql(String sql) {
		List<EmployeePayroll> payrollList = new ArrayList<>();

		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);

			payrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return payrollList;
	}

	public Map<String, Double> getAvgSalaryByGender() {
		String sql = "select gender, avg(salary) as average_salary from employee_payroll group by gender";
		Map<String, Double> avgSalaryByGender = new HashMap<>();
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				Double avgSalary = resultSet.getDouble("average_salary");
				avgSalaryByGender.put(gender, avgSalary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return avgSalaryByGender;
	}

	public EmployeePayroll addNewEmployeeToTheDBUC7(String name, double salary, LocalDate startDate, String gender) {
		int empPayrollID = -1;
		EmployeePayroll employeePayroll = null;
		String sql = String.format(
				"insert into employee_payroll(name, gender, salary, start_date) values ('%s','%s', '%s', '%s');", name,
				gender, salary, Date.valueOf(startDate));

		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					empPayrollID = resultSet.getInt(1);
				}
			}
			employeePayroll = new EmployeePayroll(empPayrollID, name, salary, startDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayroll;
	}

	public EmployeePayroll addNewEmployeeToTheDB(String name, double salary, LocalDate startDate, String gender) {
		int empID = -1;
		EmployeePayroll employeePayroll = null;
		Connection connection = this.getConnection();

		try (Statement statement = connection.createStatement()) {
			String sql = String.format("insert into employee_payroll(name, gender, salary, start_date) values "
					+ "('%s', '%s', '%s', '%s'); ", name, gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					empID = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Statement statement = connection.createStatement()) {
			double deduction = salary * 0.2;
			double taxable_pay = salary - deduction;
			double tax = taxable_pay * 0.1;
			double net_pay = salary - tax;
			String sql = String.format(
					"insert into payroll_details(emp_id, basic_pay, deductions, taxable_pay, tax, net_pay)"
							+ " values(%s, %s, %s, %s, %s, %s);",
					empID, salary, deduction, taxable_pay, tax, net_pay);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				employeePayroll = new EmployeePayroll(empID, name, salary, startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayroll;
	}

}
