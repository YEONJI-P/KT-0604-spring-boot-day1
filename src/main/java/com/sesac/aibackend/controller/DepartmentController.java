package com.sesac.aibackend.controller;

import com.sesac.aibackend.domain.Department;
import com.sesac.aibackend.dto.DepartmentRequest;
import com.sesac.aibackend.dto.DepartmentResponse;
import com.sesac.aibackend.error.NotFoundException;
import com.sesac.aibackend.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

//    @GetMapping
//    public List<DepartmentResponse> list(){
//        return departmentService.findAll().stream().map(DepartmentResponse::from).toList();
//    }

    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id){
        Department department = departmentService.findById(id)
                .orElseThrow(()-> NotFoundException.of("department",id));
        return DepartmentResponse.from(department);
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid@RequestBody DepartmentRequest req){
        if(departmentService.existsByName(req.departmentName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "departmentName already exists: "+ req.departmentName());
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
