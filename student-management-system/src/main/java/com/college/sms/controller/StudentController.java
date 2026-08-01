package com.college.sms.controller;

import com.college.sms.dto.ApiResponse;
import com.college.sms.dto.StudentRequestDTO;
import com.college.sms.dto.StudentResponseDTO;
import com.college.sms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Management API", description = "Endpoints for creating, retrieving, updating, and deleting student records")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Add a new student", description = "Creates a new student record in the database")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(@Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO createdStudent = studentService.createStudent(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student record created successfully", createdStudent));
    }

    @GetMapping
    @Operation(summary = "Retrieve all students", description = "Fetches a list of all enrolled students")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity
                .ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a student by ID", description = "Fetches details of a specific student by their ID")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        return ResponseEntity
                .ok(ApiResponse.success("Student retrieved successfully", student));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student details", description = "Updates an existing student's record by ID")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(id, requestDTO);
        return ResponseEntity
                .ok(ApiResponse.success("Student record updated successfully", updatedStudent));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Removes a student record from the system by ID")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity
                .ok(ApiResponse.success("Student deleted successfully"));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Search students by department", description = "Finds all students belonging to a specified department")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsByDepartment(@PathVariable String department) {
        List<StudentResponseDTO> students = studentService.getStudentsByDepartment(department);
        return ResponseEntity
                .ok(ApiResponse.success("Department search completed", students));
    }

    @GetMapping("/cgpa")
    @Operation(summary = "Retrieve students by CGPA filter", description = "Fetches students with CGPA strictly greater than the specified minimum threshold")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsByCgpaGreaterThan(
            @RequestParam(name = "min") Double minCgpa) {
        List<StudentResponseDTO> students = studentService.getStudentsByCgpaGreaterThan(minCgpa);
        return ResponseEntity
                .ok(ApiResponse.success("CGPA filter search completed", students));
    }
}
