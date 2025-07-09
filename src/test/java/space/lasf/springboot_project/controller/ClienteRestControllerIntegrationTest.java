package space.lasf.springboot_project.controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.dto.ClienteDto;
import space.lasf.springboot_project.dto.mapper.ClienteMapper;
import space.lasf.springboot_project.service.ClienteService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteRestControllerIntegrationTest extends TestFactory {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClienteService service;

    @Test
    public void testeCliente_quandoConsultarClientes_thenStatus200()
      throws Exception {

        ClienteDto joao = gerarClienteDto("João Silva","(11) 99999-0000");
        ClienteDto maria = gerarClienteDto("Maria Santos","(21) 99999-2222");
        ClienteDto pedro = gerarClienteDto("Pedro Oliveira","(31) 99999-3333");
        List<ClienteDto> todosClientes = Arrays.asList(joao,maria,pedro);

        when(service.buscarTodosClientes()).thenReturn(ClienteMapper.toListClienteEntity(todosClientes));
        when(service.buscarClientesComPedidos()).thenReturn(ClienteMapper.toListClienteEntity(todosClientes));

        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome", is("João Silva")))
                .andExpect(jsonPath("$[1].nome", is("Maria Santos")))
                .andExpect(jsonPath("$[2].nome", is("Pedro Oliveira")));
    }
    
}
