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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Catalog API", description = "Endpoints for creating courses, viewing course catalog, and retrieving registered students per course")
public class CourseController {

    private final CourseRegistrationService registrationService;

    @PostMapping
    @Operation(summary = "Create course", description = "Creates a new academic course in the catalog")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> createCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO responseDTO = registrationService.createCourse(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", responseDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Fetches course details by ID")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> getCourseById(@PathVariable Long id) {
        CourseResponseDTO responseDTO = registrationService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Course details retrieved successfully", responseDTO));
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Fetches a paginated list of all courses in catalog")
    public ResponseEntity<ApiResponse<Page<CourseResponseDTO>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseResponseDTO> courses = registrationService.getAllCourses(page, size);
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "View students registered in course", description = "Fetches set of all students currently enrolled in the specified course")
    public ResponseEntity<ApiResponse<Set<StudentResponseDTO>>> getStudentsByCourseId(@PathVariable Long courseId) {
        Set<StudentResponseDTO> students = registrationService.getStudentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Enrolled students retrieved successfully", students));
    }
}
