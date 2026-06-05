package com.sesac.aibackend.company.employee.dto;

import com.sesac.aibackend.company.employee.Employee;

public record EmployeeResponse(
        Long id,
        Long departmentId,
        String departmentName,
        String employeeName
) {
    public static EmployeeResponse from(Employee employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getDepartment().getId(),
                null,
                employee.getName()
        );
    }

    // fetchJoin
    public static EmployeeResponse fromWIthDepartmentName(Employee employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getDepartment().getId(),
                employee.getDepartment().getName(),
                employee.getName()
                );
    }
}
