package com.sesac.aibackend.company.department.dto;

import com.sesac.aibackend.company.department.Department;

import java.util.List;

public record DepartmentWithEmployeeResponse(
        Long id,
        String name,
        List<DepartmentWithEmployeeMember> employees
){
    public static DepartmentWithEmployeeResponse from(Department department, List<DepartmentWithEmployeeMember> employees){
        return new DepartmentWithEmployeeResponse(
                department.getId(),
                department.getName(),
                employees
        );
    }
}

