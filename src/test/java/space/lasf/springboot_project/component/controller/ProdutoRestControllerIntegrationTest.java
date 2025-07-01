package space.lasf.springboot_project.component.controller;

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

import space.lasf.springboot_project.domain.repository.ProdutoRepository;
import space.lasf.springboot_project.dto.ProdutoDto;
import space.lasf.springboot_project.dto.mapper.ProdutoMapper;
import space.lasf.springboot_project.service.ProdutoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProdutoRestControllerIntegrationTest  {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProdutoRepository repository;

    @MockitoBean
    private ProdutoService service;


    @Test
    public void testeProduto_quandoConsultarTodosProdutos_entaoRetornaProdutosComSucesso()
      throws Exception {

        ProdutoDto produto1 = ProdutoDto.builder().nome("Produto1").build();
        ProdutoDto produto2 = ProdutoDto.builder().nome("Produto2").build();
        List<ProdutoDto> todosProdutos = Arrays.asList(produto1,produto2);

        doReturn(ProdutoMapper.toListProdutoEntity(todosProdutos))
          .when(service).buscarTodosProdutos();

        mvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Produto1")))
                .andExpect(jsonPath("$[1].nome", is("Produto2")));
    }
    
}
