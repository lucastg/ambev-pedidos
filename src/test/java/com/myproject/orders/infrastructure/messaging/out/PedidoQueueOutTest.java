package com.myproject.orders.infrastructure.messaging.out;

import com.myproject.orders.application.config.RabbitMQConfig;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoQueueOutTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidoQueueOut pedidoQueueOut;

    private PedidoRequestDto pedidoRequestDto;
    private Pedido pedidoDomain;
    private PedidoResponseDto pedidoResponseDto;

    @BeforeEach
    void setUp() {
        pedidoRequestDto = MassaDeDadosFactory.criarExemploPedidoRequestDto("PEDIDO-EXT-001");
        pedidoDomain = MassaDeDadosFactory.criarExemploPedidoDomain(1L);
        pedidoResponseDto = MassaDeDadosFactory.criarExemploPedidoResponseDto(1L, "PEDIDO-EXT-001");
    }

    @Test
    @DisplayName("Deve publicar a mensagem com sucesso quando nenhuma exceção ocorrer")
    void publishMessage_DevePublicarComSucesso() {
        pedidoQueueOut.publishMessage(pedidoResponseDto);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_PEDIDOS),
                eq(RabbitMQConfig.ROUTING_KEY_SAIDA),
                eq(pedidoResponseDto)
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando a publicação da mensagem falhar")
    void publishMessage_DeveLancarExcecao() {
        String errorMessage = "Erro simulado na publicação da mensagem no RabbitMQ";
        doThrow(new AmqpException(errorMessage)).when(rabbitTemplate).convertAndSend(
                any(String.class),
                any(String.class),
                any(PedidoResponseDto.class)
        );

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            pedidoQueueOut.publishMessage(pedidoResponseDto);
        });

        assertThat(thrown.getMessage())
                .contains("Erro ao publicar mensagem para o RabbitMQ");
        assertThat(thrown.getCause())
                .isInstanceOf(AmqpException.class)
                .hasMessageContaining(errorMessage);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_PEDIDOS),
                eq(RabbitMQConfig.ROUTING_KEY_SAIDA),
                eq(pedidoResponseDto)
        );
    }
}
