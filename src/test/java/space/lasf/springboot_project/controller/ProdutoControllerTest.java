package space.lasf.springboot_project.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import space.lasf.springboot_project.domain.model.Pedido;
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
    private List<Produto> produtoAssets = new ArrayList<Produto>();

    @BeforeEach
    public void setUp() {
        
        // Cria produtos para testes
        produto1 = gerarProduto();
        produto2 = gerarProduto();
        Produto produto3 = gerarProduto();
        produtoAssets.addAll(Arrays.asList(produto1,produto2,produto3));
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
        // Configura entidade utilizada
        BigDecimal totalInvetario = produtoAssets.stream()
                                    .map(Produto::calcularTotalEstoque)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Configura o mock
        doReturn(totalInvetario)
            .when(produtoService).calcularValorInventario();

        // Executa e verificas
        mockMvc.perform(get("/api/produtos/total-inventario"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalInvetario.toString()));

        // Verifica se o método do serviço foi chamado
        verify(produtoService, times(1)).calcularValorInventario();
    }

    @Test
    public void deveRetornarProdutosDentroDaFaixaDePreco() throws Exception {
        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2)).when(produtoService)
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

	@Test
    public void deveRetornarUmProdutoAPartirDoId() throws Exception {
		// Configura entidade utilizada
        Produto produtoSelecionado = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        // Configura o mock
        doReturn(Optional.of(produtoSelecionado)).when(produtoService).buscarProdutoPorId(produtoSelecionado.getId());
		// Executa e verifica
        mockMvc.perform(get("/api/produtos/{id}",produtoSelecionado.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(produtoSelecionado.getId())))
                .andExpect(jsonPath("$.nome", is(produtoSelecionado.getNome())))
                .andExpect(jsonPath("$.sku", is(produtoSelecionado.getSku())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
            .buscarProdutoPorId(produtoSelecionado.getId());
	}
	
	@Test
    public void deveRetornarUmProdutoAPartirDoSkuDoProduto() throws Exception {
		// Configura entidade utilizada
        Produto produtoSelecionado = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        // Configura o mock
        doReturn(Optional.of(produtoSelecionado)).when(produtoService)
            .buscarProdutoPorSku(produtoSelecionado.getSku());
		// Executa e verifica
        mockMvc.perform(get("/api/produtos/sku/{sku}",produtoSelecionado.getSku()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(produtoSelecionado.getId())))
                .andExpect(jsonPath("$.nome", is(produtoSelecionado.getNome())))
                .andExpect(jsonPath("$.sku", is(produtoSelecionado.getSku())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
            .buscarProdutoPorSku(produtoSelecionado.getSku());
	}
	@Test
    public void deveAlterarOProdutoComIdSolicitado() throws Exception {
		// Configura entidade utilizada
        Produto produtoAlterado = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        produtoAlterado.setNome("Produto Alterado");
        produtoAlterado.setPreco(new BigDecimal("100.00"));
        // Configura o mock
        doReturn(produtoAlterado).when(produtoService)
                .alterarProduto(produtoAlterado);
        doReturn(Optional.of(produtoAlterado)).when(produtoService)
                .buscarProdutoPorId(produtoAlterado.getId());
		// Executa e verifica
        mockMvc.perform(put("/api/produtos/{id}",produtoAlterado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ProdutoMapper.toProdutoDto(produtoAlterado))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(produtoAlterado.getId())))
                .andExpect(jsonPath("$.nome", is(produtoAlterado.getNome())))
                .andExpect(jsonPath("$.sku", is(produtoAlterado.getSku())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
                .alterarProduto(produtoAlterado);
        verify(produtoService, times(1))
                .buscarProdutoPorId(produtoAlterado.getId());
	}

	@Test
    public void deveRemoverOProdutoComIdSolicitado() throws Exception {
		// Configura entidade utilizada
        Produto produtoRemovido = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        // Configura o mock
        doNothing().when(produtoService)
            .removerProduto(produtoRemovido.getId());
		// Executa e verifica
        mockMvc.perform(delete("/api/produtos/{id}",produtoRemovido.getId()))
                .andExpect(status().isNoContent());

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
            .removerProduto(produtoRemovido.getId());
	}

	  @Test
    public void deveAlterarOEstoqueDoProdutoComIdSolicitado() throws Exception {
		// Configura entidade utilizada
        Produto produtoAlterado = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        // Configura o mock
        doNothing().when(produtoService)
            .alterarEstoqueProduto(produtoAlterado.getId(), 20);
		// Executa e verifica
        mockMvc.perform(put("/api/produtos/{id}/estoque",produtoAlterado.getId())
                .param("estoque", "20"))
                .andExpect(status().isOk());

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
            .alterarEstoqueProduto(produtoAlterado.getId(), 20);
	}

	  @Test
    public void deveAlterarOPrecoDoProdutoComIdSolicitado() throws Exception {
		// Configura entidade utilizada
        Produto produtoAlterado = produtoAssets.get(new Random().nextInt(this.produtoAssets.size()));
        // Configura o mock
        doNothing().when(produtoService)
            .alterarPrecoProduto(produtoAlterado.getId(), new BigDecimal("75.00"));
		// Executa e verifica
        mockMvc.perform(put("/api/produtos/{id}/preco",produtoAlterado.getId())
                        .param("preco", "75.00"))
                .andExpect(status().isOk());

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(produtoService, times(1))
            .alterarPrecoProduto(produtoAlterado.getId(), new BigDecimal("75.00"));
	}

}
