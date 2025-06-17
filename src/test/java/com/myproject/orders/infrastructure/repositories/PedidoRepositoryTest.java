package com.myproject.orders.infrastructure.repositories;

import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import com.myproject.orders.infrastructure.entity.PedidoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PedidoRequestDto pedidoRequestDto;
    private Pedido pedidoDomain;
    private PedidoResponseDto pedidoResponseDto;
    private PedidoEntity pedidoSalvo;

    @BeforeEach
    void setUp() {
        this.pedidoSalvo = new PedidoEntity();
        this.pedidoSalvo.setIdExterno("PEDIDO-EXT-1");
        this.pedidoSalvo.setStatus(StatusPedido.PROCESSANDO);
        this.pedidoSalvo.setValorTotal(new BigDecimal("100.00"));
        this.pedidoSalvo.setItens(new ArrayList<>());
        this.pedidoSalvo.setCreatedAt(LocalDateTime.now());
        this.pedidoSalvo.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve buscar um PedidoEntity pelo idExterno quando ele existe")
    void buscarPedidoPorIdExterno_DeveRetornarPedidoEntity() {
        entityManager.persist(this.pedidoSalvo);
        entityManager.flush();

        Optional<PedidoEntity> encontrado = pedidoRepository.buscarPedidoPorIdExterno("PEDIDO-EXT-1");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getIdExterno()).isEqualTo("PEDIDO-EXT-1");
        assertThat(encontrado.get().getId()).isNotNull();
    }

    @Test
    @DisplayName("Não deve buscar um PedidoEntity pelo idExterno quando ele não existe")
    void buscarPedidoPorIdExterno_DeveRetornarPedidoEntityNaoExiste() {
        Optional<PedidoEntity> encontrado = pedidoRepository.buscarPedidoPorIdExterno("PEDIDO-EXT-999");
        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve salvar e encontrar um PedidoEntity por ID")
    void salveEBuscarPedidoPorId() {
        PedidoEntity pedidoSalvo = pedidoRepository.save(this.pedidoSalvo);

        Optional<PedidoEntity> encontrado = pedidoRepository.findById(pedidoSalvo.getId());

        assertThat(pedidoSalvo.getId()).isNotNull();
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getIdExterno()).isEqualTo("PEDIDO-EXT-1");
        assertThat(encontrado.get().getValorTotal()).isEqualTo(new BigDecimal("100.00"));
    }
}
