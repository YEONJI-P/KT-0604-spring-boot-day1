package com.sesac.aibackend.company.department;

import com.sesac.aibackend.company.employee.EmployeeRepository;
import com.sesac.aibackend.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    // get list
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    // get one
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("department", id));
    }

    // 생성
    @Transactional
    public Department save(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "departmentName already exists: " + department.getName());
        }
        return departmentRepository.save(department);
    }

    // 삭제
    @Transactional
    public void deleteById(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw NotFoundException.of("department", id);
        }
        // OneToMany relation 설정 X
        // 참조중인 employee 가 있을 경우 그 employee 객체는 FK constraint 위반
        // 부서가 없어진다고 직원이 사라지는건 아니므로 참조 객체가 있으면 삭제 불가 처리
        // employee service에서 departmentId로 exist 검사
        if(employeeRepository.existsByDepartmentId(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "부서에 소속된 직원 존재: " + id);
        }
        departmentRepository.deleteById(id);
    }

}
