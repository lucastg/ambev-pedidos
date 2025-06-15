package com.myproject.orders.application.presenters.responses;

import com.myproject.orders.domain.enums.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponseDto {
    private Long id;
    private String idExterno;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemResponseDto> itens;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
