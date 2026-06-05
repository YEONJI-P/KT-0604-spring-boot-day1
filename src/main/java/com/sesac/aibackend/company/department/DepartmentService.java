package com.sesac.aibackend.company.department;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    // get list
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    // get one
    @Transactional(readOnly = true)
    public Optional<Department> findById(Long departmentId) {
        return departmentRepository.findById(departmentId);
    }

    // 생성
    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    // 삭제
    @Transactional
    public void deleteById(Long departmentId) {
        departmentRepository.deleteById(departmentId);
    }

    // id로 존재 여부 체크
    @Transactional(readOnly = true)
    public boolean existsById(Long departmentId) {
        return departmentRepository.existsById(departmentId);
    }

    // name으로 존재 여부 체크
    @Transactional(readOnly = true)
    public boolean existsByName(String departmentName) {
        return departmentRepository.existsByName(departmentName);
    }


}
