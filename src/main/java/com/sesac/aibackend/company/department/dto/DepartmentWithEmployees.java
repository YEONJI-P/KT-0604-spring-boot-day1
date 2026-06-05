package com.sesac.aibackend.company.department.dto;

import com.sesac.aibackend.company.department.Department;
import com.sesac.aibackend.company.employee.Employee;

import java.util.List;

public record DepartmentWithEmployees(
        Long id,
        String name,
        List<EmployeeSummary> employees
) {

    public static DepartmentWithEmployees from(Department department, List<EmployeeSummary> employees) {
        return new DepartmentWithEmployees(
                department.getId(),
                department.getName(),
                employees
        );
    }

    // 캡슐화
    public record EmployeeSummary(Long id, String name) {
        public static EmployeeSummary from(Employee employee) {
            return new EmployeeSummary(
                    employee.getId(),
                    employee.getName()
            );
        }
    }
}
