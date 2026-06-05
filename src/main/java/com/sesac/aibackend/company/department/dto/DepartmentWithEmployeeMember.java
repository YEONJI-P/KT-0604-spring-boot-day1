package com.sesac.aibackend.company.department.dto;

import com.sesac.aibackend.company.employee.Employee;

public record DepartmentWithEmployeeMember(
        Long employeeId,
        String employeeName
) {
    public static DepartmentWithEmployeeMember from (Employee employee){
        return new DepartmentWithEmployeeMember(
                employee.getId(),
                employee.getName()
        );
    }
}
