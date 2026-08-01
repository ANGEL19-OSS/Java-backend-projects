package com.college.sms.service;

import com.college.sms.dto.StudentRequestDTO;
import com.college.sms.dto.StudentResponseDTO;
import com.college.sms.entity.Student;
import com.college.sms.exception.DuplicateEmailException;
import com.college.sms.exception.ResourceNotFoundException;
import com.college.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .name("Arjun Sharma")
                .email("arjun@college.edu")
                .phoneNumber("9876543210")
                .department("Computer Science")
                .yearOfStudy(3)
                .cgpa(8.5)
                .build();

        requestDTO = StudentRequestDTO.builder()
                .name("Arjun Sharma")
                .email("arjun@college.edu")
                .phoneNumber("9876543210")
                .department("Computer Science")
                .yearOfStudy(3)
                .cgpa(8.5)
                .build();
    }

    @Test
    @DisplayName("Should successfully create student")
    void createStudent_Success() {
        when(studentRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO response = studentService.createStudent(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("arjun@college.edu");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when creating student with duplicate email")
    void createStudent_DuplicateEmail_ThrowsException() {
        when(studentRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should return student when valid ID is provided")
    void getStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponseDTO response = studentService.getStudentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Arjun Sharma");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student ID not found")
    void getStudentById_NotFound_ThrowsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student not found with ID: 99");
    }

    @Test
    @DisplayName("Should successfully update student")
    void updateStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmailAndIdNot(requestDTO.getEmail(), 1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO response = studentService.updateStudent(1L, requestDTO);

        assertThat(response).isNotNull();
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should successfully delete student")
    void deleteStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).delete(student);
    }
}
