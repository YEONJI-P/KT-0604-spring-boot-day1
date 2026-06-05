package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Department;
import jakarta.validation.constraints.NotNull;

public record DepartmentRequest(
        @NotNull String departmentName
) {
    public Department toEntity(){
        return Department.builder().departmentName(departmentName).build();
    }
}
