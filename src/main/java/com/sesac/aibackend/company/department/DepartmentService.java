package com.sesac.aibackend.company.department;

import com.sesac.aibackend.company.department.dto.DepartmentWithEmployeeMember;
import com.sesac.aibackend.company.department.dto.DepartmentWithEmployeeResponse;
import com.sesac.aibackend.company.employee.Employee;
import com.sesac.aibackend.company.employee.EmployeeRepository;
import com.sesac.aibackend.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    // Get
    @Transactional(readOnly = true)
    public List<Department> findAll(){
        return departmentRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<Department> findById(Long departmentId){
        return departmentRepository.findById(departmentId);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long departmentId){
        return departmentRepository.existsById(departmentId);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String departmentName){
        return departmentRepository.existsByName(departmentName);
    }
    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteById(Long departmentId){ departmentRepository.deleteById(departmentId);}


}
