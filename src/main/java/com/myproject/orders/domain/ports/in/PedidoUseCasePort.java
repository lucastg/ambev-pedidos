package com.myproject.orders.domain.ports.in;

import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.domain.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoUseCasePort {

    Page<Pedido> listarPedidos(Pageable pageable);

    Pedido buscarPedidoPorId(Long id);

    Pedido processarPedidoCore(Pedido pedido);

    Pedido processarPedidoRecebido(PedidoRequestDto pedidoRequestDto);

    void deletarPedido(Long id);
}
