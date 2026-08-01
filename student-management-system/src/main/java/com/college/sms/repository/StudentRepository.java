package com.college.sms.repository;

import com.college.sms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Student> findByDepartmentIgnoreCase(String department);

    List<Student> findByCgpaGreaterThan(Double cgpa);
}
