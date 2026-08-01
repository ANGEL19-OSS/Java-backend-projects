package com.college.scr.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String department;
    private Set<CourseResponseDTO> courses;
    private LocalDateTime createdAt;
}
