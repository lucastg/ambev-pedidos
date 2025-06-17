package com.myproject.orders.domain.useCases;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import com.myproject.orders.domain.exception.PedidoDuplicadoException;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import com.myproject.orders.domain.helpers.CalculadoraPedidoService;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import com.myproject.orders.domain.ports.out.PedidoPersistancePort;
import com.myproject.orders.domain.ports.out.PedidoQueueOutPort;
import com.myproject.orders.infrastructure.repositories.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoUseCaseTest {
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private PedidoQueueOutPort pedidoQueueOutPort;

    @Mock
    private CalculadoraPedidoService calculadoraPedidoService;

    @Mock
    private PedidoPersistancePort pedidoPersistancePort;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private PedidoRequestDto pedidoRequestDto;
    private Pedido pedidoDomain;
    private PedidoResponseDto pedidoResponseDto;

    @BeforeEach
    void setUp() {
        pedidoDomain = MassaDeDadosFactory.criarExemploPedidoDomain(1L);
        pedidoRequestDto = MassaDeDadosFactory.criarExemploPedidoRequestDto("PEDIDO-EXT-1");
        pedidoResponseDto = MassaDeDadosFactory.criarExemploPedidoResponseDto(1L, "PEDIDO-EXT-1");
    }

    @Test
    @DisplayName("Deve processar um pedido recebido com sucesso")
    void processarPedidoRecebido_DeveProcessarUmPedidoComSucesso() {
        Pedido pedidoRequest = MassaDeDadosFactory.criarPedidoDomain(null, pedidoRequestDto.getIdExterno(), BigDecimal.ZERO, null,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(null, "PROD-A", new BigDecimal("10.00"), 2)));

        Pedido pedidoSalvo = MassaDeDadosFactory.criarPedidoDomain(1L, pedidoRequestDto.getIdExterno(), new BigDecimal("20.00"), StatusPedido.PROCESSANDO,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(1L, "PROD-A", new BigDecimal("10.00"), 2)));

        Pedido finalProcessedPedido = MassaDeDadosFactory.criarPedidoDomain(1L, pedidoRequestDto.getIdExterno(), new BigDecimal("20.00"), StatusPedido.PROCESSADO,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(1L, "PROD-A", new BigDecimal("10.00"), 2)));


        when(pedidoMapper.toDomain(pedidoRequestDto)).thenReturn(pedidoRequest);
        when(pedidoPersistancePort.buscarPedidoPorIdExterno(anyString())).thenReturn(false);
        when(calculadoraPedidoService.calcularValorTotalPedido(any(Pedido.class))).thenReturn(new BigDecimal("20.00"));

        when(pedidoPersistancePort.salvarPedido(pedidoRequest)).thenReturn(pedidoSalvo);
        when(pedidoPersistancePort.salvarPedido(pedidoSalvo)).thenReturn(finalProcessedPedido);

        when(pedidoMapper.toResponseDto(finalProcessedPedido)).thenReturn(pedidoResponseDto);
        doNothing().when(pedidoQueueOutPort).publishMessage(pedidoResponseDto);

        Pedido result = pedidoUseCase.processarPedidoRecebido(pedidoRequestDto);

        assertNotNull(result);
        assertEquals(pedidoRequestDto.getIdExterno(), result.getIdExterno());
        assertEquals(StatusPedido.PROCESSADO, result.getStatus());
        assertEquals(new BigDecimal("20.00"), result.getValorTotal());

        verify(pedidoMapper, times(1)).toDomain(pedidoRequestDto);
        verify(pedidoPersistancePort, times(1)).buscarPedidoPorIdExterno(pedidoRequestDto.getIdExterno());
        verify(calculadoraPedidoService, times(1)).calcularValorTotalPedido(pedidoRequest);
        verify(pedidoPersistancePort, times(1)).salvarPedido(pedidoRequest);
        verify(pedidoPersistancePort, times(1)).salvarPedido(pedidoSalvo);
        verify(pedidoMapper, times(1)).toResponseDto(finalProcessedPedido);
        verify(pedidoQueueOutPort, times(1)).publishMessage(pedidoResponseDto);
    }

    @Test
    @DisplayName("Deve lançar PedidoDuplicadoException se o ID externo já existir")
    void processarPedidoRecebido_DeveLancarPedidoDuplicadoException() {
        when(pedidoMapper.toDomain(pedidoRequestDto)).thenReturn(pedidoDomain);
        when(pedidoPersistancePort.buscarPedidoPorIdExterno(anyString())).thenReturn(true);

        assertThrows(PedidoDuplicadoException.class, () ->
                pedidoUseCase.processarPedidoRecebido(pedidoRequestDto));

        verify(pedidoMapper, times(1)).toDomain(pedidoRequestDto);
        verify(pedidoPersistancePort, times(1)).buscarPedidoPorIdExterno(pedidoRequestDto.getIdExterno());
        verify(calculadoraPedidoService, never()).calcularValorTotalPedido(any(Pedido.class));
        verify(pedidoPersistancePort, never()).salvarPedido(any(Pedido.class));
        verify(pedidoQueueOutPort, never()).publishMessage(any(PedidoResponseDto.class));
    }

    @Test
    @DisplayName("processarPedidoCore deve calcular, salvar e publicar o pedido")
    void processarPedidoCore_DeveSalvarPedido() {
        BigDecimal calculatedValue = new BigDecimal("30.00");

        Pedido initialPedidoForCore = MassaDeDadosFactory.criarPedidoDomain(null, "EXT-CORE-001", BigDecimal.ZERO, null,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(null, "PROD-B", new BigDecimal("30.00"), 1)));

        Pedido firstSavedPedido = MassaDeDadosFactory.criarPedidoDomain(2L, "EXT-CORE-001", calculatedValue, StatusPedido.PROCESSANDO,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(2L, "PROD-B", new BigDecimal("30.00"), 1)));

        Pedido finalProcessedPedido = MassaDeDadosFactory.criarPedidoDomain(2L, "EXT-CORE-001", calculatedValue, StatusPedido.PROCESSADO,
                Collections.singletonList(MassaDeDadosFactory.criarItemDomain(2L, "PROD-B", new BigDecimal("30.00"), 1)));

        PedidoResponseDto expectedResponseDto = MassaDeDadosFactory.criarExemploPedidoResponseDto(2L, "EXT-CORE-001");

        when(calculadoraPedidoService.calcularValorTotalPedido(any(Pedido.class))).thenReturn(calculatedValue);
        when(pedidoPersistancePort.salvarPedido(initialPedidoForCore)).thenReturn(firstSavedPedido);
        when(pedidoPersistancePort.salvarPedido(firstSavedPedido)).thenReturn(finalProcessedPedido);
        when(pedidoMapper.toResponseDto(finalProcessedPedido)).thenReturn(expectedResponseDto);
        doNothing().when(pedidoQueueOutPort).publishMessage(expectedResponseDto);

        Pedido result = pedidoUseCase.processarPedidoCore(initialPedidoForCore);

        assertNotNull(result);
        assertEquals(calculatedValue, result.getValorTotal());
        assertEquals(StatusPedido.PROCESSADO, result.getStatus());

        verify(calculadoraPedidoService, times(1)).calcularValorTotalPedido(initialPedidoForCore);
        verify(pedidoPersistancePort, times(1)).salvarPedido(initialPedidoForCore);
        verify(pedidoPersistancePort, times(1)).salvarPedido(firstSavedPedido);
        verify(pedidoMapper, times(1)).toResponseDto(finalProcessedPedido);
        verify(pedidoQueueOutPort, times(1)).publishMessage(expectedResponseDto);
    }

    @Test
    @DisplayName("Deve listar pedidos paginados e retornar Page de Pedido")
    void listarPedidos_DeveListarPedidos_RetornarPedidoPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> expectedPage = new PageImpl<>(Collections.singletonList(pedidoDomain), pageable, 1);

        when(pedidoPersistancePort.listarPedidos(any(Pageable.class))).thenReturn(expectedPage);

        Page<Pedido> result = pedidoUseCase.listarPedidos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(pedidoDomain.getIdExterno(), result.getContent().get(0).getIdExterno());

        verify(pedidoPersistancePort, times(1)).listarPedidos(pageable);
    }

    @Test
    @DisplayName("Deve relançar exceção ao listar pedidos se a persistência falhar")
    void listarPedidos_DeveRetornarExceptionQuandoPersistenciaFalhar() {
        Pageable pageable = PageRequest.of(0, 10);
        doThrow(new RuntimeException("Erro de DB")).when(pedidoPersistancePort).listarPedidos(any(Pageable.class));

        assertThrows(RuntimeException.class, () ->
                pedidoUseCase.listarPedidos(pageable));

        verify(pedidoPersistancePort, times(1)).listarPedidos(pageable);
    }

    @Test
    @DisplayName("Deve buscar pedido por ID e retornar Pedido se encontrado")
    void buscarPedidoPorId_DeveRetornarPedidoPorIdExterno() {
        Long pedidoId = 1L;
        when(pedidoPersistancePort.buscarPedidoPorId(pedidoId)).thenReturn(Optional.of(pedidoDomain));

        Pedido result = pedidoUseCase.buscarPedidoPorId(pedidoId);

        assertNotNull(result);
        assertEquals(pedidoId, result.getId());
        assertEquals(pedidoDomain.getIdExterno(), result.getIdExterno());

        verify(pedidoPersistancePort, times(1)).buscarPedidoPorId(pedidoId);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontratoException ao buscar pedido por ID inexistente")
    void buscarPedidoPorId_DeveRetornarRecursoNaoEncontratoExceptionQuandoNaoEncontrado() {
        Long pedidoId = 99L;
        when(pedidoPersistancePort.buscarPedidoPorId(pedidoId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontratoException.class, () ->
                pedidoUseCase.buscarPedidoPorId(pedidoId));

        verify(pedidoPersistancePort, times(1)).buscarPedidoPorId(pedidoId);
    }

    @Test
    @DisplayName("Deve relançar exceção ao buscar pedido por ID se a persistência falhar")
    void buscarPedidoPorId_DeveRetornarExceptionQuandoPersistenciaFalhar() {
        Long pedidoId = 1L;
        doThrow(new RuntimeException("Erro inesperado no DB")).when(pedidoPersistancePort).buscarPedidoPorId(pedidoId);

        assertThrows(RuntimeException.class, () ->
                pedidoUseCase.buscarPedidoPorId(pedidoId));

        verify(pedidoPersistancePort, times(1)).buscarPedidoPorId(pedidoId);
    }

    @Test
    @DisplayName("Deve deletar um pedido com sucesso se ele existir")
    void deletarPedido_DeveDeletarPedidoComSucesso() {
        Long pedidoId = 1L;
        when(pedidoPersistancePort.existsById(pedidoId)).thenReturn(true);
        doNothing().when(pedidoPersistancePort).deletarPedido(pedidoId);

        pedidoUseCase.deletarPedido(pedidoId);

        verify(pedidoPersistancePort, times(1)).existsById(pedidoId);
        verify(pedidoPersistancePort, times(1)).deletarPedido(pedidoId);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontratoException ao tentar deletar pedido inexistente")
    void deletarPedido_DeveRetornarRecursoNaoEncontratoException() {
        Long pedidoId = 99L;
        when(pedidoPersistancePort.existsById(pedidoId)).thenReturn(false);

        assertThrows(RecursoNaoEncontratoException.class, () ->
                pedidoUseCase.deletarPedido(pedidoId));

        verify(pedidoPersistancePort, times(1)).existsById(pedidoId);
        verify(pedidoPersistancePort, never()).deletarPedido(any(Long.class));
    }
}
