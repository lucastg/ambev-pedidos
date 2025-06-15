package com.myproject.orders.application.presenters.mappers;

import com.myproject.orders.application.presenters.requests.ItemRequestDto;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.ItemResponseDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Item;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.infrastructure.entity.ItemEntity;
import com.myproject.orders.infrastructure.entity.PedidoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public Pedido toDomain(PedidoRequestDto pedidoRequestDto) {
        Pedido pedido = new Pedido();
        pedido.setIdExterno(pedidoRequestDto.getIdExterno());

        if (pedidoRequestDto.getItens() != null) {
            List<Item> itens = pedidoRequestDto.getItens().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
            pedido.setItens(itens);
        } else {
            pedido.setItens(new ArrayList<>());
        }
        return pedido;
    }

    private Item toDomain(ItemRequestDto itemRequestDto) {
        Item item = new Item();
        item.setProdutoId(itemRequestDto.getProdutoId());
        item.setValorUnitario(itemRequestDto.getValorUnitario());
        item.setQuantidade(itemRequestDto.getQuantidade());
        return item;
    }

    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(pedido.getId());
        pedidoEntity.setIdExterno(pedido.getIdExterno());
        pedidoEntity.setStatus(pedido.getStatus());
        pedidoEntity.setValorTotal(pedido.getValorTotal());

        if (pedido.getItens() != null) {
            List<ItemEntity> itemEntities = pedido.getItens().stream()
                    .map(item -> toItemEntity(item, pedidoEntity))
                    .collect(Collectors.toList());
            pedidoEntity.setItens(itemEntities);
        } else {
            pedidoEntity.setItens(new ArrayList<>());
        }

        return pedidoEntity;
    }

    private ItemEntity toItemEntity(Item item, PedidoEntity parentPedidoEntity) {
        ItemEntity itemEntity = new ItemEntity();

        itemEntity.setId(item.getId());
        itemEntity.setProdutoId(item.getProdutoId());
        itemEntity.setValorUnitario(item.getValorUnitario());
        itemEntity.setQuantidade(item.getQuantidade());
        itemEntity.setValorTotalItem(item.getValorTotalItem());

        itemEntity.setPedido(parentPedidoEntity);
        return itemEntity;
    }

    public Pedido toDomain(PedidoEntity pedidoEntity) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoEntity.getId());
        pedido.setIdExterno(pedidoEntity.getIdExterno());
        pedido.setStatus(pedidoEntity.getStatus());
        pedido.setValorTotal(pedidoEntity.getValorTotal());
        pedido.setCreatedAt(pedidoEntity.getCreatedAt());
        pedido.setUpdatedAt(pedidoEntity.getUpdatedAt());

        if (pedidoEntity.getItens() != null) {
            pedido.setItens(pedidoEntity.getItens().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList()));
        } else {
            pedido.setItens(new ArrayList<>());
        }
        return pedido;
    }

    public List<Pedido> toDomain(List<PedidoEntity> pedidoEntities) {
        if (pedidoEntities == null) {
            return new ArrayList<>();
        }
        return pedidoEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Item toDomain(ItemEntity itemEntity) {
        Item item = new Item();
        item.setId(itemEntity.getId());
        item.setProdutoId(itemEntity.getProdutoId());
        item.setValorUnitario(itemEntity.getValorUnitario());
        item.setQuantidade(itemEntity.getQuantidade());
        item.setValorTotalItem(itemEntity.getValorTotalItem());
        item.setCreatedAt(itemEntity.getCreatedAt());
        item.setUpdatedAt(itemEntity.getUpdatedAt());
        return item;
    }

    public PedidoResponseDto toResponseDto(PedidoEntity pedidoEntity) {
        PedidoResponseDto pedidoResponseDto = new PedidoResponseDto();
        pedidoResponseDto.setId(pedidoEntity.getId());
        pedidoResponseDto.setIdExterno(pedidoEntity.getIdExterno());
        pedidoResponseDto.setStatus(pedidoEntity.getStatus());
        pedidoResponseDto.setValorTotal(pedidoEntity.getValorTotal());
        pedidoResponseDto.setCreatedAt(pedidoEntity.getCreatedAt());
        pedidoResponseDto.setUpdatedAt(pedidoEntity.getUpdatedAt());

        if (pedidoEntity.getItens() != null && !pedidoEntity.getItens().isEmpty()) {
            List<ItemResponseDto> itemResponseDtos = pedidoEntity.getItens().stream()
                    .map(this::toItemResponseDto)
                    .collect(Collectors.toList());
            pedidoResponseDto.setItens(itemResponseDtos);
        } else {
            pedidoResponseDto.setItens(new ArrayList<>());
        }
        return pedidoResponseDto;
    }

    private ItemResponseDto toItemResponseDto(ItemEntity itemEntity) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setProdutoId(itemEntity.getProdutoId());
        itemResponseDto.setQuantidade(itemEntity.getQuantidade());
        itemResponseDto.setValorUnitario(itemEntity.getValorUnitario());
        itemResponseDto.setValorTotalItem(itemEntity.getValorTotalItem());
        itemResponseDto.setCreatedAt(itemEntity.getCreatedAt());
        itemResponseDto.setUpdatedAt(itemEntity.getUpdatedAt());
        return itemResponseDto;
    }

    public PedidoResponseDto toResponseDto(Pedido pedido) {
        PedidoResponseDto pedidoResponseDto = new PedidoResponseDto();
        pedidoResponseDto.setId(pedido.getId());
        pedidoResponseDto.setIdExterno(pedido.getIdExterno());
        pedidoResponseDto.setStatus(pedido.getStatus());
        pedidoResponseDto.setValorTotal(pedido.getValorTotal());
        pedidoResponseDto.setCreatedAt(pedido.getCreatedAt());
        pedidoResponseDto.setUpdatedAt(pedido.getUpdatedAt());

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            List<ItemResponseDto> itemResponseDtos = pedido.getItens().stream()
                    .map(this::toItemResponseDto)
                    .collect(Collectors.toList());
            pedidoResponseDto.setItens(itemResponseDtos);
        } else {
            pedidoResponseDto.setItens(new ArrayList<>());
        }
        return pedidoResponseDto;
    }

    private ItemResponseDto toItemResponseDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setProdutoId(item.getProdutoId());
        itemResponseDto.setQuantidade(item.getQuantidade());
        itemResponseDto.setValorUnitario(item.getValorUnitario());
        itemResponseDto.setValorTotalItem(item.getValorTotalItem());
        itemResponseDto.setCreatedAt(item.getCreatedAt());
        itemResponseDto.setUpdatedAt(item.getUpdatedAt());
        return itemResponseDto;
    }
}