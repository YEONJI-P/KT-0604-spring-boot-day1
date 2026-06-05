package com.sesac.aibackend.service;

import com.sesac.aibackend.domain.Department;
import com.sesac.aibackend.domain.Employee;
import com.sesac.aibackend.dto.EmployeeRequest;
import com.sesac.aibackend.dto.EmployeeResponse;
import com.sesac.aibackend.error.NotFoundException;
import com.sesac.aibackend.repository.DepartmentRepository;
import com.sesac.aibackend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    // 직원 전체 정보 get
    @Transactional(readOnly = true)
    public List<Employee> list(){
        return employeeRepository.findAll();
    }

    // 직원 단일 정보 get
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id){
        return employeeRepository.findById(id);
    }

    // 부서 정보까지 return 할 경우
    @Transactional(readOnly = true)
    public EmployeeResponse findByIdWithDepartment(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> NotFoundException.of("employee",id));

        return EmployeeResponse.fromWIthDepartmentName(employee);

    }

    @Transactional(readOnly = true)
    public List<Employee> findByDepartmentIdWithDepartment(Long departmentId){
        return employeeRepository.findByDepartmentIdWithDepartment(departmentId);
    }

    // create
    @Transactional
    public Employee save(Long departmentId,String employeeName ){
        // 참조 department 존재 여부
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()-> NotFoundException.of("department",departmentId));
        return employeeRepository.save(
                Employee.builder().
                        employeeName(employeeName)
                        .department(department)
                        .build()
        );
    }


    // update
    @Transactional
    public Employee update(Long id,EmployeeRequest req){
        // 변경하려는 유저 객체가 존재하는지
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(()-> NotFoundException.of("employee",id));
        Department department = employee.getDepartment();

        // 만약 department 를 변경한다면 department 존재하는지 체크
        if(!department.getId().equals(req.departmentId())){
            department = departmentRepository.findById(req.departmentId())
                    .orElseThrow(()->NotFoundException.of("department",req.departmentId()));
        }

        // 전부 통과했으면 update
        employee.updateAll(req.employeeName(),department);
        employeeRepository.save(employee);

        return employee;

    }

    @Transactional(readOnly = true)
    public boolean existsById(Long employeeId){
        return employeeRepository.existsById(employeeId);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmployeeName(String employeeName){
        return employeeRepository.existsByEmployeeName(employeeName);
    }


    // delete
    @Transactional
    public void deleteById(Long id){
        employeeRepository.deleteById(id);
    }


}
