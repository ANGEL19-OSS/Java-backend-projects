package com.college.scr.repository;

import com.college.scr.entity.Course;
import com.college.scr.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();

        student = studentRepository.save(Student.builder()
                .name("Kiran Bedi")
                .email("kiran@college.edu")
                .department("Cyber Security")
                .build());

        course = courseRepository.save(Course.builder()
                .courseCode("CY101")
                .title("Network Security")
                .credits(3)
                .build());
    }

    @Test
    @DisplayName("Should enroll student in course via @ManyToMany join table")
    void enrollStudent_Success() {
        student.addCourse(course);
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findWithCoursesById(student.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCourses()).hasSize(1);
        assertThat(found.get().getCourses().iterator().next().getCourseCode()).isEqualTo("CY101");
    }

    @Test
    @DisplayName("Should unenroll student from course")
    void unenrollStudent_Success() {
        student.addCourse(course);
        studentRepository.save(student);

        student.removeCourse(course);
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findWithCoursesById(student.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCourses()).isEmpty();
    }
}
