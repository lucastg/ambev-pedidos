package com.myproject.orders.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.orders.application.presenters.mappers.PedidoMapper;
import com.myproject.orders.application.presenters.requests.PedidoRequestDto;
import com.myproject.orders.application.presenters.responses.PedidoResponseDto;
import com.myproject.orders.domain.entities.Pedido;
import com.myproject.orders.domain.exception.PedidoDuplicadoException;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import com.myproject.orders.domain.helpers.MassaDeDadosFactory;
import com.myproject.orders.domain.ports.in.PedidoUseCasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoUseCasePort pedidoUseCasePort;

    @MockitoBean
    private PedidoMapper pedidoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private PedidoRequestDto pedidoRequestDto;
    private Pedido pedidoDomain;
    private PedidoResponseDto pedidoResponseDto;

    @BeforeEach
    void setUp() {
        pedidoRequestDto = MassaDeDadosFactory.criarExemploPedidoRequestDto("PEDIDO-EXT-001");
        pedidoDomain = MassaDeDadosFactory.criarExemploPedidoDomain(1L);
        pedidoResponseDto = MassaDeDadosFactory.criarExemploPedidoResponseDto(1L, "PEDIDO-EXT-001");
    }

    @Test
    @DisplayName("Deve listar pedidos paginados e retornar status 200 OK")
    void listarPedidos_DeveRetornarPedidosPaginados() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> pedidoPage = new PageImpl<>(List.of(pedidoDomain), pageable, 1);
        Page<PedidoResponseDto> pedidoResponsePage = new PageImpl<>(List.of(pedidoResponseDto), pageable, 1);

        when(pedidoUseCasePort.listarPedidos(any(Pageable.class))).thenReturn(pedidoPage);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(pedidoResponseDto);

        mockMvc.perform(get("/pedidos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(pedidoResponseDto.getId()))
                .andExpect(jsonPath("$.content[0].idExterno").value(pedidoResponseDto.getIdExterno()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(pedidoUseCasePort, times(1)).listarPedidos(any(Pageable.class));
        verify(pedidoMapper, times(1)).toResponseDto(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar pedido por ID inexistente")
    void buscarPedidoPorId_DeveRetornarNotFound() throws Exception {
        Long pedidoId = 99L;

        doThrow(new RecursoNaoEncontratoException("Pedido não encontrado"))
                .when(pedidoUseCasePort).buscarPedidoPorId(pedidoId);

        mockMvc.perform(get("/pedidos/{id}", pedidoId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pedidoUseCasePort, times(1)).buscarPedidoPorId(pedidoId);
    }

    @Test
    @DisplayName("Deve criar um novo pedido e retornar status 201 Created")
    void salvarPedido_DeveCriarPedidoComSucesso() throws Exception {
        when(pedidoUseCasePort.processarPedidoRecebido(any(PedidoRequestDto.class))).thenReturn(pedidoDomain);
        when(pedidoMapper.toResponseDto(pedidoDomain)).thenReturn(pedidoResponseDto);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pedidoResponseDto.getId()))
                .andExpect(jsonPath("$.idExterno").value(pedidoResponseDto.getIdExterno()));

        verify(pedidoUseCasePort, times(1)).processarPedidoRecebido(any(PedidoRequestDto.class));
        verify(pedidoMapper, times(1)).toResponseDto(pedidoDomain);
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict ao tentar criar pedido duplicado")
    void salvarPedido_DeveRetornarConflicQuandoDuplicado() throws Exception {
        doThrow(new PedidoDuplicadoException("Pedido duplicado!"))
                .when(pedidoUseCasePort).processarPedidoRecebido(any(PedidoRequestDto.class));

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequestDto)))
                .andExpect(status().isConflict());

        verify(pedidoUseCasePort, times(1)).processarPedidoRecebido(any(PedidoRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar criar pedido com dados inválidos")
    void salvarPedido_DeveRetornarBadRequestAoSalvarComDadosInvalidos() throws Exception {
        PedidoRequestDto invalidPedido = PedidoRequestDto.builder()
                .idExterno(null)
                .itens(Collections.emptyList())
                .build();

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPedido)))
                .andExpect(status().isBadRequest());

        verify(pedidoUseCasePort, times(0)).processarPedidoRecebido(any(PedidoRequestDto.class));
    }

    @Test
    @DisplayName("Deve deletar um pedido e retornar status 204 No Content")
    void deletarPedido_DeveRetornarNoContent() throws Exception {
        Long pedidoId = 1L;

        doNothing().when(pedidoUseCasePort).deletarPedido(pedidoId);

        mockMvc.perform(delete("/pedidos/{id}", pedidoId))
                .andExpect(status().isNoContent());

        verify(pedidoUseCasePort, times(1)).deletarPedido(pedidoId);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar pedido inexistente")
    void deletarPedido_DeveRetornarNotFound() throws Exception {
        Long pedidoId = 99L;

        doThrow(new RecursoNaoEncontratoException("Pedido não encontrado."))
                .when(pedidoUseCasePort).deletarPedido(pedidoId);

        mockMvc.perform(delete("/pedidos/{id}", pedidoId))
                .andExpect(status().isNotFound());

        verify(pedidoUseCasePort, times(1)).deletarPedido(pedidoId);
    }
}
