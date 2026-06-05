package com.sesac.aibackend.company.department.dto;

import com.sesac.aibackend.company.department.Department;
import jakarta.validation.constraints.NotNull;

public record DepartmentRequest(
        @NotNull String name
) {
    public Department toEntity(){
        return Department.builder().name(name).build();
    }
}
