package com.myproject.orders.infrastructure.repositories;

import com.myproject.orders.infrastructure.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    @Query("SELECT p FROM PedidoEntity p WHERE p.idExterno = :idExterno")
    Optional<PedidoEntity> buscarPedidoPorIdExterno(String idExterno);
}
