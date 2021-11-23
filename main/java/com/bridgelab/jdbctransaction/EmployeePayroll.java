package com.bridgelab.jdbctransaction;

import java.time.LocalDate;

public class EmployeePayroll {

	int id;
	String name;
	double salary;
	LocalDate date;

	public EmployeePayroll(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayroll(int id, String name, double salary, LocalDate date) {
		this(id, name, salary);
		this.date = date;
	}

	public String toString() {
		return "Id : " + id + ", Name : " + name + ", Salary : " + salary + ", Joining Date : " + date;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayroll other = (EmployeePayroll) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		return true;
	}

}
