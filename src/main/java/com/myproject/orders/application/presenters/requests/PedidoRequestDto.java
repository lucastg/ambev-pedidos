package com.myproject.orders.application.presenters.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequestDto {
    private String idExterno;
    private List<ItemRequestDto> itens;
}
