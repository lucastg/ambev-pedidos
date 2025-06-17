package com.myproject.orders.domain.helpers;

import com.myproject.orders.application.presenters.requests.ItemRequestDto;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.ItemResponseDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Item;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import com.myproject.orders.infrastructure.entity.ItemEntity;
import com.myproject.orders.infrastructure.entity.PedidoEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MassaDeDadosFactory {

    public static Item criarItemDomain(Long id, String produtoId, BigDecimal valorUnitario, int quantidade) {
        BigDecimal valorUnitarioCalculo = Objects.nonNull(valorUnitario) ? valorUnitario : BigDecimal.ZERO;
        BigDecimal valorTotalItemCalculado = valorUnitarioCalculo.multiply(BigDecimal.valueOf(quantidade));
        Item item = new Item();
        item.setId(id);
        item.setProdutoId(produtoId);
        item.setValorUnitario(valorUnitario);
        item.setQuantidade(quantidade);
        item.setValorTotalItem(valorTotalItemCalculado);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }

    public static Pedido criarPedidoDomain(Long id, String idExterno, BigDecimal valorTotal, StatusPedido status, List<Item> itens) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setIdExterno(idExterno);
        pedido.setValorTotal(valorTotal);
        pedido.setStatus(status);
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setUpdatedAt(LocalDateTime.now());
        pedido.setItens(itens);

        if (itens != null) {
            itens.forEach(item -> item.setPedido(pedido));
        }
        return pedido;
    }

    public static ItemEntity criarItemEntity(Long id, String produtoId, BigDecimal valorUnitario, int quantidade) {
        BigDecimal valorTotalItem = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(id);
        itemEntity.setProdutoId(produtoId);
        itemEntity.setValorUnitario(valorUnitario);
        itemEntity.setQuantidade(quantidade);
        itemEntity.setValorTotalItem(valorTotalItem);
        return itemEntity;
    }

    public static PedidoEntity criarPedidoEntity(Long id, String idExterno, BigDecimal valorTotal, StatusPedido status, List<ItemEntity> itens) {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(id);
        pedidoEntity.setIdExterno(idExterno);
        pedidoEntity.setValorTotal(valorTotal);
        pedidoEntity.setStatus(status);
        pedidoEntity.setCreatedAt(LocalDateTime.now());
        pedidoEntity.setUpdatedAt(LocalDateTime.now());
        pedidoEntity.setItens(itens);

        if (itens != null) {
            itens.forEach(itemEntity -> itemEntity.setPedido(pedidoEntity));
        }
        return pedidoEntity;
    }

    public static ItemRequestDto criarItemRequestDto(String produtoId, BigDecimal valorUnitario, Integer quantidade) {
        return ItemRequestDto.builder()
                .produtoId(produtoId)
                .valorUnitario(valorUnitario)
                .quantidade(quantidade)
                .build();
    }

    public static PedidoRequestDto criarPedidoRequestDto(String idExterno, List<ItemRequestDto> itens) {
        return PedidoRequestDto.builder()
                .idExterno(idExterno)
                .itens(itens)
                .build();
    }

    public static ItemResponseDto criarItemResponseDto(String produtoId, BigDecimal valorUnitario, Integer quantidade, BigDecimal valorTotalItem) {
        return ItemResponseDto.builder()
                .produtoId(produtoId)
                .valorUnitario(valorUnitario)
                .quantidade(quantidade)
                .valorTotalItem(valorTotalItem)
                .build();
    }

    public static PedidoResponseDto criarPedidoResponseDto(Long id, String idExterno, BigDecimal valorTotal, StatusPedido status, List<ItemResponseDto> itens) {
        return PedidoResponseDto.builder()
                .id(id)
                .idExterno(idExterno)
                .valorTotal(valorTotal)
                .status(status)
                .itens(itens)
                .build();
    }

    public static Pedido criarExemploPedidoDomain(Long id) {
        Item item = criarItemDomain(id * 10L, "PROD-" + id, new BigDecimal("50.00"), 2);
        return criarPedidoDomain(id, "PEDIDO-EXT-" + id, new BigDecimal("100.00"), StatusPedido.PROCESSADO, Collections.singletonList(item));
    }

    public static PedidoEntity criarExemploPedidoEntityIdNull(Long id) {
        ItemEntity itemEntity = criarItemEntity(null, "PROD-" + id, new BigDecimal("50.00"), 2);
        return criarPedidoEntity(null, "PEDIDO-EXT-" + id, new BigDecimal("100.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemEntity));
    }

    public static PedidoEntity criarExemploPedidoEntity(Long id) {
        ItemEntity itemEntity = criarItemEntity(id * 10L, "PROD-" + id, new BigDecimal("50.00"), 2);
        return criarPedidoEntity(id, "PEDIDO-EXT-" + id, new BigDecimal("100.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemEntity));
    }

    public static PedidoRequestDto criarExemploPedidoRequestDto(String idExterno) {
        ItemRequestDto itemRequestDto = criarItemRequestDto("PROD-001", new BigDecimal("75.00"), 1);
        return criarPedidoRequestDto(idExterno, Collections.singletonList(itemRequestDto));
    }

    public static PedidoResponseDto criarExemploPedidoResponseDto(Long id, String idExterno) {
        ItemResponseDto itemResponseDto = criarItemResponseDto("PROD-001", new BigDecimal("75.00"), 1, new BigDecimal("75.00"));
        return criarPedidoResponseDto(id, idExterno, new BigDecimal("75.00"), StatusPedido.PROCESSADO, Collections.singletonList(itemResponseDto));
    }
}
