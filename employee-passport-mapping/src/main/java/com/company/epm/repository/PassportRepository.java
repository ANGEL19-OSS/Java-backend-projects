package com.company.epm.repository;

import com.company.epm.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {

    boolean existsByPassportNumber(String passportNumber);

    boolean existsByPassportNumberAndIdNot(String passportNumber, Long id);

    Optional<Passport> findByPassportNumber(String passportNumber);
}
