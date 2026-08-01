package com.college.sms.service;

import com.college.sms.dto.StudentRequestDTO;
import com.college.sms.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);

    List<StudentResponseDTO> getAllStudents();

    StudentResponseDTO getStudentById(Long id);

    StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO);

    void deleteStudent(Long id);

    List<StudentResponseDTO> getStudentsByDepartment(String department);

    List<StudentResponseDTO> getStudentsByCgpaGreaterThan(Double minCgpa);
}
