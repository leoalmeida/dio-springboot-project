package me.dio.dio_springboot_project.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.repository.PedidoRepository;
import me.dio.dio_springboot_project.dto.PedidoDto;
import me.dio.dio_springboot_project.integration.base.AbstractIntegrationTest;


public class PedidoControllerIntegrationTest extends AbstractIntegrationTest{


    @Autowired
    private PedidoRepository repository;
    private Cliente cliente;
    private PedidoDto pedidoDto;

    @PostConstruct
    public void init() {
        cliente = gerarCliente("Marta Rocha", "(51) 99999-5555");
        pedidoDto = gerarPedidoDto(cliente, null);
    }
    
    @Test
    @DisplayName("Pedido Path Test: salvar pedido dto e retornar")
    public void dadoPedidoDtoCorreto_entaoSalvaPedido_eRetornaPedidoDto()
      throws Exception {
        UriComponentsBuilder
                    .fromUriString(PEDIDOS_API_ENDPOINT)
                    .queryParam("idCliente", cliente.getId())
                    .build()
                    .toUri();

        // when
        PedidoDto savedPedidoDto = performPostRequestExpectedSuccess(
                                    PEDIDOS_API_ENDPOINT, pedidoDto, PedidoDto.class);


        //then
        assertNotNull(savedPedidoDto);
        assertEquals(pedidoDto.getId(), savedPedidoDto.getId());
        assertEquals(pedidoDto.getNumeroPedido(), savedPedidoDto.getNumeroPedido());
    }
    
}
