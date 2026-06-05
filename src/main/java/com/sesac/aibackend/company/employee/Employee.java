package com.sesac.aibackend.company.employee;

import com.sesac.aibackend.company.department.Department;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,
            nullable = false,
            length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public void updateAll(String name, Department department){
        this.name = name;
        this.department = department;
    }
}
