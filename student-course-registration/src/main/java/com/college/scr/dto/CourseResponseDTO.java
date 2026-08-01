package com.college.scr.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {

    private Long id;
    private String courseCode;
    private String title;
    private Integer credits;
    private LocalDateTime createdAt;
}
