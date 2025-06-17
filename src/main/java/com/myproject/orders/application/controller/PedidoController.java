package com.myproject.orders.application.controller;

import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.ports.in.PedidoUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@AllArgsConstructor
@NoArgsConstructor
public class PedidoController {

    @Autowired
    PedidoUseCasePort pedidoUseCasePort;

    @Autowired
    PedidoMapper pedidoMapper;

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Operation(summary = "Buscar todos os pedidos", description = "Retorna todos os pedidos encontrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @GetMapping("")
    public ResponseEntity<Page<PedidoResponseDto>> listarPedidos(
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "id",
                    direction = org.springframework.data.domain.Sort.Direction.ASC
            ) Pageable pageable) {
        Page<Pedido> pedidosDomainPage = pedidoUseCasePort.listarPedidos(pageable);
        Page<PedidoResponseDto> pedidosResponseDtoPage = pedidosDomainPage.map(pedidoMapper::toResponseDto);
        return ResponseEntity.ok(pedidosResponseDtoPage);
    }

    @Operation(summary = "Buscar pedido pelo Id.", description = "Recebe e retorna o pedido com respectivo id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @GetMapping("/{id}")
    @Cacheable(value = "pedidos", key = "#id")
    public ResponseEntity<PedidoResponseDto> buscarPedidoPorId(@PathVariable(value = "id") Long id) {
        Pedido pedido = pedidoUseCasePort.buscarPedidoPorId(id);
        PedidoResponseDto pedidoResponseDto = pedidoMapper.toResponseDto(pedido);
        return ResponseEntity.ok(pedidoResponseDto);
    }

    @Operation(summary = "Criar um novo pedido", description = "Recebe os dados de PedidoRequestDto, processa e salva um novo pedido com base nos dados recebidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "409", description = "Pedido duplicado detectado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @PostMapping("")
    public ResponseEntity<PedidoResponseDto> salvarPedido(@RequestBody @Valid PedidoRequestDto pedidoRequestDto) {
        logger.info("Recebido pedido via REST: {}", pedidoRequestDto.getIdExterno());
        Pedido pedidoProcessado = pedidoUseCasePort.processarPedidoRecebido(pedidoRequestDto);
        PedidoResponseDto responseDto = pedidoMapper.toResponseDto(pedidoProcessado);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Deletar um pedido", description = "Rcebe e deleta o pedido com respectivo id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable(value = "id") Long id) {
        pedidoUseCasePort.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
