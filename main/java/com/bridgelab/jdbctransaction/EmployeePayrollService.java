package com.bridgelab.jdbctransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {

	private EmployeePayrollDBIOService employeePayrollDBIOService;

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	Scanner scanner = new Scanner(System.in);
	List<EmployeePayroll> employeePayrollList = new ArrayList<>();

	public EmployeePayrollService() {
		employeePayrollDBIOService = EmployeePayrollDBIOService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayroll> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {

		EmployeePayrollService service = new EmployeePayrollService();
		service.readEmployeePayrollData();
		service.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}

	public void readEmployeePayrollData() {
		System.out.println("Enter Employee ID : ");
		int id = scanner.nextInt();
		System.out.println("Enter Employee Name : ");
		String name = scanner.next();
		System.out.println("Enter Employee Salary : ");
		double salary = scanner.nextDouble();
		LocalDate date = LocalDate.of(2011, 5, 4);
		System.out.println("Details Added!");
		employeePayrollList.add(new EmployeePayroll(id, name, salary, date));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Writing Employee Payroll Data to the console : " + employeePayrollList);
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeEmployeePayrollData(employeePayrollList);
		}
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public long readData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().readData().size();
		}
		return 0;
	}

	public List<EmployeePayroll> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			try {
				this.employeePayrollList = employeePayrollDBIOService.readData();
			} catch (ConnectivityIssueException e) {
				e.printStackTrace();
			}
		}
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBIOService.updateEmployeeSalary(name, salary);
		if (result == 0) {
			return;
		}
		EmployeePayroll employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null) {
			employeePayrollData.salary = salary;
		}
	}

	private EmployeePayroll getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(data -> data.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkPayrollObjectDataIsSyncWithDB(String name) {
		List<EmployeePayroll> employeePayrollDataList = EmployeePayrollDBIOService.getInstance()
				.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public List<EmployeePayroll> getEmployeePayrollDataFromDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) {
		if(ioService == IOService.DB_IO) {
			return employeePayrollDBIOService.getEmployeePayrollDataFromDateRange(startDate, endDate);
		}
		return null;
	}

	public Map<String, Double> getAvgSalaryByGender(IOService ioService) {
		if(ioService == IOService.DB_IO) {
			return employeePayrollDBIOService.getAvgSalaryByGender();
		}
		return null;
	}

	public void addNewEmployee(String name, double salary, LocalDate startDate, String gender) {
		this.employeePayrollList.add(employeePayrollDBIOService.addNewEmployeeToTheDB(name, salary, startDate, gender));
	}

}
