package com.company.epm.controller;

import com.company.epm.dto.*;
import com.company.epm.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee & Passport Management API", description = "Endpoints for managing Employees, Passport mapping, and Cascade Delete operations")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create employee", description = "Creates a new employee record with optional nested passport details")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO responseDTO = employeeService.createEmployee(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee record created successfully", responseDTO));
    }

    @PostMapping("/{id}/passport")
    @Operation(summary = "Assign or update passport", description = "Assigns a new passport or updates existing passport for an employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> assignPassport(
            @PathVariable Long id,
            @Valid @RequestBody PassportRequestDTO passportDTO) {
        EmployeeResponseDTO responseDTO = employeeService.assignPassport(id, passportDTO);
        return ResponseEntity.ok(ApiResponse.success("Passport assigned/updated successfully", responseDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve employee by ID", description = "Fetches complete employee details along with mapped passport information")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO responseDTO = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee details retrieved successfully", responseDTO));
    }

    @GetMapping
    @Operation(summary = "Retrieve all employees", description = "Fetches a paginated list of all employees with their passport details")
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<EmployeeResponseDTO> employees = employeeService.getAllEmployees(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee (Cascade Delete)", description = "Deletes an employee record and automatically deletes the associated passport via Cascade Delete")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee and associated passport deleted successfully"));
    }

    @DeleteMapping("/{id}/passport")
    @Operation(summary = "Unassign passport from employee", description = "Removes the assigned passport from the employee record")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> removePassportFromEmployee(@PathVariable Long id) {
        EmployeeResponseDTO responseDTO = employeeService.removePassportFromEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Passport unassigned successfully", responseDTO));
    }
}
