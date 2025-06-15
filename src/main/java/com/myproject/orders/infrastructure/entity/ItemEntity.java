package com.myproject.orders.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "item")
public class ItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "produto_id")
    private String produtoId;

    @Column(name = "valor_unitario")
    private BigDecimal valorUnitario;

    @Column(name = "quantidade")
    private int quantidade;

    @Column(name = "valor_total_item")
    private BigDecimal valorTotalItem;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedido;
}
