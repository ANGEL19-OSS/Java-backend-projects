package com.company.epm.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportRequestDTO {

    @NotBlank(message = "Passport number is mandatory")
    @Size(min = 5, max = 50, message = "Passport number must be between 5 and 50 characters")
    private String passportNumber;

    @NotBlank(message = "Country is mandatory")
    private String country;

    @NotNull(message = "Expiry date is mandatory")
    @Future(message = "Passport expiry date must be in the future")
    private LocalDate expiryDate;
}
