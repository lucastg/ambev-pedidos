package com.myproject.orders.application.adapters;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import com.myproject.orders.infrastructure.entity.PedidoEntity;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoPersistanceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoPersistance pedidoPersistance;

    private Pedido pedidoDomain;
    private PedidoEntity pedidoEntity;

    @BeforeEach
    void setUp() {
        pedidoDomain = MassaDeDadosFactory.criarExemploPedidoDomain(1L);
        pedidoEntity = MassaDeDadosFactory.criarExemploPedidoEntity(1L);
    }

    @Test
    @DisplayName("Deve listar pedidos paginados")
    void listarPedidos_DeveRetornarPedidosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PedidoEntity> pedidosEntitiesPage = new PageImpl<>(List.of(pedidoEntity), pageable, 1);
        Page<Pedido> expectedPedidosPage = new PageImpl<>(List.of(pedidoDomain), pageable, 1);

        when(pedidoRepository.findAll(pageable)).thenReturn(pedidosEntitiesPage);
        when(pedidoMapper.toDomain(any(PedidoEntity.class))).thenReturn(pedidoDomain);

        Page<Pedido> result = pedidoPersistance.listarPedidos(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(pedidoDomain.getId(), result.getContent().get(0).getId());
        assertEquals(pedidoDomain.getIdExterno(), result.getContent().get(0).getIdExterno());

        verify(pedidoRepository, times(1)).findAll(pageable);
        verify(pedidoMapper, times(1)).toDomain(any(PedidoEntity.class));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID e retornar Optional com Pedido se encontrado")
    void buscarPedidoPorId_DeveRetornarOptionalDePedidoQuandoEncontradoPorId() {
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedidoEntity));
        when(pedidoMapper.toDomain(pedidoEntity)).thenReturn(pedidoDomain);

        Optional<Pedido> result = pedidoPersistance.buscarPedidoPorId(id);

        assertTrue(result.isPresent());
        assertEquals(pedidoDomain.getId(), result.get().getId());
        assertEquals(pedidoDomain.getIdExterno(), result.get().getIdExterno());

        verify(pedidoRepository, times(1)).findById(id);
        verify(pedidoMapper, times(1)).toDomain(pedidoEntity);
    }

    @Test
    @DisplayName("Deve buscar pedido por ID e retornar Optional vazio se não encontrado")
    void buscarPedidoPorId_DeveRetornarOptionalVazioQuandoNaoEncontradoId() {
        Long id = 99L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Pedido> result = pedidoPersistance.buscarPedidoPorId(id);

        assertFalse(result.isPresent());

        verify(pedidoRepository, times(1)).findById(id);
        verify(pedidoMapper, times(0)).toDomain(any(PedidoEntity.class));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID Externo e retornar true se encontrado")
    void buscarPedidoPorIdExterno_DeveBuscarPorIdExternoERetornarVerdadeiroQuandoEncontrado() {
        String idExterno = "PEDIDO-EXT-TESTE-001";
        when(pedidoRepository.buscarPedidoPorIdExterno(idExterno)).thenReturn(Optional.of(pedidoEntity));

        boolean result = pedidoPersistance.buscarPedidoPorIdExterno(idExterno);
        assertTrue(result);

        verify(pedidoRepository, times(1)).buscarPedidoPorIdExterno(idExterno);
    }

    @Test
    @DisplayName("Deve buscar pedido por ID Externo e retornar false se não encontrado")
    void buscarPedidoPorIdExterno_DeveRetornarFalsoQuandoNaoEncontrado() {
        String idExterno = "PEDIDO-EXT-INEXISTENTE";
        when(pedidoRepository.buscarPedidoPorIdExterno(idExterno)).thenReturn(Optional.empty());

        boolean result = pedidoPersistance.buscarPedidoPorIdExterno(idExterno);
        assertFalse(result);

        verify(pedidoRepository, times(1)).buscarPedidoPorIdExterno(idExterno);
    }

    @Test
    @DisplayName("Deve salvar um pedido e retornar")
    void salvarPedido_DeveSalvarERetornarPedido() {
        when(pedidoMapper.toEntity(pedidoDomain)).thenReturn(pedidoEntity);
        when(pedidoRepository.save(pedidoEntity)).thenReturn(pedidoEntity);
        when(pedidoMapper.toDomain(pedidoEntity)).thenReturn(pedidoDomain);

        Pedido result = pedidoPersistance.salvarPedido(pedidoDomain);

        assertNotNull(result);
        assertEquals(pedidoDomain.getId(), result.getId());
        assertEquals(pedidoDomain.getIdExterno(), result.getIdExterno());

        verify(pedidoMapper, times(1)).toEntity(pedidoDomain);
        verify(pedidoRepository, times(1)).save(pedidoEntity);
        verify(pedidoMapper, times(1)).toDomain(pedidoEntity);
    }

    @Test
    @DisplayName("Deve deletar um pedido por ID")
    void deletarPedido_DeveDeletarPedidoPorID() {
        Long id = 1L;
        doNothing().when(pedidoRepository).deleteById(id);

        pedidoPersistance.deletarPedido(id);

        verify(pedidoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve buscar um pedido por ID e retornar true")
    void existsById_DeveBuscarPedidoPorIdERetornarVerdadeiro() {
        Long id = 1L;

        when(pedidoRepository.existsById(id)).thenReturn(true);

        boolean result = pedidoPersistance.existsById(id);
        assertTrue(result);

        verify(pedidoRepository, times(1)).existsById(id);
    }

    @Test
    @DisplayName("Deve buscar um pedido por ID e retornar false")
    void existsById_DeveRetornarFalsoQuandoNaoEncontrado() {
        Long id = 99L;

        when(pedidoRepository.existsById(id)).thenReturn(false);

        boolean result = pedidoPersistance.existsById(id);
        assertFalse(result);

        verify(pedidoRepository, times(1)).existsById(id);
    }
}
