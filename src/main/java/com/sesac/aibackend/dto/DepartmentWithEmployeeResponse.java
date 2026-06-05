package com.sesac.aibackend.dto;

import java.util.List;

public record DepartmentWithEmployeeResponse(
        Long id,
        String name,
        List<DepartmentWithEmployeeMember> employees
){
}

