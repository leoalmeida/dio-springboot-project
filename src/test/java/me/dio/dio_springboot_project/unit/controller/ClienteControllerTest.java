package me.dio.dio_springboot_project.unit.controller;

import java.util.ArrayList;
import java.util.Arrays;
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

import me.dio.dio_springboot_project.controller.ClienteController;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.dto.ClienteDto;
import me.dio.dio_springboot_project.dto.mapper.ClienteMapper;
import me.dio.dio_springboot_project.service.ClienteService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebMvcTest(ClienteController.class) 
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private ObjectMapper mapper = new ObjectMapper();

    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente cliente3;
    private Pedido pedido;

    @BeforeEach
    public void setUp() {
        
        // Cria clientes para testes
        // Prepara Mock cliente1
        cliente1 = new Cliente();
        cliente1.setId(UUID.randomUUID().toString());
        cliente1.setNome("João Silva");
        cliente1.setEmail("joao.silva@example.com");
        cliente1.setTelefone("(11) 99999-1111");
                            
        // Prepara Mock cliente2
        cliente2 = new Cliente();
        cliente2.setId(UUID.randomUUID().toString());
        cliente2.setNome("Maria Santos");
        cliente2.setEmail("maria.santos@example.com");
        cliente2.setTelefone("(11) 99999-2222");
        
        pedido = new Pedido();
        pedido.setId(UUID.randomUUID().toString());
        pedido.setNumeroPedido("ORD-150001");
        cliente2.incluirPedido(pedido);

        // Prepara Mock cliente3
        cliente3 = new Cliente();
        cliente3.setId(UUID.randomUUID().toString());
        cliente3.setNome("Pedro Oliveira");
        cliente3.setEmail("pedro.oliveira@example.com");
        cliente3.setTelefone("(11) 99999-3333");
    }

    @Test
    public void deveRetornarNovoClienteCriado() throws Exception {
        
        // Cria clientes para testes
        ClienteDto novoCliente = ClienteDto.builder()
                .nome("Cliente Novo")
                .email("teste@teste.com")
                .telefone("3399999999")
                .pedidos(new ArrayList<>())
                .build();

        Cliente entity = ClienteMapper.toClienteEntity(novoCliente);
        entity.setId(UUID.randomUUID().toString());
        // Configura o mock
        doReturn(entity).when(clienteService).criarCliente(entity);

        // Executa e verifica
        mockMvc.perform(post("/api/clientes")
                            .content(mapper.writeValueAsString(novoCliente))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.id", is(entity.getId())))
                .andExpect(jsonPath("$.nome", is(entity.getNome())));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).criarCliente(entity);
    }

    @Test
    public void deveRetornarTodosClientes() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(cliente1, cliente2, cliente3))
            .when(clienteService).buscarTodosClientes();

        // Executa e verifica
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Cliente 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Cliente 2")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].nome", is("Cliente 3")));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).buscarTodosClientes();
    }

    @Test
    public void deveRetornarClientesComEstoqueBaixo() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(cliente2))
            .when(clienteService).buscarClientesComPedidos();

        // Executa e verifica
        mockMvc.perform(get("/api/clientes/com-pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].nome", is("Cliente 2")));

        // Verifica se o método do serviço foi chamado
        verify(clienteService, times(1)).buscarClientesComPedidos();
    }

}
