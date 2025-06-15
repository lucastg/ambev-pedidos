package com.myproject.orders.domain.ports.in;

import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import org.springframework.messaging.handler.annotation.Payload;

public interface PedidoQueueInPort {

    void receiveMessage(@Payload PedidoRequestDto pedidoRequestDto);
}
