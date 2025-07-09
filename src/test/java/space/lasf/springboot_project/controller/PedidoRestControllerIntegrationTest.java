package space.lasf.springboot_project.controller;

import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.is;
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

import space.lasf.springboot_project.domain.model.Status;
import space.lasf.springboot_project.domain.repository.PedidoRepository;
import space.lasf.springboot_project.dto.PedidoDto;
import space.lasf.springboot_project.dto.mapper.PedidoMapper;
import space.lasf.springboot_project.service.PedidoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PedidoRestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PedidoRepository repository;

    @MockitoBean
    private PedidoService service;

    @Test
    public void testePedido_quandoConsultarTodosPedidos_entaoRetornaPedidosComSucesso()
      throws Exception {

        PedidoDto pedido1 = PedidoDto.builder()
                          .status(Status.PENDENTE.name())
                          .numeroPedido("ORD-170001").build();
        PedidoDto pedido2 = PedidoDto.builder()
                        .status(Status.PENDENTE.name())
                        .numeroPedido("ORD-170002").build();
        List<PedidoDto> todosPedidos = Arrays.asList(pedido1,pedido2);

        doReturn(PedidoMapper.toListPedidoEntity(todosPedidos))
          .when(service).buscarTodosPedidos();

        mvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroPedido", is("ORD-170001")))
                .andExpect(jsonPath("$[1].numeroPedido", is("ORD-170002")));
    }
    
}
