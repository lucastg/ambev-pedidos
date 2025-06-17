package com.myproject.orders.application.presenters.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "O ID externo do pedido é obrigatório e não pode ser vazio.")
    private String idExterno;
    @NotNull(message = "A lista de itens não pode ser nula.")
    private List<ItemRequestDto> itens;
}
