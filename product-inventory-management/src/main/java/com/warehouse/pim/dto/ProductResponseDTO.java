package com.warehouse.pim.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
