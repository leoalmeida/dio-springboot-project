package me.dio.dio_springboot_project.unit.controller;

import java.math.BigDecimal;
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

import me.dio.dio_springboot_project.controller.ProdutoController;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.dto.ProdutoDto;
import me.dio.dio_springboot_project.dto.mapper.ProdutoMapper;
import me.dio.dio_springboot_project.service.ProdutoService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebMvcTest(ProdutoController.class) 
@ActiveProfiles("test")
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    private ObjectMapper mapper = new ObjectMapper();

    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    public void setUp() {
        
        // Cria produtos para testes
        produto1 = new Produto();
        produto1.setId(UUID.randomUUID().toString());
        produto1.setNome("Produto 1");
        produto1.setDescricao("Descrição do Produto 1");
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setEstoque(100);
        produto1.setSku("SKU001");

        produto2 = new Produto();
        produto2.setId(UUID.randomUUID().toString());
        produto2.setNome("Produto 2");
        produto2.setDescricao("Descrição do Produto 2");
        produto2.setPreco(new BigDecimal("20.00"));
        produto2.setEstoque(5);
        produto2.setSku("SKU002");
    }

    @Test
    public void deveRetornarNovoProdutoCriado() throws Exception {
        // Cria produtos para testes
        ProdutoDto novoProduto = ProdutoDto.builder()
                .nome("Produto Novo")
                .descricao("Descrição de Produto Novo")
                .preco(new BigDecimal("100.00"))
                .estoque(40)
                .sku("SKU003").build();

        Produto entity = ProdutoMapper.toProdutoEntity(novoProduto);
        entity.setId(UUID.randomUUID().toString());
        // Configura o mock
        doReturn(entity).when(produtoService).criarProduto(entity);

        // Executa e verifica
        mockMvc.perform(post("/api/produtos")
                            .content(mapper.writeValueAsString(novoProduto))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.id", is(entity.getId())))
                .andExpect(jsonPath("$.nome", is(entity.getNome())));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).criarProduto(entity);
    }

    @Test
    public void deveRetornarTodosProdutos() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2))
            .when(produtoService).buscarTodosProdutos();

        // Executa e verifica
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Produto 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Produto 2")));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).buscarTodosProdutos();
    }

    @Test
    public void deveRetornarProdutosComEstoqueBaixo() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(produto2))
            .when(produtoService).buscarProdutosComEstoqueBaixo();

        // Executa e verifica
        mockMvc.perform(get("/api/produtos/emfalta"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].nome", is("Produto 2")));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).buscarProdutosComEstoqueBaixo();
    }

    @Test
    public void deveRetornarValorTotalInventario() throws Exception {
        // Configura o mock
        doReturn(new BigDecimal("1100.00"))
            .when(produtoService).calcularValorInventario();

        // Executa e verificas
        mockMvc.perform(get("/api/produtos/total-inventario"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", is(1100.00)));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).calcularValorInventario();
    }

    @Test
    public void deveRetornarProdutosDentroDaFaixaDePreco() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2))
            .when(produtoService)
            .buscarProdutosPorFaixaDePreco(new BigDecimal("5.00"), new BigDecimal("25.00"));
        
        // Executa e verifica
        mockMvc.perform(get("/api/produtos/preco")
                .param("minPreco", "5.00")
                .param("maxPreco", "25.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Produto 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Produto 2")));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1)).buscarProdutosPorFaixaDePreco(
            new BigDecimal("5.00"), new BigDecimal("25.00"));
    }

}
