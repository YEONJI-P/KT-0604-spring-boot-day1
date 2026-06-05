package com.sesac.aibackend.company.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByName(String employeeName);

    boolean existsByIdNotAndName(Long id, String employeeName);

    boolean existsByDepartmentId(Long departmentId);

    // @Query 로 fetch join 사용하지 않은 이유
    // 응답 dto 조립 시 department 는 department domain 단에서 처리
    // 현재 employee list dto에서 department relation 참조 X
    // N+1 문제 X
    List<Employee> findByDepartmentId(Long departmentId);




}
