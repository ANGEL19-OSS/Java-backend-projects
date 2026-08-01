package com.college.scr.controller;

import com.college.scr.dto.*;
import com.college.scr.service.CourseRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Registration & Enrollment API", description = "Endpoints for managing student profiles, course enrollments, and course removal")
public class StudentController {

    private final CourseRegistrationService registrationService;

    @PostMapping
    @Operation(summary = "Register new student", description = "Registers a new student in the system")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> registerStudent(@Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO responseDTO = registrationService.registerStudent(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student registered successfully", responseDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Fetches student details along with their enrolled courses")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        StudentResponseDTO responseDTO = registrationService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student details retrieved successfully", responseDTO));
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Fetches a paginated list of all students")
    public ResponseEntity<ApiResponse<Page<StudentResponseDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentResponseDTO> students = registrationService.getAllStudents(page, size);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @PostMapping("/{studentId}/enroll/{courseId}")
    @Operation(summary = "Enroll student in course", description = "Establishes a Many-to-Many enrollment link between student and course")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> enrollStudentInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        StudentResponseDTO responseDTO = registrationService.enrollStudentInCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student enrolled in course successfully", responseDTO));
    }

    @DeleteMapping("/{studentId}/unenroll/{courseId}")
    @Operation(summary = "Remove course enrollment", description = "Removes course enrollment link for a student")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> removeEnrollment(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        StudentResponseDTO responseDTO = registrationService.removeEnrollment(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Enrollment removed successfully", responseDTO));
    }

    @GetMapping("/{studentId}/courses")
    @Operation(summary = "View courses enrolled by student", description = "Fetches set of all courses currently registered by the student")
    public ResponseEntity<ApiResponse<Set<CourseResponseDTO>>> getCoursesByStudentId(@PathVariable Long studentId) {
        Set<CourseResponseDTO> courses = registrationService.getCoursesByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Enrolled courses retrieved successfully", courses));
    }
}
