package com.sesac.aibackend.company.department.dto;

import com.sesac.aibackend.company.department.Department;

public record DepartmentResponse(
        Long id,
        String name

) {
    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}
