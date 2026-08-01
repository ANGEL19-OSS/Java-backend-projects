package com.company.epm.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String department;
    private PassportResponseDTO passport;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
