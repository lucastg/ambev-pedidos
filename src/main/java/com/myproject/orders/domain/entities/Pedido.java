package com.myproject.orders.domain.entities;

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
public class Pedido {
    private Long id;
    private String idExterno;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<Item> itens;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean processandoPedido() {
        return this.getStatus() == StatusPedido.PROCESSANDO;
    }
}
