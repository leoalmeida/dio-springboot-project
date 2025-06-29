package me.dio.dio_springboot_project.unit.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.dio.dio_springboot_project.controller.PedidoController;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.dto.ItemPedidoDto;
import me.dio.dio_springboot_project.dto.PedidoDto;
import me.dio.dio_springboot_project.dto.ProdutoDto;
import me.dio.dio_springboot_project.dto.mapper.ItemPedidoMapper;
import me.dio.dio_springboot_project.dto.mapper.PedidoMapper;
import me.dio.dio_springboot_project.service.PedidoService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebMvcTest(PedidoController.class) 
@ActiveProfiles("test")
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private ObjectMapper mapper = new ObjectMapper();

    PedidoDto pedido;
    ProdutoDto produto;
    ProdutoDto produto2;
    ItemPedidoDto itemPedido;

    Cliente cliente;
    Pedido pedido1;
    Pedido pedido2;

    @BeforeEach
    public void setUp() {
        
        // Cria cliente para testes
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID().toString());
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");
        cliente.setTelefone("(11) 99999-1111");
        
        // Cria produto para testes
        produto = new ProdutoDto();
        produto.setId(UUID.randomUUID().toString());
        produto.setNome("Produto 1");
        produto.setDescricao("Descrição do Produto 1");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setEstoque(100);
        produto.setSku("SKU001");
        
        produto2 = new ProdutoDto();
        produto2.setId(UUID.randomUUID().toString());
        produto2.setNome("Produto 2");
        produto2.setDescricao("Descrição do Produto 2");
        produto2.setPreco(new BigDecimal("20.00"));
        produto2.setEstoque(100);
        produto2.setSku("SKU002");
        
        // Cria pedido para testes
        pedido = new PedidoDto();
        pedido.setNumeroPedido("ORD-190001");
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setItemsPedido(new ArrayList<>());
        pedido.setValorTotalPedido(BigDecimal.ZERO);


        // Cria item de pedido para testes
        itemPedido = new ItemPedidoDto();
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(new BigDecimal("10.00"));
        
        pedido1 = PedidoMapper.toPedidoEntity(pedido);
        ItemPedido itemPedidoEntity = ItemPedidoMapper.toItemPedidoEntity(itemPedido);
        itemPedidoEntity.setId(UUID.randomUUID().toString());
        pedido1.setId(UUID.randomUUID().toString());
        pedido1.incluirItemPedido(itemPedidoEntity);
        cliente.incluirPedido(pedido1);

        pedido2 = new Pedido();
        pedido2.setId(UUID.randomUUID().toString());
        pedido2.setNumeroPedido("ORD-190002");
        pedido2.setDataPedido(LocalDateTime.now());
        pedido2.setItemsPedido(new ArrayList<>());
        pedido2.setValorTotalPedido(BigDecimal.ZERO);
        cliente.incluirPedido(pedido2);
    }

    @Test
    public void deveRetornarNovoPedidoCriado() throws Exception {
        // Cria pedidos para testes
        ItemPedido entityItemPedido = ItemPedidoMapper.toItemPedidoEntity(itemPedido);
        entityItemPedido.setId(UUID.randomUUID().toString());
        entityItemPedido.updateSubtotal();
        
        Pedido entityPedido = PedidoMapper.toPedidoEntity(pedido);
        entityPedido.setId(UUID.randomUUID().toString());
        entityPedido.incluirItemPedido(entityItemPedido);
        entityPedido.atualizaValorTotalPedido();
        entityPedido.setCliente(cliente);

        List<ItemPedido> entityList = entityPedido.getItemsPedido();
        // Configura o mock
        doReturn(entityPedido).when(pedidoService).criarPedido(cliente.getId(), entityList);

        List<ItemPedidoDto> itemsPedido = new ArrayList<>();
        itemsPedido.add(itemPedido);
        // Executa e verifica
        mockMvc.perform(post("/api/pedidos")
                            .content(mapper.writeValueAsString(itemsPedido))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.id", is(entityPedido.getId())))
                .andExpect(jsonPath("$.numeroPedido", is(entityPedido.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).criarPedido(cliente.getId(), entityList);
    }

    @Test
    public void deveRetornarTodosPedidos() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(pedido1, pedido2))
            .when(pedidoService).buscarTodosPedidos();

        // Executa e verifica
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(pedido1.getId())))
                .andExpect(jsonPath("$[0].numeroPedido", is(pedido1.getNumeroPedido())))
                .andExpect(jsonPath("$[1].id", is(pedido2.getId())))
                .andExpect(jsonPath("$[1].numeroPedido", is(pedido2.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).buscarTodosPedidos();
    }

}
