package com.myproject.orders.domain.useCases;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.enums.StatusPedido;
import com.myproject.orders.domain.exception.PedidoDuplicadoException;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import com.myproject.orders.domain.helpers.CalculadoraPedidoService;
import com.myproject.orders.domain.ports.in.PedidoUseCasePort;
import com.myproject.orders.domain.ports.out.PedidoPersistancePort;
import com.myproject.orders.domain.ports.out.PedidoQueueOutPort;
import com.myproject.orders.infrastructure.repositories.PedidoRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.text.MessageFormat.format;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PedidoUseCase implements PedidoUseCasePort {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Autowired
    private PedidoQueueOutPort pedidoQueueOutPort;

    @Autowired
    private CalculadoraPedidoService calculadoraPedidoService;

    @Autowired
    PedidoPersistancePort pedidoPersistancePort;

    private static final Logger logger = LoggerFactory.getLogger(PedidoUseCase.class);

    @Override
    public Pedido processarPedidoRecebido(PedidoRequestDto pedidoRequestDto) {
        Pedido pedido = pedidoMapper.toDomain(pedidoRequestDto);
        validarDuplicidade(pedido.getIdExterno());
        return processarPedidoCore(pedido);
    }

    @Override
    public Pedido processarPedidoCore(Pedido pedido) {
        BigDecimal valorTotalDoPedido = calculadoraPedidoService.calcularValorTotalPedido(pedido);
        pedido.setValorTotal(valorTotalDoPedido);
        pedido.setStatus(StatusPedido.PROCESSANDO);

        Pedido pedidoSalvo = pedidoPersistancePort.salvarPedido(pedido);
        logger.info("Pedido {} (ID Externo: {}) salvo com status inicial: {}", pedidoSalvo.getId(), pedidoSalvo.getIdExterno(), pedidoSalvo.getStatus());

        pedidoSalvo.setStatus(StatusPedido.PROCESSADO);
        Pedido pedidoFinal = pedidoPersistancePort.salvarPedido(pedidoSalvo);
        logger.info("Pedido {} (ID Externo: {}) salvo com status: {}", pedidoFinal.getId(), pedidoFinal.getIdExterno(), pedidoFinal.getStatus());

        PedidoResponseDto pedidoResponseDto = pedidoMapper.toResponseDto(pedidoFinal);
        pedidoQueueOutPort.publishMessage(pedidoResponseDto);
        return pedidoFinal;
    }

    private void validarDuplicidade(String idExterno) throws PedidoDuplicadoException {
        var pedidoOptional = pedidoPersistancePort.buscarPedidoPorIdExterno(idExterno);
        if (pedidoOptional) {
            logger.warn("Tentativa de criar pedido duplicado com idExterno: {}", idExterno);
            throw new PedidoDuplicadoException(format("Pedido com ID Externo {0} já existe no sistema.", idExterno));
        }
    }

    @Override
    public Page<Pedido> listarPedidos(Pageable pageable) {
        logger.info("Iniciando listagem paginada de pedidos. Página: {}, Tamanho: {}, Ordenação: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            Page<Pedido> pedidosPage = pedidoPersistancePort.listarPedidos(pageable);
            logger.info("Listagem paginada de pedidos concluída. Total de elementos: {}, Total de páginas: {}",
                    pedidosPage.getTotalElements(), pedidosPage.getTotalPages());
            return pedidosPage;
        } catch (Exception e) {
            logger.error("Erro ao listar pedidos. Página: {}, Tamanho: {}. Erro: {}",
                    pageable.getPageNumber(), pageable.getPageSize(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Pedido buscarPedidoPorId(Long id) throws RecursoNaoEncontratoException {
        logger.info("Iniciando busca de pedido por ID: {}", id);
        try {
            Pedido pedido = pedidoPersistancePort.buscarPedidoPorId(id)
                    .orElseThrow(() -> {
                        String errorMessage = format("Registro não encontrado com código {0}", id);
                        logger.warn(errorMessage);
                        return new RecursoNaoEncontratoException(errorMessage);
                    });
            logger.info("Pedido encontrado com sucesso para o ID: {}. ID Externo: {}", id, pedido.getIdExterno());
            return pedido;
        } catch (RecursoNaoEncontratoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar pedido por ID: {}. Erro: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deletarPedido(Long id) {
        if (!pedidoPersistancePort.existsById(id)) {
            throw new RecursoNaoEncontratoException(format("Registro não encontrado com código {0}", id));
        }
        pedidoPersistancePort.deletarPedido(id);
        logger.info("Pedido {} excluido com sucesso!", id);
    }
}

