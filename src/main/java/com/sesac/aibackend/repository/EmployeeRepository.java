package com.sesac.aibackend.repository;

import com.sesac.aibackend.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsByEmployeeName(String employeeName);

    @Query("""
select e from Employee e
join fetch e.department
where e.department.id = :departmentId
""")
    List<Employee> findByDepartmentIdWithDepartment(Long departmentId);


}
