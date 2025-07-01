package space.lasf.springboot_project.unit.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import static org.hamcrest.Matchers.is;
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

import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.controller.ProdutoController;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.dto.ProdutoDto;
import space.lasf.springboot_project.dto.mapper.ProdutoMapper;
import space.lasf.springboot_project.service.ProdutoService;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@WebMvcTest(ProdutoController.class) 
public class ProdutoControllerTest extends TestFactory{

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ObjectsValidator<ProdutoDto> produtoValidator;

    @MockitoBean
    private ProdutoService produtoService;

    private ObjectMapper mapper = new ObjectMapper();

    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    public void setUp() {
        
        // Cria produtos para testes
        produto1 = gerarProduto();
        produto2 = gerarProduto();
    }

    @Test
    public void deveRetornarNovoProdutoCriado() throws Exception {
        // Cria produtos para testes
        Produto novoProduto = gerarProduto();
        ProdutoDto dto = ProdutoMapper.toProdutoDto(novoProduto);
        
        // Configura o mock
        doReturn(novoProduto).when(produtoService).criarProduto(novoProduto);

        // Executa e verifica
        mockMvc.perform(post("/api/produtos")
                            .content(mapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").value(is(novoProduto.getId())))
                .andExpect(jsonPath("nome").value(is(novoProduto.getNome())));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).criarProduto(novoProduto);
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
                .andExpect(jsonPath("$[0].id", is(produto1.getId())))
                .andExpect(jsonPath("$[0].nome", is(produto1.getNome())))
                .andExpect(jsonPath("$[1].id", is(produto2.getId())))
                .andExpect(jsonPath("$[1].nome", is(produto2.getNome())));

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
                .andExpect(jsonPath("$[0].id", is(produto2.getId())))
                .andExpect(jsonPath("$[0].nome", is(produto2.getNome())));

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
                .andExpect(jsonPath("$[0].id", is(produto1.getId())))
                .andExpect(jsonPath("$[0].nome", is(produto1.getNome())))
                .andExpect(jsonPath("$[1].id", is(produto2.getId())))
                .andExpect(jsonPath("$[1].nome", is(produto2.getNome())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1)).buscarProdutosPorFaixaDePreco(
            new BigDecimal("5.00"), new BigDecimal("25.00"));
    }

    

//	  @Test
//    public void deveRetornarUmPedidoAPartirDoId() throws Exception {
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/{id}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
//	
//	  @Test
//    public void deveRetornarUmPedidoAPartirDoSkuDoProduto() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/sku/{sku}",produto1.getSku()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
//	  @Test
//    deveAlterarOProdutoComIdSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(put("/api/produtos/{id}",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
//
//	  @Test
//    public void deveRemoverOProdutoComIdSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(delete("/api/produtos/{id}",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
//
//	  @Test
//    public void deveAlterarOEstoqueDoProdutoComIdSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(put("/api/produtos/{id}/estoque",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}
//
//	  @Test
//    public void deveAlterarOPrecoDoProdutoComIdSolicitado() throws Exception {}
//		// Configura o mock
//        doReturn(produto1)
//            .when(produtoService)
//            .buscarProdutos();
//		// Executa e verifica
//        mockMvc.perform(get("/api/produtos/{id}/preco",produto1.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$.id", is(produto1.getId()))
//                .andExpect(jsonPath("$.nome", is(produto1.getNome())));
//
//        // Verifica se o método do serviço foi chamado com os parâmetros corretos
//        verify(produtoService, times(1)).buscarProdutos();
//	}

}
