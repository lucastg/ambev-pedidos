package com.myproject.orders.application.presenters.mappers;

import com.myproject.orders.application.presenters.requests.ItemRequestDto;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Item;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import com.myproject.orders.infrastructure.entity.ItemEntity;
import com.myproject.orders.infrastructure.entity.PedidoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PedidoMapperTest {

    private PedidoMapper pedidoMapper;

    @BeforeEach
    void setUp() {
        pedidoMapper = new PedidoMapper();
    }


    @Test
    @DisplayName("Deve mapear PedidoRequestDto para Pedido de domínio com itens")
    void toDomain_PedidoRequestDto_DeveMapearComItens() {
        ItemRequestDto itemRequestDto = MassaDeDadosFactory.criarItemRequestDto("PROD-REQ-001", new BigDecimal("10.00"), 2);
        PedidoRequestDto pedidoRequestDto = MassaDeDadosFactory.criarPedidoRequestDto("EXT-001", Collections.singletonList(itemRequestDto));

        Pedido pedido = pedidoMapper.toDomain(pedidoRequestDto);

        assertNotNull(pedido);
        assertEquals(pedidoRequestDto.getIdExterno(), pedido.getIdExterno());
        assertThat(pedido.getItens()).hasSize(1);
        assertEquals(itemRequestDto.getProdutoId(), pedido.getItens().get(0).getProdutoId());
        assertEquals(itemRequestDto.getValorUnitario(), pedido.getItens().get(0).getValorUnitario());
        assertEquals(itemRequestDto.getQuantidade(), pedido.getItens().get(0).getQuantidade());
    }

    @Test
    @DisplayName("Deve mapear PedidoRequestDto para Pedido de domínio com lista de itens nula")
    void toDomain_PedidoRequestDto_DeveMapearComItensNulos() {
        PedidoRequestDto pedidoRequestDto = MassaDeDadosFactory.criarPedidoRequestDto("EXT-002", null);

        Pedido pedido = pedidoMapper.toDomain(pedidoRequestDto);

        assertNotNull(pedido);
        assertEquals(pedidoRequestDto.getIdExterno(), pedido.getIdExterno());
        assertThat(pedido.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear PedidoRequestDto para Pedido de domínio com lista de itens vazia")
    void toDomain_PedidoRequestDto_DeveMapearComItensVazia() {
        PedidoRequestDto pedidoRequestDto = MassaDeDadosFactory.criarPedidoRequestDto("EXT-003", new ArrayList<>());

        Pedido pedido = pedidoMapper.toDomain(pedidoRequestDto);

        assertNotNull(pedido);
        assertEquals(pedidoRequestDto.getIdExterno(), pedido.getIdExterno());
        assertThat(pedido.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoEntity com itens")
    void toEntity_Pedido_DeveMapearComItens() {
        Item itemDomain = MassaDeDadosFactory.criarItemDomain(1L, "PROD-DOM-001", new BigDecimal("20.00"), 3);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(1L, "EXT-DOM-001", new BigDecimal("60.00"), StatusPedido.PROCESSANDO, Collections.singletonList(itemDomain));
        itemDomain.setPedido(pedido);

        PedidoEntity pedidoEntity = pedidoMapper.toEntity(pedido);

        assertNotNull(pedidoEntity);
        assertEquals(pedido.getId(), pedidoEntity.getId());
        assertEquals(pedido.getIdExterno(), pedidoEntity.getIdExterno());
        assertEquals(pedido.getStatus(), pedidoEntity.getStatus());
        assertEquals(pedido.getValorTotal(), pedidoEntity.getValorTotal());
        assertThat(pedidoEntity.getItens()).hasSize(1);
        assertEquals(itemDomain.getProdutoId(), pedidoEntity.getItens().get(0).getProdutoId());
        assertEquals(itemDomain.getValorUnitario(), pedidoEntity.getItens().get(0).getValorUnitario());
        assertEquals(itemDomain.getQuantidade(), pedidoEntity.getItens().get(0).getQuantidade());
        assertEquals(itemDomain.getValorTotalItem(), pedidoEntity.getItens().get(0).getValorTotalItem());
        assertEquals(pedidoEntity, pedidoEntity.getItens().get(0).getPedido());
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoEntity com lista de itens nula")
    void toEntity_Pedido_DeveMapearComItensNula() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(2L, "EXT-DOM-002", new BigDecimal("0.00"), StatusPedido.PROCESSANDO, null);

        PedidoEntity pedidoEntity = pedidoMapper.toEntity(pedido);

        assertNotNull(pedidoEntity);
        assertEquals(pedido.getIdExterno(), pedidoEntity.getIdExterno());
        assertThat(pedidoEntity.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoEntity com lista de itens vazia")
    void toEntity_Pedido_DeveMapearComItensVazia() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(3L, "EXT-DOM-003", new BigDecimal("0.00"), StatusPedido.PROCESSANDO, new ArrayList<>());

        PedidoEntity pedidoEntity = pedidoMapper.toEntity(pedido);

        assertNotNull(pedidoEntity);
        assertEquals(pedido.getIdExterno(), pedidoEntity.getIdExterno());
        assertThat(pedidoEntity.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para Pedido de domínio com itens")
    void toDomain_PedidoEntity_DeveMapearComItens() {
        ItemEntity itemEntity = MassaDeDadosFactory.criarItemEntity(10L, "PROD-ENT-001", new BigDecimal("30.00"), 1);
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(10L, "EXT-ENT-001", new BigDecimal("30.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemEntity));
        itemEntity.setPedido(pedidoEntity);

        Pedido pedido = pedidoMapper.toDomain(pedidoEntity);

        assertNotNull(pedido);
        assertEquals(pedidoEntity.getId(), pedido.getId());
        assertEquals(pedidoEntity.getIdExterno(), pedido.getIdExterno());
        assertEquals(pedidoEntity.getStatus(), pedido.getStatus());
        assertEquals(pedidoEntity.getValorTotal(), pedido.getValorTotal());
        assertEquals(pedidoEntity.getCreatedAt(), pedido.getCreatedAt());
        assertEquals(pedidoEntity.getUpdatedAt(), pedido.getUpdatedAt());
        assertThat(pedido.getItens()).hasSize(1);
        assertEquals(itemEntity.getProdutoId(), pedido.getItens().get(0).getProdutoId());
        assertEquals(itemEntity.getValorUnitario(), pedido.getItens().get(0).getValorUnitario());
        assertEquals(itemEntity.getQuantidade(), pedido.getItens().get(0).getQuantidade());
        assertEquals(itemEntity.getValorTotalItem(), pedido.getItens().get(0).getValorTotalItem());
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para Pedido de domínio com lista de itens nula")
    void toDomain_PedidoEntity_DeveMapearComItensNula() {
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(11L, "EXT-ENT-002", new BigDecimal("0.00"), StatusPedido.PROCESSADO, null);

        Pedido pedido = pedidoMapper.toDomain(pedidoEntity);

        assertNotNull(pedido);
        assertEquals(pedidoEntity.getIdExterno(), pedido.getIdExterno());
        assertThat(pedido.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para Pedido de domínio com lista de itens vazia")
    void toDomain_PedidoEntity_DeveMapearComItensVazia() {
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(12L, "EXT-ENT-003", new BigDecimal("0.00"), StatusPedido.PROCESSANDO, new ArrayList<>());

        Pedido pedido = pedidoMapper.toDomain(pedidoEntity);

        assertNotNull(pedido);
        assertEquals(pedidoEntity.getIdExterno(), pedido.getIdExterno());
        assertThat(pedido.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para PedidoResponseDto com itens")
    void toResponseDto_PedidoEntity_DeveMapearComItens() {
        ItemEntity itemEntity = MassaDeDadosFactory.criarItemEntity(20L, "PROD-RES-001", new BigDecimal("40.00"), 2);
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(20L, "EXT-RES-001", new BigDecimal("80.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemEntity));
        itemEntity.setPedido(pedidoEntity);

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedidoEntity);

        assertNotNull(responseDto);
        assertEquals(pedidoEntity.getId(), responseDto.getId());
        assertEquals(pedidoEntity.getIdExterno(), responseDto.getIdExterno());
        assertEquals(pedidoEntity.getStatus(), responseDto.getStatus());
        assertEquals(pedidoEntity.getValorTotal(), responseDto.getValorTotal());
        assertEquals(pedidoEntity.getCreatedAt(), responseDto.getCreatedAt());
        assertEquals(pedidoEntity.getUpdatedAt(), responseDto.getUpdatedAt());
        assertThat(responseDto.getItens()).hasSize(1);
        assertEquals(itemEntity.getProdutoId(), responseDto.getItens().get(0).getProdutoId());
        assertEquals(itemEntity.getValorUnitario(), responseDto.getItens().get(0).getValorUnitario());
        assertEquals(itemEntity.getQuantidade(), responseDto.getItens().get(0).getQuantidade());
        assertEquals(itemEntity.getValorTotalItem(), responseDto.getItens().get(0).getValorTotalItem());
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para PedidoResponseDto com lista de itens nula")
    void toResponseDto_PedidoEntity_DeveMapearComItensNula() {
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(21L, "EXT-RES-002", new BigDecimal("0.00"), StatusPedido.PROCESSADO, null);

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedidoEntity);

        assertNotNull(responseDto);
        assertEquals(pedidoEntity.getIdExterno(), responseDto.getIdExterno());
        assertThat(responseDto.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear PedidoEntity para PedidoResponseDto com lista de itens vazia")
    void toResponseDto_PedidoEntity_DeveMapearComItensVazia() {
        PedidoEntity pedidoEntity = MassaDeDadosFactory.criarPedidoEntity(22L, "EXT-RES-003", new BigDecimal("0.00"), StatusPedido.PROCESSADO, new ArrayList<>());

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedidoEntity);

        assertNotNull(responseDto);
        assertEquals(pedidoEntity.getIdExterno(), responseDto.getIdExterno());
        assertThat(responseDto.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoResponseDto com itens")
    void toResponseDto_Pedido_DeveMapearComItens() {
        Item itemDomain = MassaDeDadosFactory.criarItemDomain(30L, "PROD-DOM-RES-001", new BigDecimal("50.00"), 1);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(30L, "EXT-DOM-RES-001", new BigDecimal("50.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemDomain));
        itemDomain.setPedido(pedido);

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedido);

        assertNotNull(responseDto);
        assertEquals(pedido.getId(), responseDto.getId());
        assertEquals(pedido.getIdExterno(), responseDto.getIdExterno());
        assertEquals(pedido.getStatus(), responseDto.getStatus());
        assertEquals(pedido.getValorTotal(), responseDto.getValorTotal());
        assertEquals(pedido.getCreatedAt(), responseDto.getCreatedAt());
        assertEquals(pedido.getUpdatedAt(), responseDto.getUpdatedAt());
        assertThat(responseDto.getItens()).hasSize(1);
        assertEquals(itemDomain.getProdutoId(), responseDto.getItens().get(0).getProdutoId());
        assertEquals(itemDomain.getValorUnitario(), responseDto.getItens().get(0).getValorUnitario());
        assertEquals(itemDomain.getQuantidade(), responseDto.getItens().get(0).getQuantidade());
        assertEquals(itemDomain.getValorTotalItem(), responseDto.getItens().get(0).getValorTotalItem());
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoResponseDto com lista de itens nula")
    void toResponseDto_Pedido_DeveMapearComItensNula() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(31L, "EXT-DOM-RES-002", new BigDecimal("0.00"), StatusPedido.PROCESSANDO, null);

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedido);

        assertNotNull(responseDto);
        assertEquals(pedido.getIdExterno(), responseDto.getIdExterno());
        assertThat(responseDto.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear Pedido de domínio para PedidoResponseDto com lista de itens vazia")
    void toResponseDto_Pedido_DeveMapearComItensVazia() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(32L, "EXT-DOM-RES-003", new BigDecimal("0.00"), StatusPedido.PROCESSANDO, new ArrayList<>());

        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedido);

        assertNotNull(responseDto);
        assertEquals(pedido.getIdExterno(), responseDto.getIdExterno());
        assertThat(responseDto.getItens()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear uma lista de PedidoEntity para uma lista de Pedido de domínio")
    void toDomain_ListOfPedidoEntity_ListaDePedidoEntidadeParaDominio() {
        PedidoEntity pedidoEntity1 = MassaDeDadosFactory.criarExemploPedidoEntity(40L);
        PedidoEntity pedidoEntity2 = MassaDeDadosFactory.criarExemploPedidoEntity(41L);
        List<PedidoEntity> pedidoEntities = Arrays.asList(pedidoEntity1, pedidoEntity2);

        List<Pedido> pedidos = pedidoMapper.toDomain(pedidoEntities);

        assertNotNull(pedidos);
        assertThat(pedidos).hasSize(2);
        assertEquals(pedidoEntity1.getId(), pedidos.get(0).getId());
        assertEquals(pedidoEntity2.getId(), pedidos.get(1).getId());
    }

    @Test
    @DisplayName("Deve mapear uma lista nula de PedidoEntity para uma lista vazia de Pedido de domínio")
    void toDomain_ListOfPedidoEntity_DeveMapearNulaDePedidoEntidadeParaDominio() {
        List<Pedido> pedidos = pedidoMapper.toDomain((List<PedidoEntity>) null);

        assertNotNull(pedidos);
        assertThat(pedidos).isEmpty();
    }

    @Test
    @DisplayName("Deve mapear uma lista vazia de PedidoEntity para uma lista vazia de Pedido de domínio")
    void toDomain_ListOfPedidoEntity_DeveMapearVaziaDePedidoEntidadeParaDominio() {
        List<Pedido> pedidos = pedidoMapper.toDomain(new ArrayList<>());

        assertNotNull(pedidos);
        assertThat(pedidos).isEmpty();
    }
}
