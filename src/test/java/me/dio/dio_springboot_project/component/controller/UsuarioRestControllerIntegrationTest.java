package me.dio.dio_springboot_project.component.controller;

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

import me.dio.dio_springboot_project.domain.repository.UsuarioRepository;
import me.dio.dio_springboot_project.dto.UsuarioDto;
import me.dio.dio_springboot_project.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioRestControllerIntegrationTest  {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository repository;
    
    @MockitoBean
    private UsuarioService service;

    @Test
    public void testeProduto_quandoConsultarProdutos_thenStatus200()
      throws Exception {

        UsuarioDto usuario1 = new UsuarioDto("Produto1","email@test.com","user1","pass");
        UsuarioDto usuario2 = new UsuarioDto("Produto1","email2@test.com","user2","pass");
        List<UsuarioDto> todosUsuarios = Arrays.asList(usuario1,usuario2);

        doReturn(todosUsuarios)
          .when(service).listarUsuarios();

        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].login", is("user1")))
                .andExpect(jsonPath("$[1].login", is("user2")));
    }
}
