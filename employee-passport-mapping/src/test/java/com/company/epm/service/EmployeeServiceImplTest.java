package com.company.epm.service;

import com.company.epm.dto.*;
import com.company.epm.entity.Employee;
import com.company.epm.entity.Passport;
import com.company.epm.exception.DuplicateEmailException;
import com.company.epm.exception.DuplicatePassportException;
import com.company.epm.exception.ResourceNotFoundException;
import com.company.epm.repository.EmployeeRepository;
import com.company.epm.repository.PassportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PassportRepository passportRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Passport passport;
    private EmployeeRequestDTO requestDTO;
    private PassportRequestDTO passportDTO;

    @BeforeEach
    void setUp() {
        passport = Passport.builder()
                .id(10L)
                .passportNumber("Z99887766")
                .country("Germany")
                .expiryDate(LocalDate.now().plusYears(10))
                .build();

        employee = Employee.builder()
                .id(1L)
                .name("Klaus Weber")
                .email("klaus.weber@company.com")
                .department("R&D")
                .passport(passport)
                .build();

        passportDTO = PassportRequestDTO.builder()
                .passportNumber("Z99887766")
                .country("Germany")
                .expiryDate(LocalDate.now().plusYears(10))
                .build();

        requestDTO = EmployeeRequestDTO.builder()
                .name("Klaus Weber")
                .email("klaus.weber@company.com")
                .department("R&D")
                .passport(passportDTO)
                .build();
    }

    @Test
    @DisplayName("Should create employee with passport successfully")
    void createEmployee_Success() {
        when(employeeRepository.existsByEmail("klaus.weber@company.com")).thenReturn(false);
        when(passportRepository.existsByPassportNumber("Z99887766")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO response = employeeService.createEmployee(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Klaus Weber");
        assertThat(response.getPassport()).isNotNull();
        assertThat(response.getPassport().getPassportNumber()).isEqualTo("Z99887766");
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email exists")
    void createEmployee_DuplicateEmail_ThrowsException() {
        when(employeeRepository.existsByEmail("klaus.weber@company.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(requestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should throw DuplicatePassportException when passport number exists")
    void createEmployee_DuplicatePassport_ThrowsException() {
        when(employeeRepository.existsByEmail("klaus.weber@company.com")).thenReturn(false);
        when(passportRepository.existsByPassportNumber("Z99887766")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(requestDTO))
                .isInstanceOf(DuplicatePassportException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should retrieve employee by ID with mapped passport")
    void getEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO response = employeeService.getEmployeeById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPassport()).isNotNull();
    }

    @Test
    @DisplayName("Should delete employee cleanly")
    void deleteEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }
}
