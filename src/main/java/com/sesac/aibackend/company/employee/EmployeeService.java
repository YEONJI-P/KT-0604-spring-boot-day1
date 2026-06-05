package com.sesac.aibackend.company.employee;

import com.sesac.aibackend.company.department.Department;
import com.sesac.aibackend.company.employee.dto.EmployeeRequest;
import com.sesac.aibackend.company.employee.dto.EmployeeResponse;
import com.sesac.aibackend.error.NotFoundException;
import com.sesac.aibackend.company.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    // get list
    @Transactional(readOnly = true)
    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }
    // get one no persistence context
    // 영속성 고려 안하고 바로 return
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id){
        return employeeRepository.findById(id);
    }

    // get one with department
    // 영속성 고려
    // Transaction 내에서 department 정보도 포함하는 dto로 변환
    @Transactional(readOnly = true)
    public EmployeeResponse findByIdWithDepartment(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> NotFoundException.of("employee",id));
        // 영속성 context 로 인해 department 정보도 받아와짐
        return EmployeeResponse.fromWithDepartmentName(employee);
    }

    // create
    @Transactional
    public Employee save(Long departmentId,String name ){
        // 참조 department 존재 여부
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()-> NotFoundException.of("department",departmentId));
        return employeeRepository.save(
                Employee.builder()
                        .name(name)
                        .department(department)
                        .build()
        );
    }

    // update
    @Transactional
    public Employee update(Long id, EmployeeRequest req){
        // 변경하려는 유저 객체가 존재하는지
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(()-> NotFoundException.of("employee",id));
        Department department = employee.getDepartment();

        // 만약 이름을 변경하려고 할 때 변경하려는 유저이름이 존재하는지
        // employee name이 unique 제약일때만 사용
        // 본인은 제외하고 체크
        if(employeeRepository.existsByIdNotAndName(id,req.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "employee name already exists: " + req.name());
        }
        // 만약 department 를 변경한다면 department 존재하는지 체크
        if(!department.getId().equals(req.departmentId())){
            department = departmentRepository.findById(req.departmentId())
                    .orElseThrow(()->NotFoundException.of("department",req.departmentId()));
        }

        // 전부 통과했으면 update
        employee.updateAll(req.name(),department);
        employeeRepository.save(employee);

        return employee;
    }

    // delete
    @Transactional
    public void deleteById(Long id){
        employeeRepository.deleteById(id);
    }

    // employee id 로 존재 여부 체크
    @Transactional(readOnly = true)
    public boolean existsById(Long id){
        return employeeRepository.existsById(id);
    }

    // employee name 으로 존재 여부 체크
    @Transactional(readOnly = true)
    public boolean existsByName(String name){
        return employeeRepository.existsByName(name);
    }

    // get list with department
    // department id fk 를 조건으로 employee 목록 조회
    // department controller에서 사용
    @Transactional(readOnly = true)
    public List<Employee> findByDepartmentId(Long departmentId){
        return employeeRepository.findByDepartmentId(departmentId);
    }

}
