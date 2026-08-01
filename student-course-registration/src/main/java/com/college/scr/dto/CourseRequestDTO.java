package com.college.scr.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    @NotBlank(message = "Course code is mandatory")
    @Size(min = 2, max = 50, message = "Course code must be between 2 and 50 characters")
    private String courseCode;

    @NotBlank(message = "Course title is mandatory")
    private String title;

    @NotNull(message = "Credits are mandatory")
    @Min(value = 1, message = "Course credits must be at least 1")
    private Integer credits;
}
