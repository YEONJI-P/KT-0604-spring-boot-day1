package com.sesac.aibackend.company.employee;

import com.sesac.aibackend.company.employee.dto.EmployeeRequest;
import com.sesac.aibackend.company.employee.dto.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // get list
    @GetMapping
    public List<EmployeeResponse> list() {
        return employeeService.findAll().stream().map(EmployeeResponse::from).toList();
    }
    // get one no persistence context
    // service에서 transaction 내에서 department를 조회하지않음
    // 이쪽에서 employee의 department 를 참조하려고 하면 오류남
    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id){
        // 없을 때 error return
        return EmployeeResponse.from(employeeService.findById(id));
    }
    // get one with department persistence context
    @GetMapping("/{id}/with-department")
    public EmployeeResponse findByIdWithDepartment(@PathVariable Long id){
        return employeeService.findByIdWithDepartment(id);
    }

    // create
    // CUD write 작업에서는 transaction 내(서비스계층) 에서 조건 처리 - 원자성 보장
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest req){
        Employee saved = employeeService.save(req.departmentId(),req.name());
        URI location = URI.create("/employees/" + saved.getId());
        return ResponseEntity.created(location).body(EmployeeResponse.from(saved));
    }

    // update
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeRequest req){
        // 영속성 때문에 여기서 employee 객체를 가져오면 transaction X department 정보 Get X
        return EmployeeResponse.from(
                employeeService.update(id,req.name(),req.departmentId()));
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
