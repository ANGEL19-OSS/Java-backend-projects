package com.shopping.cart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private Long userId;
    private String userName;
    private List<CartItemResponseDTO> items;
    private int totalItems;
    private BigDecimal totalAmount;
}
