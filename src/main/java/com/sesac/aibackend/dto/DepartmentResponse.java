package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Department;

public record DepartmentResponse(
        Long id,
        String departmentName

) {
    public static DepartmentResponse from(Department department){
        return new DepartmentResponse(department.getId(),department.getDepartmentName());
    }
}
