package com.myproject.orders.infrastructure.messaging.in;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import com.myproject.orders.domain.useCases.PedidoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoQueueInTest {

    @Mock
    private PedidoUseCase pedidoUseCase;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoQueueIn pedidoQueueIn;

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
    @DisplayName("Deve processar o pedido com sucesso quando nenhuma exceção ocorrer")
    void receiveMessage_DeveProcessarComSucesso() {
        pedidoQueueIn.receiveMessage(pedidoRequestDto);
        verify(pedidoUseCase, times(1)).processarPedidoRecebido(pedidoRequestDto);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando PedidoUseCase.processarPedidoRecebido falhar")
    void receiveMessage_DeveLancarExcecao() throws Exception {
        String errorMessage = "Erro simulado no processamento do pedido";
        doThrow(new RuntimeException(errorMessage)).when(pedidoUseCase).processarPedidoRecebido(any(PedidoRequestDto.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            pedidoQueueIn.receiveMessage(pedidoRequestDto);
        });

        assertThat(thrown.getMessage()).contains("Houve um problema ao receber o pedido:");
        assertThat(thrown.getMessage()).contains(errorMessage);
        verify(pedidoUseCase, times(1)).processarPedidoRecebido(pedidoRequestDto);
    }
}
