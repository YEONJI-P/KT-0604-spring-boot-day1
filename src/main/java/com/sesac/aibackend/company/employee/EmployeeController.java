package com.sesac.aibackend.company.employee;

import com.sesac.aibackend.company.employee.dto.EmployeeRequest;
import com.sesac.aibackend.company.employee.dto.EmployeeResponse;
import com.sesac.aibackend.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // get list
    @GetMapping
    public List<EmployeeResponse> list(){
        return  employeeService.findAll().stream().map(EmployeeResponse::from).toList();
    }

    // get
    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id){
        // 없을 때 error return
        Employee employee = employeeService.findById(id)
                .orElseThrow(()-> NotFoundException.of("employee",id));
        return EmployeeResponse.from(employee);

    }


    // employee 리스트 부서정보포함
    @GetMapping("/with-department")
    public List<EmployeeResponse> listWithDepartment(@RequestParam Long departmentId) {
        return employeeService.findByDepartmentIdWithDepartment(departmentId).stream()
                .map(EmployeeResponse::fromWIthDepartmentName)
                .toList();
    }

    // 특정 사용자 부서 정보 포함
    @GetMapping("/with-department/{id}")
    public EmployeeResponse findByIdWithDepartment(@PathVariable Long id){
        return employeeService.findByIdWithDepartment(id);
    }

    // create
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest req){
        if (employeeService.existsByEmployeeName(req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "employee name already exists: " + req.name());
        }
        Employee saved = employeeService.save(req.departmentId(),req.name());
        URI location = URI.create("/employees/" + saved.getId());
        return ResponseEntity.created(location).body(EmployeeResponse.from(saved));
    }

    // update
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeRequest req){



        // 영속성 때문에 여기서 employee 객체를 가져오면 transaction X department 정보 Get X
        // 부서 변경 시 변경하려는 부서가 존재하는지 체크 > Service 계층에서
        // service 에 request 정보 전달
        return EmployeeResponse.from(employeeService.update(id,req));



    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!employeeService.existsById(id)){
            throw NotFoundException.of("employee",id);
        }
        employeeService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
