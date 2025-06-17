package com.myproject.orders.domain.helpers;

import com.myproject.orders.domain.entities.Item;
import com.myproject.orders.domain.entities.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CalculadoraPedidoService {

    public BigDecimal calcularValorTotalPedido(Pedido pedido) {
        BigDecimal valorTotalDoPedido = BigDecimal.ZERO;

        if (Objects.nonNull(pedido.getItens()) && !pedido.getItens().isEmpty()) {
            for (Item item : pedido.getItens()) {
                BigDecimal valorUnitario = Objects.nonNull(item.getValorUnitario()) ? item.getValorUnitario() : BigDecimal.ZERO;
                int quantidade = item.getQuantidade();

                BigDecimal valorTotalItem = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
                item.setValorTotalItem(valorTotalItem);
                valorTotalDoPedido = valorTotalDoPedido.add(valorTotalItem);
            }
        }
        return valorTotalDoPedido;
    }
}
