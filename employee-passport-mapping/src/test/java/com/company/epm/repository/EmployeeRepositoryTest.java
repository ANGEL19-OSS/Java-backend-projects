package com.company.epm.repository;

import com.company.epm.entity.Employee;
import com.company.epm.entity.Passport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PassportRepository passportRepository;

    private Employee employeeWithPassport;
    private Employee employeeWithoutPassport;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        passportRepository.deleteAll();

        Passport passport = Passport.builder()
                .passportNumber("CAN11223344")
                .country("Canada")
                .expiryDate(LocalDate.now().plusYears(5))
                .build();

        employeeWithPassport = Employee.builder()
                .name("John Doe")
                .email("john.doe@company.com")
                .department("Finance")
                .passport(passport)
                .build();

        employeeWithoutPassport = Employee.builder()
                .name("Jane Smith")
                .email("jane.smith@company.com")
                .department("Marketing")
                .passport(null)
                .build();

        employeeRepository.save(employeeWithPassport);
        employeeRepository.save(employeeWithoutPassport);
    }

    @Test
    @DisplayName("Should save employee and cascade persist passport (@OneToOne CascadeType.ALL)")
    void save_CascadePersistPassport() {
        Optional<Employee> found = employeeRepository.findByEmail("john.doe@company.com");
        assertThat(found).isPresent();
        assertThat(found.get().getPassport()).isNotNull();
        assertThat(found.get().getPassport().getPassportNumber()).isEqualTo("CAN11223344");
    }

    @Test
    @DisplayName("Should save employee without passport")
    void save_EmployeeWithoutPassport() {
        Optional<Employee> found = employeeRepository.findByEmail("jane.smith@company.com");
        assertThat(found).isPresent();
        assertThat(found.get().getPassport()).isNull();
    }

    @Test
    @DisplayName("Should cascade delete passport when employee is deleted")
    void delete_CascadeDeletePassport() {
        Long passportId = employeeWithPassport.getPassport().getId();
        employeeRepository.delete(employeeWithPassport);

        Optional<Employee> foundEmployee = employeeRepository.findById(employeeWithPassport.getId());
        Optional<Passport> foundPassport = passportRepository.findById(passportId);

        assertThat(foundEmployee).isEmpty();
        assertThat(foundPassport).isEmpty(); // Proves cascade delete worked!
    }
}
