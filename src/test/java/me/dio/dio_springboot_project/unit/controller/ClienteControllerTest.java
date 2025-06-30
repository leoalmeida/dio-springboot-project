package me.dio.dio_springboot_project.unit.controller;

import java.util.ArrayList;
import java.util.List;


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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.dio.dio_springboot_project.base.TestFactory;
import me.dio.dio_springboot_project.controller.ClienteController;
import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.dto.ClienteDto;
import me.dio.dio_springboot_project.dto.mapper.ClienteMapper;
import me.dio.dio_springboot_project.service.ClienteService;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@WebMvcTest(ClienteController.class) 
public class ClienteControllerTest extends TestFactory{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObjectsValidator<ClienteDto> clienteValidator;

    @MockitoBean
    private ClienteService clienteService;

    private ObjectMapper mapper = new ObjectMapper();

    List<Cliente> clientesAssets = new ArrayList<>();
    List<Cliente> clientesComPedido = new ArrayList<>();
    private Pedido pedido;

    @BeforeEach
    public void setUp() {
        
        // Cria clientes para testes
        // Prepara Mock cliente1
        Cliente cliente1 = gerarCliente("João Silva","(11) 99999-1111");
                            
        // Prepara Mock cliente2
        Cliente cliente2 = gerarCliente("Maria Santos","(11) 99999-2222");
        pedido = gerarPedido(cliente2);
        cliente2.incluirPedido(pedido);

        // Prepara Mock cliente3
        Cliente cliente3 = gerarCliente("Pedro Oliveira","(11) 99999-3333");

        clientesAssets.add(cliente1);
        clientesAssets.add(cliente2);
        clientesAssets.add(cliente3);
        clientesComPedido.add(cliente2);
    }

    @Test
    public void deveRetornarNovoClienteCriado() throws Exception {
        
        // Cria clientes para testes
        Cliente novoCliente = gerarCliente("Cliente Novo","(33) 9999-9999");

        ClienteDto dto = ClienteMapper.toClienteDto(novoCliente);

        // Configura o mock
        doReturn(novoCliente).when(clienteService).criarCliente(novoCliente);

        // Executa e verifica
        mockMvc.perform(post("/api/clientes")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").value(is(novoCliente.getId())))
                .andExpect(jsonPath("nome").value(is(novoCliente.getNome())));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).criarCliente(novoCliente);
    }

    @Test
    public void deveRetornarTodosClientes() throws Exception {
        // Configura o mock
        doReturn(clientesAssets)
            .when(clienteService).buscarTodosClientes();

        // Executa e verifica
        mockMvc.perform(get("/api/clientes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(clientesAssets.get(0).getId())))
                .andExpect(jsonPath("$[0].nome", is(clientesAssets.get(0).getNome())))
                .andExpect(jsonPath("$[1].id", is(clientesAssets.get(1).getId())))
                .andExpect(jsonPath("$[1].nome", is(clientesAssets.get(1).getNome())))
                .andExpect(jsonPath("$[2].id", is(clientesAssets.get(2).getId())))
                .andExpect(jsonPath("$[2].nome", is(clientesAssets.get(2).getNome())));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).buscarTodosClientes();
    }

    @Test
    public void deveRetornarClientesComPedido() throws Exception {
        // Configura o mock
        doReturn(clientesComPedido)
            .when(clienteService).buscarClientesComPedidos();

        // Executa e verifica
        mockMvc.perform(get("/api/clientes/com-pedidos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(clientesComPedido.get(0).getId())))
                .andExpect(jsonPath("$[0].nome", is(clientesComPedido.get(0).getNome())));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).buscarClientesComPedidos();
    }

//    @Test
//    public void deveRetornarUmClienteAPartirDoIdCliente() throws Exception {}
//		// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/clientes/{id}",cliente1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	  }
//    @Test
//    public void deveRetornarUmClienteAPartirDoEmailCliente() throws Exception {}
//		// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/clientes/email/{email}",cliente1.getEmail()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	  }
//    @Test
//    public void devePesquisarClientesAPartirDoNomeCliente() throws Exception {}
//		// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/clientes/pesquisar"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	  }
//    @Test
//    public void deveAlterarClienteAPartirDoIdCliente() throws Exception {}
//    	// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(put("/api/clientes/{id}",cliente1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	  }
//    @Test
//    public void deveRemoverClienteAPartirDoIdCliente() throws Exception {}
//    	// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(delete("/api/clientes/{id}",cliente1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	  }
//    @Test
//    public void deveValidarSeUmEmailPossuiFormatoValido() throws Exception {}
//		// Configura o mock
//        doReturn(cliente1)
//            .when(clienteService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(post("/api/clientes/validar-email"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.nome", is("Produto 1")));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(clienteService, times(1)).buscarProdutos();
//	}

}
