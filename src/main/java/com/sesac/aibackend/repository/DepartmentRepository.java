package com.sesac.aibackend.repository;

import com.sesac.aibackend.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {

    boolean existsByDepartmentName(String departmentName);
}
