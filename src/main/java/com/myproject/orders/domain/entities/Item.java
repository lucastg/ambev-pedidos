package com.myproject.orders.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    private String produtoId;
    private BigDecimal valorUnitario;
    private int quantidade;
    private BigDecimal valorTotalItem;
    private Pedido pedido;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
