package com.sesac.aibackend.dto;

import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
        @NotNull Long departmentId,
        @NotNull String employeeName
) {

}
