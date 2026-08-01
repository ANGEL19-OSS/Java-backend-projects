package com.company.epm.service;

import com.company.epm.dto.*;
import com.company.epm.entity.Employee;
import com.company.epm.entity.Passport;
import com.company.epm.exception.DuplicateEmailException;
import com.company.epm.exception.DuplicatePassportException;
import com.company.epm.exception.ResourceNotFoundException;
import com.company.epm.repository.EmployeeRepository;
import com.company.epm.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PassportRepository passportRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException("Employee with email '" + requestDTO.getEmail() + "' already exists.");
        }

        Passport passport = null;
        if (requestDTO.getPassport() != null) {
            String passportNum = requestDTO.getPassport().getPassportNumber();
            if (passportRepository.existsByPassportNumber(passportNum)) {
                throw new DuplicatePassportException("Passport with number '" + passportNum + "' already exists.");
            }
            passport = Passport.builder()
                    .passportNumber(requestDTO.getPassport().getPassportNumber())
                    .country(requestDTO.getPassport().getCountry())
                    .expiryDate(requestDTO.getPassport().getExpiryDate())
                    .build();
        }

        Employee employee = Employee.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .department(requestDTO.getDepartment())
                .passport(passport)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToDTO(savedEmployee);
    }

    @Override
    public EmployeeResponseDTO assignPassport(Long employeeId, PassportRequestDTO passportDTO) {
        Employee employee = findEmployeeById(employeeId);

        Long currentPassportId = employee.getPassport() != null ? employee.getPassport().getId() : null;
        if (currentPassportId != null) {
            if (passportRepository.existsByPassportNumberAndIdNot(passportDTO.getPassportNumber(), currentPassportId)) {
                throw new DuplicatePassportException("Passport with number '" + passportDTO.getPassportNumber() + "' already exists.");
            }
        } else {
            if (passportRepository.existsByPassportNumber(passportDTO.getPassportNumber())) {
                throw new DuplicatePassportException("Passport with number '" + passportDTO.getPassportNumber() + "' already exists.");
            }
        }

        if (employee.getPassport() != null) {
            employee.getPassport().setPassportNumber(passportDTO.getPassportNumber());
            employee.getPassport().setCountry(passportDTO.getCountry());
            employee.getPassport().setExpiryDate(passportDTO.getExpiryDate());
        } else {
            Passport newPassport = Passport.builder()
                    .passportNumber(passportDTO.getPassportNumber())
                    .country(passportDTO.getCountry())
                    .expiryDate(passportDTO.getExpiryDate())
                    .build();
            employee.setPassport(newPassport);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToDTO(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return mapToDTO(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Employee> employees = employeeRepository.findAll(pageable);
        return employees.map(this::mapToDTO);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeResponseDTO removePassportFromEmployee(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        if (employee.getPassport() == null) {
            throw new ResourceNotFoundException("Employee with ID " + employeeId + " does not have an assigned passport.");
        }
        employee.setPassport(null); // orphanRemoval = true will delete passport from DB
        Employee updated = employeeRepository.save(employee);
        return mapToDTO(updated);
    }

    private Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }

    private EmployeeResponseDTO mapToDTO(Employee employee) {
        EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
        if (employee.getPassport() != null) {
            dto.setPassport(modelMapper.map(employee.getPassport(), PassportResponseDTO.class));
        }
        return dto;
    }
}
