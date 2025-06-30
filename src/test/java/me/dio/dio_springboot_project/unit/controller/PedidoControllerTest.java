package me.dio.dio_springboot_project.unit.controller;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.dio.dio_springboot_project.base.TestFactory;
import me.dio.dio_springboot_project.controller.PedidoController;
import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.dto.ItemPedidoDto;
import me.dio.dio_springboot_project.dto.PedidoDto;
import me.dio.dio_springboot_project.dto.mapper.ItemPedidoMapper;
import me.dio.dio_springboot_project.service.PedidoService;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@WebMvcTest(PedidoController.class) 
public class PedidoControllerTest extends TestFactory{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObjectsValidator<ItemPedidoDto> itemPedidoValidator;

    @MockitoBean
    private ObjectsValidator<PedidoDto> pedidoValidator;

    @MockitoBean
    private PedidoService pedidoService;

    private ObjectMapper mapper = new ObjectMapper();

    //PedidoDto pedido;
    //ItemPedidoDto itemPedido;

    Cliente clienteEntity1;
    Cliente clienteEntity2;
    Pedido pedidoEntity1;
    Pedido pedidoEntity2;
    Pedido pedidoEntity3;
    Produto produtoEntity1;
    Produto produtoEntity2;
    ItemPedido itemPedidoEntity1;
    ItemPedido itemPedidoEntity2;
    
    List<Pedido> pedidosAssets;
    List<Cliente> clientesAssets;
    
    @BeforeEach
    public void setUp() {
        
        // Cria cliente para testes
        clienteEntity1 = gerarCliente("João Silva","(11) 99999-1111");
        clienteEntity2 = gerarCliente("Manoel Nobrega","(91) 99999-8888");
        
        // Cria produto para testes
        produtoEntity1 = gerarProduto();
        produtoEntity2 = gerarProduto();
        
        // Cria pedido para testes
        pedidoEntity1 = gerarPedido(clienteEntity1);

        // Cria item de pedido para testes
        itemPedidoEntity1 = gerarItemPedido(pedidoEntity1, produtoEntity1);
        itemPedidoEntity2 = gerarItemPedido();
        
        pedidoEntity2 = gerarPedido(clienteEntity1);
        pedidoEntity3 = gerarPedido(clienteEntity1);

        clientesAssets = Arrays.asList(clienteEntity1,clienteEntity2);
        pedidosAssets = Arrays.asList(pedidoEntity1,pedidoEntity2,pedidoEntity3);
    }

    @Test
    public void deveRetornarNovoPedidoCriado() throws Exception {
        // Cria pedidos para testes
        ItemPedido novoItem = gerarItemPedido();
        
        List<ItemPedido> itemPedidoList = new ArrayList<>();
        itemPedidoList.add(novoItem);
        // Configura o mock
        doReturn(pedidoEntity1).when(pedidoService).criarPedido(clienteEntity1.getId(), itemPedidoList);

        // Executa e verifica
        mockMvc.perform(post("/api/pedidos")
                            .param("idCliente", clienteEntity1.getId())
                            .content(mapper.writeValueAsString(ItemPedidoMapper.toListItemPedidoDto(itemPedidoList)))
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").value(is(pedidoEntity1.getId())))
                .andExpect(jsonPath("numeroPedido").value(is(pedidoEntity1.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).criarPedido(clienteEntity1.getId(), itemPedidoList);
    }

    @Test
    public void deveRetornarTodosPedidos() throws Exception {
        // Configura o mock
        doReturn(pedidosAssets)
            .when(pedidoService).buscarTodosPedidos();

        // Executa e verifica
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(pedidosAssets.size())))
                .andExpect(jsonPath("$[0].id", is(pedidoEntity1.getId())))
                .andExpect(jsonPath("$[0].numeroPedido", is(pedidoEntity1.getNumeroPedido())))
                .andExpect(jsonPath("$[1].id", is(pedidoEntity2.getId())))
                .andExpect(jsonPath("$[1].numeroPedido", is(pedidoEntity2.getNumeroPedido())))
                .andExpect(jsonPath("$[2].id", is(pedidoEntity3.getId())))
                .andExpect(jsonPath("$[2].numeroPedido", is(pedidoEntity3.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).buscarTodosPedidos();
    }

    @Test
    public void deveRetornarUmPedidoAPartirDoId() throws Exception {
        Pedido pedidoConsultado = pedidosAssets.get(new Random().nextInt(this.pedidosAssets.size()));
        // Configura o mock
        doReturn(Optional.of(pedidoConsultado))
            .when(pedidoService).buscarPedidoPorId(pedidoConsultado.getId());

        // Executa e verifica
        mockMvc.perform(get("/api/pedidos/{id}",pedidoConsultado.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(pedidoConsultado.getId())))
                .andExpect(jsonPath("$.numeroPedido", is(pedidoConsultado.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).buscarPedidoPorId(pedidoConsultado.getId());
    }

    @Test
    public void deveRetornarUmPedidoAPartirDoNumeroPedido() throws Exception {
        Pedido pedidoConsultado = pedidosAssets.get(new Random().nextInt(this.pedidosAssets.size()));
        // Configura o mock
        doReturn(Optional.of(pedidoConsultado))
            .when(pedidoService).buscarPedidoPorNumero(pedidoConsultado.getNumeroPedido());

        // Executa e verifica
        mockMvc.perform(get("/api/pedidos/numero/{numeroPedido}",pedidoConsultado.getNumeroPedido()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(pedidoConsultado.getId())))
                .andExpect(jsonPath("$.numeroPedido", is(pedidoConsultado.getNumeroPedido())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).buscarPedidoPorNumero(pedidoConsultado.getNumeroPedido());
    }

    @Test
    public void deveRetornarPedidosAPartirDoIdCliente() throws Exception {
        Pedido pedidoConsultado = pedidosAssets.get(new Random().nextInt(this.pedidosAssets.size()));
        // Configura o mock
        doReturn(pedidosAssets)
            .when(pedidoService).buscarPedidosPorIdCliente(pedidoConsultado.getCliente().getId());

        // Executa e verifica
        mockMvc.perform(get("/api/pedidos/cliente/{idCliente}",pedidoConsultado.getCliente().getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(pedidoEntity1.getId())))
                .andExpect(jsonPath("$[0].numeroPedido", is(pedidoEntity1.getNumeroPedido())))
                .andExpect(jsonPath("$[1].id", is(pedidoEntity2.getId())))
                .andExpect(jsonPath("$[2].id", is(pedidoEntity3.getId())));

        // Verifica se o método do serviço foi chamado
        verify(pedidoService, times(1)).buscarPedidosPorIdCliente(pedidoConsultado.getCliente().getId());
    }

    //    @Test
//    public void deveIncluirItemAoPedidoSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/{id}/preco",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
    
//    @Test
//    public void deveRemoverUmItemDoPedidoSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(delete("/api/produtos/{id}",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
    
//    @Test
//    public void deveAlterarUmItemDoPedidoSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(put("/api/produtos/{id}",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
    
//    @Test
//    public void deveCalcularOTotalDoPedidoSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/{id}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
    
//    @Test
//    public void deveFinalizarUmPedidoPeloIdDoPedido() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/sku/{sku}",produto1.getSku()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
    
//    @Test
//    public void deveCancelarUmPedidoPeloIdDoPedido() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/sku/{sku}",produto1.getSku()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}

//    @Test
//    public void deveValidarUmPedidoPeloIdDoPedido() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/{id}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//    //     Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
}
