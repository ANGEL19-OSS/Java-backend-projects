package com.company.epm.service;

import com.company.epm.dto.EmployeeRequestDTO;
import com.company.epm.dto.EmployeeResponseDTO;
import com.company.epm.dto.PassportRequestDTO;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    EmployeeResponseDTO assignPassport(Long employeeId, PassportRequestDTO passportDTO);

    EmployeeResponseDTO getEmployeeById(Long employeeId);

    Page<EmployeeResponseDTO> getAllEmployees(int pageNo, int pageSize, String sortBy, String sortDir);

    void deleteEmployee(Long employeeId);

    EmployeeResponseDTO removePassportFromEmployee(Long employeeId);
}
