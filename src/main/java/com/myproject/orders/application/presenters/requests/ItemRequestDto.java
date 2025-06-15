package com.myproject.orders.application.presenters.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private String produtoId;
    private BigDecimal valorUnitario;
    private int quantidade;
}
