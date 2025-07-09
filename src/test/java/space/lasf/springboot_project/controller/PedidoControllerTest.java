package space.lasf.springboot_project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.dto.ItemPedidoDto;
import space.lasf.springboot_project.dto.PedidoDto;
import space.lasf.springboot_project.service.PedidoService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    // Mock validators to avoid NullPointerException as they are not initialized in this slice test
    @MockitoBean
    private ObjectsValidator<ItemPedidoDto> itemPedidoValidator;
    @MockitoBean
    private ObjectsValidator<PedidoDto> pedidoValidator;

    @Test
    void validarPedido_shouldReturnNotFound_whenPedidoDoesNotExist() throws Exception {
        String pedidoId = UUID.randomUUID().toString();
        when(pedidoService.buscarPedidoPorId(pedidoId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/pedidos/{idPedido}/validar", pedidoId))
                .andExpect(status().isNotFound());
    }

    // Add more tests for other endpoints...

}