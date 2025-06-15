package com.myproject.orders.domain.ports.out;

import com.myproject.orders.application.presenters.responses.PedidoResponseDto;

public interface PedidoQueueOutPort {
    void publishMessage(PedidoResponseDto pedidoResponseDto);
}
