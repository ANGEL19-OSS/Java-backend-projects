package com.college.sms.repository;

import com.college.sms.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        student1 = Student.builder()
                .name("Rahul Kumar")
                .email("rahul.kumar@college.edu")
                .phoneNumber("9876543210")
                .department("Computer Science")
                .yearOfStudy(3)
                .cgpa(8.5)
                .build();

        student2 = Student.builder()
                .name("Sneha Roy")
                .email("sneha.roy@college.edu")
                .phoneNumber("9876543211")
                .department("Electrical Engineering")
                .yearOfStudy(2)
                .cgpa(9.1)
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_True() {
        boolean exists = studentRepository.existsByEmail("rahul.kumar@college.edu");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_False() {
        boolean exists = studentRepository.existsByEmail("unknown@college.edu");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find students by department case-insensitive")
    void findByDepartmentIgnoreCase() {
        List<Student> result = studentRepository.findByDepartmentIgnoreCase("computer science");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rahul Kumar");
    }

    @Test
    @DisplayName("Should find students with CGPA greater than threshold")
    void findByCgpaGreaterThan() {
        List<Student> result = studentRepository.findByCgpaGreaterThan(8.8);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sneha Roy");
    }
}
