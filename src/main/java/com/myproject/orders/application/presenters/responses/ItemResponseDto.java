package com.myproject.orders.application.presenters.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private String produtoId;
    private BigDecimal valorUnitario;
    private int quantidade;
    private BigDecimal valorTotalItem;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
