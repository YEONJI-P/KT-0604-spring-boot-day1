package com.sesac.aibackend.company.department;

import com.sesac.aibackend.company.department.dto.DepartmentRequest;
import com.sesac.aibackend.company.department.dto.DepartmentResponse;
import com.sesac.aibackend.company.department.dto.DepartmentWithEmployeeMember;
import com.sesac.aibackend.company.department.dto.DepartmentWithEmployeeResponse;
import com.sesac.aibackend.company.employee.Employee;
import com.sesac.aibackend.company.employee.EmployeeService;
import com.sesac.aibackend.error.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    // 목록 조회
    @GetMapping
    public List<DepartmentResponse> list(){
        return departmentService.findAll().stream().map(DepartmentResponse::from).toList();
    }
    // 특정부서 조회 (employee 정보 미포함)
    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id){
        Department department = departmentService.findById(id)
                .orElseThrow(()-> NotFoundException.of("department",id));
        return DepartmentResponse.from(department);
    }
    // 특정 부서 조회 (employee 정보 포함)
    // 새로운 DTO 로 처리
    @GetMapping("/{id}/with-employees")
    public DepartmentWithEmployeeResponse listWithEmployees(@PathVariable Long id){
        // department
        // OneToMany 관계 설정 X 직접 참조 X
        // 간접참조 디커플링
        Department department = departmentService.findById(id)
                .orElseThrow(()-> NotFoundException.of("department",id));
        List<Employee> employees = employeeService.findByDepartmentIdWithDepartment(id);

        return DepartmentWithEmployeeResponse.from(department,
                employees.stream().map(DepartmentWithEmployeeMember::from).toList());

    }


    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid@RequestBody DepartmentRequest req){
        if(departmentService.existsByName(req.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "departmentName already exists: "+ req.name());
        }

        Department saved = departmentService.save(req.toEntity());
        URI location = URI.create("/departments/"+saved.getId());

        return ResponseEntity.created(location).body(DepartmentResponse.from(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!departmentService.existsById(id)){
            throw NotFoundException.of("department",id);
        }
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
