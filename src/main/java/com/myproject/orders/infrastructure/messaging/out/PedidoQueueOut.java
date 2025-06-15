package com.myproject.orders.infrastructure.messaging.out;

import com.myproject.orders.application.config.RabbitMQConfig;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.ports.out.PedidoQueueOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoQueueOut implements PedidoQueueOutPort {

    private static final Logger logger = LoggerFactory.getLogger(PedidoQueueOut.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void publishMessage(PedidoResponseDto pedidoResponseDto) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, RabbitMQConfig.ROUTING_KEY_SAIDA, pedidoResponseDto);
            logger.info("Pedido {} (ID Externo: {}) enviado com sucesso para o Produto Externo B.", pedidoResponseDto.getId(), pedidoResponseDto.getIdExterno());

        } catch (Exception e) {
            logger.error("Falha ao enviar pedido {} (ID Externo: {}) para o RabbitMQ: {}", pedidoResponseDto.getId(), pedidoResponseDto.getIdExterno(), e.getMessage(), e);
            throw new RuntimeException("Erro ao publicar mensagem para o RabbitMQ", e);
        }
    }
}
