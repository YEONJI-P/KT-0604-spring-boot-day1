package com.sesac.aibackend.company.department;

import com.sesac.aibackend.company.department.dto.*;
import com.sesac.aibackend.company.employee.Employee;
import com.sesac.aibackend.company.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    // 부서 목록 조회
    @GetMapping
    public List<DepartmentResponse> list() {
        return departmentService.findAll().stream().map(DepartmentResponse::from).toList();
    }

    // 특정부서 조회 (employee list 정보 미포함)
    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        return DepartmentResponse.from(department);
    }

    // 특정 부서 조회 (employee list 정보 포함)
    // 새로운 DTO 로 처리
    @GetMapping("/{id}/with-employees")
    public DepartmentWithEmployees listWithEmployees(@PathVariable Long id) {
        // OneToMany 관계 설정 X 직접 참조 X
        // 간접참조 디커플링
        Department department = departmentService.findById(id);
        List<Employee> employees = employeeService.findByDepartmentId(id);
        // 응답 dto 조립
        return DepartmentWithEmployees.from(department,
                employees.stream().map(DepartmentWithEmployees.EmployeeSummary::from).toList());
    }
    // 부서 생성
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest req) {
        Department saved = departmentService.save(req.toEntity());
        URI location = URI.create("/departments/" + saved.getId());
        return ResponseEntity.created(location).body(DepartmentResponse.from(saved));
    }
    // 부서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
