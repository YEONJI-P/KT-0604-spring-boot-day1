package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Employee;

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
                employee.getEmployeeName()
        );
    }

    // fetchJoin
    public static EmployeeResponse fromWIthDepartmentName(Employee employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getDepartment().getId(),
                employee.getDepartment().getDepartmentName(),
                employee.getEmployeeName()
                );
    }
}
