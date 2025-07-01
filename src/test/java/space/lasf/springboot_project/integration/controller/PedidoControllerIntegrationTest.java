package space.lasf.springboot_project.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import space.lasf.springboot_project.base.AbstractIntegrationTest;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.domain.repository.PedidoRepository;
import space.lasf.springboot_project.dto.ItemPedidoDto;
import space.lasf.springboot_project.dto.PedidoDto;


public class PedidoControllerIntegrationTest extends AbstractIntegrationTest{


    @Autowired
    private PedidoRepository repository;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    private Cliente cliente;
    private ItemPedidoDto itemPedido;


    @BeforeEach
    public void setUp() {
        cliente = gerarCliente("Marta Rocha", "(51) 99999-5555");
        itemPedido = gerarItemPedidoDto();
        mongoTemplate.insertAll(Arrays.asList(cliente));
    }

    @AfterEach
    void clean() {
        mongoTemplate.remove(cliente);
    }
    
    @Test
    public void dadoPedidoDtoCorreto_entaoSalvaPedido_eRetornaPedidoDto()
      throws Exception {
        List<ItemPedidoDto> items = new ArrayList<>();
        items.add(itemPedido);
        String endpoint = UriComponentsBuilder
                    .fromUriString(PEDIDOS_API_ENDPOINT)
                    .queryParam("idCliente", cliente.getId())
                    .build()
                    .toUriString();

        // when
        PedidoDto savedPedidoDto = performPostRequestExpectedSuccess(
                                    endpoint, items, PedidoDto.class);


        //then
        assertNotNull(savedPedidoDto);
        assertEquals(itemPedido.getId(), savedPedidoDto.getItemsPedido().get(0).getId());
        assertEquals(itemPedido.getQuantidade(), savedPedidoDto.getItemsPedido().get(0).getQuantidade());
    }
    
}
