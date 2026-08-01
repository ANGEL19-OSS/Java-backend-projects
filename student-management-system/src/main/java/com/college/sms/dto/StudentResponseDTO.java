package com.college.sms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String department;
    private Integer yearOfStudy;
    private Double cgpa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
