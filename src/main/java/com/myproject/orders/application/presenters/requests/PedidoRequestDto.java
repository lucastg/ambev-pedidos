package com.myproject.orders.application.presenters.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PedidoRequestDto {
    private String idExterno;
    private List<ItemRequestDto> itens;
}
