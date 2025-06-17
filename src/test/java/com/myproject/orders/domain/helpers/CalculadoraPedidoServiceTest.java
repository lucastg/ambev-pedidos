package com.myproject.orders.domain.helpers;

import com.myproject.orders.domain.entities.Item;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraPedidoServiceTest {

    private CalculadoraPedidoService calculadoraPedidoService;

    @BeforeEach
    void setUp() {
        calculadoraPedidoService = new CalculadoraPedidoService();
    }

    @Test
    @DisplayName("Deve calcular o valor total de um pedido com múltiplos itens")
    void calcularValorTotalPedido_DeveCalcularValorTotalPedido() {
        Item item1 = MassaDeDadosFactory.criarItemDomain(1L, "PROD-A", new BigDecimal("10.00"), 2);
        Item item2 = MassaDeDadosFactory.criarItemDomain(2L, "PROD-B", new BigDecimal("5.50"), 4);
        Item item3 = MassaDeDadosFactory.criarItemDomain(3L, "PROD-C", new BigDecimal("2.00"), 10);
        List<Item> itens = Arrays.asList(item1, item2, item3);

        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(1L, "PED-EXT-001", BigDecimal.ZERO, StatusPedido.PROCESSANDO, itens);

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(new BigDecimal("62.00"));

        assertThat(item1.getValorTotalItem()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(item2.getValorTotalItem()).isEqualByComparingTo(new BigDecimal("22.00"));
        assertThat(item3.getValorTotalItem()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Deve calcular o valor total de um pedido com um único item")
    void calcularValorTotalPedido_DeveCalcularValorTotalPedidoComUmItem() {
        Item item = MassaDeDadosFactory.criarItemDomain(1L, "PROD-X", new BigDecimal("50.00"), 3);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(2L, "PED-EXT-002", BigDecimal.ZERO, StatusPedido.PROCESSANDO, Collections.singletonList(item));

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(item.getValorTotalItem()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve retornar zero para um pedido com lista de itens vazia")
    void calcularValorTotalPedido_DeveRetornarZeroParaListaDeItensVazia() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(3L, "PED-EXT-003", BigDecimal.ZERO, StatusPedido.PROCESSANDO, new ArrayList<>());

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve retornar zero para um pedido com lista de itens nula")
    void calcularValorTotalPedido_DeveRetornarZeroParaListaDeItensNula() {
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(4L, "PED-EXT-004", BigDecimal.ZERO, StatusPedido.PROCESSANDO, null);

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lidar com itens com valor unitário nulo, tratando-o como zero")
    void calcularValorTotalPedido_DeveLidarComValorUnitarioNulo() {
        Item item = MassaDeDadosFactory.criarItemDomain(5L, "PROD-Z", null, 5);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(5L, "PED-EXT-005", BigDecimal.ZERO, StatusPedido.PROCESSANDO, Collections.singletonList(item));

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(item.getValorTotalItem()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lidar com itens com quantidade zero")
    void calcularValorTotalPedido_DeveLidarComQuantidadeZero() {
        Item item = MassaDeDadosFactory.criarItemDomain(6L, "PROD-W", new BigDecimal("100.00"), 0);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(6L, "PED-EXT-006", BigDecimal.ZERO, StatusPedido.PROCESSANDO, Collections.singletonList(item));

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(item.getValorTotalItem()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lidar com itens com valor unitário zero")
    void calcularValorTotalPedido_DeveLidarComValorUnitarioZero() {
        Item item = MassaDeDadosFactory.criarItemDomain(7L, "PROD-Y", BigDecimal.ZERO, 5);
        Pedido pedido = MassaDeDadosFactory.criarPedidoDomain(7L, "PED-EXT-007", BigDecimal.ZERO, StatusPedido.PROCESSANDO, Collections.singletonList(item));

        BigDecimal valorTotalCalculado = calculadoraPedidoService.calcularValorTotalPedido(pedido);

        assertThat(valorTotalCalculado).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(item.getValorTotalItem()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
