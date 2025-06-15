package com.myproject.orders.infrastructure.messaging.in;

import com.myproject.orders.application.config.RabbitMQConfig;
import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.domain.ports.in.PedidoQueueInPort;
import com.myproject.orders.domain.useCases.PedidoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PedidoQueueIn implements PedidoQueueInPort {

    @Autowired
    private PedidoUseCase pedidoUseCase;

    @Autowired
    private PedidoMapper pedidoMapper;

    private static final Logger logger = LoggerFactory.getLogger(PedidoQueueIn.class);


    @RabbitListener(queues = RabbitMQConfig.QUEUE_PEDIDOS_ENTRADA)
    @Override
    public void receiveMessage(@Payload PedidoRequestDto pedidoRequestDto) {
        logger.info("Recebido pedido do Produto Externo: {}", pedidoRequestDto.getIdExterno());
        try {
            pedidoUseCase.processarPedidoRecebido(pedidoRequestDto);
            logger.info("Pedido com ID Externo {} processado com sucesso.", pedidoRequestDto.getIdExterno());

        } catch (Exception ex) {
            var errorMessage = "Houve um problema ao receber o pedido: " + ex.getMessage();
            logger.error(errorMessage, ex);

            throw new RuntimeException(errorMessage);
        }
    }
}
