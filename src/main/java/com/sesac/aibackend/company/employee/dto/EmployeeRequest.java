package com.sesac.aibackend.company.employee.dto;

import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
        @NotNull Long departmentId,
        @NotNull String name
) {

}
