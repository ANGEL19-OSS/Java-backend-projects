package com.college.scr.service;

import com.college.scr.dto.*;
import com.college.scr.entity.Course;
import com.college.scr.entity.Student;
import com.college.scr.exception.DuplicateEnrollmentException;
import com.college.scr.exception.ResourceNotFoundException;
import com.college.scr.repository.CourseRepository;
import com.college.scr.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CourseRegistrationServiceImpl service;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(10L)
                .courseCode("CS101")
                .title("Data Structures")
                .credits(4)
                .students(new HashSet<>())
                .build();

        student = Student.builder()
                .id(1L)
                .name("Aarav Sharma")
                .email("aarav@college.edu")
                .department("CS")
                .courses(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should successfully enroll student in course")
    void enrollStudentInCourse_Success() {
        when(studentRepository.findWithCoursesById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO response = service.enrollStudentInCourse(1L, 10L);

        assertThat(response).isNotNull();
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Should throw DuplicateEnrollmentException when already enrolled")
    void enrollStudentInCourse_Duplicate_ThrowsException() {
        student.addCourse(course);

        when(studentRepository.findWithCoursesById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> service.enrollStudentInCourse(1L, 10L))
                .isInstanceOf(DuplicateEnrollmentException.class)
                .hasMessageContaining("already enrolled");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student ID not found")
    void enrollStudentInCourse_StudentNotFound_ThrowsException() {
        when(studentRepository.findWithCoursesById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enrollStudentInCourse(99L, 10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    @DisplayName("Should successfully remove enrollment")
    void removeEnrollment_Success() {
        student.addCourse(course);

        when(studentRepository.findWithCoursesById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO response = service.removeEnrollment(1L, 10L);

        assertThat(response).isNotNull();
        verify(studentRepository, times(1)).save(student);
    }
}
