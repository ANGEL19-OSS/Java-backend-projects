package com.company.epm.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportResponseDTO {

    private Long id;
    private String passportNumber;
    private String country;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
}
