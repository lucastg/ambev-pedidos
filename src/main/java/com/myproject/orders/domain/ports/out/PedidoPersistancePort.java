package com.myproject.orders.domain.ports.out;

import com.myproject.orders.domain.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PedidoPersistancePort {

    Page<Pedido> listarPedidos(Pageable pageable);

    Optional<Pedido> buscarPedidoPorId(Long id);

    boolean buscarPedidoPorIdExterno(String id);

    Pedido salvarPedido(Pedido pedido);

    boolean existsById(Long id);

    void deletarPedido(Long id);
}
