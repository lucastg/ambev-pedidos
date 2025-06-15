package com.myproject.orders.application.adapters;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import com.myproject.orders.domain.ports.out.PedidoPersistancePort;
import com.myproject.orders.infrastructure.entity.PedidoEntity;
import com.myproject.orders.infrastructure.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PedidoPersistance implements PedidoPersistancePort {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<Pedido> listarPedidos(Pageable pageable) {
        Page<PedidoEntity> pedidosEntitiesPage = pedidoRepository.findAll(pageable);
        return pedidosEntitiesPage.map(pedidoMapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Pedido> buscarPedidoPorId(Long id) throws RecursoNaoEncontratoException {
        Optional<PedidoEntity> pedidoEntityOptional = pedidoRepository.findById(id);
        return pedidoEntityOptional.map(pedidoMapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean buscarPedidoPorIdExterno(String id) {
        Optional<PedidoEntity> pedidoOptional = pedidoRepository.buscarPedidoPorIdExterno(id);
        return pedidoOptional.isPresent();
    }

    @Transactional
    @Override
    public Pedido salvarPedido(Pedido pedido) {
        PedidoEntity pedidoEntity = pedidoMapper.toEntity(pedido);
        return pedidoMapper.toDomain(pedidoRepository.save(pedidoEntity));
    }

    @Transactional
    @Override
    public void deletarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long id) {
        return pedidoRepository.existsById(id);
    }
}
