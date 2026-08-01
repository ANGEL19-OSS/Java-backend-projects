package com.college.scr.service;

import com.college.scr.dto.*;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface CourseRegistrationService {

    StudentResponseDTO registerStudent(StudentRequestDTO requestDTO);

    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);

    StudentResponseDTO enrollStudentInCourse(Long studentId, Long courseId);

    StudentResponseDTO removeEnrollment(Long studentId, Long courseId);

    StudentResponseDTO getStudentById(Long studentId);

    CourseResponseDTO getCourseById(Long courseId);

    Page<StudentResponseDTO> getAllStudents(int pageNo, int pageSize);

    Page<CourseResponseDTO> getAllCourses(int pageNo, int pageSize);

    Set<CourseResponseDTO> getCoursesByStudentId(Long studentId);

    Set<StudentResponseDTO> getStudentsByCourseId(Long courseId);
}
